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

export type ExpenseCategory =
  | 'FOOD'
  | 'GROCERIES'
  | 'TRANSPORTATION'
  | 'HOUSING'
  | 'BILLS'
  | 'SHOPPING'
  | 'FUN'
  | 'HEALTH'
  | 'OTHER';

export const EXPENSE_CATEGORIES: ExpenseCategory[] = [
  'FOOD',
  'GROCERIES',
  'TRANSPORTATION',
  'HOUSING',
  'BILLS',
  'SHOPPING',
  'FUN',
  'HEALTH',
  'OTHER',
];

export type Expense = {
  id: number;
  amountCents: number;
  merchant: string;
  category: ExpenseCategory;
  spentOn: string;
  note: string | null;
  createdAt: string;
};

export type CreateExpenseRequest = {
  amountCents: number;
  merchant: string;
  category: ExpenseCategory;
  spentOn: string;
  note: string | null;
};

export type Me = {
  id: number;
  username: string;
  email: string;
  timezone: string;
};

type Props = {
  me: Me;
  onLoggedOut: () => void;
};

export type BudgetPeriod = 'WEEKLY' | 'MONTHLY';

export type BudgetStatus = {
  category: ExpenseCategory;
  period: BudgetPeriod;
  limitCents: number;
  spentCents: number;
  periodStart: string;
  periodEnd: string;
  daysLeft: number;
};

export type BudgetResponse = {
  category: ExpenseCategory;
  period: BudgetPeriod;
  limitCents: number;
  effectiveFrom: string;
};

export type BudgetResult = {
  period: BudgetPeriod;
  periodStart: string;
  periodEnd: string;
  qualified: boolean;
  categoriesMet: number;
  categoriesTotal: number;
  xpAwarded: number;
};

export type ReceiptDraft = {
  amountCents: number | null;
  merchant: string | null;
  category: ExpenseCategory;  
  spentOn: string;       
};