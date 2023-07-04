import React, { useState } from "react";

import axios from "axios";

function LoginPage() {
  // set up the user
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      //send req
      const res = await axios.post("http://localhost:8080/login", { username, password });
      //var role = "instructor";
      console.log(res.data);
      window.location.href = res.data;
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <div>
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
      </form>
    </div>
  );
}

export default LoginPage;
