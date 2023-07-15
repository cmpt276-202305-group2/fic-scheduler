import { Navigate, useLocation } from "react-router-dom";
import { parseJwtToken } from '../utils'; 

function CheckAuth({children, roles}) {
  const jwtToken = localStorage.getItem('jwtToken');
  const userRole = jwtToken ? parseJwtToken(jwtToken).roles : null;
  const location = useLocation();

  if (!jwtToken || !roles.includes(userRole)) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  
  return children;
}

export default CheckAuth;
