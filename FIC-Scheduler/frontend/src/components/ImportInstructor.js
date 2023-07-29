import React, { useState } from "react";

import styles from "./FileImport.module.css";
import Button from "@mui/material/Button";
import { ViewUploadedInstructorList } from "./ViewUploadedInstructorList";

const ImportInstructor = () => {
  const [isInstructorListVisible, setIsInstructorListVisible] = useState(false);

  const handleShowInstructorList = () => {
    setIsInstructorListVisible(
      (prevIsInstructorListVisible) => !prevIsInstructorListVisible
    );
  };

  return (
    <div className={styles.tableHolder}>
      <h2 className={styles.title}>Instructors</h2>

      <Button
        onClick={handleShowInstructorList}
        variant="contained"
        color="primary"
        sx={{
          color: "white",
          backgroundColor: "#417A1A",
          "&:hover": { backgroundColor: "#417A1A" },
        }}
        style={{ marginBottom: 10, marginTop: 10 }}
      >
        {isInstructorListVisible ? "Hide" : "Show"} Current Instructor List
      </Button>
      {isInstructorListVisible && <ViewUploadedInstructorList />}
    </div>
  );
};

export default ImportInstructor;
