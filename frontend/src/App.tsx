import { useEffect, useState } from 'react';
import type { StatProgress, Task } from './types';
import { completeTask, getStats, getTasks } from './api';
import { StatBar } from './components/StatBar';
import { TaskItem } from './components/TaskItem';

const USER_ID = 15;

function todayLocal(): string {
  return new Date().toLocaleDateString('en-CA');
}

export default function App() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [stats, setStats] = useState<StatProgress[]>([]);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([getTasks(USER_ID, todayLocal()), getStats(USER_ID)])
      .then(([loadedTasks, loadedStats]) => {
        setTasks(loadedTasks);
        setStats(loadedStats);
      })
      .catch((err) => setMessage(err.message));
  }, []);

  async function handleComplete(taskId: number) {
    try {
      const result = await completeTask(USER_ID, taskId);

      setTasks((prev) => prev.map((t) => (t.id === result.task.id ? result.task : t)));
      setStats((prev) => prev.map((s) => (s.stat === result.stat.stat ? result.stat : s)));

      setMessage(`+${result.xpGained} ${result.stat.stat} XP!`);
    } catch (err) {
      setMessage((err as Error).message);
    }
  }

  return (
    <main style={{ padding: 24 }}>
      <h2>Stats</h2>
      {stats.map((s) => (
        <StatBar key={s.stat} progress={s} />
      ))}

      <h2>Today's Anchors</h2>
      <ul>
        {tasks.map((task) => (
          <TaskItem key={task.id} task={task} onComplete={handleComplete} />
        ))}
      </ul>

      {message && <p>{message}</p>}
    </main>
  );
}