import { useEffect, useState } from 'react'
import { api } from '../services/api'

export default function Admin() {
  const today = new Date().toLocaleDateString('en-CA')
  const [date, setDate] = useState(today)
  const [daily, setDaily] = useState(null)
  const [username, setUsername] = useState('')
  const [userRows, setUserRows] = useState([])
  const [words, setWords] = useState([])
  const [newWord, setNewWord] = useState('')
  const [error, setError] = useState('')

  async function loadWords() {
    try { setWords(await api.words()) } catch (err) { setError(err.message) }
  }
  useEffect(() => { loadWords(); runDayReport() }, [])

  async function runDayReport() {
    try { setError(''); setDaily(await api.dayReport(date)) } catch (err) { setError(err.message) }
  }

  async function runUserReport(e) {
    e?.preventDefault()
    if (!username.trim()) return
    try { setError(''); setUserRows(await api.userReport(username.trim())) } catch (err) { setError(err.message); setUserRows([]) }
  }

  async function addWord(e) {
    e.preventDefault()
    try { await api.addWord(newWord); setNewWord(''); loadWords() } catch (err) { setError(err.message) }
  }

  async function removeWord(id) {
    try { await api.deleteWord(id); loadWords() } catch (err) { setError(err.message) }
  }

  return (
    <section className="admin-page">
      <div className="admin-hero"><div><div className="eyebrow">/ ADMIN CONTROL ROOM</div><h1>OPERATIONS<br /><em>Dashboard.</em></h1></div><div className="admin-badge">ROLE: ADMIN<br />REPORT ENGINE ONLINE</div></div>
      {error && <div className="error-box">{error}</div>}

      <div className="report-grid">
        <article className="admin-card">
          <div className="card-top"><span>DAILY REPORT</span><span>01</span></div>
          <div className="report-controls"><input type="date" value={date} onChange={e => setDate(e.target.value)} /><button className="primary-btn" onClick={runDayReport}>RUN REPORT</button></div>
          <div className="metric-row"><div><span>USERS</span><strong>{daily?.users ?? '—'}</strong></div><div><span>CORRECT GUESSES</span><strong>{daily?.correctGuesses ?? '—'}</strong></div></div>
          <p className="admin-note">Users = distinct players who started a round on the selected date. Correct guesses = games won.</p>
        </article>

        <article className="admin-card">
          <div className="card-top"><span>USER REPORT</span><span>02</span></div>
          <form className="report-controls" onSubmit={runUserReport}><input value={username} onChange={e => setUsername(e.target.value)} placeholder="player username" /><button className="primary-btn">LOOK UP</button></form>
          <div className="table-wrap"><table><thead><tr><th>DATE</th><th>WORDS TRIED</th><th>CORRECT</th></tr></thead><tbody>{userRows.length ? userRows.map(row => <tr key={row.date}><td>{row.date}</td><td>{row.wordsTried}</td><td>{row.correctGuesses}</td></tr>) : <tr><td colSpan="3" className="empty-table">No report loaded.</td></tr>}</tbody></table></div>
        </article>
      </div>

      <article className="admin-card word-bank">
        <div className="card-top"><span>WORD BANK</span><span>{words.length} LOADED</span></div>
        <form className="word-add" onSubmit={addWord}><input maxLength={5} value={newWord} onChange={e => setNewWord(e.target.value.replace(/[^a-zA-Z]/g, '').toUpperCase())} placeholder="ADD 5-LETTER WORD" /><button className="primary-btn">ADD WORD +</button></form>
        <div className="word-pills">{words.map(word => <div className="word-pill" key={word.id}><span>{word.value}</span><button onClick={() => removeWord(word.id)} aria-label={`Delete ${word.value}`}>×</button></div>)}</div>
      </article>
    </section>
  )
}
