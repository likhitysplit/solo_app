import type { CompleteTaskResponse, StatProgress, Task } from './types';

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, options);
  if (!res.ok) {
    const problem = await res.json().catch(() => null);
    throw new Error(problem?.detail ?? `HTTP ${res.status}`);
  }
  return res.json();
}

export function getTasks(userId: number, date: string) {
  return request<Task[]>(`/api/users/${userId}/tasks?date=${date}`);
}

export function getStats(userId: number) {
  return request<StatProgress[]>(`/api/users/${userId}/stats`);
}

export function completeTask(userId: number, taskId: number) {
  return request<CompleteTaskResponse>(
    `/api/users/${userId}/tasks/${taskId}/complete`,
    { method: 'POST' },
  );
}