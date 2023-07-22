import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import DebugAuth from "../components/debug/DebugAuth";
// import DebugClassroom from "../components/debug/DebugClassroom";
// import DebugCoordinator from "../components/debug/DebugCoordinator";
// import DebugCourseOffering from "../components/debug/DebugCourseOffering";
// import DebugFacility from "../components/debug/DebugFacility";
// import DebugInstructorAvailability from "../components/debug/DebugInstructorAvailability";
// import DebugInstructor from "../components/debug/DebugInstructor";
// import DebugInstructors from "../components/debug/DebugInstructors";
// import DebugSchedule from "../components/debug/DebugSchedule";
import DebugUser from "../components/debug/DebugUser";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";

function GenerateSchedulePage() {
  return (
    <CheckAuth permittedRoles={["DEBUG"]} fallback={<Navigate to="/" replace />}>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar />
        </div>
        <div className={styles.PageContent} data-testid="debug-menu">
          <DebugAuth />
          {/* <DebugClassroom />
          <DebugCoordinator />
          <DebugCourseOffering />
          <DebugFacility />
          <DebugInstructorAvailability />
          <DebugInstructor />
          <DebugInstructors />
          <DebugSchedule /> */}
          <DebugUser />
        </div>
      </div>
    </CheckAuth>
  );
}

export default GenerateSchedulePage;
