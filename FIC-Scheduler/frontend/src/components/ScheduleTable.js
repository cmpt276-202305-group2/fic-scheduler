import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";

import Paper from "@mui/material/Paper";

import axios from "axios";
import React, { useEffect, useState } from "react";



import { tokenConfig } from "../utils"

export function ScheduleTable() {
    const [scheduleData, setScheduleData] = useState(null);

    useEffect(() => {
        axios.get("api/schedules", tokenConfig()).then(
            (response) => { setScheduleData(response.data); },
            (_) => { setScheduleData(null); });
    }, [setScheduleData]);
    if (!scheduleData) {
        return (
            <div data-testid="schedule">
                <div>No schedules generated!</div>
            </div>
        );
    }



    // // Function to fetch block split names by IDs
    // const fetchBlockSplitNames = (blockSplitIds) => {
    //     const requests = blockSplitIds.map((id) => axios.get(`api/block-splits/${id}`, tokenConfig()));
    //     axios.all(requests)
    //         .then(axios.spread((...responses) => {
    //             const blockSplitData = responses.map((response) => response.data);
    //             // Create an object with block split names using the IDs as keys
    //             const blockSplitNamesObject = {};
    //             blockSplitData.forEach((blockSplit) => {
    //                 blockSplitNamesObject[blockSplit.id] = blockSplit.name;
    //             });
    //             setBlockSplitNames(blockSplitNamesObject);
    //         }))
    //         .catch((error) => {
    //             console.error("Error fetching block split names:", error);
    //         });
    // };

    // Combine the scheduleData with csvData
    const combinedData = scheduleData.flatMap((row) => {
        return row.courses.map((courseObj) => ({
            id: row.id,
            name: row.name,
            notes: row.notes,
            semester: row.semester,
            courseId: courseObj.course.id,
            courseName: courseObj.course.name,
            courseNumber: courseObj.course.courseNumber,
            courseNotes: courseObj.course.notes,
            approvedInstructors: courseObj.course.approvedInstructors,
            allowedBlockSplits: courseObj.course.allowedBlockSplits,
            blocks: courseObj.blocks,
        }));
    });

    var data = (<div>No schedules</div>);
    if (((scheduleData ?? null) !== null) && (scheduleData instanceof Array)) {
        data = (
            <div data-testid="schedule" style={{ display: "flex", flexDirection: "column", alignItems: "flex-start" }}>
                <Paper sx={{ width: "100%", overflow: "hidden" }}>
                    <TableContainer sx={{ width: "100%", maxHeight: window.innerWidth >= 600 ? 700 : 350 }}>
                        <Table stickyHeader aria-label="sticky table">
                            <TableHead>
                                <TableRow>
                                    <TableCell key="0">ID</TableCell>
                                    <TableCell key="1">Name</TableCell>
                                    <TableCell key="2">Notes</TableCell>
                                    <TableCell key="3">Semester</TableCell>
                                    <TableCell key="5">Course Name</TableCell>
                                    <TableCell key="7">Course Notes</TableCell>
                                    <TableCell key="8">Approved Instructors</TableCell>
                                    <TableCell key="9">Allowed Block Splits</TableCell>
                                    <TableCell key="10">Blocks</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {combinedData.map((row, index) => (
                                    <TableRow key={index}>
                                        <TableCell key="0">{row.id}</TableCell>
                                        <TableCell key="1">{row.name}</TableCell>
                                        <TableCell key="2">{row.notes}</TableCell>
                                        <TableCell key="3">{row.semester}</TableCell>
                                        <TableCell key="5">{row.courseName}</TableCell>
                                        <TableCell key="7">{row.courseNotes}</TableCell>
                                        <TableCell key="8">{JSON.stringify(row.approvedInstructors)}</TableCell>
                                        <TableCell key="9">{JSON.stringify(row.allowedBlockSplits)}</TableCell>
                                        <TableCell key="10">{JSON.stringify(row.blocks)}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Paper>
            </div>

        );
    }
    return (
        <div>{data}</div>
    );
}

export default ScheduleTable;
