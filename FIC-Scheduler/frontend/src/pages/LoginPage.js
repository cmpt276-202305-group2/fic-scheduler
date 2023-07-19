import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import { storeUserInfo, fetchUserInfo } from "../App";
import styles from "./LoginPage.module.css";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!username || !password) {
      setError("Please enter both username and password");
      return;
    }

    const payload = {
      username: username,
      password: password,
    };

    try {
      // console.log("login request");
      const loginResponse = await axios.post("auth/login", payload, { withCredentials: true });

      if (loginResponse.status === 200) {
        // console.log("login OK response", loginResponse.data);
        if (loginResponse.data.user) {
          storeUserInfo(loginResponse.data.user);
        } else {
          // console.log("login bad user, fallback to fetchUserInfo");
          await fetchUserInfo();
        }
      }

      navigate("/");
    } catch (err) {
      console.log("login request exception", err);
      console.error(err);
      setError("Failed to login. Please try again.");
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.cardContainer}>
        <h1>Login Page</h1>
        <form onSubmit={handleSubmit}>
          <label>
            Username
            <input
              type="text"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              placeholder="Enter your username"
              autoFocus
            />
          </label>
          <br />
          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Enter your password"
            />
          </label>
          <br />
          <button type="submit">Login</button>

          {error && <p className={styles.errormessage}>{error}</p>}
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
