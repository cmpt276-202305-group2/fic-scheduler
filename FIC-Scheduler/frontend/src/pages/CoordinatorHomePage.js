import React from "react";
import styles from "./InstructorCoordinatorHomePage.module.css";
import CoordinatorSidebar from "../components/CoordinatorSidebar";

function CoordinatorHomePage() {
  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <CoordinatorSidebar /> 
        </div>
        <div className={styles.Schedule} data-testid="schedule">SCHEDULE GOES HERE</div>
      </div>
    </React.Fragment>
  );
}

export default CoordinatorHomePage;
