import React , { useState }from "react";
import styles from "./InstructorCoordinatorHomePage.module.css";
import CoordinatorSidebar from "../components/CoordinatorSidebar";
import UploadPrefereces from "../components/UploadPreferences";

function CoordinatorHomePage() {
  const [showUploadPreferences, setShowUploadPreferences] = React.useState(false);
  const [showSchedule, setShowSchedule] = React.useState(false);
  const [showManageCourse, setShowManageCourse] = React.useState(false);
  const [showManageProfessors, setShowManageProfessors] = React.useState(false);
  const [data, setData] = useState([]);
  const [coloumnArray, setColoumnArray] = useState([]);
  const [values, setValues] = useState([]);
  const handleSidebarItemClick = (item) => {
    if (item === "Upload Preferences") {
      setShowUploadPreferences(true);
      setShowManageCourse(false);
      setShowManageProfessors(false);
    }
    else if(item === "Manage Course") {
      setShowManageCourse(true);
      setShowUploadPreferences(false);
      setShowManageProfessors(false);
    }
    else if(item === "Manage Professors") {
      setShowManageProfessors(true);
      setShowUploadPreferences(false);
      setShowManageCourse(false);
    }
    else {
      setShowUploadPreferences(false);
      setShowManageCourse(false);
      setShowManageProfessors(false);
      setShowSchedule(true);
    }
  };
  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <CoordinatorSidebar onItemClick={handleSidebarItemClick}/> 
        </div>
        <div className={styles.Schedule} data-testid="schedule">
          {(showUploadPreferences || showManageCourse || showManageProfessors) ? (
            <UploadPrefereces 
              data = {data}
              coloumnArray = {coloumnArray}
              values={values}
              setValues={setValues}
              setdata = {setData}
              setcoloumnArray = {setColoumnArray}/> 
            ) : <div> Schedule </div>
          } 
        </div>
      </div>
    </React.Fragment>
  );
}

export default CoordinatorHomePage;
