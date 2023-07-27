import { useState } from "react";
import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";
import ImportCourses from "../components/ImportCourses";

function ManageCoursePage() {
  const [coursesSpreadsheetData, setCoursesSpreadsheetData] = useState([]);

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
          <ImportCourses
            coursesSpreadsheetData={coursesSpreadsheetData}
            setCoursesSpreadsheetData={setCoursesSpreadsheetData}
          />
        </div>
      </div>
    </CheckAuth>
  );
}

export default ManageCoursePage;
