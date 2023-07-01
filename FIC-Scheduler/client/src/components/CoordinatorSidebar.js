import React from "react";
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import Button from '@mui/material/Button';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import ClassIcon from '@mui/icons-material/Class';
import GroupIcon from '@mui/icons-material/Group';

function CoordinatorSidebar() {
    const handleLogout = () => {
       fetch('/logout', {method: 'POST'
    
        }).then((response) => {
            if(response.ok){
                
            }
            else{
                alert("Logout Failed");
            }
        }).catch((error) => {
            console.log(error);
        });
    }
  return (
    <div>
        <li><AccountCircleIcon/>Coordinator Name</li>
        <li><CalendarMonthIcon/>Schedule</li>
        <li><CloudUploadIcon/>Upload Peferences</li>
        <li><ClassIcon/>Manage Course</li>
        <li><GroupIcon/>Manage Professors</li>
        <li><Button 
            variant = "text" 
            sx={{backgroundColor: "red", color: "white", "&:hover": {backgroundColor: "red"}}} 
            disableElevation
            onClick={handleLogout}
            >
        Log Out</Button></li>
    </div>
  );
}

export default CoordinatorSidebar;