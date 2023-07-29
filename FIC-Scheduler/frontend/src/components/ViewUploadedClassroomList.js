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

export function ViewUploadedClassroomList() {
  const [allClassrooms, setAllClassrooms] = useState(null);

  useEffect(() => {
    axios.get("api/classrooms", tokenConfig()).then(
      (response) => {
        setAllClassrooms(response.data);
      },
      (_) => {
        setAllClassrooms(null);
      }
    );
  }, [setAllClassrooms]);

  var data = <h3>There is no classroom in the system please insert one</h3>;
  if (
    (allClassrooms ?? null) !== null &&
    allClassrooms instanceof Array &&
    allClassrooms.length > 0
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
                  Room Number
                </TableCell>
                <TableCell
                  key="2"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Room Type
                </TableCell>
                <TableCell
                  key="3"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Notes
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {allClassrooms.map((row, rowIndex) => (
                <TableRow key={rowIndex}>
                  <TableCell key="0">{row.id}</TableCell>
                  <TableCell key="1">{row.roomNumber}</TableCell>
                  <TableCell key="2">{row.roomType}</TableCell>
                  <TableCell key="3">{row.notes}</TableCell>
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
      <h2 className={styles.title}>Current Classroom List</h2>
      {data}
    </div>
  );
}

export default ViewUploadedClassroomList;
