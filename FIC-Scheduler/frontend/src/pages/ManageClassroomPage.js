import { useState } from "react";
import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";
import ImportClassroom from "../components/ImportClassroom";
import ImportFacilities from "../components/ImportFacilities";


function ManageClassroomPage() {
    const [classroomSpreadsheetData, setClassroomSpreadsheetData] = useState([]);
    const [facilitiesSpreadsheetData, setFacilitiesSpreadsheetData] = useState([]);
    return (
        <CheckAuth permittedRoles={["ADMIN", "COORDINATOR"]} fallback={<Navigate to="/" replace />}>
            <div className={styles.Container}>
                <div className={styles.Sidebar}>
                    <Sidebar />
                </div>
                <div className={styles.PageContent} data-testid="configuration">
                    <ImportClassroom
                        classroomSpreadsheetData={classroomSpreadsheetData}
                        setClassroomSpreadsheetData={setClassroomSpreadsheetData}
                    />
                    <ImportFacilities
                        facilitiesSpreadsheetData={facilitiesSpreadsheetData}
                        setFacilitiesSpreadsheetData={setFacilitiesSpreadsheetData}
                    />
                </div>
            </div>
        </CheckAuth>
    );
}

export default ManageClassroomPage;