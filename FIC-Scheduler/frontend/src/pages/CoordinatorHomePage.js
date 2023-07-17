import React , { useState }from "react";
import styles from "./InstructorCoordinatorHomePage.module.css";
import CoordinatorSidebar from "../components/CoordinatorSidebar";
import ExcelViewer from "../components/ExcelViewer";
import UploadPrefereces from "../components/UploadPreferences";

function CoordinatorHomePage() {
  const [showUploadPreferences, setShowUploadPreferences] = React.useState(false);
  const [data, setData] = useState([]);
  const [coloumnArray, setColoumnArray] = useState([]);
  const [values, setValues] = useState([]);
  const handleSidebarItemClick = (item) => {
    if (item === "Upload Preferences") {
      setShowUploadPreferences(true);
    }
    else {
      setShowUploadPreferences(false);
    }
  };
  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <CoordinatorSidebar onItemClick={handleSidebarItemClick}/> 
        </div>
        <div className={styles.Schedule} data-testid="schedule">
          {(showUploadPreferences) ? (
            <UploadPrefereces 
              data = {data}
              setData = {setData}
              coloumnArray = {coloumnArray}
              setColoumnArray = {setColoumnArray}
              values={values}
              setValues={setValues}
              /> 
            ) : <ExcelViewer /> 
          } 
        </div>
      </div>
    </React.Fragment>
  );
}

export default CoordinatorHomePage;
