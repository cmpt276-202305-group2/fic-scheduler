import { useContext } from 'react';
import { Navigate } from "react-router-dom";
import { UserInfoContext } from '../App';

function LandingPage() {
  const { userInfo } = useContext(UserInfoContext);

  if ((userInfo ?? null) !== null) {
    const userRoles = Array.from(userInfo.roles ?? []);
    if (userRoles.includes("ADMIN")) {
      return (<Navigate to="/manageInstructors" replace />);
    }
    if (userRoles.includes("COORDINATOR")) {
      return (<Navigate to="/viewFullSchedule" replace />);
    }
    if (userRoles.includes("INSTRUCTOR")) {
      return (<Navigate to="/viewSchedule" replace />);
    }
  }
  return (<Navigate to="/login" replace />);
}

export default LandingPage;
