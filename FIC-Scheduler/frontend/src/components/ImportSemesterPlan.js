import React, { useState, useEffect } from "react";

import axios from "axios";

import styles from "./FileImport.module.css";
import { tokenConfig } from "../utils";
import { SpreadsheetTable } from "./FileUploader";

const ImportSemesterPlan = () => {
  const [coursesData, setCoursesData] = useState([]);
  const [instructorsData, setInstructorsData] = useState([]);
  const [classroomsData, setClassroomsData] = useState([]);
  const [semesterPlanData, setSemesterPlanData] = useState([]);

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
        axios
          .get(`${process.env.REACT_APP_BACKEND_URL}/api/avaliability`)
          .then((response) => {
            const filteredAvaliabilityData = response.data.map((row) => {
              // Check if the instructor ID exists in filteredInstructorData
              const instructorID = filteredInstructorData.includes(
                row.instructorID
              )
                ? row.instructorID
                : null;

              return {
                partofDay: row.partOfDay,
                time: row.time,
                instructorID: instructorID,
              };
            });
            setClassroomsData(filteredAvaliabilityData);
          })
          .catch((error) => {
            console.error("Error fetching avaliability data:", error);
          });
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

    // In this example, let's assume semester plan data is already available or fetched from elsewhere.
    // You can replace this with the actual logic to fetch the semester plan data if needed.
    const exampleSemesterPlanData = [
      {
        name: "Semester Plan 1",
        notes: "Example notes for Semester Plan 1",
        semesterID: 1,
      },
      {
        name: "Semester Plan 2",
        notes: "Example notes for Semester Plan 2",
        semesterID: 2,
      },
      // Add more semester plan data objects as needed
    ];

    setSemesterPlanData(exampleSemesterPlanData);
  }, []);

  const handleSendToBackEnd = async () => {
    if (semesterPlanData.length > 0) {
      // Convert data to JSON format and include ID fields from coursesData, instructorsData, and classroomsData
      const jsonData = semesterPlanData.map((item) => ({
        name: item.name,
        notes: item.notes,
        semesterID: item.semesterID,
        coursesOffered: coursesData,
        instructorsAvailable: instructorsData,
        classroomsAvailable: classroomsData,
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

      <SpreadsheetTable spreadsheetData={semesterPlanData} styles={styles} />
      <button onClick={handleSendToBackEnd}>Send Data to Backend</button>
    </div>
  );
};

export default ImportSemesterPlan;
