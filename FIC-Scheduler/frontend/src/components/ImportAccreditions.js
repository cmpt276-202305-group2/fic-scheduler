import React, { useState } from "react";
import axios from "axios";
import readExcelFile from "./readExcelfile";
import styles from "./FileImport.module.css";
import { tokenConfig } from "../utils";
import { FileUploader, SpreadsheetTable } from "./FileUploader";

const ImportAccreditation = ({
  accreditationSpreadsheetData,
  setAccreditationSpreadsheetData,
}) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [isPreviewVisible, setIsPreviewVisible] = useState(false);

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
    setAccreditationSpreadsheetData,
    setIsPreviewVisible
  );

  const handleSendToBackEnd = async () => {
    if (accreditationSpreadsheetData.length > 0) {
      // Convert data to JSON format
      const jsonData = accreditationSpreadsheetData.map((item) => ({
        name: item,
      }));

      try {
        const response = await axios.post(
          "api/accreditations",
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
          // In case of conflicts (status code 409), the response data contains created and conflict accreditations
          const responseData = response.data;
          console.log("Conflict accreditations:", responseData.conflicts);
          console.log(
            "Successfully created accreditations:",
            responseData.created
          );
        } else {
          console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        console.error("Error sending JSON data to the backend:", error);
      }
    }
  };

  return (
    <div className={styles.tableHolder}>
      <h2 className={styles.title}>Accreditation</h2>
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
        spreadsheetData={accreditationSpreadsheetData}
        styles={styles}
      />
    </div>
  );
};

export default ImportAccreditation;
