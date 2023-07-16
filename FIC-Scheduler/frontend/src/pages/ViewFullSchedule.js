import React, { Suspense } from "react";

import styles from "./InstructorCoordinatorHomePage.module.css";

import Sidebar from "../components/Sidebar";
import ScheduleTable from "../components/ScheduleTable";

function ViewFullSchedule() {
  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar />
        </div>
        <Suspense fallback={<div>Loading...</div>}>
          <ScheduleTable />
        </Suspense>
      </div>
    </React.Fragment>
  );
}

export default ViewFullSchedule;
