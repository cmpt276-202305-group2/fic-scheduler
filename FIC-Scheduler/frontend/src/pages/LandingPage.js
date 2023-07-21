import { useContext } from 'react';
import { Navigate } from "react-router-dom";
import { UserInfoContext } from '../App';

function LandingPage() {
  const { userInfo } = useContext(UserInfoContext);

  // console.log("Landing page:");
  if ((userInfo ?? null) !== null) {
    // console.log("  has userinfo");
    const userRoles = Array.from(userInfo.roles ?? []);
    if (userRoles.includes("ADMIN")) {
      // console.log("  is admin");
      return (<Navigate to="/manageInstructors" replace />);
    }
    if (userRoles.includes("COORDINATOR")) {
      // console.log("  is coordinator");
      return (<Navigate to="/viewFullSchedule" replace />);
    }
    if (userRoles.includes("INSTRUCTOR")) {
      // console.log("  is instructor");
      return (<Navigate to="/viewInstructorSchedule" replace />);
    }
  }
  return (<Navigate to="/login" replace />);
}

export default LandingPage;
