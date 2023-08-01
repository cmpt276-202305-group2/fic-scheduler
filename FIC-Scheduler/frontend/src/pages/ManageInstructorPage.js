import { useState } from "react";
import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";
import ImportAvailability from "../components/ImportAvailability";
import ImportInstructor from "../components/ImportInstructor";

function ManageClassroomPage() {
  const [availabilitySpreadsheetData, setAvailabilitySpreadsheetData] =
    useState([]);
  const [instructorSpreadsheetData, setInstructorSpreadsheetData] = useState(
    []
  );
  return (
    <CheckAuth
      permittedRoles={["ADMIN", "COORDINATOR"]}
      fallback={<Navigate to="/" replace />}
    >
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar />
        </div>
        <div className={styles.PageContent} data-testid="configuration">
          <ImportAvailability
            availabilitySpreadsheetData={availabilitySpreadsheetData}
            setAvailabilitySpreadsheetData={setAvailabilitySpreadsheetData}
          />
          <ImportInstructor
            instructorSpreadsheetData={instructorSpreadsheetData}
            setInstructorSpreadsheetData={setInstructorSpreadsheetData}
          />
        </div>
      </div>
    </CheckAuth>
  );
}

export default ManageClassroomPage;
