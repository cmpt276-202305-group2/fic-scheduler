import React, { useState, useEffect } from "react";
import axios from "axios";
import styles from "./InstructorCoordinatorHomePage.module.css";
import CoordinatorSidebar from "../components/CoordinatorSidebar";

function CoordinatorHomePage() {
  const [message, setMessage] = useState("Loading..."); // Declare a new state variable to hold the response message

  useEffect(() => {
    // Fetch the message from your backend
    axios
      .get("http://localhost:8080/api/coordinator", { withCredentials: true })
      .then((response) => {
        setMessage(response.data);
      })
      .catch((error) => {
        setMessage("Failed to load message");
        console.error(error);
      });
  }, []); // Run the effect on mount

  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <CoordinatorSidebar />
        </div>
        <div className={styles.Schedule} data-testid="schedule">
          <p>{message}</p> {/* Display the message from the server */}
          Schedule Goes Here
        </div>
      </div>
    </React.Fragment>
  );
}

export default CoordinatorHomePage;
