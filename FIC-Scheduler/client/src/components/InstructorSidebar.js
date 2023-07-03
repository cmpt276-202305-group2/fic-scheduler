import React from "react";
import {useNavigate} from 'react-router-dom';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import Button from '@mui/material/Button';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';

function InstructorSidebar() {
    const navigate = useNavigate();
    const handleLogout = () => {
       fetch('/logout', {method: 'POST'
    
        }).then((response) => {
            if(response.ok){
                navigate('/LoginPage');
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
        <li><AccountCircleIcon/>Instructor Name</li>
        <li><CalendarMonthIcon/>Schedule</li>
        <li><CloudUploadIcon/>Upload Peferences</li>
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

export default InstructorSidebar;