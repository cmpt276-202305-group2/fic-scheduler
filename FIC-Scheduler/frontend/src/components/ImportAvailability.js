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
            const instructorDataMap = {};
            const duplicateNames = [];

            for (const row of availabilitySpreadsheetData.slice(1)) {
                const instructorName = row[0];
                console.log("Instructor Name:", instructorName);
                console.log("Row:", row);

                // Define the days of the week and parts of the day
                const daysOfWeek = [
                    "MONDAY",
                    "TUESDAY",
                    "WEDNESDAY",
                    "THURSDAY",
                    "FRIDAY",
                ];
                const partsOfDay = ["MORNING", "AFTERNOON", "EVENING"];

                // Get the availability data for the instructor
                const availabilityData = row.slice(1); // Assuming the first element is the instructor's name

                for (let dayIndex = 0; dayIndex < daysOfWeek.length; dayIndex++) {
                    for (let partIndex = 0; partIndex < partsOfDay.length; partIndex++) {
                        const isAvailable =
                            availabilityData[dayIndex * partsOfDay.length + partIndex] === 1;

                        if (isAvailable) {
                            const availabilityEntry = {
                                instructor: instructorName,
                                partOfDay: partsOfDay[partIndex],
                                dayOfWeek: daysOfWeek[dayIndex],
                            };

                            // Check for duplicate names
                            if (instructorDataMap[instructorName]) {
                                instructorDataMap[instructorName].push(availabilityEntry);
                            } else {
                                instructorDataMap[instructorName] = [availabilityEntry];
                            }
                        }
                    }
                }
            }

            // Flatten the instructorDataMap into jsonData array
            jsonData.push(Object.values(instructorDataMap).flat());

            // use these to set data for the instructor and JSONdata
            setInstructors(jsonData);
            setJsonData(jsonData);

            // Check if duplicate names are found and handle the error
            if (duplicateNames.length > 0) {
                // You can display the duplicate names or show an error message to the user
                console.error("Duplicate names found in the file:", duplicateNames);
                return;
            }

            try {
                console.log("Sending data to backend:", jsonData);

                let response = await axios.get(
                    `${process.env.REACT_APP_BACKEND_URL}/api/semester-plans`,
                    tokenConfig()
                );

                console.log("this is get response Response:", response);
                console.log(
                    "this is get response Data.data.latest:",
                    response.data[response.data.length - 1]
                );
                console.log("this is response.data.length:", response.data.length);

                // If the latest semester plan doesn't exist, create a new one
                if (response.data && response.data.length === 0) {
                    const semesterPlan = {
                        id: "",
                        name: "semesterPlan",
                        notes: "some notes",
                        semester: "semesterPlanTest",
                        coursesOffered: [],
                        instructorsAvailable: [],
                        classroomsAvailable: [],
                    };

                    console.log("there was no semesterPlan creating ...");

                    response = await axios.post(
                        `${process.env.REACT_APP_BACKEND_URL}/api/semester-plans`,
                        [semesterPlan],
                        tokenConfig()
                    );
                }

                const thisId = response.data[response.data.length - 1].id;

                // Then update the semester plan with the new instructor availability data
                if (response.status === 200 || response.status === 201) {
                    console.log("we are in the update part ...");
                    const unwrappedData = jsonData[0];
                    console.log("stringified object:", JSON.stringify(unwrappedData));
                    const semesterPlan = {
                        id: thisId.toString(),
                        name: "semesterPlan",
                        notes: "some notes",
                        semester: "semesterPlanTest",
                        coursesOffered: [],
                        instructorsAvailable: unwrappedData,
                        classroomsAvailable: [],
                    };

                    console.log("this is semesterPlan id for update:", semesterPlan.id);
                    response = await axios.post(
                        `${process.env.REACT_APP_BACKEND_URL}/api/semester-plans`,
                        [semesterPlan],
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
