import type { Stat, StatProgress } from '../types';

const STAT_LABELS: Record<Stat, string> = {        // ①
  INTELLIGENCE: 'Intelligence',
  DILIGENCE: 'Diligence',
  EXPRESSION: 'Expression',
  SOCIABILITY: 'Sociability',
  FITNESS: 'Fitness',
};

type Props = {
  progress: StatProgress;
};

export function StatBar({ progress }: Props) {
  const percent = (progress.xpIntoLevel / progress.xpToNextLevel) * 100;

  return (
    <div className="stat" data-stat={progress.stat}>
      <div className="stat__header">
        <span className="stat__name">{STAT_LABELS[progress.stat]}</span>
        <span className="stat__level">Lv {progress.level}</span>
      </div>
      <div className="xp-bar">
        <div className="xp-bar__fill" style={{ width: `${percent}%` }} />    {/* ② */}
      </div>
      <div className="stat__xp">
        {progress.xpIntoLevel} / {progress.xpToNextLevel} XP
      </div>
    </div>
  );
}