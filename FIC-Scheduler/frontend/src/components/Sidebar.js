import React from "react";
import { useNavigate } from "react-router-dom";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import Button from "@mui/material/Button";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import ClassIcon from "@mui/icons-material/Class";
import GroupIcon from "@mui/icons-material/Group";
import { parseJwtToken } from '../utils';

function Sidebar() {
  const navigate = useNavigate();
  const handleLogout = () => {
    localStorage.removeItem("jwtToken");
    navigate("/logout");
  };

  const jwtToken = localStorage.getItem("jwtToken");
  var user = null;
  var roles = [];
  if (jwtToken) {
    const decodedToken = parseJwtToken(jwtToken);
    user = decodedToken.sub;
    roles = decodedToken.roles;
  }

  return (
    <nav>
      <ul>
        {roles.indexOf("INSTRUCTOR") >= 0 || roles.indexOf("COORDINATOR") >= 0 || roles.indexOf("ADMIN") >= 0 ? (
          <li>
            <AccountCircleIcon />
            User Full Name
          </li>
        ) : ""}
        {roles.indexOf("INSTRUCTOR") >= 0 ? (
          <React.Fragment>
            <li>
              <CalendarMonthIcon />
              Instructor Schedule
            </li>
            <li>
              <CloudUploadIcon />
              Upload My Preferences
            </li>
          </React.Fragment>
        ) : ""}
        {roles.indexOf("COORDINATOR") >= 0 ? (
          <React.Fragment>
            <li>
              <CalendarMonthIcon />
              Full Schedule
            </li>
            <li>
              <CloudUploadIcon />
              Upload All Peferences
            </li>
            <li>
              <ClassIcon />
              Manage Courses
            </li>
            <li>
              <GroupIcon />
              Manage Professors
            </li>
          </React.Fragment>
        ) : ""}
        {user !== null ? (
          <li>
            <Button
              fullWidth
              variant="text"
              sx={{
                backgroundColor: "red",
                color: "white",
                "&:hover": { backgroundColor: "red" },
              }}
              disableElevation
              onClick={handleLogout}
            >
              Log Out
            </Button>
          </li>
        ) : ""}
      </ul>
    </nav>
  );
}

export default Sidebar;
