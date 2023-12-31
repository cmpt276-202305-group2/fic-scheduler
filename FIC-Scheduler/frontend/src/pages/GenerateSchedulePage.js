import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";

function GenerateSchedulePage() {
  return (
    <CheckAuth permittedRoles={["ADMIN", "COORDINATOR"]} fallback={<Navigate to="/" replace />}>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar item="generateSchedule" />
        </div>
        <div className={styles.PageContent} data-testid="generate-schedule">
        </div>
      </div>
    </CheckAuth>
  );
}

export default GenerateSchedulePage;
