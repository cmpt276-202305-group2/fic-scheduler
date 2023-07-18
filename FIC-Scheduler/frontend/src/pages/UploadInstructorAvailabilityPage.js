import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";
import ExcelViewer from "../components/ExcelViewer";

function UploadInstructorAvailabilityPage() {
  return (
    <CheckAuth permittedRoles={["ADMIN", "COORDINATOR"]} fallback={<Navigate to="/" replace />}>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar />
        </div>
        <div className={styles.PageContent} data-testid="schedule">
          <ExcelViewer />
        </div>
      </div>
    </CheckAuth>
  );
}

export default UploadInstructorAvailabilityPage;
