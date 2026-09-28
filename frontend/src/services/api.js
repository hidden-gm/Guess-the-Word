const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

async function request(path, options = {}) {
  const token = localStorage.getItem('wordtrace_token')
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) }
  if (token) headers.Authorization = `Bearer ${token}`

  const res = await fetch(`${API}${path}`, { ...options, headers })
  const data = await res.json().catch(() => ({}))
  if (!res.ok) throw new Error(data.message || 'Something went wrong.')
  return data
}

export const api = {
  register: (body) => request('/auth/register', { method: 'POST', body: JSON.stringify(body) }),
  login: (body) => request('/auth/login', { method: 'POST', body: JSON.stringify(body) }),
  gameStatus: () => request('/game/status'),
  startGame: () => request('/game/start', { method: 'POST' }),
  getGame: (id) => request(`/game/${id}`),
  submitGuess: (id, guess) => request(`/game/${id}/guess`, { method: 'POST', body: JSON.stringify({ guess }) }),
  dayReport: (date) => request(`/admin/reports/day?date=${date}`),
  userReport: (username) => request(`/admin/reports/user/${encodeURIComponent(username)}`),
  words: () => request('/admin/words'),
  addWord: (value) => request('/admin/words', { method: 'POST', body: JSON.stringify({ value }) }),
  deleteWord: (id) => request(`/admin/words/${id}`, { method: 'DELETE' }),
}
