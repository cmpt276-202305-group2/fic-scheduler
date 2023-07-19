import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import Button from "@mui/material/Button";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import SettingsIcon from "@mui/icons-material/Settings";
import CheckAuth from "./CheckAuth";
import { UserInfoContext } from '../App';

function Sidebar({ onItemClick }) {
  const { userInfo } = useContext(UserInfoContext);
  const navigate = useNavigate();

  return (
    <nav>
      <ul>
        <CheckAuth permittedRoles={["INSTRUCTOR", "COORDINATOR", "ADMIN"]}>
          <li>
            <AccountCircleIcon />
            {userInfo.fullName}
          </li>
        </CheckAuth>
        <CheckAuth permittedRoles={["INSTRUCTOR"]}>
          <li onClick={() => navigate("/viewInstructorSchedule")}>
            <CalendarMonthIcon />
            Instructor Schedule
          </li>
          <li>
            <CloudUploadIcon />
            Upload My Preferences
          </li>
        </CheckAuth>
        <CheckAuth permittedRoles={["COORDINATOR"]}>
          <li onClick={() => navigate("/viewFullSchedule")}>
            <CalendarMonthIcon onClick={() => onItemClick("Schedule")} />
            Full Schedule
          </li>
          <li onClick={() => navigate("/uploadInstructorAvailability")}>
            <CloudUploadIcon />
            Upload All Preferences
          </li>
          <li onClick={() => navigate("/Configuration")}>
            <SettingsIcon />
            Configuration
          </li>
        </CheckAuth>
        {(userInfo ?? null) !== null ? (
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
              onClick={() => { navigate("/logout"); }}>
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
