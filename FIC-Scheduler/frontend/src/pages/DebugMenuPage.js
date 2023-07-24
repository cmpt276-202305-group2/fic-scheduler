import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import DebugAuth from "../components/debug/DebugAuth";
// import DebugClassroom from "../components/debug/DebugClassroom";
// import DebugCourseOffering from "../components/debug/DebugCourseOffering";
// import DebugFacility from "../components/debug/DebugFacility";
// import DebugInstructorAvailability from "../components/debug/DebugInstructorAvailability";
// import DebugInstructor from "../components/debug/DebugInstructor";
import DebugSchedule from "../components/debug/DebugSchedule";
import DebugSemesterPlan from "../components/debug/DebugSemesterPlan";
import DebugUser from "../components/debug/DebugUser";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";

function GenerateSchedulePage({ subpage }) {
  return (
    <CheckAuth
      permittedRoles={["DEBUG"]}
      fallback={<Navigate to="/" replace />}
    >
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar item="debugMenu" subitem={subpage} />
        </div>
        <div className={styles.PageContent} data-testid="debug-menu">
          {subpage === "auth" ? <DebugAuth /> : ""}
          {/*
          { subpage === 'auth' ? <DebugClassroom /> : '' }
          { subpage === 'auth' ? <DebugCourseOffering /> : '' }
          { subpage === 'auth' ? <DebugFacility /> : '' }
          { subpage === 'auth' ? <DebugInstructor /> : '' }
          { subpage === 'auth' ? <DebugInstructorAvailability /> : '' }
           */}
          {subpage === "semesterPlan" ? <DebugSemesterPlan /> : ""}
          {subpage === "schedule" ? <DebugSchedule /> : ""}
          {subpage === "user" ? <DebugUser /> : ""}
        </div>
      </div>
    </CheckAuth>
  );
}

export default GenerateSchedulePage;
