import type { StatProgress } from '../types';

type Props = {
  progress: StatProgress;
};

export function StatBar({ progress }: Props) {
  const percent = (progress.xpIntoLevel / progress.xpToNextLevel) * 100;

  return (
    <div style={{ marginBottom: 12 }}>
      <div>
        {progress.stat} · Lv {progress.level} ({progress.xpIntoLevel}/{progress.xpToNextLevel} XP)
      </div>
      <div style={{ width: 240, height: 14, border: '2px solid black' }}>
        <div
          style={{
            width: `${percent}%`,
            height: '100%',
            background: 'black',
            transition: 'width 0.4s',
          }}
        />
      </div>
    </div>
  );
}