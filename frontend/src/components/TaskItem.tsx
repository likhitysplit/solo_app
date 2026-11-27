import type { Task } from '../types';

type Props = {
  task: Task;
  onComplete: (taskId: number) => void;
};

export function TaskItem({ task, onComplete }: Props) {
  const done = task.status === 'COMPLETED';

  return (
    <li>
      [{task.stat}] {task.title} ({task.baseXp} XP){' '}
      {done ? '✓' : <button onClick={() => onComplete(task.id)}>Complete</button>}
    </li>
  );
}