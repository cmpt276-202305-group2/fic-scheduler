import React from "react";
import styles from "./InstructorCoordinatorHomePage.module.css";
import InstructorSidebar from "../components/InstructorSidebar";
import ExcelViewer from "../components/ExcelViewer";

function InstructorHomePage() {
  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <InstructorSidebar />
        </div>
        <div className={styles.Schedule} data-testid="schedule">
          <ExcelViewer />
        </div>
      </div>
    </React.Fragment>
  );
}

export default InstructorHomePage;
