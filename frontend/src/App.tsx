import { useEffect, useState } from 'react';
import type { Task } from './types';

const USER_ID = 15;

function todayLocal(): string {
  return new Date().toLocaleDateString('en-CA');
}

export default function App() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch(`/api/users/${USER_ID}/tasks?date=${todayLocal()}`)
        .then((res) => {
          if (!res.ok) throw new Error(`HTTP ${res.status}`);
          return res.json();
        })
        .then((data: Task[]) => setTasks(data))
        .catch((err) => setError(err.message));
  }, []);

  if (error) return <p>Error: {error}</p>;

  return (
      <main>
        <h1>Today's Anchors</h1>
        <ul>
          {tasks.map((task) => (
              <li key={task.id}>
                [{task.stat}] {task.title} ({task.baseXp} XP) {task.status === 'COMPLETED' ? '✓' : ''}
              </li>
          ))}
        </ul>
      </main>
  );
}