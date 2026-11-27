import type { ExpenseCategory, ReceiptDraft } from '../types';

const MAX_CENTS = 10_000_000;

// A money amount: dollars, then "." or "," (with optional stray spaces), then exactly 2 digits.
// (?!\d) = "not followed by another digit", so "23.345" isn't read as 23.34.
const MONEY = /(\d{1,7})\s*[.,]\s*(\d{2})(?!\d)/g;

// Lines that mention a total but are NOT the final amount paid.
const EXCLUDE = /\b(SUB\s*TOTAL|TOTAL\s+SAVINGS|TOTAL\s+ITEMS|ITEMS?\s+TOTAL)\b/i;

// Lines that usually hold the final amount paid.
const TOTAL_KEYWORD = /\b(GRAND\s+TOTAL|TOTAL|AMOUNT\s+DUE|BALANCE\s+DUE)\b/i;

// ---------------------------------------------------------------------------

/** Split raw OCR text into trimmed, non-empty lines. */
export function toLines(text: string): string[] {
  return text
    .split('\n')
    .map((line) => line.trim())
    .filter((line) => line !== '');
}

/** Every money amount on a line, in cents. */
function amountsIn(line: string): number[] {
  // "1,234.56" → "1234.56": drop thousands separators (a comma followed by exactly 3 digits)
  const normalized = line.replace(/(\d),(\d{3})(?!\d)/g, '$1$2');

  const result: number[] = [];
  for (const m of normalized.matchAll(MONEY)) {
    const cents = Number(m[1]) * 100 + Number(m[2]);   // integer math, no floats
    if (cents >= 1 && cents <= MAX_CENTS) {
      result.push(cents);
    }
  }
  return result;
}

/** The amount actually paid, in cents, or null. */
export function findTotalCents(lines: string[]): number | null {
  let fromKeyword: number | null = null;
  let largest: number | null = null;

  for (const line of lines) {
    const amounts = amountsIn(line);
    if (amounts.length === 0) continue;

    const lineMax = Math.max(...amounts);
    if (largest === null || lineMax > largest) {
      largest = lineMax;
    }

    if (TOTAL_KEYWORD.test(line) && !EXCLUDE.test(line)) {
      fromKeyword = amounts[amounts.length - 1];   // keep overwriting → the LAST total line wins
    }
  }

  return fromKeyword ?? largest;                   // keyword total first, else the biggest amount
}

// ---------------------------------------------------------------------------

/** 'YYYY-MM-DD' if the date really exists, otherwise null. */
function toIso(year: number, month: number, day: number): string | null {
  const d = new Date(year, month - 1, day);       // JS months are 0-based
  const real =
    d.getFullYear() === year && d.getMonth() === month - 1 && d.getDate() === day;
  if (!real) return null;                          // e.g. Feb 30 silently became Mar 2

  return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
}

/** The purchase date as 'YYYY-MM-DD', or today if none is valid. */
export function findDate(text: string, today: string): string {
  const candidates: (string | null)[] = [];

  // 2026-11-25
  for (const m of text.matchAll(/\b(\d{4})-(\d{2})-(\d{2})\b/g)) {
    candidates.push(toIso(Number(m[1]), Number(m[2]), Number(m[3])));
  }

  // 11/25/2026 or 11/25/26
  for (const m of text.matchAll(/\b(\d{1,2})\/(\d{1,2})\/(\d{4}|\d{2})\b/g)) {
    let year = Number(m[3]);
    if (year < 100) year += 2000;
    candidates.push(toIso(year, Number(m[1]), Number(m[2])));
  }

  for (const candidate of candidates) {
    if (candidate !== null && candidate <= today) {   // 'YYYY-MM-DD' strings compare correctly
      return candidate;
    }
  }
  return today;
}

// ---------------------------------------------------------------------------

/** The store name from the top of the receipt, or null. */
export function findMerchant(lines: string[]): string | null {
  for (const line of lines.slice(0, 5)) {
    if (/^\d/.test(line)) continue;                    // addresses, phone numbers
    if (/www\.|\.com/i.test(line)) continue;           // websites

    const letters = (line.match(/[a-z]/gi) ?? []).length;
    if (letters < 3) continue;                         // "***", "#552", stray symbols

    const cleaned = line
      .replace(/#\s*\d+/g, '')                         // store numbers: "TRADER JOE'S #552"
      .replace(/\s+/g, ' ')                            // collapse repeated spaces
      .trim()
      .slice(0, 100);                                  // backend limit

    if (cleaned !== '') return cleaned;
  }
  return null;
}

// ---------------------------------------------------------------------------

// Checked in this order: the first category with a match wins.
// FOOD comes before TRANSPORTATION so "uber eats" is food, not a ride.
const CATEGORY_KEYWORDS: Record<ExpenseCategory, string[]> = {
  GROCERIES: ['trader joe', 'whole foods', 'safeway', 'kroger', 'aldi', 'costco', 'wegmans',
    'publix', 'grocery', 'supermarket', 'market'],
  FOOD: ['uber eats', 'doordash', 'grubhub', 'starbucks', 'dunkin', 'mcdonald', 'chipotle',
    'shake shack', 'cafe', 'coffee', 'restaurant', 'pizza', 'grill', 'bakery', 'diner'],
  TRANSPORTATION: ['uber', 'lyft', 'mta', 'metro', 'transit', 'parking', 'shell', 'chevron',
    'exxon', 'amtrak', 'fuel'],
  HOUSING: ['rent', 'lease', 'apartment', 'property management'],
  BILLS: ['verizon', 't-mobile', 'comcast', 'xfinity', 'spectrum', 'con edison', 'electric',
    'utility', 'insurance'],
  HEALTH: ['cvs', 'walgreens', 'rite aid', 'pharmacy', 'clinic', 'dental', 'medical',
    'hospital', 'gym', 'fitness'],
  FUN: ['amc', 'regal', 'cinema', 'theater', 'theatre', 'netflix', 'spotify', 'steam',
    'ticketmaster', 'bowling', 'arcade'],
  SHOPPING: ['amazon', 'target', 'walmart', 'best buy', 'ikea', 'home depot', 'uniqlo', 'zara'],
  OTHER: [],
};

/** Escape regex special characters so a keyword is matched literally. */
function escapeRegExp(s: string): string {
  return s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

/** True if the keyword appears as a whole word (so "rent" doesn't match "current"). */
function containsWord(haystack: string, keyword: string): boolean {
  return new RegExp(`\\b${escapeRegExp(keyword)}\\b`, 'i').test(haystack);
}

function categoryFor(haystack: string): ExpenseCategory | null {
  for (const [category, keywords] of Object.entries(CATEGORY_KEYWORDS)) {
    if (keywords.some((k) => containsWord(haystack, k))) {
      return category as ExpenseCategory;
    }
  }
  return null;
}

/** A category from keywords: the merchant name first, then the whole text. Defaults to OTHER. */
export function guessCategory(merchant: string | null, text: string): ExpenseCategory {
  const fromMerchant = merchant ? categoryFor(merchant) : null;
  return fromMerchant ?? categoryFor(text) ?? 'OTHER';
}

// ---------------------------------------------------------------------------

/** Turn raw OCR text into a draft the form can be filled from. */
export function parseReceipt(text: string, today: string): ReceiptDraft {
  const lines = toLines(text);
  const merchant = findMerchant(lines);

  return {
    amountCents: findTotalCents(lines),
    merchant,
    category: guessCategory(merchant, text),
    spentOn: findDate(text, today),
  };
}