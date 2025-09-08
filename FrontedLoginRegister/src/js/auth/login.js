import { apiFetch } from "../api/api.js";

document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  try {
    const res = await apiFetch("/auth/login", {
      method: "POST",
      body: JSON.stringify({ username, password })
    });

    if (res.accessToken) {
      localStorage.setItem("jwt", res.accessToken);
      localStorage.setItem("refreshToken", res.refreshToken);
      window.location.href = "home.html";
    }
  } catch (err) {
    document.getElementById("output").textContent = "❌ " + err.message;
  }
});
