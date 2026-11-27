import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { EXPENSE_CATEGORIES } from '../types';
import type { BudgetPeriod, BudgetStatus, ExpenseCategory } from '../types';
import { closeBudgetPeriods, getBudgetSummary, setBudget } from '../api';
import { dollarsToCents, formatCents } from '../utils/money';

type Props = {
  refreshKey: number;                    // changes whenever an expense is logged
  onMessage: (message: string) => void;
  onXpAwarded: () => void;               // lets the dashboard reload stats
};

export function BudgetPanel({ refreshKey, onMessage, onXpAwarded }: Props) {
  const [statuses, setStatuses] = useState<BudgetStatus[]>([]);
  const [category, setCategory] = useState<ExpenseCategory>('GROCERIES');
  const [period, setPeriod] = useState<BudgetPeriod>('WEEKLY');
  const [amount, setAmount] = useState('');

  // Once, on load: close any finished weeks/months and announce XP
  useEffect(() => {
    closeBudgetPeriods()
      .then((results) => {
        const xp = results.reduce((sum, r) => sum + r.xpAwarded, 0);
        if (xp > 0) {
          onMessage(`Budget check: +${xp} Diligence XP for staying on budget!`);
          onXpAwarded();
        }
      })
      .catch(() => null);               // harmless if it fails; it'll run again next visit
  }, []);

  // Reload the summary on load AND whenever an expense is logged
  useEffect(() => {
    getBudgetSummary()
      .then(setStatuses)
      .catch((err) => onMessage(err.message));
  }, [refreshKey]);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const limitCents = dollarsToCents(amount);
    if (limitCents === null) {
      onMessage('Enter a budget like 80.00');
      return;
    }
    try {
      const saved = await setBudget(category, period, limitCents);
      setAmount('');
      onMessage(`${saved.category} budget set to ${formatCents(saved.limitCents)} ${saved.period.toLowerCase()} — starts ${saved.effectiveFrom}.`);
    } catch (err) {
      onMessage((err as Error).message);
    }
  }

  return (
    <section className="window">
      <h2>Budgets</h2>

      {statuses.length === 0 && (
        <p className="section-label">No active budgets yet. New budgets start next period.</p>
      )}

      {statuses.map((s) => {
        const percent = Math.min(100, (s.spentCents / s.limitCents) * 100);
        const over = s.spentCents > s.limitCents;
        return (
          <div key={s.category} className="stat">
            <div className="stat__header">
              <span>{s.category} · {s.period.toLowerCase()}</span>
              <span className={over ? 'budget--over' : 'stat__level'}>
                {formatCents(s.spentCents)} / {formatCents(s.limitCents)}
              </span>
            </div>
            <div className="xp-bar">
              <div
                className={over ? 'xp-bar__fill xp-bar__fill--over' : 'xp-bar__fill'}
                style={{ width: `${percent}%` }}
              />
            </div>
            <div className="stat__xp">{s.daysLeft} day(s) left</div>
          </div>
        );
      })}

      <form onSubmit={handleSubmit} className="add-task">
        <select
          className="pixel-input"
          value={category}
          onChange={(e) => setCategory(e.target.value as ExpenseCategory)}
        >
          {EXPENSE_CATEGORIES.map((c) => (
            <option key={c} value={c}>{c}</option>
          ))}
        </select>
        <select
          className="pixel-input"
          value={period}
          onChange={(e) => setPeriod(e.target.value as BudgetPeriod)}
        >
          <option value="WEEKLY">Weekly</option>
          <option value="MONTHLY">Monthly</option>
        </select>
        <input
          className="pixel-input pixel-input--xp"
          type="number"
          step="0.01"
          min="1"
          placeholder="80.00"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />
        <button className="pixel-button" type="submit">Set</button>
      </form>
    </section>
  );
}