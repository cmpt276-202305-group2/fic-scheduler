import React from "react";
import styles from "./Common.module.css";
import Sidebar from "../components/Sidebar";

function InstructorHomePage() {
  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar />
        </div>
        <div className={styles.Schedule} data-testid="schedule">
          <div>schedule</div>
        </div>
      </div>
    </React.Fragment>
  );
}

export default InstructorHomePage;
