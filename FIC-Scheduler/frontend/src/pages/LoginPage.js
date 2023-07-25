import React, { useContext, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import axios from "axios";
import {
  TextField,
  Button,
  Container,
  Typography,
  Box,
  Paper,
  Alert,
  createTheme,
  ThemeProvider,
} from "@mui/material";
import { UserInfoContext } from "../App";
import { tokenConfig } from "../utils";
import {
  boxStyles,
  paperStyles,
  headerStyles,
  formStyles,
} from "./LoginPageStyles";

// Create a custom theme
const theme = createTheme({
  palette: {
    primary: {
      main: "#0a5e28",
    },
  },
});

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const { userInfo, setUserInfo } = useContext(UserInfoContext);
  const navigate = useNavigate();

  if (userInfo !== null && tokenConfig()) {
    return <Navigate to="/" replace />;
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
        if (
          loginResponse.status === 200 &&
          (loginResponse.data.user ?? null) !== null &&
          (loginResponse.data.jwt ?? null) !== null
        ) {
          setUserInfo(loginResponse.data.user);
          localStorage.setItem("jwtToken", loginResponse.data.jwt);
          navigate("/");
        }
      },
      (err) => {
        console.error(err);
        setError("Failed to login. Please try again.");
      }
    );
  };

  return (
    <ThemeProvider theme={theme}>
      <Box {...boxStyles}>
        <Container maxWidth="sm">
          <Paper {...paperStyles}>
            <Box {...headerStyles}>
              <Typography component="h1" variant="h4" color="white">
                FIC Scheduler App
              </Typography>
            </Box>
            <Typography component="h1" variant="h6">
              Login
            </Typography>
            <Box {...formStyles} onSubmit={handleSubmit}>
              <TextField
                variant="outlined"
                margin="normal"
                required
                fullWidth
                id="username"
                label="Username"
                name="username"
                autoComplete="username"
                value={username}
                onChange={(event) => setUsername(event.target.value)}
                autoFocus
              />
              <TextField
                variant="outlined"
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                autoComplete="current-password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                color="primary"
                sx={{ mt: 3, mb: 2, minHeight: "45px" }}
              >
                Sign In
              </Button>
              {error && (
                <Alert severity="error" sx={{ mt: 2 }}>
                  {error}
                </Alert>
              )}
            </Box>
          </Paper>
        </Container>
      </Box>
    </ThemeProvider>
  );
}

export default LoginPage;
