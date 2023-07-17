import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import { UserRoleContext } from "../App";
import styles from "./LoginPage.module.css";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const { setUserRole } = useContext(UserRoleContext);

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
      const response = await axios.post("auth/login", payload, { withCredentials: true });

      const userInfoResponse = await axios.get("auth/userinfo", { withCredentials: true }
      );
      const { roles } = userInfoResponse.data;
      console.log(roles);
      setUserRole(roles[0]);

      if (roles.includes("COORDINATOR")) {
        navigate("/coordinator");
      } else if (roles.includes("INSTRUCTOR")) {
        navigate("/instructor");
      } else {
        setError("You do not have permission to access this application");
      }
    } catch (err) {
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
