import React, { useState } from "react";
import axios from "axios";
import readExcelFile from "./readExcelfile";
import styles from "./FileImport.module.css";
import Button from "@mui/material/Button";
import { ViewUploadedClassroomList } from "./ViewUploadedClassroomList";
import { tokenConfig } from "../utils";
import { FileUploader, SpreadsheetTable } from "./FileUploader";

const ImportClassroom = ({
  classroomSpreadsheetData,
  setClassroomSpreadsheetData,
}) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [isPreviewVisible, setIsPreviewVisible] = useState(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [isClassroomListVisible, setIsClassroomListVisible] = useState(false);

  const handleShowClassroomList = () => {
    setIsClassroomListVisible(
      (prevIsClassroomListVisible) => !prevIsClassroomListVisible
    );
  };

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
    setClassroomSpreadsheetData,
    setIsPreviewVisible
  );

  const handleSendToBackEnd = async () => {
    if (classroomSpreadsheetData.length > 0) {
      const jsonData = classroomSpreadsheetData.map((row) => {
        const data = {
          id: null,
          roomNumber: row[0].trim(),
          roomType: row[1].trim(),
          notes: row[2].trim(),
        };

        return data;
      });

      console.log("jsonData", jsonData);

      try {
        let response = null;
        response = await axios.get("api/classrooms", tokenConfig());
        console.log("response", response);

        const classroomsToUpdate = [];
        const classroomsToDelete = [];

        const currentClassrooms = response.data;
        currentClassrooms.forEach((classroom) => {
          let found = false;
          jsonData.forEach((newClassroom) => {
            if (classroom.roomNumber === newClassroom.roomNumber) {
              found = true;
            }
          });

          if (!found) {
            classroomsToDelete.push({ id: classroom.id });
          }
        });

        jsonData.slice(1).forEach((newClassroom) => {
          let exists = false;
          currentClassrooms.forEach((classroom) => {
            if (
              newClassroom.roomNumber === classroom.roomNumber &&
              newClassroom.roomType === classroom.roomType &&
              newClassroom.notes === classroom.notes
            ) {
              exists = true;
            }
          });

          if (!exists) {
            classroomsToUpdate.push(newClassroom);
          }
        });

        console.log("Classrooms to delete:", classroomsToDelete);
        if (classroomsToDelete.length > 0) {
          classroomsToDelete.forEach(async (classroom) => {
            await axios.delete(`api/classrooms/${classroom.id}`, tokenConfig());
            console.log(`Deleted classroom with ID: ${classroom.id}`);
          });
        }

        console.log("Classrooms to create/update:", classroomsToUpdate);
        if (classroomsToUpdate.length > 0) {
          response = await axios.post(
            "api/classrooms",
            classroomsToUpdate,
            tokenConfig()
          );
          console.log("response from create", response);
          if (response.status === 200 || response.status === 201) {
            setShowSuccessMessage(true);
          }
        }
      } catch (error) {
        console.error("Error processing classrooms:", error);
      }
    }
  };

  return (
    <div className={styles.tableHolder}>
      <h2 className={styles.title}>Classrooms</h2>
      <FileUploader
        selectedFile={selectedFile}
        handleFileUpload={handleFileUpload}
        showErrorMessage={showErrorMessage}
        isPreviewVisible={isPreviewVisible}
        handleSendToBackend={handleSendToBackEnd}
        id={"3"}
        styles={styles}
      />
      {showSuccessMessage && (
        <h2 className={styles.successMessage}>
          List of classrooms successfully uploaded!
        </h2>
      )}
      <SpreadsheetTable
        spreadsheetData={classroomSpreadsheetData}
        styles={styles}
      />
      <Button
        onClick={handleShowClassroomList}
        variant="contained"
        color="primary"
        sx={{
          color: "white",
          backgroundColor: "#417A1A",
          "&:hover": { backgroundColor: "#417A1A" },
        }}
        style={{ marginBottom: 10, marginTop: 10 }}
      >
        {isClassroomListVisible ? "Hide" : "Show"} Current Classroom List
      </Button>
      {isClassroomListVisible && <ViewUploadedClassroomList />}
    </div>
  );
};

export default ImportClassroom;
