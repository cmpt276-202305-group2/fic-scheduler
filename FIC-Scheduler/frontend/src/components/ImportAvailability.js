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
  const [instructors, setInstructors] = useState([]);
  const [jsonData, setJsonData] = useState([]);
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
      //   const jsonData = [];
      const instructorDataMap = {};

      for (const row of availabilitySpreadsheetData) {
        const time = row.time;
        const dayOfWeek = row.dayOfWeek;
        const partOfDay = row.partOfDay;
        const instructorName = row.instructorName;

        const key = `${time}_${dayOfWeek}_${partOfDay}`;

        if (!instructorDataMap[key]) {
          instructorDataMap[key] = {
            time,
            dayOfWeek,
            partOfDay,
            instructorData: {
              id: null,
              name: instructorName,
            },
          };
          jsonData.push(instructorDataMap[key]);
        }
      }

      try {
        console.log("Sending data to backend:", jsonData);

        let response = await axios.get(
          `${process.env.REACT_APP_BACKEND_URL}/api/semester-plans/latest`,
          tokenConfig()
        );

        // If the latest semester plan doesn't exist, create a new one
        if (!response.data) {
          const semesterPlan = {
            name: "SomeName",
            semester: "SomeSemester",
            notes: "",
            coursesOffered: [],
            instructorsAvailable: [],
            classroomsAvailable: [],
            courseCorequisites: [],
            instructorSchedulingRequests: [],
          };

          response = await axios.post(
            `${process.env.REACT_APP_BACKEND_URL}/api/semester-plans`,
            semesterPlan,
            tokenConfig()
          );
        }

        // Then update the semester plan with the new instructor availability data
        if (response.status === 200 || response.status === 201) {
          response = await axios.put(
            `${process.env.REACT_APP_BACKEND_URL}/api/semester-plans`,
            jsonData,
            tokenConfig()
          );

          // If the PUT request is successful, send the instructor data
          if (response.status === 200) {
            response = await axios.post(
              `${process.env.REACT_APP_BACKEND_URL}/api/instructors`,
              instructors,
              tokenConfig()
            );

            if (response.status === 200 || response.status === 201) {
              console.log("Instructor data upload successful:", response.data);
            }
          }
        }
      } catch (error) {
        console.error("Error sending data to backend:", error);
      }
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
