import { useEffect, useState } from 'react';
import type { Me } from './types';
import { getMe } from './api';
import { AuthScreen } from './components/AuthScreen';
import { Dashboard } from './Dashboard';

export default function App() {
  const [me, setMe] = useState<Me | null | undefined>(undefined);

  useEffect(() => {
    getMe()
      .then(setMe)
      .catch(() => setMe(null));
  }, []);

  if (me === undefined) {
    return <main className="dashboard"><p>Loading...</p></main>;
  }
  if (me === null) {
    return <AuthScreen onLoggedIn={setMe} />;
  }
  return <Dashboard me={me} onLoggedOut={() => setMe(null)} />;
}