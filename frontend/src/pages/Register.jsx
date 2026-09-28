import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../services/api'

export default function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const data = await api.register(form)
      localStorage.setItem('wordtrace_token', data.token)
      localStorage.setItem('wordtrace_username', data.username)
      localStorage.setItem('wordtrace_role', data.role)
      navigate('/game')
    } catch (err) {
      setError(err.message)
    } finally { setLoading(false) }
  }

  return (
    <section className="auth-page compact">
      <div className="auth-intro">
        <div className="eyebrow">/ NEW PLAYER</div>
        <h1>Build your<br /><em>word log.</em></h1>
        <p>Your history is saved round by round. Login later and continue from where you left off during the day.</p>
        <div className="rule-list"><span>01</span> USERNAME · LETTERS ONLY · 5+<span>02</span> PASSWORD · LETTER + NUMBER + $ % *<span>03</span> MAX · 3 GAMES / DAY</div>
      </div>
      <form className="auth-card" onSubmit={submit}>
        <div className="card-top"><span>PLAYER REGISTRY</span><span>NEW</span></div>
        <label>USERNAME<input value={form.username} onChange={e => setForm({...form, username: e.target.value})} autoComplete="username" placeholder="playerone" required /></label>
        <label>PASSWORD<input type="password" value={form.password} onChange={e => setForm({...form, password: e.target.value})} autoComplete="new-password" placeholder="Alpha9*" required /></label>
        <div className="requirements">Password needs letters, a number, and one of <b>$ % *</b>.</div>
        {error && <div className="error-box">{error}</div>}
        <button className="primary-btn" disabled={loading}>{loading ? 'CREATING…' : 'CREATE PLAYER ↗'}</button>
        <p className="subtle">Already registered? <Link to="/login">Return to access</Link></p>
      </form>
    </section>
  )
}
