import {
  Container,
  Box,
  Card,
  Typography,
  createTheme,
  ThemeProvider,
  Button,
} from "@mui/material";
import React, { useEffect, useState, useContext } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { UserInfoContext } from "../App"; // Import UserInfoContext from App.js
import { tokenConfig } from "../utils";
import { boxStyles, cardStyles } from "./LogoutPageStyles";

const theme = createTheme({
  palette: {
    primary: {
      main: "#0a5e28", // The dark green color you want for the button and the focus outline
    },
  },
});

function LogoutPage() {
  const navigate = useNavigate();
  const [timer, setTimer] = useState(5);
  const { setUserInfo } = useContext(UserInfoContext); // Access setUserInfo from UserInfoContext

  const handleButtonClick = () => {
    navigate("/login");
  };

  useEffect(() => {
    setUserInfo(null);

    axios
      .post("auth/logout", {}, tokenConfig())
      .then((_response) => {})
      .catch((error) => {
        console.error(error);
      });

    const countdown = setInterval(() => {
      setTimer((prevTimer) => Math.max(prevTimer - 1, 0));
    }, 1000);

    const redirect = setTimeout(() => {
      navigate("/login");
    }, 5000);

    return () => {
      clearTimeout(redirect);
      clearInterval(countdown);
    };
  }, [navigate, setUserInfo]);

  return (
    <ThemeProvider theme={theme}>
      <Box sx={boxStyles}>
        <Container maxWidth="sm">
          <Card elevation={6} sx={cardStyles}>
            <Typography variant="h5">
              You have Successfully logged out
            </Typography>
            <Typography variant="body1">
              Redirecting to login in {timer} seconds...
            </Typography>
            <Button
              variant="contained"
              sx={{ mt: 10, mb: 2, minHeight: "45px", width: "80%" }}
              onClick={handleButtonClick}
            >
              Go to Login
            </Button>
          </Card>
        </Container>
      </Box>
    </ThemeProvider>
  );
}

export default LogoutPage;
