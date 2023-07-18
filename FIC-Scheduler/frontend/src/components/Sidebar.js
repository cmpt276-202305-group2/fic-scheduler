import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import Button from "@mui/material/Button";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import ClassIcon from "@mui/icons-material/Class";
import GroupIcon from "@mui/icons-material/Group";
import { parseJwtToken } from "../utils";

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(";").shift();
}

function Sidebar({ onItemClick }) {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [roles, setRoles] = useState([]);

  useEffect(() => {
    const jwtToken = getCookie("jwtToken");
    if (jwtToken) {
      const decodedToken = parseJwtToken(jwtToken);
      console.log(decodedToken);
      setUser(decodedToken.sub);
      setRoles(decodedToken.roles);
    }
  }, []);

  const handleLogout = () => {
    navigate("/logout");
  };

  return (
    <nav>
      <ul>
        {roles.indexOf("INSTRUCTOR") >= 0 ||
        roles.indexOf("COORDINATOR") >= 0 ||
        roles.indexOf("ADMIN") >= 0 ? (
          <li>
            <AccountCircleIcon />
            User Full Name
          </li>
        ) : (
          ""
        )}
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
        ) : (
          ""
        )}
        {roles.indexOf("COORDINATOR") >= 0 ? (
          <React.Fragment>
            <li>
              <CalendarMonthIcon onClick={() => onItemClick("Schedule")} />
              Full Schedule
            </li>
            <li>
              <CloudUploadIcon
                onClick={() => onItemClick("Upload Preferences")}
              />
              Upload All Peferences
            </li>
            <li>
              <ClassIcon onClick={() => onItemClick("Manage Course")} />
              Manage Courses
            </li>
            <li>
              <GroupIcon onClick={() => onItemClick("Manage Professor")} />
              Manage Professors
            </li>
          </React.Fragment>
        ) : (
          ""
        )}
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
        ) : (
          ""
        )}
      </ul>
    </nav>
  );
}

export default Sidebar;
