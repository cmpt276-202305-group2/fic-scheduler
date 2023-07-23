import { useContext } from 'react';
import { Navigate } from "react-router-dom";
import { UserInfoContext } from '../App';

function LandingPage() {
  const { userInfo } = useContext(UserInfoContext);

  // console.log("Landing page:");
  if ((userInfo ?? null) !== null) {
    // console.log("  has userinfo");
    const userRoles = Array.from(userInfo.roles ?? []);

    // console.log('roles:', (userRoles.includes("DEBUG") ? 'DEBUG ' : '') +
    //   (userRoles.includes("ADMIN") ? 'ADMIN ' : '') +
    //   (userRoles.includes("COORDINATOR") ? 'COORDINATOR ' : '') +
    //   (userRoles.includes("INSTRUCTOR") ? 'INSTRUCTOR ' : ''));

    var result = '';
    if (userRoles.includes("DEBUG")) {
      // console.log("  is debug");
      result = (<Navigate to="/debugMenu" replace />);
    }
    else if (userRoles.includes("ADMIN")) {
      // console.log("  is admin");
      result = (<Navigate to="/manageInstructors" replace />);
    }
    else if (userRoles.includes("COORDINATOR")) {
      // console.log("  is coordinator");
      result = (<Navigate to="/viewFullSchedule" replace />);
    }
    else if (userRoles.includes("INSTRUCTOR")) {
      // console.log("  is instructor");
      result = (<Navigate to="/viewInstructorSchedule" replace />);
    }
  }
  // if (result === '') {
  //   result = (<Navigate to="/logout" replace />);
  // }
  return result;
}

export default LandingPage;
