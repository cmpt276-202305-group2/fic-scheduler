import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import styles from "./LoginPage.module.css"; // Import the CSS module


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
      const response = await axios.post("https://ficbackend.onrender.com/login", payload);
      console.log(response);
      if (response.status === 200) {
        if (response.data.role === "ADMIN") {
          navigate("/CoordinatorHomePage");
        } else if (response.data.role === "PROFESSOR") {
          navigate("/InstructorHomePage");
        }
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
