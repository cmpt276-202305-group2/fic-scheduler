import React, { useState } from "react";
import styles from "./InstructorCoordinatorHomePage.module.css";
import InstructorSidebar from "../components/InstructorSidebar";

function InstructorHomePage() {
    return (
        <>
        <div className = {styles.Container}>
        <div className = {styles.Sidebar}>
            <InstructorSidebar className = {styles.Sidebar}/>
        </div>
        <div className = {styles.Schedule}>
            SCHEDULE GOES HERE
        </div>
        </div>
        </>   
    );
}

export default InstructorHomePage;