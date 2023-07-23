import { useContext } from "react";
import { Navigate } from "react-router-dom";
import { UserInfoContext } from "../App";

function LandingPage() {
  const { userInfo } = useContext(UserInfoContext);
  let result = <Navigate to="/logout" replace />;

  if (userInfo && userInfo.roles) {
    const userRoles = Array.from(userInfo.roles ?? []);
    // console.log(
    //   "roles:",
    //   (userRoles.includes("DEBUG") ? "DEBUG " : "") +
    //     (userRoles.includes("ADMIN") ? "ADMIN " : "") +
    //     (userRoles.includes("COORDINATOR") ? "COORDINATOR " : "") +
    //     (userRoles.includes("INSTRUCTOR") ? "INSTRUCTOR " : "")
    // );
    if (userRoles.includes("DEBUG")) {
      result = <Navigate to="/debugMenu" replace />;
    } else if (userRoles.includes("ADMIN")) {
      result = <Navigate to="/manageInstructors" replace />;
    } else if (userRoles.includes("COORDINATOR")) {
      result = <Navigate to="/viewFullSchedule" replace />;
    } else if (userRoles.includes("INSTRUCTOR")) {
      result = <Navigate to="/viewInstructorSchedule" replace />;
    }
  }
  return result;
}

export default LandingPage;
