import React, { useState, useEffect } from "react";
import axios from 'axios';
import styles from "./InstructorCoordinatorHomePage.module.css";
import Sidebar from "../components/Sidebar";
import ExcelViewer from "../components/ExcelViewer";
import Schedule from "../components/Schedule";

function CoordinatorHomePage() {
  const [message, setMessage] = useState('Loading...');  // Declare a new state variable to hold the response message

  useEffect(() => {
    // Fetch the message from your backend
    axios.get('api/coordinator', { withCredentials: true })
      .then(response => {
        setMessage(response.data);
      })
      .catch(error => {
        setMessage('Failed to load message');
        console.error(error);
      });
  }, []);  // Run the effect on mount
  const [showUploadPreferences, setShowUploadPreferences] = useState(false);
  const [showSchedule, setShowSchedule] = useState(false);
  const [showManageCourse, setShowManageCourse] = useState(false);
  const [showManageProfessor, setShowManageProfessor] = useState(false);
  const handleSidebarItemClick = (item) => {
    if (item === "Upload Preferences") {
      setShowUploadPreferences(true);
      setShowSchedule(false);
      setShowManageCourse(false);
      setShowManageProfessor(false);
    } else if (item === "Schedule"){
      setShowUploadPreferences(false);
      setShowSchedule(true);
      setShowManageCourse(false);
      setShowManageProfessor(false);
    } else if (item === "Manage Course"){
      setShowManageCourse(true);
      setShowUploadPreferences(false);
      setShowSchedule(false);
      setShowManageProfessor(false);
    } else if (item === "Manage Professor"){
      setShowManageProfessor(true);
      setShowUploadPreferences(false);
      setShowSchedule(false);
      setShowManageCourse(false);
    }
  }
  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar onItemClick={handleSidebarItemClick}/>
        </div>
        <div className={styles.Schedule} data-testid="schedule">
          {((showUploadPreferences || showManageCourse || showManageProfessor) && !showSchedule) ? (<ExcelViewer />) : (<Schedule />)}
        </div>
        <br></br>
        <p>{message}</p> Display the message from the server
      </div>
    </React.Fragment>
  );
}

export default CoordinatorHomePage;
