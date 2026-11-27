export type Stat = 'INTELLIGENCE' | 'DILIGENCE' | 'EXPRESSION' | 'SOCIABILITY' | 'FITNESS';
export type TaskType = 'ANCHOR' | 'EXTRA';
export type TaskStatus = 'OPEN' | 'COMPLETED';

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