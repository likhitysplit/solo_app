export type Stat = 'INTELLIGENCE' | 'DILIGENCE' | 'EXPRESSION' | 'SOCIABILITY' | 'FITNESS';
export type TaskType = 'ANCHOR' | 'EXTRA';
export type TaskStatus = 'OPEN' | 'COMPLETED';

export const STATS: Stat[] = ['INTELLIGENCE', 'DILIGENCE', 'EXPRESSION', 'SOCIABILITY', 'FITNESS'];

export type Task = {
  id: number;
  title: string;
  type: TaskType;
  stat: Stat;
  baseXp: number;
  status: TaskStatus;
  scheduledDate: string;
  originalDate: string;
  completedAt: string | null;
};

export type CreateTaskRequest = {
  title: string;
  type: TaskType;
  stat: Stat;
  baseXp: number;
  date: string;
};

export type StatProgress = {
  stat: Stat;
  totalXp: number;
  level: number;
  xpIntoLevel: number;
  xpToNextLevel: number;
};

export type CompleteTaskResponse = {
  task: Task;
  stat: StatProgress;
  xpGained: number;
};

export type RolloverResult = {
  rolledOver: number;
  demotedToExtra: number;
};

export type LogEntry = {
  id: number;
  content: string;
  tag: Stat | null;
  createdAt: string;
};

export type FocusSession = {
  id: number;
  taskId: number | null;
  plannedMinutes: number;
  startedAt: string;
  endedAt: string | null;
  xpAwarded: number;
};

export type FinishFocusResponse = {
  session: FocusSession;
  stat: StatProgress | null;
};