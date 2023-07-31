import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";
import GenerateSchedule from "../components/GenerateSchedule";
import ScheduleTable from "../components/ScheduleTable";

function ViewFullSchedulePage() {
    return (
        <CheckAuth permittedRoles={["ADMIN", "COORDINATOR", "INSTRUCTOR"]} fallback={<Navigate to="/" replace />}>
            <div className={styles.Container}>
                <div className={styles.Sidebar}>
                    <Sidebar item="viewFullSchedule" />
                </div>
                <div className={styles.PageContent} data-testid="schedule">
                    <ScheduleTable />
                </div>
            </div>
        </CheckAuth>
    );
}

export default ViewFullSchedulePage;
