import React from "react";
import styles from "./InstructorCoordinatorHomePage.module.css";
import CoordinatorSidebar from "../components/CoordinatorSidebar";

function CoordinatorHomePage() {
    return (
        <>
        <div className = {styles.Container}>
        <div className = {styles.Sidebar}>
            <CoordinatorSidebar className = {styles.Sidebar}/>
        </div>
        <div className = {styles.Schedule}>
            SCHEDULE GOES HERE
        </div>
        </div>
        </>   
    );
}

export default CoordinatorHomePage;