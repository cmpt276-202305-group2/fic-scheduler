import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import './LoginPage.module.css'; 

function LoginPage() {
  // set up the user
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();

    // simple validation
    if (!username || !password) {
      setError("Please enter all fields");
      return;
    }

    const payload = {
      username: username,
      password: password,
    };

    try {
      // send req
      const response = await axios.post("http://localhost:8080/login", payload);
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
    <div className="login-page">
      <div className="login-form">
        <h1>Login</h1>
        <form onSubmit={handleSubmit}>
          <label>
            Username
            <input
              type="text"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
            />
          </label>
          <br />
          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </label>

          <br />

          <button type="submit">Login</button>
          {error && <p>{error}</p>}
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
