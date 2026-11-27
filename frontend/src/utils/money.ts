const usd = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' });

export function formatCents(cents: number): string {
  return usd.format(cents / 100);
}

export function dollarsToCents(input: string): number | null {
  const value = parseFloat(input);
  if (isNaN(value) || value <= 0) {
    return null;
  }
  return Math.round(value * 100);
}