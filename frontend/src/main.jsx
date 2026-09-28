import React, { useState } from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter, Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import './styles.css'
import { api } from './services/api'
import Login from './pages/Login'
import Register from './pages/Register'
import Game from './pages/Game'
import Admin from './pages/Admin'

const auth = () => {
  const token = localStorage.getItem('wordtrace_token')
  const role = localStorage.getItem('wordtrace_role')
  return { token, role }
}

function Shell({ children }) {
  const navigate = useNavigate()
  const { token, role } = auth()
  const username = localStorage.getItem('wordtrace_username') || 'PLAYER'

  function logout() {
    localStorage.clear()
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <div className="scanline" />
      <header className="topbar">
        <button className="brand" onClick={() => navigate(role === 'ADMIN' ? '/admin' : '/game')}>
          <span className="brand-mark">W/</span>
          <span><strong>WORD</strong>//TRACE</span>
        </button>
        {token && (
          <div className="top-actions">
            <span className="identity"><i />{username} <small>{role}</small></span>
            {role === 'PLAYER' && <button onClick={() => navigate('/game')} className="ghost-btn">GAME</button>}
            {role === 'ADMIN' && <button onClick={() => navigate('/admin')} className="ghost-btn">CONTROL</button>}
            <button onClick={logout} className="ghost-btn">EXIT</button>
          </div>
        )}
      </header>
      <main>{children}</main>
      <footer><span>WORD//TRACE</span><span>Five letters. Five tries.</span></footer>
    </div>
  )
}

function Protected({ role, children }) {
  const current = auth()
  if (!current.token) return <Navigate to="/login" replace />
  if (role && current.role !== role) return <Navigate to={current.role === 'ADMIN' ? '/admin' : '/game'} replace />
  return children
}

function App() {
  return (
    <Shell>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/game" element={<Protected role="PLAYER"><Game /></Protected>} />
        <Route path="/admin" element={<Protected role="ADMIN"><Admin /></Protected>} />
        <Route path="*" element={<Navigate to={auth().token ? (auth().role === 'ADMIN' ? '/admin' : '/game') : '/login'} replace />} />
      </Routes>
    </Shell>
  )
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode><BrowserRouter><App /></BrowserRouter></React.StrictMode>
)
