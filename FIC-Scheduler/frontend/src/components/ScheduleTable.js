import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Button from "@mui/material/Button";
import Paper from "@mui/material/Paper";
import styles from "./ExcelViewer.module.css";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { CSVLink } from "react-csv";


import { tokenConfig } from "../utils"

export function ScheduleTable() {
  const [fetchResult, setFetchResult] = useState(null);

  useEffect(() => {
    axios.get("api/schedules/latest", tokenConfig()).then(
      (response) => { setFetchResult(response.data); },
      (_) => { setFetchResult(null); });
  }, [setFetchResult]);

  if ((fetchResult === null) || !(fetchResult.classScheduleAssignments instanceof Array)) {
    return (
      <div data-testid="schedule">
        <div>No schedules generated!</div>
      </div>);
  }

  const csvData = fetchResult.classScheduleAssignments.map(row => ({
    id: row.id,
    courseNumber: row.courseNumber,
    partOfDay: row.partOfDay,
    classroom: row.classroom.roomNumber,
    instructor: row.instructor.name,
  }));

  return (
    <div
      data-testid="schedule"
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "flex-start",
      }}>
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
                <TableCell className={styles.cell} key="0">ID</TableCell>
                <TableCell className={styles.cell} key="1">Course Number</TableCell>
                <TableCell className={styles.cell} key="2">Time</TableCell>
                <TableCell className={styles.cell} key="3">Classroom</TableCell>
                <TableCell className={styles.cell} key="4">Instructor</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {fetchResult.classScheduleAssignments.map((row, rowIndex) => (
                <TableRow key={rowIndex}>
                  <TableCell key="0">{row.id}</TableCell>
                  <TableCell key="1">{row.courseNumber}</TableCell>
                  <TableCell key="2">{row.partOfDay}</TableCell>
                  <TableCell key="3">{row.classroom.roomNumber}</TableCell>
                  <TableCell key="4">{row.instructor.name}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
      <label htmlFor="download-table">
        <CSVLink
          id="download-table"
          data={csvData}
          filename={"schedule.csv"}
          className="btn btn-primary"
          target="_blank"
        >
          <Button
            id="upload-table"
            variant="contained"
            color="primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              "&:hover": { backgroundColor: "#417A1A" },
            }}
            style={{ marginTop: 15 }}
          >
            Download Table
          </Button>
        </CSVLink>
      </label>
    </div>
  );
};

export default ScheduleTable;
