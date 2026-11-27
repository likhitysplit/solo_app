import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { STATS } from '../types';
import type { LogEntry, Stat } from '../types';
import { createLog, getLogs } from '../api';

type Props = {
  onMessage: (message: string) => void;
};

export function SoloLog({onMessage }: Props) {
  const [entries, setEntries] = useState<LogEntry[]>([]);
  const [content, setContent] = useState('');
  const [tag, setTag] = useState<Stat | ''>('');         

  useEffect(() => {
    getLogs()
      .then(setEntries)
      .catch((err) => onMessage(err.message));
  });                                            

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    try {
      const entry = await createLog(content, tag === '' ? null : tag);
      setEntries((prev) => [entry, ...prev]);        
      setContent('');
      setTag('');
      onMessage('Win logged. Nice work going solo.');
    } catch (err) {
      onMessage((err as Error).message);
    }
  }

  return (
    <section className="window">
      <h2>Solo Log</h2>
      <form onSubmit={handleSubmit}>
        <textarea
          className="pixel-input"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="What did you handle on your own today?"
          maxLength={1000}
        />
        <div className="add-task">
          <select className="pixel-input" value={tag} onChange={(e) => setTag(e.target.value as Stat | '')}>
            <option value="">No stat</option>
            {STATS.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
          <button className="pixel-button" type="submit">Log it</button>
        </div>
      </form>

      <ul className="log-list">
        {entries.map((entry) => (
          <li key={entry.id} className="log-entry" data-stat={entry.tag ?? undefined}>
            <div>{entry.content}</div>
            <div className="log-entry__meta">
              {new Date(entry.createdAt).toLocaleString()}
              {entry.tag && <span className="stat__name"> · {entry.tag}</span>}
            </div>
          </li>
        ))}
      </ul>
    </section>
  );
}