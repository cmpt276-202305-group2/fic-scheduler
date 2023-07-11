import React from "react";
import { useNavigate } from "react-router-dom";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import Button from "@mui/material/Button";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";

function InstructorSidebar() {
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
          Instructor Name
        </li>
        <li>
          <CalendarMonthIcon />
          Schedule
        </li>
        <li>
          <CloudUploadIcon />
          Upload Preferences
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

export default InstructorSidebar;
