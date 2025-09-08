import { apiFetch } from "../api/api.js";

document.getElementById("perfilBtn").addEventListener("click", async () => {
  try {
    const res = await apiFetch("/admin/home", { method: "GET" });
    document.getElementById("output").textContent = JSON.stringify(res, null, 2);
  } catch (err) {
    document.getElementById("output").textContent = "❌ " + err.message;
  }
});

document.getElementById("logoutBtn").addEventListener("click", async () => {
  const token = localStorage.getItem("jwt");
  try {
    await apiFetch("/auth/logout", {
      method: "POST",
      headers: { Authorization: "Bearer " + token }
    });
  } catch (err) {
    console.error("Error en logout", err);
  } finally {
    localStorage.removeItem("jwt");
    localStorage.removeItem("refreshToken");
    window.location.href = "login.html";
  }
});
