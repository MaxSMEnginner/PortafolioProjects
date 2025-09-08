const API_URL = "http://localhost:8080"; // Ajusta al puerto de tu backend

export async function apiFetch(endpoint, options = {}) {
  const token = localStorage.getItem("jwt");
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {})
  };
  if (token) headers["Authorization"] = "Bearer " + token;

  const res = await fetch(`${API_URL}${endpoint}`, { ...options, headers });
  if (!res.ok) {
    throw new Error(`Error ${res.status}: ${await res.text()}`);
  }
  return res.json();
}
