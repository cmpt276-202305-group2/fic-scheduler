import React from "react";
import styles from "./InstructorCoordinatorHomePage.module.css";
import InstructorSidebar from "../components/InstructorSidebar";

function InstructorHomePage() {
  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <InstructorSidebar />
        </div>
        <div className={styles.Schedule} data-testid="schedule">
          SCHEDULE GOES HERE
        </div>
      </div>
    </React.Fragment>
  );
}

export default InstructorHomePage;
