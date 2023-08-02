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

export function ViewUploadedAvailabilityList() {
  const [backendData, setBackendData] = useState(null);
  const partOfDayOptions = ["AM", "EVE", "PM"];
  const partOfDayMap = {
    MORNING: "AM",
    AFTERNOON: "PM",
    EVENING: "EVE",
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [instructorsResponse, semesterPlanResponse] = await Promise.all([
          axios.get("api/instructors", tokenConfig()),
          axios.get("api/semester-plans/latest", tokenConfig()),
        ]);

        const instructorsData = instructorsResponse.data;
        // console.log("instructorsData: ", instructorsData);
        const instructorsById = instructorsData.reduce((acc, instructor) => {
          acc[instructor.id] = instructor;
          return acc;
        }, {});

        const instructorsAvailable =
          semesterPlanResponse.data.instructorsAvailable;
        instructorsAvailable.sort((a, b) => a.instructor.id - b.instructor.id);
        // console.log("instructorsAvailable: ", instructorsAvailable);
        let idCounter = 0;
        const grouped = instructorsAvailable.reduce((acc, item) => {
          const existing = acc.find(
            (i) => i.instructorId === item.instructor.id
          );
          if (existing) {
            existing.availabilities.push({
              dayOfWeek: item.dayOfWeek,
              partOfDay: item.partOfDay,
            });
          } else {
            acc.push({
              id: ++idCounter,
              instructorId: item.instructor.id,
              instructorName:
                instructorsById[item.instructor.id]?.name || "N/A",
              availabilities: [
                { dayOfWeek: item.dayOfWeek, partOfDay: item.partOfDay },
              ],
            });
          }
          return acc;
        }, []);

        // console.log("grouped: ", grouped);
        setBackendData(grouped);
      } catch (error) {
        console.error("Error fetching data:", error);
        setBackendData(null);
      }
    };

    fetchData();
  }, []);

  var data = (
    <div className={styles.displayNullData}>
      No current availability, please upload an availabilities file.
    </div>
  );
  if ((backendData ?? null) !== null && backendData instanceof Array) {
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
                  key="id"
                  sx={{ fontWeight: "bold", backgroundColor: "#f0f0f0" }}
                >
                  Id
                </TableCell>
                <TableCell
                  key="instructor"
                  sx={{ fontWeight: "bold", backgroundColor: "#f0f0f0" }}
                >
                  Instructor
                </TableCell>
                {["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"].map(
                  (day) =>
                    partOfDayOptions.map((partOfDay) => (
                      <TableCell
                        key={`${day}-${partOfDay}`}
                        sx={{ fontWeight: "bold", backgroundColor: "#f0f0f0" }}
                      >
                        {`${day} - ${partOfDay}`}
                      </TableCell>
                    ))
                )}
              </TableRow>
            </TableHead>
            <TableBody>
              {backendData.map((row, rowIndex) => {
                const availabilities = {};
                [
                  "MONDAY",
                  "TUESDAY",
                  "WEDNESDAY",
                  "THURSDAY",
                  "FRIDAY",
                ].forEach((day) =>
                  partOfDayOptions.forEach((partOfDay) => {
                    availabilities[`${day} - ${partOfDay}`] = "-";
                  })
                );

                row.availabilities.forEach((avail) => {
                  const partOfDayDisplay =
                    partOfDayMap[avail.partOfDay.toUpperCase()];
                  const time = `${avail.dayOfWeek.toUpperCase()} - ${partOfDayDisplay}`;
                  availabilities[time] = "Available";
                });

                return (
                  <TableRow key={rowIndex}>
                    <TableCell key="id">{row.instructorId}</TableCell>
                    <TableCell key="instructor">{row.instructorName}</TableCell>
                    {Object.entries(availabilities).map(
                      ([time, availability], i) => (
                        <TableCell key={`${time}-${i}`}>
                          {availability}
                        </TableCell>
                      )
                    )}
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    );
  }
  return (
    <div>
      <h2 className={styles.title}>Current Instructor List</h2>
      {data}
    </div>
  );
}

export default ViewUploadedAvailabilityList;
