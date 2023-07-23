import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import BugReportIcon from '@mui/icons-material/BugReport';
import Button from "@mui/material/Button";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import SettingsIcon from "@mui/icons-material/Settings";
import ClassIcon from '@mui/icons-material/Class';
import SchoolIcon from '@mui/icons-material/School';
import DashboardCustomizeIcon from '@mui/icons-material/DashboardCustomize';
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
          <li onClick={() => navigate("/manageInstructor")}>
            <SchoolIcon />
            Manage Instructors
          </li>
          <li onClick={() => navigate("/manageClassroom")}>
            <DashboardCustomizeIcon />
            Manage Classrooms
          </li>
          <li onClick={() => navigate("/manageCourse")}>
            <ClassIcon />
            Manage Courses
          </li>
        </CheckAuth>
        <CheckAuth permittedRoles={["DEBUG"]}>
          <li onClick={() => navigate("/debugMenu")}>
            <BugReportIcon />
            Debug Menu
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
