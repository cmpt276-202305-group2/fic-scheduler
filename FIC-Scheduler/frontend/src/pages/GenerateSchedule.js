import React from "react";
import styles from "./InstructorCoordinatorHomePage.module.css";
import Sidebar from "../components/Sidebar";

function GenerateSchedule() {
  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar />
        </div>
        <div className={styles.Schedule} data-testid="generate-schedule">
        </div>
      </div>
    </React.Fragment>
  );
}

export default GenerateSchedule;
