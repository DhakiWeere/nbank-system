// bank-api.js

const API_BASE = "/bank/api";

async function loginUser(username, password) {
    try {
        const response = await fetch(`${API_BASE}/user/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) throw new Error("Login failed");

        return await response.json();
    } catch (err) {
        console.error("Login Error:", err.message);
        throw err;
    }
}

async function registerUser(userData) {
    // userData is an object with required fields like { username, password, email, nic, etc. }
    try {
        const response = await fetch(`${API_BASE}/user/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        });

        if (!response.ok) throw new Error("Registration failed");

        return await response.json();
    } catch (err) {
        console.error("Register Error:", err.message);
        throw err;
    }
}

async function getUserAccounts(nic) {
    try {
        const response = await fetch(`${API_BASE}/info/accounts?nic=${encodeURIComponent(nic)}`, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        });

        if (!response.ok) throw new Error("Failed to fetch accounts");

        return await response.json();
    } catch (err) {
        console.error("Accounts Fetch Error:", err.message);
        throw err;
    }
}

async function logoutUser() {
    // Assuming your app uses session-based login and this endpoint invalidates the session
    try {
        const response = await fetch("/logout", {
            method: "POST"
        });

        if (!response.ok) throw new Error("Logout failed");

        return true;
    } catch (err) {
        console.error("Logout Error:", err.message);
        throw err;
    }
}
