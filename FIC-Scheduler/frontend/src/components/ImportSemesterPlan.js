import React, { useState, useEffect } from "react";

import axios from "axios";
import readExcelFile from "./readExcelfile";
import styles from "./FileImport.module.css";
import { tokenConfig } from "../utils";
import { FileUploader, SpreadsheetTable } from "./FileUploader";

const ImportSemesterPlan = ({
  semesterPlanSpreadsheetData,
  setSemesterPlanSpreadsheetData,
}) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [isPreviewVisible, setIsPreviewVisible] = useState(false);
  const [coursesData, setCoursesData] = useState([]);
  const [instructorsData, setInstructorsData] = useState([]);
  const [classroomsData, setClassroomsData] = useState([]);

  useEffect(() => {
    // Fetch data from the backend
    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/courses`)
      .then((response) => {
        const filteredCourseData = response.data.map((course) => course.id);
        setCoursesData(filteredCourseData);
      })
      .catch((error) => {
        console.error("Error fetching courses data:", error);
      });

    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/instructors`)
      .then((response) => {
        const filteredInstructorData = response.data.map((ins) => ins.id);
        setInstructorsData(filteredInstructorData);
      })
      .catch((error) => {
        console.error("Error fetching instructors data:", error);
      });

    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/classrooms`)
      .then((response) => {
        const filteredClassData = response.data.map((cls) => cls.id);
        setClassroomsData(filteredClassData);
      })
      .catch((error) => {
        console.error("Error fetching classrooms data:", error);
      });
  }, []);

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
    setSemesterPlanSpreadsheetData,
    setIsPreviewVisible
  );

  const handleSendToBackEnd = async () => {
    if (semesterPlanSpreadsheetData.length > 0) {
      // Convert data to JSON format
      const jsonData = semesterPlanSpreadsheetData.map((item) => ({
        name: item.name,
        notes: item.notes,
        semesterID: item.semesterID,
        coursesOffered: coursesData, // Replace coursesData with the actual data received from the backend
        instructorsAvailable: instructorsData, // Replace instructorsData with the actual data received from the backend
        classroomsAvailable: classroomsData, // Replace classroomsData with the actual data received from the backend
      }));

      try {
        const response = await axios.post(
          `${process.env.REACT_APP_BACKEND_URL}/api/semester-plans`,
          jsonData,
          tokenConfig(),
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        if (response.status === 200 || response.status === 201) {
          const responseData = response.data;
          console.log("File upload successful. Response data:", responseData);
        } else if (response.status === 409) {
          // In case of conflicts (status code 409), the response data contains created and conflict semester plans
          const responseData = response.data;
          console.log("Conflict semester plans:", responseData.conflicts);
          console.log(
            "Successfully created semester plans:",
            responseData.created
          );
        } else {
          console.error("Error uploading file:", response.statusText);
        }
      } catch (error) {
        console.error("Error sending JSON data to the backend:", error);
      }
    }
  };

  return (
    <div className={styles.tableHolder}>
      <h2 className={styles.title}>Semester Plan</h2>
      <FileUploader
        selectedFile={selectedFile}
        handleFileUpload={handleFileUpload}
        showErrorMessage={showErrorMessage}
        isPreviewVisible={isPreviewVisible}
        handleSendToBackend={handleSendToBackEnd}
        id={1}
        styles={styles}
      />
      <SpreadsheetTable
        spreadsheetData={semesterPlanSpreadsheetData}
        styles={styles}
      />
    </div>
  );
};

export default ImportSemesterPlan;
