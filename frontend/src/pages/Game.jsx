import { useEffect, useMemo, useState } from 'react'
import { api } from '../services/api'

const emptyTiles = Array.from({ length: 5 }, () => '')

export default function Game() {
  const [status, setStatus] = useState(null)
  const [game, setGame] = useState(null)
  const [guess, setGuess] = useState('')
  const [error, setError] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [loading, setLoading] = useState(true)

  async function load() {
    setError('')
    try {
      const s = await api.gameStatus()
      setStatus(s)
      if (s.activeGameId) {
        const g = await api.getGame(s.activeGameId)
        setGame(g)
      }
    } catch (err) { setError(err.message) }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  async function start() {
    setError('')
    try {
      const s = await api.startGame()
      setStatus({ gamesUsedToday: s.gamesUsedToday, gamesRemainingToday: s.gamesRemainingToday, activeGameId: s.gameId })
      const g = await api.getGame(s.gameId)
      setGame(g)
      setGuess('')
    } catch (err) { setError(err.message) }
  }

  async function submit(e) {
    e.preventDefault()
    const clean = guess.toUpperCase()
    if (!/^[A-Z]{5}$/.test(clean)) {
      setError('Enter exactly 5 uppercase letters.')
      return
    }
    setError('')
    try {
      const g = await api.submitGuess(game.gameId, clean)
      setGame(g)
      setGuess('')
      const s = await api.gameStatus()
      setStatus(s)
      if (g.completed) setShowModal(true)
    } catch (err) { setError(err.message) }
  }

  const board = useMemo(() => {
    const rows = game?.guesses || []
    return Array.from({ length: 5 }, (_, index) => rows[index] || null)
  }, [game])

  const remaining = status?.gamesRemainingToday ?? 0
  const used = status?.gamesUsedToday ?? 0

  if (loading) return <section className="loading-screen">INITIALIZING WORD//TRACE <span>▌</span></section>

  return (
    <section className="game-page">
      <div className="game-head">
        <div>
          <div className="eyebrow">/ PLAYER CONSOLE</div>
          <h1>GUESS<span>THE</span>WORD</h1>
          <p className="game-copy">Find the hidden five-letter word. The grid remembers every move.</p>
        </div>
        <div className="quota">
          <div className="quota-label">DAILY QUOTA</div>
          <div className="quota-value">{used}<small>/ 3</small></div>
          <div className="dots">{[0,1,2].map(i => <i key={i} className={i < used ? 'spent' : ''} />)}</div>
        </div>
      </div>

      <div className="game-layout">
        <div className="board-card">
          {!game && (
            <div className="start-overlay">
              <div className="orb"><span>W/</span></div>
              <h2>READY TO TRACE?</h2>
              <p>Every round selects a word from the admin word bank. You get five guesses.</p>
              <button className="primary-btn wide" onClick={start} disabled={remaining === 0}>START ROUND ↗</button>
              {remaining === 0 && <div className="error-box">You have used all 3 rounds today.</div>}
            </div>
          )}
          {game && (
            <>
              <div className="board-status"><span>ROUND #{String(game.gameId).padStart(4, '0')}</span><span>{game.guessCount} / 5 ATTEMPTS</span></div>
              <div className="board">
                {board.map((row, r) => row ? (
                  <div className="row" key={r}>{row.guess.split('').map((letter, i) => <div className={`tile ${row.tiles[i].toLowerCase()}`} key={i}>{letter}</div>)}</div>
                ) : (
                  <div className="row" key={r}>{emptyTiles.map((_, i) => <div className="tile empty" key={i}>{r === game.guessCount && i === 0 ? '·' : ''}</div>)}</div>
                ))}
              </div>
              {!game.completed && <form className="guess-form" onSubmit={submit}>
                <input autoFocus maxLength={5} value={guess} onChange={e => setGuess(e.target.value.replace(/[^a-zA-Z]/g, '').toUpperCase())} placeholder="TYPE YOUR GUESS" aria-label="Guess" />
                <button className="primary-btn" type="submit">TRACE ↗</button>
              </form>}
              {error && <div className="error-box centered">{error}</div>}
            </>
          )}
        </div>

        <aside className="legend-card">
          <div className="card-top"><span>SIGNAL KEY</span><span>LIVE</span></div>
          <div className="legend-item"><b className="sample green">A</b><div><strong>EXACT</strong><span>Correct letter, right slot.</span></div></div>
          <div className="legend-item"><b className="sample orange">B</b><div><strong>PRESENT</strong><span>Letter exists elsewhere.</span></div></div>
          <div className="legend-item"><b className="sample grey">C</b><div><strong>ABSENT</strong><span>Letter is not in the word.</span></div></div>
          <div className="tip"><span>TIP</span> Don't repeat grey letters. Use every confirmed signal to narrow the next attempt.</div>
        </aside>
      </div>

      {showModal && <div className="modal-backdrop"><div className="result-modal"><div className="result-kicker">ROUND COMPLETE</div><h2>{game.won ? 'YOU CRACKED IT.' : 'KEEP THE LOG RUNNING.'}</h2><p>{game.message}</p>{!game.won && <p className="muted">Your five attempts are used. Start another round when you are ready.</p>}<button className="primary-btn" onClick={() => { setShowModal(false); setGame(null); load() }}>OK, CLOSE</button></div></div>}
    </section>
  )
}
