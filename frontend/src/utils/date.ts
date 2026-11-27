export function toIsoDate(d: Date): string {
  return d.toLocaleDateString('en-CA');         
}

export function todayLocal(): string {
  return toIsoDate(new Date());
}

export function tomorrowLocal(): string {
  const d = new Date();
  d.setDate(d.getDate() + 1);               
  return toIsoDate(d);
}