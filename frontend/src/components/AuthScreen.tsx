import { useState } from 'react';
import type { FormEvent } from 'react';
import type { Me } from '../types';
import { login, signup } from '../api';

type Props = {
  onLoggedIn: (me: Me) => void;
};

export function AuthScreen({ onLoggedIn }: Props) {
  const [mode, setMode] = useState<'login' | 'signup'>('login');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setBusy(true);
    try {
      if (mode === 'signup') {
        const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;   // e.g. "America/New_York"
        await signup(username, email, password, timezone);
      }
      const me = await login(email, password);                               // signup → then log in
      onLoggedIn(me);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="dashboard">
      <section className="window window--wide">
        <h2>{mode === 'login' ? 'Continue your journey' : 'Begin your journey'}</h2>

        <form onSubmit={handleSubmit} className="auth-form">
          {mode === 'signup' && (
            <input
              className="pixel-input"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
            />
          )}
          <input
            className="pixel-input"
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
          />
          <input
            className="pixel-input"
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
          />
          <button className="pixel-button" type="submit" disabled={busy}>
            {mode === 'login' ? 'Log in' : 'Sign up'}
          </button>
        </form>

        {error && <p className="dialog--error">{error}</p>}

        <button
          className="link-button"
          onClick={() => {
            setMode(mode === 'login' ? 'signup' : 'login');
            setError(null);
          }}
        >
          {mode === 'login' ? 'New here? Create an account' : 'Have an account? Log in'}
        </button>
      </section>
    </main>
  );
}