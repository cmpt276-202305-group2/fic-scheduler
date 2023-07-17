import React from "react";
import { useNavigate } from "react-router-dom";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import Button from "@mui/material/Button";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import ClassIcon from "@mui/icons-material/Class";
import GroupIcon from "@mui/icons-material/Group";

function CoordinatorSidebar({onItemClick}) {
  const navigate = useNavigate();
  const handleLogout = () => {
    localStorage.removeItem("jwtToken"); 
    navigate("/logout"); 
  };
  return (
    <nav>
      <ul>
        <li>
          <AccountCircleIcon />
          Coordinator Name
        </li>
        <li onClick = {() => onItemClick("Schedule")}>
          <CalendarMonthIcon />
          Schedule
        </li>
        <li onClick = {() => onItemClick("Upload Preferences")}>
          <CloudUploadIcon />
          Upload Peferences
        </li>
        <li onClick = {() => onItemClick("Manage Course")}>
          <ClassIcon />
          Manage Course
        </li>
        <li onClick = {() => onItemClick("Manage Professor")}>
          <GroupIcon />
          Manage Professors
        </li>
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
      </ul>
    </nav>
  );
}

export default CoordinatorSidebar;
