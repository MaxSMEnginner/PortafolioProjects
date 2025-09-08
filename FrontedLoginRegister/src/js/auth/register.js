import { apiFetch } from "../api/api.js";

document.getElementById("registerForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  try {
    const res = await apiFetch("/users/register", {
      method: "POST",
      body: JSON.stringify({ username, password })
    });
    document.getElementById("output").textContent = "✅ Usuario creado: " + res.username;
  } catch (err) {
    document.getElementById("output").textContent = "❌ " + err.message;
  }
});
