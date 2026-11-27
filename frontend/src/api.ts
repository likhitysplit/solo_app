import type {
  CompleteTaskResponse,
  CreateExpenseRequest,
  CreateTaskRequest,
  Expense,
  FinishFocusResponse,
  FocusSession,
  LogEntry,
  Me,
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

export function getTasks(date: string) {
  return request<Task[]>(`/api/tasks?date=${date}`);
}

export function createTask(req: CreateTaskRequest) {
  return request<Task>(`/api/tasks`, {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify(req),
  });
}

export function completeTask(taskId: number) {
  return request<CompleteTaskResponse>(`/api/tasks/${taskId}/complete`, {
    method: 'POST',
  });
}

export function rollOver() {
  return request<RolloverResult>('/api/tasks/rollover', { method: 'POST' });
}

// ---------- Stats ----------

export function getStats() {
  return request<StatProgress[]>(`/api/stats`);
}

// ---------- Solo Log ----------

export function getLogs() {
  return request<LogEntry[]>(`/api/logs`);
}

export function createLog(content: string, tag: Stat | null) {
  return request<LogEntry>(`/api/logs`, {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify({ content, tag }),
  });
}

// ---------- Focus ----------

export function getActiveFocus() {
  return request<FocusSession | null>(`/api/focus/active`);
}

export function startFocus(taskId: number | null, plannedMinutes: number) {
  return request<FocusSession>(`/api/focus`, {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify({ taskId, plannedMinutes }),
  });
}

export function finishFocus(sessionId: number) {
  return request<FinishFocusResponse>(`/api/focus/${sessionId}/finish`, {
    method: 'POST',
  });
}

export function getExpenses() {
  return request<Expense[]>(`/api/expenses`);
}

export function createExpense(req: CreateExpenseRequest) {
  return request<Expense>(`/api/expenses`, {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify(req),
  });
}


export function getMe() {
  return request<Me>('/api/auth/me');
}

export function signup(username: string, email: string, password: string, timezone: string) {
  return request<Me>('/api/auth/signup', {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify({ username, email, password, timezone }),
  });
}

export function login(email: string, password: string) {
  return request<Me>('/api/auth/login', {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify({ email, password }),
  });
}

export function logout() {
  return request<null>('/api/auth/logout', { method: 'POST' });
}