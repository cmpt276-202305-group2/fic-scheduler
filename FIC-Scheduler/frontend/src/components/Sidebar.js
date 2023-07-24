import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import BugReportIcon from '@mui/icons-material/BugReport';
import Button from "@mui/material/Button";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import ClassIcon from '@mui/icons-material/Class';
import SchoolIcon from '@mui/icons-material/School';
import DashboardCustomizeIcon from '@mui/icons-material/DashboardCustomize';
import CheckAuth from "./CheckAuth";
import { UserInfoContext } from '../App';

function Sidebar({ item, subitem }) {
  const { userInfo } = useContext(UserInfoContext);
  const navigate = useNavigate();

  return (
    <nav>
      <ul>
        <CheckAuth permittedRoles={["INSTRUCTOR", "COORDINATOR", "ADMIN"]}>
          <li>
            <AccountCircleIcon /> {userInfo.fullName}
          </li>
        </CheckAuth>
        <CheckAuth permittedRoles={["INSTRUCTOR"]}>
          <li onClick={() => navigate("/viewInstructorSchedule")}>
            <CalendarMonthIcon /> Instructor Schedule
          </li>
          <li>
            <CloudUploadIcon /> Upload My Preferences
          </li>
        </CheckAuth>
        <CheckAuth permittedRoles={["COORDINATOR"]}>
          <li onClick={() => navigate("/viewFullSchedule")}>
            <CalendarMonthIcon /> Full Schedule
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
          {item === 'debugMenu' ? (
            <React.Fragment>
              <li>
                <BugReportIcon /> Debug Menu
              </li>
              <li onClick={() => navigate("/debugMenu/auth")}>
                &nbsp;&nbsp;&nbsp;<BugReportIcon /> Auth
              </li>
              <li onClick={() => navigate("/debugMenu/classroom")}>
                &nbsp;&nbsp;&nbsp;<BugReportIcon /> Classrooms
              </li>
              <li onClick={() => navigate("/debugMenu/courseOffering")}>
                &nbsp;&nbsp;&nbsp;<BugReportIcon /> Course Offerings
              </li>
              <li onClick={() => navigate("/debugMenu/facility")}>
                &nbsp;&nbsp;&nbsp;<BugReportIcon /> Facilities
              </li>
              <li onClick={() => navigate("/debugMenu/instructor")}>
                &nbsp;&nbsp;&nbsp;<BugReportIcon /> Instructors
              </li>
              <li onClick={() => navigate("/debugMenu/instructorAvailability")}>
                &nbsp;&nbsp;&nbsp;<BugReportIcon /> Instructor Availabilities
              </li>
              <li onClick={() => navigate("/debugMenu/semesterPlan")}>
                &nbsp;&nbsp;&nbsp;<BugReportIcon /> Semester Plans
              </li>
              <li onClick={() => navigate("/debugMenu/user")}>
                &nbsp;&nbsp;&nbsp;<BugReportIcon /> Users
              </li>
            </React.Fragment>)
            : (
              <li onClick={() => navigate("/debugMenu/semesterPlan")}>
                <BugReportIcon /> Debug Menu
              </li>
            )}
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
