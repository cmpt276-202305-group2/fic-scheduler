import React, { useState } from "react";
import axios from "axios";
import readExcelFile from "./readExcelfile";
import styles from "./FileImport.module.css";
import Button from "@mui/material/Button";
import { tokenConfig } from "../utils";
import { FileUploader, SpreadsheetTable } from "./FileUploader";
import { ViewUploadedAvailabilityList } from "./ViewUploadedAvailabilityList";
const ImportAvailabity = ({
  availabilitySpreadsheetData,
  setAvailabilitySpreadsheetData,
}) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [isPreviewVisible, setIsPreviewVisible] = useState(false);
  const [isInstructorAvailabilityVisible, setisInstructorAvailabilityVisible] =
    useState(false);

  const createFileUploadHandler =
    (setFile, setErrorMessage, setData, setIsPreviewVisible) =>
    async (event) => {
      const file = event.target.files[0];
      if (!file) return;
      const allowedFormats = ["xlsx", "csv"];
      const fileExtension = file.name.split(".").pop().toLowerCase();
      if (!allowedFormats.includes(fileExtension)) {
        setErrorMessage(true);
        return;
      }
      setErrorMessage(false);
      try {
        const data = await readExcelFile(file);
        setData(data);
        setFile(file.name);
        setIsPreviewVisible(true);
      } catch (error) {
        console.error("Error reading Excel file:", error);
      }
    };

  const handleFileUpload = createFileUploadHandler(
    setSelectedFile,
    setShowErrorMessage,
    setAvailabilitySpreadsheetData,
    setIsPreviewVisible
  );

  const handleSendToBackEnd = async () => {
    if (availabilitySpreadsheetData.length > 0) {
      const jsonData = [];
      const instructorDataMap = {};

      for (const row of availabilitySpreadsheetData) {
        const time = row.time;
        const dayOfWeek = row.dayOfWeek;
        const partOfDay = row.partOfDay;
        const instructorName = row.instructorName; // Assuming the instructor name is in a column named 'instructorName' in the spreadsheet

        const key = `${time}_${dayOfWeek}_${partOfDay}`;

        if (!instructorDataMap[key]) {
          instructorDataMap[key] = {
            time,
            dayOfWeek,
            partOfDay,
            instructorData: {
              id: null, // Assuming the ID will be determined later on the server-side
              name: instructorName,
            },
          };
          jsonData.push(instructorDataMap[key]);
        }
      }

      try {
        const response = await axios.post(
          `${process.env.REACT_APP_BACKEND_URL}/api/instructors`,
          jsonData,
          tokenConfig()
        );

        if (response.status === 200) {
          const result = response.data;
          console.log("File upload successful:", result);
        } else {
        }
      } catch (error) {}
    }
  };

  const handleShowAvailabilityList = () => {
    setisInstructorAvailabilityVisible(
      (setisInstructorAvailabilityVisible) =>
        !setisInstructorAvailabilityVisible
    );
  };

  return (
    <div className={styles.tableHolder}>
      <h2 className={styles.title}>Instructor Availability</h2>
      <FileUploader
        selectedFile={selectedFile}
        handleFileUpload={handleFileUpload}
        showErrorMessage={showErrorMessage}
        isPreviewVisible={isPreviewVisible}
        handleSendToBackend={handleSendToBackEnd}
        id={2}
        styles={styles}
      />
      <SpreadsheetTable
        spreadsheetData={availabilitySpreadsheetData}
        styles={styles}
      />
      <Button
        onClick={handleShowAvailabilityList}
        variant="contained"
        color="primary"
        sx={{
          color: "white",
          backgroundColor: "#417A1A",
          "&:hover": { backgroundColor: "#417A1A" },
        }}
        style={{ marginBottom: 10, marginTop: 10 }}
      >
        {isInstructorAvailabilityVisible ? "Hide" : "Show"} Current Availability
        List
      </Button>
      {isInstructorAvailabilityVisible && <ViewUploadedAvailabilityList />}
    </div>
  );
};

export default ImportAvailabity;
