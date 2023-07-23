import React, { useState } from "react";
import axios from "axios";
import readExcelFile from "./readExcelfile";
import styles from "./FileImport.module.css";
import { tokenConfig } from "../utils";
import { FileUploader, SpreadsheetTable } from "./FileUploader";

const ImportCourses = ({ coursesSpreadsheetData, setCoursesSpreadsheetData }) => {
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
        setCoursesSpreadsheetData,
        setIsPreviewVisible
    );

    const handleSendToBackEnd = async () => {
        if (coursesSpreadsheetData.length > 0) {
            // Convert data to JSON format
            const jsonData = coursesSpreadsheetData.map((row) => {
                return {
                    courseNumber: row.courseNumber,
                    semesterPlanID: row.semesterPlanId,
                    accredationRequiredName: row.accredationRequiredName,
                    blockTypeName: row.blockTypeName,
                    facilitesRequiredNames: [
                        row.facilitesRequiredNames1,
                        row.facilitesRequiredNames2,
                        row.facilitesRequiredNames3,
                    ].filter(Boolean),
                    conflictedCourseNumbers: [
                        row.conflictedCourseNumbers1,
                        row.conflictedCourseNumbers2,
                        row.conflictedCourseNumbers3,
                    ].filter(Boolean),
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

    return (
        <div className={styles.tableHolder}>
            <h2 className={styles.title}>Course Offering</h2>
            <FileUploader
                selectedFile={selectedFile}
                handleFileUpload={handleFileUpload}
                showErrorMessage={showErrorMessage}
                isPreviewVisible={isPreviewVisible}
                handleSendToBackend={handleSendToBackEnd}
                id="file-upload-one"
                styles={styles}
            />
            <SpreadsheetTable
                spreadsheetData={coursesSpreadsheetData}
                styles={styles}
            />
        </div>
    );
};

export default ImportCourses;