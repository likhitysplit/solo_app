import { useEffect, useState } from 'react';
import type { ChangeEvent, FormEvent } from 'react';
import { EXPENSE_CATEGORIES } from '../types';
import type { Expense, ExpenseCategory } from '../types';
import { createExpense, getExpenses } from '../api';
import { dollarsToCents, formatCents } from '../utils/money';
import { todayLocal } from '../utils/date';
import { readReceiptText } from '../utils/ocr';
import { parseReceipt } from '../utils/receiptParser';

type Props = {
  onMessage: (message: string) => void;
  onLogged: () => void;
};

export function ExpenseTracker({ onMessage, onLogged }: Props) {
  const [expenses, setExpenses] = useState<Expense[]>([]);

  const [amount, setAmount] = useState('');
  const [merchant, setMerchant] = useState('');
  const [category, setCategory] = useState<ExpenseCategory>('FOOD');
  const [spentOn, setSpentOn] = useState(todayLocal());
  const [note, setNote] = useState('');
  const [scanning, setScanning] = useState(false);

  useEffect(() => {
    getExpenses()
      .then(setExpenses)
      .catch((err) => onMessage(err.message));
  }, []);

  async function handleScan(e: ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    e.target.value = '';                       // lets the same file be picked again
    if (!file) return;

    setScanning(true);
    onMessage('Reading your receipt... (the first scan takes a little longer)');
    try {
      const text = await readReceiptText(file);
      const draft = parseReceipt(text, todayLocal());

      if (draft.amountCents !== null) {
        setAmount((draft.amountCents / 100).toFixed(2));
      }
      if (draft.merchant !== null) {
        setMerchant(draft.merchant);
      }
      setCategory(draft.category);
      setSpentOn(draft.spentOn);

      onMessage(
        draft.amountCents === null
          ? "Couldn't find the total. Please enter it, then press \"Log it\"."
          : 'Check the details, then press "Log it".',
      );
    } catch {
      onMessage("Couldn't read that image. Try a clearer photo, or enter it by hand.");
    } finally {
      setScanning(false);
    }
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();

    const amountCents = dollarsToCents(amount);
    if (amountCents === null) {
      onMessage('Enter an amount like 12.49');
      return;
    }

    try {
      const saved = await createExpense({
        amountCents,
        merchant,
        category,
        spentOn,
        note: note.trim() === '' ? null : note,
      });
      setExpenses((prev) =>
        [saved, ...prev].sort((a, b) => b.spentOn.localeCompare(a.spentOn) || b.id - a.id),
      );
      onLogged();
      setAmount('');
      setMerchant('');
      setNote('');
      onMessage(`Logged ${formatCents(saved.amountCents)} at ${saved.merchant}`);
    } catch (err) {
      onMessage((err as Error).message);
    }
  }

  const total = expenses.reduce((sum, e) => sum + e.amountCents, 0);

  return (
    <section className="window">
      <h2>Expenses</h2>

      <label className={scanning ? 'pixel-button pixel-button--disabled' : 'pixel-button'}>
        {scanning ? 'Scanning...' : 'Scan receipt'}
        <input
          type="file"
          accept="image/*"
          onChange={handleScan}
          disabled={scanning}
          hidden
        />
      </label>

      <form onSubmit={handleSubmit}>
        <div className="add-task">
          <input
            className="pixel-input pixel-input--xp"
            type="number"
            step="0.01"
            min="0.01"
            placeholder="0.00"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />
          <input
            className="pixel-input add-task__title"
            placeholder="Where? (merchant)"
            maxLength={100}
            value={merchant}
            onChange={(e) => setMerchant(e.target.value)}
          />
          <select
            className="pixel-input"
            value={category}
            onChange={(e) => setCategory(e.target.value as ExpenseCategory)}
          >
            {EXPENSE_CATEGORIES.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
          <input
            className="pixel-input"
            type="date"
            max={todayLocal()}
            value={spentOn}
            onChange={(e) => setSpentOn(e.target.value)}
          />
          <input
            className="pixel-input add-task__title"
            placeholder="Note (optional)"
            maxLength={500}
            value={note}
            onChange={(e) => setNote(e.target.value)}
          />
          <button className="pixel-button" type="submit">Log it</button>
        </div>
      </form>

      <div className="section-label">Recent · {formatCents(total)}</div>
      <ul className="log-list">
        {expenses.map((e) => (
          <li key={e.id} className="expense">
            <div className="expense__merchant">
              <div>{e.merchant}</div>
              <div className="expense__meta">
                {e.category} · {e.spentOn}
                {e.note && ` · ${e.note}`}
              </div>
            </div>
            <div className="expense__amount">{formatCents(e.amountCents)}</div>
          </li>
        ))}
      </ul>
    </section>
  );
}