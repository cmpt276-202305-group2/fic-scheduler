import React, { useState } from "react";
import axios from "axios";
import readExcelFile from "./readExcelfile";
import styles from "./ExcelViewer.module.css";
import { tokenConfig } from "../utils";
import { FileUploader, SpreadsheetTable } from "./FileUploader";

const ConfigurationExcelView = ({
  configurationSpreadsheetDataOne,
  setConfigurationSpreadsheetDataOne,
  configurationSpreadsheetDataTwo,
  setConfigurationSpreadsheetDataTwo,
  configurationSpreadsheetDataThree,
  setConfigurationSpreadsheetDataThree,
  configurationSpreadsheetDataFour,
  setConfigurationSpreadsheetDataFour,
  configurationSpreadsheetDataFive,
  setConfigurationSpreadsheetDataFive,
}) => {
  const [selectedFileOne, setSelectedFileOne] = useState(null);
  const [selectedFileTwo, setSelectedFileTwo] = useState(null);
  const [selectedFileThree, setSelectedFileThree] = useState(null);
  const [selectedFileFour, setSelectedFileFour] = useState(null);
  const [selectedFileFive, setSelectedFileFive] = useState(null);
  const [showErrorMessageOne, setShowErrorMessageOne] = useState(false);
  const [showErrorMessageTwo, setShowErrorMessageTwo] = useState(false);
  const [showErrorMessageThree, setShowErrorMessageThree] = useState(false);
  const [showErrorMessageFour, setShowErrorMessageFour] = useState(false);
  const [showErrorMessageFive, setShowErrorMessageFive] = useState(false);
  const [isPreviewVisibleOne, setIsPreviewVisibleOne] = useState(false);
  const [isPreviewVisibleTwo, setIsPreviewVisibleTwo] = useState(false);
  const [isPreviewVisibleThree, setIsPreviewVisibleThree] = useState(false);
  const [isPreviewVisibleFour, setIsPreviewVisibleFour] = useState(false);
  const [isPreviewVisibleFive, setIsPreviewVisibleFive] = useState(false);

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

  const handleFileUploadOne = createFileUploadHandler(
    setSelectedFileOne,
    setShowErrorMessageOne,
    setConfigurationSpreadsheetDataOne,
    setIsPreviewVisibleOne
  );

  const handleFileUploadTwo = createFileUploadHandler(
    setSelectedFileTwo,
    setShowErrorMessageTwo,
    setConfigurationSpreadsheetDataTwo,
    setIsPreviewVisibleTwo
  );

  const handleFileUploadThree = createFileUploadHandler(
    setSelectedFileThree,
    setShowErrorMessageThree,
    setConfigurationSpreadsheetDataThree,
    setIsPreviewVisibleThree
  );

  const handleFileUploadFour = createFileUploadHandler(
    setSelectedFileFour,
    setShowErrorMessageFour,
    setConfigurationSpreadsheetDataFour,
    setIsPreviewVisibleFour
  );

  const handleFileUploadFive = createFileUploadHandler(
    setSelectedFileFive,
    setShowErrorMessageFive,
    setConfigurationSpreadsheetDataFive,
    setIsPreviewVisibleFive
  );

  const handleSendToBackEndOne = async () => {
    if (configurationSpreadsheetDataOne.length > 0) {
      // Convert data to JSON format
      const jsonData = configurationSpreadsheetDataOne.map((row) => {
        return {
          courseNumber: row.courseNumber,
          semesterPlanID: row.semesterPlanId,
          accredationRequiredName: row.accredationRequiredName,
          blockTypeName: row.blockTypeName,
          facilitesRequiredNames: [
            row.facilitesRequiredNames1 || null,
            row.facilitesRequiredNames2 || null,
            row.facilitesRequiredNames3 || null,
          ],
          conflictedCourseNumbers: [
            row.conflictedCourseNumbers1 || null,
            row.conflictedCourseNumbers2 || null,
            row.conflictedCourseNumbers3 || null,
          ],
        };
      });

      try {
        // Instead of sending the formData, we send jsonData as the request data
        const response = await axios.post(
          " http://localhost:8080/api/courseOffering ",
          jsonData,
          tokenConfig(),
          {
            headers: {
              "Content-Type": "application/json", // Set the appropriate content type for JSON data
            },
          }
        );

        if (response.status === 200) {
          const result = response.data;
          console.log("File upload successful:", result);
        } else {
          console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        console.error("Error sending JSON data to the backend:", error);
      }
    }
  };

  const handleSendToBackEndTwo = async () => {
    if (configurationSpreadsheetDataTwo.length > 0) {
      const jsonData = configurationSpreadsheetDataTwo.map((row) => {
        return {
          roomNumber: row.roomNumber,
          facilitesAvaliableNames: [
            row.facilitesAvaliableNames1 || null,
            row.facilitesAvaliableNames2 || null,
            row.facilitesAvaliableNames3 || null,
            row.facilitesAvaliableNames4 || null,
            row.facilitesAvaliableNames5 || null,
            row.facilitesAvaliableNames6 || null,
            row.facilitesAvaliableNames7 || null,
            row.facilitesAvaliableNames8 || null,
            row.facilitesAvaliableNames9 || null,
          ],
        };
      });

      try {
        const response = await axios.post(
          "http://localhost:8080/api/classrooms",
          jsonData,
          tokenConfig()
        );

        if (response.status === 200) {
          const result = response.json();
          console.log("File upload successful:", result);
        } else {
          console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        console.error("Error reading Excel file:", error);
      }
    }
  };

  const handleSendToBackEndThree = async () => {
    if (configurationSpreadsheetDataThree.length > 0) {
      const jsonData = configurationSpreadsheetDataThree.map((row) => {
        return {
          name: row.name,
          accreditationNames: [
            row.accreditationNames1 || null,
            row.accreditationNames2 || null,
            row.accreditationNames3 || null,
            row.accreditationNames4 || null,
            row.accreditationNames5 || null,
          ],
        };
      });

      try {
        const response = await axios.post(
          "http://localhost:8080/api/instructors",
          jsonData,
          tokenConfig()
        );

        if (response.status === 200) {
          const result = response.data;
          console.log("File upload successful:", result);
        } else {
          console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        console.error("Error reading Excel file:", error);
      }
    }
  };

  const handleSendToBackEndFour = async () => {
    if (configurationSpreadsheetDataFour.length > 0) {
      const jsonData = configurationSpreadsheetDataFive.map((item) => ({
        name: item,
      }));

      try {
        const response = await axios.post(
          "http://localhost:8080/api/facilites",
          jsonData,
          tokenConfig()
        );

        if (response.status === 200) {
          const result = response.data;
          console.log("File upload successful:", result);
        } else {
          console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        console.error("Error reading Excel file:", error);
      }
    }
  };

  const handleSendToBackEndFive = async () => {
    if (configurationSpreadsheetDataFive.length > 0) {
      // Convert data to JSON format
      const jsonData = configurationSpreadsheetDataFive.map((item) => ({
        name: item,
      }));

      try {
        const response = await axios.post(
          "http://localhost:8080/api/accreditations",
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
    <>
      <div className={styles.tableHolder}>
        <h2 className={styles.title}>Course Offering</h2>
        <FileUploader
          selectedFile={selectedFileOne}
          handleFileUpload={handleFileUploadOne}
          showErrorMessage={showErrorMessageOne}
          isPreviewVisible={isPreviewVisibleOne}
          handleSendToBackend={handleSendToBackEndOne}
          id="file-upload-one"
          styles={styles}
        />
        <SpreadsheetTable
          spreadsheetData={configurationSpreadsheetDataOne}
          styles={styles}
        />
      </div>
      <div className={styles.tableHolder}>
        <h1 className={styles.title}>Classrooms</h1>
        <FileUploader
          selectedFile={selectedFileTwo}
          handleFileUpload={handleFileUploadTwo}
          showErrorMessage={showErrorMessageTwo}
          isPreviewVisible={isPreviewVisibleTwo}
          handleSendToBackend={handleSendToBackEndTwo}
          id="file-upload-two"
          styles={styles}
        />
        <SpreadsheetTable
          spreadsheetData={configurationSpreadsheetDataTwo}
          styles={styles}
        />
      </div>
      <div className={styles.tableHolder}>
        <h1 className={styles.title}>Instructors</h1>
        <FileUploader
          selectedFile={selectedFileThree}
          handleFileUpload={handleFileUploadThree}
          showErrorMessage={showErrorMessageThree}
          isPreviewVisible={isPreviewVisibleThree}
          handleSendToBackend={handleSendToBackEndThree}
          id="file-upload-three"
          styles={styles}
        />
        <SpreadsheetTable
          spreadsheetData={configurationSpreadsheetDataThree}
          styles={styles}
        />
      </div>

      <div className={styles.tableHolder}>
        <h1 className={styles.title}>Facilities</h1>
        <FileUploader
          selectedFile={selectedFileFour}
          handleFileUpload={handleFileUploadFour}
          showErrorMessage={showErrorMessageFour}
          isPreviewVisible={isPreviewVisibleFour}
          handleSendToBackend={handleSendToBackEndFour}
          id="file-upload-four"
          styles={styles}
        />
        <SpreadsheetTable
          spreadsheetData={configurationSpreadsheetDataFour}
          styles={styles}
        />
      </div>

      <div className={styles.tableHolder}>
        <h1 className={styles.title}>Accreditations</h1>
        <FileUploader
          selectedFile={selectedFileFive}
          handleFileUpload={handleFileUploadFive}
          showErrorMessage={showErrorMessageFive}
          isPreviewVisible={isPreviewVisibleFive}
          handleSendToBackend={handleSendToBackEndFive}
          id="file-upload-five"
          styles={styles}
        />
        <SpreadsheetTable
          spreadsheetData={configurationSpreadsheetDataFive}
          styles={styles}
        />
      </div>
    </>
  );
};

export default ConfigurationExcelView;
