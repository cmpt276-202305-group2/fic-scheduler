import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";
import Schedule from "../components/Schedule";

function ViewInstructorSchedulePage() {
  return (
    <CheckAuth permittedRoles={["INSTRUCTOR"]} fallback={<Navigate to="/" replace />}>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar item="viewInstructorSchedule" />
        </div>
        <div className={styles.PageContent} data-testid="schedule">
          <Schedule />
        </div>
      </div>
    </CheckAuth>
  );
}

export default ViewInstructorSchedulePage;
