import { Card } from "@mui/material";
import React, { useEffect, useState, useContext } from "react";
import axios from 'axios';
import styles from "./LogoutPage.module.css";
import { useNavigate } from "react-router-dom";
import { UserInfoContext } from "../App"; // Import UserInfoContext from App.js
import { tokenConfig } from "../utils"

function LogoutPage() {
  const navigate = useNavigate();
  const [timer, setTimer] = useState(5);
  const { setUserInfo } = useContext(UserInfoContext); // Access setUserInfo from UserInfoContext

  useEffect(() => {
    // console.log("logout useEffect");
    setUserInfo(null);

    axios.post('auth/logout', {}, tokenConfig())
      .then((_response) => {
        // console.log("Logout successful");
      })
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
    <div className={styles.container}>
      <Card className={styles.cardContainer}>
        <h2>You have Successfully logged out</h2>
        <p>Redirecting in {timer} seconds...</p>
      </Card>
    </div>
  );
}

export default LogoutPage;
