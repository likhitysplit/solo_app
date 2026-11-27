import { useEffect, useState } from 'react';
import type { Me, StatProgress, Task } from './types';
import { completeTask, getStats, getTasks, logout, rollOver } from './api';
import { todayLocal } from './utils/date';
import { StatBar } from './components/StatBar';
import { TaskItem } from './components/TaskItem';
import { AddTaskForm } from './components/AddTaskForm';
import { SoloLog } from './components/SoloLog';
import { FocusTimer } from './components/FocusTimer';
import { ExpenseTracker } from './components/ExpenseTracker';
import { BudgetPanel } from './components/BudgetPanel';

type Props = {
  me: Me;
  onLoggedOut: () => void;
};

export function Dashboard({ me, onLoggedOut }: Props) {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [stats, setStats] = useState<StatProgress[]>([]);
  const [message, setMessage] = useState<string | null>(null);
  const [expensesVersion, setExpensesVersion] = useState(0);

  useEffect(() => {
    async function load() {
      try {
        const rolled = await rollOver();
        const [loadedTasks, loadedStats] = await Promise.all([
          getTasks(todayLocal()),
          getStats(),
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
      const result = await completeTask(taskId);
      setTasks((prev) => prev.map((t) => (t.id === result.task.id ? result.task : t)));
      handleStatUpdated(result.stat);
      setMessage(`+${result.xpGained} ${result.stat.stat} XP!`);
    } catch (err) {
      setMessage((err as Error).message);
    }
  }

  async function handleLogout() {
    await logout().catch(() => null);
    onLoggedOut();
  }

  function handleCreated(task: Task) {
    setTasks((prev) => [...prev, task]);
    setMessage(`Added "${task.title}".`);
  }

  function handleStatUpdated(stat: StatProgress) {
    setStats((prev) => prev.map((s) => (s.stat === stat.stat ? stat : s)));
  }

  function refreshStats() {
    getStats()
      .then(setStats)
      .catch((err) => setMessage(err.message));
  }

  const anchors = tasks.filter((t) => t.type === 'ANCHOR');
  const extras = tasks.filter((t) => t.type === 'EXTRA');
  const openTasks = tasks.filter((t) => t.status === 'OPEN');

  return (
    <main className="dashboard">
      <header className="topbar">
        <span>{me.username}</span>
        <button className="link-button" onClick={handleLogout}>Log out</button>
      </header>

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

        <AddTaskForm date={todayLocal()} onCreated={handleCreated} onError={setMessage} />
      </section>

      <SoloLog onMessage={setMessage} />

      <ExpenseTracker
        onMessage={setMessage}
        onLogged={() => setExpensesVersion((v) => v + 1)}
      />

      <BudgetPanel
        refreshKey={expensesVersion}
        onMessage={setMessage}
        onXpAwarded={refreshStats}
      />

      <FocusTimer
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