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
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
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
    if (availabilitySpreadsheetData.length === 0) {
      return;
    }

    // Define the days of the week and parts of the day
    const daysOfWeek = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"];
    const partsOfDay = ["MORNING", "AFTERNOON", "EVENING"];

    const instructorAvailabilities = [];

    for (const row of availabilitySpreadsheetData.slice(1)) {
      const instructorName = row[0];
      console.log("Instructor Name:", instructorName);
      console.log("Row:", row);

      // Get the availability data for the instructor
      const availabilityData = row.slice(1); // Assuming the first element is the instructor's name

      for (let dayIndex = 0; dayIndex < daysOfWeek.length; dayIndex++) {
        for (let partIndex = 0; partIndex < partsOfDay.length; partIndex++) {
          const isAvailable =
            availabilityData[dayIndex * partsOfDay.length + partIndex] === 1;

          if (isAvailable) {
            instructorAvailabilities.push({
              name: instructorName.trim(),
              partOfDay: partsOfDay[partIndex].trim(),
              dayOfWeek: daysOfWeek[dayIndex].trim(),
            });
          }
        }
      }
    }

    try {
      console.log("Sending data to backend:", instructorAvailabilities);

      let response = null;

      // GET the existing instructors
      response = await axios.get("api/instructors", tokenConfig());

      // Figure out which instructors we're creating/updating and which we're deleting
      const instructors = new Map();
      for (const v of response.data) {
        instructors.set(v.name, v);
      }
      console.log("Instructors NOW: ", instructors);
      const staleInstructors = new Map(instructors);
      const instructorsToUpdate = [];
      for (const ia of instructorAvailabilities) {
        if (!instructors.has(ia.name)) {
          let newInstructor = { id: null, name: ia.name, notes: "" };
          instructors.set(ia.name, newInstructor);
          instructorsToUpdate.push(newInstructor);
        }
        if (staleInstructors.has(ia.name)) {
          staleInstructors.delete(ia.name);
        }
      }
      const instructorsToDelete = [];
      for (const instructor of staleInstructors.values()) {
        instructorsToDelete.push({ id: instructor.id });
        instructors.delete(instructor.name);
      }
      // "instructors" now contains the new, minimized list of instructors, mapped by name

      console.log("Instructors to delete:", instructorsToDelete);
      // Send the instructor delete list
      if (instructorsToDelete.length > 0) {
        response = await axios.delete(
          "api/instructors",
          instructorsToDelete,
          tokenConfig()
        );
      }

      console.log("Instructors to create/update:", instructorsToUpdate);
      // Send the instructor update/create list
      if (instructorsToUpdate.length > 0) {
        response = await axios.post(
          "api/instructors",
          instructorsToUpdate,
          tokenConfig()
        );
        for (let instructor of response.data) {
          console.log("Replacing " + instructor.name + " with:", instructor);
          instructors.set(instructor.name, instructor);
        }
      }

      let delayPromise = new Promise((resolve) => setTimeout(resolve, 100));
      await delayPromise;

      // GET the existing semester plan if there is one
      response = await axios.get("api/semester-plans", tokenConfig());

      console.log("this is get response Response:", response);
      console.log(
        "this is get response Data.data.latest:",
        response.data[response.data.length - 1]
      );
      console.log("this is response.data.length:", response.data.length);

      // If the latest semester plan doesn't exist, create a new one
      if (response.data && response.data.length === 0) {
        const semesterPlan = {
          id: null,
          name: "semesterPlan",
          notes: "some notes",
          semester: "semesterPlanTest",
          coursesOffered: [],
          instructorsAvailable: [],
          classroomsAvailable: [],
          courseCorequisites: [],
          instructorSchedulingRequests: [],
        };

        console.log("there was no semesterPlan creating ...");

        response = await axios.post(
          "api/semester-plans",
          [semesterPlan],
          tokenConfig()
        );
      }

      const thisId = response.data[response.data.length - 1].id;

      // Then update the semester plan with the new instructor availability data
      const semesterPlan = {
        id: thisId,
        name: "semesterPlan",
        notes: "some notes",
        semester: "semesterPlanTest",
        coursesOffered: [],
        instructorsAvailable: instructorAvailabilities.map((ia) => {
          return {
            instructor: {
              id: instructors.get(ia.name).id,
            },
            dayOfWeek: ia.dayOfWeek,
            partOfDay: ia.partOfDay,
          };
        }),
        classroomsAvailable: [],
        courseCorequisites: [],
        instructorSchedulingRequests: [],
      };

      console.log("this is semesterPlan for update:", semesterPlan);
      response = await axios.post(
        "api/semester-plans",
        [semesterPlan],
        tokenConfig()
      );

      if (response.status === 200 || response.status === 201) {
        console.log("Instructor data upload successful:", response.data);
        setShowSuccessMessage(true);
      }
    } catch (error) {
      console.error("Error sending data to backend:", error);
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
        id={"2"}
        styles={styles}
      />
      {showSuccessMessage && (
        <h2 className={styles.successMessage}>
          List of instructors, and thier availability, successfully uploaded!
        </h2>
      )}
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
