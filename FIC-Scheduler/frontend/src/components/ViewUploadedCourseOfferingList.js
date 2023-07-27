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

export function ViewUploadedCourseOfferingList() {
  const [allCourseOfferings, setAllCourseOfferings] = useState(null);
  const [instructorsMap, setInstructorsMap] = useState({});
  const [blocksMap, setBlocksMap] = useState({});

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [courseOfferingsResponse, instructorsResponse, blocksResponse] =
          await Promise.all([
            axios.get("api/course-offerings", tokenConfig()),
            axios.get("api/instructors", tokenConfig()),
            axios.get("api/block-splits", tokenConfig()),
          ]);

        setAllCourseOfferings(courseOfferingsResponse.data);

        //this is how the mapping should look like
        const instructorsData = instructorsResponse.data;
        const instructorsById = instructorsData.reduce((acc, instructor) => {
          acc[instructor.id] = instructor;
          return acc;
        }, {});
        setInstructorsMap(instructorsById);

        //this is similar but gon be for blocks
        const blocksData = blocksResponse.data;
        const blocksById = blocksData.reduce((acc, block) => {
          acc[block.id] = block;
          return acc;
        }, {});
        setBlocksMap(blocksById);
      } catch (error) {
        console.error("Error fetching data:", error);
        setAllCourseOfferings(null);
        setInstructorsMap({});
        setBlocksMap({});
      }
    };

    fetchData();
  }, []);

  const getInstructorName = (instructorId) => {
    return instructorsMap[instructorId]?.name || "N/A";
  };

  const getBlockName = (blockId) => {
    return blocksMap[blockId]?.name || "N/A";
  };

  var data = <h3>No Data</h3>;
  if (
    (allCourseOfferings ?? null) !== null &&
    allCourseOfferings instanceof Array
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
                  Course Number
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
                <TableCell
                  key="4"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Approved Instructors
                </TableCell>
                <TableCell
                  key="5"
                  sx={{
                    fontWeight: "bold",
                    backgroundColor: "#f0f0f0",
                  }}
                >
                  Allowed Block Splits
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {allCourseOfferings.map((row, rowIndex) => (
                <TableRow key={rowIndex}>
                  <TableCell key="0">{row.id}</TableCell>
                  <TableCell key="1">{row.name}</TableCell>
                  <TableCell key="2">{row.courseNumber}</TableCell>
                  <TableCell key="3">{row.notes}</TableCell>
                  <TableCell key="4">
                    {row.approvedInstructors.map((instructor) => (
                      <span key={instructor.id}>
                        {getInstructorName(instructor.id)}
                        <br />
                      </span>
                    ))}
                  </TableCell>
                  <TableCell key="5">
                    {row.allowedBlockSplits.map((block) => (
                      <span key={block.id}>
                        {getBlockName(block.id)}
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
      <h2 className={styles.title}>Current Course Offerings</h2>
      {data}
    </div>
  );
}

export default ViewUploadedCourseOfferingList;
