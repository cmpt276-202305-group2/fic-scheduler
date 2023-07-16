import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import { parseJwtToken } from "../utils";

import styles from "./LoginPage.module.css";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const navigateByRole = useCallback(
    (role) => {
      if (role === "COORDINATOR") {
        navigate("/coordinator");
      } else if (role === "INSTRUCTOR") {
        navigate("/instructor");
      } else {
        setError("You do not have permission to access this application");
      }
    },
    [navigate]
  );

  useEffect(() => {
    const jwtToken = localStorage.getItem("jwtToken");
    if (jwtToken) {
      const decodedToken = parseJwtToken(jwtToken);
      navigateByRole(decodedToken.roles);
    }
  }, [navigateByRole]);

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
      const response = await axios.post("auth/login", payload);

      if (response.status === 200) {
        const jwtToken = response.data.jwt;
        localStorage.setItem("jwtToken", jwtToken);
        const decodedToken = parseJwtToken(jwtToken);
        console.log(decodedToken);
        navigateByRole(decodedToken.roles);
      }
    } catch (err) {
      console.log(err);
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
