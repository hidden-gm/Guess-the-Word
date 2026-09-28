import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../services/api'

export default function Login() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const data = await api.login(form)
      localStorage.setItem('wordtrace_token', data.token)
      localStorage.setItem('wordtrace_username', data.username)
      localStorage.setItem('wordtrace_role', data.role)
      navigate(data.role === 'ADMIN' ? '/admin' : '/game')
    } catch (err) {
      setError(err.message)
    } finally { setLoading(false) }
  }

  return (
    <section className="auth-page">
      <div className="auth-intro">
        <div className="eyebrow">/ ACCESS NODE 01</div>
        <h1>Read the pattern.<br /><em>Crack the word.</em></h1>
        <p>Five letters. Five tries. Three rounds per day. A clean guessing game with a little terminal energy.</p>
        <div className="signal-row"><span className="signal green" /> EXACT <span className="signal orange" /> PRESENT <span className="signal grey" /> ABSENT</div>
      </div>
      <form className="auth-card" onSubmit={submit}>
        <div className="card-top"><span>WORD//TRACE</span><span>LOGIN</span></div>
        <label>USERNAME<input value={form.username} onChange={e => setForm({...form, username: e.target.value})} autoComplete="username" placeholder="yourname" /></label>
        <label>PASSWORD<input type="password" value={form.password} onChange={e => setForm({...form, password: e.target.value})} autoComplete="current-password" placeholder="••••••••" /></label>
        {error && <div className="error-box">{error}</div>}
        <button className="primary-btn" disabled={loading}>{loading ? 'AUTHENTICATING…' : 'ENTER THE GRID ↗'}</button>
        <p className="subtle">New player? <Link to="/register">Create a player account</Link></p>
        <div className="demo-note">ADMIN DEMO<br /><b>admin</b> / <b>Admin1*</b></div>
      </form>
    </section>
  )
}
