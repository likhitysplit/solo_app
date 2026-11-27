import { useState } from 'react';
import type { FormEvent } from 'react';
import { STATS } from '../types';
import type { Stat, Task, TaskType } from '../types';
import { createTask } from '../api';

type Props = {
  date: string;
  onCreated: (task: Task) => void;
  onError: (message: string) => void;
};

export function AddTaskForm({date, onCreated, onError }: Props) {
  const [title, setTitle] = useState('');
  const [type, setType] = useState<TaskType>('ANCHOR');
  const [stat, setStat] = useState<Stat>('DILIGENCE');
  const [baseXp, setBaseXp] = useState(10);
  const [saving, setSaving] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();                        
    if (title.trim() === '') {
      onError('Give your task a name first.');
      return;
    }
    setSaving(true);
    try {
      const task = await createTask({ title: title.trim(), type, stat, baseXp, date });
      onCreated(task);
      setTitle('');                             
    } catch (err) {
      onError((err as Error).message);         
    } finally {
      setSaving(false);
    }
  }

  return (
    <form className="add-task" onSubmit={handleSubmit}>
      <input
        className="pixel-input add-task__title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="New task..."
        maxLength={200}
      />
      <select className="pixel-input" value={type} onChange={(e) => setType(e.target.value as TaskType)}>
        <option value="ANCHOR">Anchor</option>
        <option value="EXTRA">Extra</option>
      </select>
      <select className="pixel-input" value={stat} onChange={(e) => setStat(e.target.value as Stat)}>
        {STATS.map((s) => (
          <option key={s} value={s}>{s}</option>
        ))}
      </select>
      <input
        className="pixel-input pixel-input--xp"
        type="number"
        min={1}
        max={20}
        value={baseXp}
        onChange={(e) => setBaseXp(Number(e.target.value))}
      />
      <button className="pixel-button" type="submit" disabled={saving}>
        Add
      </button>
    </form>
  );
}