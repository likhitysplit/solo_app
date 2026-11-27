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