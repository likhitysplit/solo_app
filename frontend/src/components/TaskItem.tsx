import type { Stat, Task } from '../types';

const STAT_SHORT: Record<Stat, string> = {
  INTELLIGENCE: 'INT',
  DILIGENCE: 'DIL',
  EXPRESSION: 'EXP',
  SOCIABILITY: 'SOC',
  FITNESS: 'FIT',
};

type Props = {
  task: Task;
  onComplete: (taskId: number) => void;
};

export function TaskItem({ task, onComplete }: Props) {
  const done = task.status === 'COMPLETED';
  const rolledOver = task.originalDate !== task.scheduledDate;

  return (
    <li className={done ? 'task task--done' : 'task'} data-stat={task.stat}>
      <span className="task__stat">{STAT_SHORT[task.stat]}</span>
      {rolledOver && (
        <span className="task__rolled" title={`Originally planned for ${task.originalDate}`}>↻</span>
      )}
      <span className="task__title">{task.title}</span>
      <span className="task__xp">{task.baseXp} XP</span>
      {done ? (
        <span className="task__check">✓</span>
      ) : (
        <button className="pixel-button" onClick={() => onComplete(task.id)}>
          Done
        </button>
      )}
    </li>
  );
}