import { Card } from "@mui/material";
import React, { useEffect, useState, useContext } from "react";
import axios from 'axios';
import styles from "./LogoutPage.module.css";
import { useNavigate } from "react-router-dom";
import { UserRoleContext } from "../App"; // Import UserRoleContext from App.js

function LogoutPage() {
  const navigate = useNavigate();
  const [timer, setTimer] = useState(5);
  const { setUserRole } = useContext(UserRoleContext); // Access setUserRole from UserRoleContext

  useEffect(() => {
    axios.post('auth/logout', {}, { withCredentials: true })
      .then((response) => {
        console.log("Logout successful");
        setUserRole(null); // Update userRole to null after successful logout
      })
      .catch((error) => {
        console.error(error);
      });

    const countdown = setInterval(() => {
      setTimer((prevTimer) => prevTimer - 1);
    }, 1000);

    const redirect = setTimeout(() => {
      navigate("/login");
    }, 5000);

    return () => {
      clearTimeout(redirect);
      clearInterval(countdown);
    };
  }, [navigate, setUserRole]); // Add setUserRole as a dependency

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
