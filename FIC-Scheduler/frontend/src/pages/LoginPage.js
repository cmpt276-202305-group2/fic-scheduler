import React, { useContext, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import axios from "axios";

import { UserInfoContext } from "../App";
import styles from "./LoginPage.module.css";
import { tokenConfig } from "../utils";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const { userInfo, setUserInfo } = useContext(UserInfoContext);
  const navigate = useNavigate();

  if ((userInfo !== null) && tokenConfig()) {
    return (<Navigate to="/" replace />);
  }

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

    axios.post("auth/login", payload, { withCredentials: false }).then(
      (loginResponse) => {
        if ((loginResponse.status === 200) && ((loginResponse.data.user ?? null) !== null)
          && ((loginResponse.data.jwt ?? null) !== null)) {

          // console.log('valid login response:', loginResponse.data.jwt);
          setUserInfo(loginResponse.data.user);
          localStorage.setItem('jwtToken', loginResponse.data.jwt);
          navigate("/");
        }
      },
      (err) => {
        // console.log("login request exception", err);
        console.error(err);
        setError("Failed to login. Please try again.");
      });
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
              name="username"
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
              name="password"
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
