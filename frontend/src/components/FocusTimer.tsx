import { useEffect, useState } from 'react';
import type { FocusSession, StatProgress, Task } from '../types';
import { finishFocus, getActiveFocus, startFocus } from '../api';

type Props = {
  openTasks: Task[];
  onStatUpdated: (stat: StatProgress) => void;
  onMessage: (message: string) => void;
};

const LENGTHS = [15, 25, 45];

function formatClock(ms: number): string {
  const totalSeconds = Math.ceil(ms / 1000);
  const m = Math.floor(totalSeconds / 60);
  const s = totalSeconds % 60;
  return `${m}:${s.toString().padStart(2, '0')}`;
}

export function FocusTimer({openTasks, onStatUpdated, onMessage }: Props) {
  const [active, setActive] = useState<FocusSession | null>(null);
  const [taskId, setTaskId] = useState<number | ''>('');
  const [minutes, setMinutes] = useState(25);
  const [now, setNow] = useState(Date.now());

  useEffect(() => {
    getActiveFocus()
      .then(setActive)
      .catch((err) => onMessage(err.message));
  });

  useEffect(() => {
    if (!active) return;
    const id = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(id);
  }, [active]);

  async function handleStart() {
    try {
      const session = await startFocus(taskId === '' ? null : taskId, minutes);
      setNow(Date.now());
      setActive(session);
      onMessage('Focus started. You got this.');
    } catch (err) {
      onMessage((err as Error).message);
    }
  }

  async function handleFinish() {
    if (!active) return;
    try {
      const result = await finishFocus(active.id);
      setActive(null);
      if (result.stat) {
        onStatUpdated(result.stat);
        onMessage(`Focus complete: +${result.session.xpAwarded} ${result.stat.stat} XP!`);
      } else {
        onMessage('Focus session ended.');
      }
    } catch (err) {
      onMessage((err as Error).message);
    }
  }

  if (active) {
    const endsAt = new Date(active.startedAt).getTime() + active.plannedMinutes * 60_000;
    const remaining = Math.max(0, endsAt - now);
    const linked = openTasks.find((t) => t.id === active.taskId);

    return (
      <section className="window">
        <h2>Focus</h2>
        <div className="section-label">{linked ? linked.title : 'General focus'}</div>
        <div className="focus__clock">{formatClock(remaining)}</div>
        <button className="pixel-button" onClick={handleFinish}>
          {remaining === 0 ? "Time's up! Claim XP" : 'End session'}
        </button>
      </section>
    );
  }

  return (
    <section className="window">
      <h2>Focus</h2>
      <div className="add-task">
        <select
          className="pixel-input"
          value={taskId}
          onChange={(e) => setTaskId(e.target.value === '' ? '' : Number(e.target.value))}
        >
          <option value="">General focus (no XP)</option>
          {openTasks.map((t) => (
            <option key={t.id} value={t.id}>{t.title}</option>
          ))}
        </select>
        <select
          className="pixel-input"
          value={minutes}
          onChange={(e) => setMinutes(Number(e.target.value))}
        >
          {LENGTHS.map((m) => (
            <option key={m} value={m}>{m} min</option>
          ))}
        </select>
        <button className="pixel-button" onClick={handleStart}>Start</button>
      </div>
    </section>
  );
}