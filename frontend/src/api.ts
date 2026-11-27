import type {
  CompleteTaskResponse,
  CreateExpenseRequest,
  CreateTaskRequest,
  Expense,
  FinishFocusResponse,
  FocusSession,
  LogEntry,
  RolloverResult,
  Stat,
  StatProgress,
  Task,
} from './types';

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, options);
  if (!res.ok) {
    const problem = await res.json().catch(() => null);
    throw new Error(problem?.detail ?? `HTTP ${res.status}`);
  }
  if (res.status === 204) {
    return null as T;
  }
  return res.json();
}

const JSON_HEADERS = { 'Content-Type': 'application/json' };

// ---------- Tasks ----------

export function getTasks(userId: number, date: string) {
  return request<Task[]>(`/api/users/${userId}/tasks?date=${date}`);
}

export function createTask(userId: number, req: CreateTaskRequest) {
  return request<Task>(`/api/users/${userId}/tasks`, {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify(req),
  });
}

export function completeTask(userId: number, taskId: number) {
  return request<CompleteTaskResponse>(`/api/users/${userId}/tasks/${taskId}/complete`, {
    method: 'POST',
  });
}

export function rollOver(userId: number, date: string) {
  return request<RolloverResult>(`/api/users/${userId}/tasks/rollover?date=${date}`, {
    method: 'POST',
  });
}

// ---------- Stats ----------

export function getStats(userId: number) {
  return request<StatProgress[]>(`/api/users/${userId}/stats`);
}

// ---------- Solo Log ----------

export function getLogs(userId: number) {
  return request<LogEntry[]>(`/api/users/${userId}/logs`);
}

export function createLog(userId: number, content: string, tag: Stat | null) {
  return request<LogEntry>(`/api/users/${userId}/logs`, {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify({ content, tag }),
  });
}

// ---------- Focus ----------

export function getActiveFocus(userId: number) {
  return request<FocusSession | null>(`/api/users/${userId}/focus/active`);
}

export function startFocus(userId: number, taskId: number | null, plannedMinutes: number) {
  return request<FocusSession>(`/api/users/${userId}/focus`, {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify({ taskId, plannedMinutes }),
  });
}

export function finishFocus(userId: number, sessionId: number) {
  return request<FinishFocusResponse>(`/api/users/${userId}/focus/${sessionId}/finish`, {
    method: 'POST',
  });
}

export function getExpenses(userId: number) {
  return request<Expense[]>(`/api/users/${userId}/expenses`);
}

export function createExpense(userId: number, req: CreateExpenseRequest) {
  return request<Expense>(`/api/users/${userId}/expenses`, {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify(req),
  });
}