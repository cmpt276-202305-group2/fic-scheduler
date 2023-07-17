// CheckAuth.js
import { Navigate, useLocation } from "react-router-dom";
import { useContext } from 'react'; 
import { UserRoleContext } from '../App';

function CheckAuth({children, roles}) {
  const { userRole } = useContext(UserRoleContext);
  const location = useLocation();

  if (!userRole || !roles.includes(userRole)) {
    // If userRole is undefined or not included in the roles prop, navigate to their role's page
    // If userRole is null (user is not logged in), this will fall back to /login
    return <Navigate to={`/${userRole ? userRole.toLowerCase() : "login"}`} state={{ from: location }} replace />;
  }

  return children;
}

export default CheckAuth;
