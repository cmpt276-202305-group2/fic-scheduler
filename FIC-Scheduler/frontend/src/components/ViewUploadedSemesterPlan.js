import axios from "axios";
import React, { useEffect, useState } from "react";

import Paper from "@mui/material/Paper";
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import styles from "./FileImport.module.css";

import { tokenConfig } from "../utils";

export function ViewUploadedSemesterPlan() {
  const [allSemesterPlans, setAllSemesterPlans] = useState(null);
  const [courseOfferedMap, setCourseOfferedMap] = useState({});
  const [instructorsMap, setInstructorsMap] = useState({});
  const [classroomAvailableMap, setClassroomAvailableMap] = useState({});

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [
          allSemesterPlansResponse,
          courseOfferedResponse,
          classroomAvailableResponse,
          instructorsResponse,
        ] = await Promise.all([
          axios.get("api/semester-plans", tokenConfig()),
          axios.get("api/course-offerings", tokenConfig()),
          axios.get("api/classrooms", tokenConfig()),
          axios.get("api/instructors", tokenConfig()),
        ]);
        setAllSemesterPlans(allSemesterPlansResponse.data);

        const courseOfferedData = courseOfferedResponse.data;
        const courseOfferedById = courseOfferedData.reduce(
          (acc, courseOffering) => {
            acc[courseOffering.id] = courseOffering;
            return acc;
          },
          {}
        );
        setCourseOfferedMap(courseOfferedById);

        const classroomAvailableData = classroomAvailableResponse.data;
        const classroomAvailableById = classroomAvailableData.reduce(
          (acc, classroom) => {
            acc[classroom.id] = classroom;
            return acc;
          },
          {}
        );
        setClassroomAvailableMap(classroomAvailableById);

        const instructorsData = instructorsResponse.data;
        const instructorsById = instructorsData.reduce((acc, instructor) => {
          acc[instructor.id] = instructor;
          return acc;
        }, {});
        setInstructorsMap(instructorsById);
      } catch (error) {
        console.error("Error fetching data:", error);
        setAllSemesterPlans(null);
        setCourseOfferedMap({});
        setClassroomAvailableMap({});
      }
    };

    fetchData();
  }, []);

  const getCourseOfferedName = (courseOfferedId) => {
    return courseOfferedMap[courseOfferedId]?.courseNumber || "N/A";
  };

  const getClassroomAvailableName = (classroomAvailableId) => {
    return classroomAvailableMap[classroomAvailableId]?.roomNumber || "N/A";
  };

  const getInstructorName = (instructorId) => {
    return instructorsMap[instructorId]?.name || "N/A";
  };

  const renderInstructorAvailability = (instructorsAvailable) => {
    const instructorAvailability = {};

    instructorsAvailable.forEach((availability) => {
      const { instructor } = availability;
      const instructorId = instructor.id;

      if (instructorAvailability[instructorId]) {
        instructorAvailability[instructorId].push(availability);
      } else {
        instructorAvailability[instructorId] = [availability];
      }
    });

    Object.keys(instructorAvailability).forEach((instructorId) => {
      const availabilities = instructorAvailability[instructorId];
      availabilities.sort((a, b) => {
        const daysOfWeek = [
          "MONDAY",
          "TUESDAY",
          "WEDNESDAY",
          "THURSDAY",
          "FRIDAY",
          "SATURDAY",
          "SUNDAY",
        ];
        const dayA = daysOfWeek.indexOf(a.dayOfWeek);
        const dayB = daysOfWeek.indexOf(b.dayOfWeek);
        if (dayA < dayB) return -1;
        if (dayA > dayB) return 1;

        const partsOfDay = ["MORNING", "AFTERNOON", "EVENING"];
        const partA = partsOfDay.indexOf(a.partOfDay);
        const partB = partsOfDay.indexOf(b.partOfDay);
        return partA - partB;
      });
    });

    return Object.keys(instructorAvailability).map((instructorId, index) => {
      const availabilities = instructorAvailability[instructorId];
      const instructorName = getInstructorName(instructorId);

      return (
        <div key={`instructor-availability-${index}`}>
          <strong>Instructor:</strong> {instructorName} <br />
          {availabilities.map((availability, i) => (
            <React.Fragment key={`availability-${i}`}>
              <strong>Day:</strong> {availability.dayOfWeek} <br />
              <strong>Time:</strong> {availability.partOfDay} <br />
              <hr />
            </React.Fragment>
          ))}
        </div>
      );
    });
  };

  var data = <h3>No current plan please upload all the parts first</h3>;
  if (
    (allSemesterPlans ?? null) !== null &&
    allSemesterPlans instanceof Array
  ) {
    data = (
      <Paper sx={{ width: "100%", overflow: "hidden" }}>
        <TableContainer
          sx={{
            width: "100%",
            maxHeight: window.innerWidth >= 600 ? 700 : 350,
          }}
        >
          <Table stickyHeader aria-label="sticky table">
            <TableHead>
              <TableRow>
                <TableCell
                  key="0"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Id
                </TableCell>
                <TableCell
                  key="1"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Name
                </TableCell>
                <TableCell
                  key="2"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Notes
                </TableCell>
                <TableCell
                  key="3"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Semester
                </TableCell>
                <TableCell
                  key="4"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Courses Offered
                </TableCell>
                <TableCell
                  key="5"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Instructor Available
                </TableCell>
                <TableCell
                  key="6"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Classrooms Available
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {allSemesterPlans.map((row, rowIndex) => (
                <TableRow key={rowIndex}>
                  <TableCell
                    key="0"
                    sx={{ verticalAlign: "top", padding: "10px" }}
                  >
                    {row.id}
                  </TableCell>
                  <TableCell
                    key="1"
                    sx={{ verticalAlign: "top", padding: "10px" }}
                  >
                    {row.name}
                  </TableCell>
                  <TableCell
                    key="2"
                    sx={{ verticalAlign: "top", padding: "10px" }}
                  >
                    {row.notes}
                  </TableCell>
                  <TableCell
                    key="3"
                    sx={{ verticalAlign: "top", padding: "10px" }}
                  >
                    {row.semester}
                  </TableCell>
                  <TableCell
                    key="4"
                    sx={{ verticalAlign: "top", padding: "10px" }}
                  >
                    {row.coursesOffered.map((courseOffered) => (
                      <span key={courseOffered.id}>
                        {getCourseOfferedName(courseOffered.id)}
                        <br />
                        <br />
                      </span>
                    ))}
                  </TableCell>
                  <TableCell
                    key={`instructors-${rowIndex}`}
                    sx={{ verticalAlign: "top", padding: "10px" }}
                  >
                    {renderInstructorAvailability(row.instructorsAvailable)}
                  </TableCell>
                  <TableCell
                    key="6"
                    sx={{ verticalAlign: "top", padding: "10px" }}
                  >
                    {row.classroomsAvailable.map((classroom) => (
                      <span key={classroom.id}>
                        {getClassroomAvailableName(classroom.id)}
                        <br />
                        <br />
                      </span>
                    ))}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    );
  }

  return (
    <div>
      <h2 className={styles.title}>Current Semester Plan</h2>
      {data}
    </div>
  );
}

export default ViewUploadedSemesterPlan;
