import React, { useState } from "react";
import axios from "axios";
import readExcelFile from "./readExcelfile";
import styles from "./FileImport.module.css";
import { tokenConfig } from "../utils";
import { FileUploader, SpreadsheetTable } from "./FileUploader";

const ImportClassroom = ({ classroomSpreadsheetData, setClassroomSpreadsheetData }) => {
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
        setClassroomSpreadsheetData,
        setIsPreviewVisible
    );

    const handleSendToBackEnd = async () => {
        if (classroomSpreadsheetData.length > 0) {
            const jsonData = classroomSpreadsheetData.map((row) => {
                return {
                    roomNumber: row.roomNumber,
                    facilitesAvaliableNames: [
                        row.facilitesAvaliableNames1,
                        row.facilitesAvaliableNames2,
                        row.facilitesAvaliableNames3,
                        row.facilitesAvaliableNames4,
                        row.facilitesAvaliableNames5,
                        row.facilitesAvaliableNames6,
                        row.facilitesAvaliableNames7,
                        row.facilitesAvaliableNames8,
                        row.facilitesAvaliableNames9,
                    ].filter(Boolean),
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

    return (
        <div className={styles.tableHolder}>
            <h2 className={styles.title}>Classrooms</h2>
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
                spreadsheetData={classroomSpreadsheetData}
                styles={styles}
            />
        </div>
    );
};

export default ImportClassroom;