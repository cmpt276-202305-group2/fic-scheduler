import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import DebugBlockSplit from "../components/debug/DebugBlockSplit";
import DebugClassroom from "../components/debug/DebugClassroom";
import DebugCourseOffering from "../components/debug/DebugCourseOffering";
import DebugInstructor from "../components/debug/DebugInstructor";
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
          {subpage === 'blockSplit' ? <DebugBlockSplit /> : ''}
          {subpage === 'classroom' ? <DebugClassroom /> : ''}
          {subpage === 'courseOffering' ? <DebugCourseOffering /> : ''}
          {subpage === 'instructor' ? <DebugInstructor /> : ''}
          {subpage === 'semesterPlan' ? <DebugSemesterPlan /> : ''}
          {subpage === 'schedule' ? <DebugSchedule /> : ''}
          {subpage === 'user' ? <DebugUser /> : ''}
        </div>
      </div>
    </CheckAuth>
  );
}

export default GenerateSchedulePage;
