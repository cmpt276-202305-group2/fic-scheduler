import { useState } from "react";
import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";
import ExcelViewer from "../components/ExcelViewer";

function UploadInstructorAvailabilityPage() {
  const [spreadsheetData, setSpreadsheetData] = useState([]);
  return (
    <CheckAuth permittedRoles={["ADMIN", "COORDINATOR"]} fallback={<Navigate to="/" replace />}>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar item="uploadInstructorAvailability" />
        </div>
        <div className={styles.PageContent} data-testid="schedule">
          <ExcelViewer spreadsheetData={spreadsheetData} setSpreadsheetData={setSpreadsheetData} />
        </div>
      </div>
    </CheckAuth>
  );
}

export default UploadInstructorAvailabilityPage;
