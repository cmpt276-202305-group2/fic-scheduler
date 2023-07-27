import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";
import ImportSemesterPlan from "../components/ImportSemesterPlan";

function ViewSemesterPlanPage() {
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
                    <ImportSemesterPlan />
                </div>
            </div>
        </CheckAuth>
    );
}

export default ViewSemesterPlanPage;