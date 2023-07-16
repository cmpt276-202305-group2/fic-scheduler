import { Card } from "@mui/material";
import React, { useEffect, useState } from "react";
import styles from "./LogoutPage.module.css";
import { useNavigate } from "react-router-dom";

function LogoutPage() {
  const navigate = useNavigate();
  const [timer, setTimer] = useState(9);

  useEffect(() => {
    localStorage.removeItem("jwtToken");

    const countdown = setInterval(() => {
      setTimer((prevTimer) => prevTimer - 1);
    }, 1000);


    const redirect = setTimeout(() => {
      navigate("/login");
    }, 9000);

    return () => {
      clearTimeout(redirect);
      clearInterval(countdown);
    };
  }, [navigate]);

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
