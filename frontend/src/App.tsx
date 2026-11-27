import { useEffect, useState } from 'react';
import type { StatProgress, Task } from './types';
import { completeTask, getStats, getTasks, rollOver } from './api';
import { todayLocal } from './utils/date';
import { StatBar } from './components/StatBar';
import { TaskItem } from './components/TaskItem';
import { AddTaskForm } from './components/AddTaskForm';
import { SoloLog } from './components/SoloLog';
import { FocusTimer } from './components/FocusTimer';
import { ExpenseTracker } from './components/ExpenseTracker';

const USER_ID = 15;

export default function App() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [stats, setStats] = useState<StatProgress[]>([]);
  const [message, setMessage] = useState<string | null>(null);

  // On load: roll over unfinished tasks first, then load tasks and stats
  useEffect(() => {
    async function load() {
      try {
        const today = todayLocal();
        const rolled = await rollOver(USER_ID, today);
        const [loadedTasks, loadedStats] = await Promise.all([
          getTasks(USER_ID, today),
          getStats(USER_ID),
        ]);
        setTasks(loadedTasks);
        setStats(loadedStats);
        if (rolled.rolledOver > 0) {
          setMessage(`${rolled.rolledOver} unfinished task(s) carried over to today.`);
        }
      } catch (err) {
        setMessage((err as Error).message);
      }
    }
    load();
  }, []);

  async function handleComplete(taskId: number) {
    try {
      const result = await completeTask(USER_ID, taskId);
      setTasks((prev) => prev.map((t) => (t.id === result.task.id ? result.task : t)));
      handleStatUpdated(result.stat);
      setMessage(`+${result.xpGained} ${result.stat.stat} XP!`);
    } catch (err) {
      setMessage((err as Error).message);
    }
  }

  function handleCreated(task: Task) {
    setTasks((prev) => [...prev, task]);
    setMessage(`Added "${task.title}".`);
  }

  function handleStatUpdated(stat: StatProgress) {
    setStats((prev) => prev.map((s) => (s.stat === stat.stat ? stat : s)));
  }

  const anchors = tasks.filter((t) => t.type === 'ANCHOR');
  const extras = tasks.filter((t) => t.type === 'EXTRA');
  const openTasks = tasks.filter((t) => t.status === 'OPEN');

  return (
    <main className="dashboard">
      <section className="window">
        <h2>Stats</h2>
        {stats.map((s) => (
          <StatBar key={s.stat} progress={s} />
        ))}
      </section>

      <section className="window">
        <h2>Anchor 3 · {anchors.length}/3</h2>
        <ul className="task-list">
          {anchors.map((task) => (
            <TaskItem key={task.id} task={task} onComplete={handleComplete} />
          ))}
        </ul>

        {extras.length > 0 && (
          <>
            <div className="section-label">Extras</div>
            <ul className="task-list">
              {extras.map((task) => (
                <TaskItem key={task.id} task={task} onComplete={handleComplete} />
              ))}
            </ul>
          </>
        )}

        <AddTaskForm
          userId={USER_ID}
          date={todayLocal()}
          onCreated={handleCreated}
          onError={setMessage}
        />
      </section>

      <SoloLog userId={USER_ID} onMessage={setMessage} />
      <ExpenseTracker userId={USER_ID} onMessage={setMessage} />
      

      <FocusTimer
        userId={USER_ID}
        openTasks={openTasks}
        onStatUpdated={handleStatUpdated}
        onMessage={setMessage}
      />

      <section className="window window--wide dialog">
        {message ?? 'Choose a task to begin your day.'}
      </section>
    </main>
  );
}