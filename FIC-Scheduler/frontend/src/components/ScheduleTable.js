import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import axios from "axios";
import { CSVLink } from "react-csv";
import Button from "@mui/material/Button";
import React, { useEffect, useState } from "react";
import { tokenConfig } from "../utils";

export function ScheduleTable() {
    const [scheduleData, setScheduleData] = useState(null);
    const [blockSplitDataMap, setBlockSplitDataMap] = useState({});
    const [instructorsMap, setInstructorsMap] = useState({});
    useEffect(() => {
        const fetchData = async () => {
            try {
                const [scheduleResponse, instructorsResponse, blockSplitResponse] = await Promise.all([
                    axios.get("api/schedules", tokenConfig()),
                    axios.get("api/instructors", tokenConfig()),
                    axios.get("api/block-splits", tokenConfig()),
                ]);
                setScheduleData(scheduleResponse.data);

                const instructorsData = instructorsResponse.data;
                const instructorsById = instructorsData.reduce((acc, instructor) => {
                    acc[instructor.id] = instructor;
                    return acc;
                }, {});
                setInstructorsMap(instructorsById);

                const blockSplitData = blockSplitResponse.data;
                const blockSplitDataById = blockSplitData.reduce((acc, blockSplit) => {
                    acc[blockSplit.id] = blockSplit;
                    return acc;
                }, {});
                setBlockSplitDataMap(blockSplitDataById);
            } catch (error) {
                console.error("Error fetching schedule data:", error);
                setScheduleData(null);
                setInstructorsMap({});
                setBlockSplitDataMap({});
            }
        };

        fetchData();
    }, []);

    // useEffect(() => {
    //     const fetchBlockSplitData = async (blockSplitIds) => {
    //         const requests = blockSplitIds.map((id) =>
    //             axios.get(`api/block-splits/${id.id}`, tokenConfig())
    //         );

    //         try {
    //             const responses = await axios.all(requests);
    //             const blockSplitData = responses.map((response) => response.data);

    //             // Create a map of block split IDs to their corresponding data
    //             const blockSplitDataMap = {};
    //             blockSplitData.forEach((blockSplit, index) => {
    //                 blockSplitDataMap[blockSplitIds[index].id] = blockSplit;
    //             });

    //             setBlockSplitDataMap(blockSplitDataMap);
    //         } catch (error) {
    //             console.error("Error fetching block split data:", error);
    //             setBlockSplitDataMap({});
    //         }
    //     };

    //     if (scheduleData) {
    //         const blockSplitIds = scheduleData.flatMap((row) =>
    //             row.courses.flatMap((courseObj) => courseObj.course.allowedBlockSplits)
    //         );

    //         fetchBlockSplitData(blockSplitIds);
    //     }
    // }, [scheduleData]);

    const getInstructorName = (instructorId) => {
        return instructorsMap[instructorId]?.name || "N/A";
    };

    const getBlockSplitName = (blockSplitId) => {
        return blockSplitDataMap[blockSplitId]?.name || "N/A";
    };

    if (!scheduleData) {
        return (
            <div data-testid="schedule">
                <div>Loading...</div>
            </div>
        );
    }

    const combinedData = scheduleData.flatMap((row) => {
        return row.courses.flatMap((courseObj) => {
            // const allowedBlockSplits = courseObj.course.allowedBlockSplits.map(
            //     (blockId) => {
            //         // Check if blockId exists in blockSplitDataMap
            //         const blockSplit = blockSplitDataMap[blockId.id];

            //         return blockSplit ? blockSplit.name : "";
            //     }
            // );

            return {
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
            };
        });
    });
    const transformDataForCSV = () => {
        return combinedData.map((row) => ({
            id: row.id,
            name: row.name,
            semester: row.semester,
            courseId: row.courseId,
            courseName: row.courseName,
            courseNumber: row.courseNumber,
            courseNotes: row.courseNotes,
            approvedInstructors: row.approvedInstructors.map((instructor) => getInstructorName(instructor.id)).join(", "),
            allowedBlockSplits: row.allowedBlockSplits.map((blockSplit) => getBlockSplitName(blockSplit.id)).join(", "),
            blocks: JSON.stringify(row.blocks),
            notes: row.notes,
        }));
    };
    return (
        <div
            data-testid="schedule"
            style={{ display: "flex", flexDirection: "column", alignItems: "flex-start" }}
        >
            <Paper sx={{ width: "100%", overflow: "hidden" }}>
                <TableContainer
                    sx={{ width: "100%", maxHeight: window.innerWidth >= 600 ? 700 : 350 }}
                >
                    <Table stickyHeader aria-label="sticky table">
                        <TableHead>
                            <TableRow>
                                <TableCell key="0">ID</TableCell>
                                <TableCell key="1">Name</TableCell>
                                <TableCell key="3">Semester</TableCell>
                                <TableCell key="5">Course Name</TableCell>
                                <TableCell key="7">Course Notes</TableCell>
                                <TableCell key="8">Approved Instructors</TableCell>
                                <TableCell key="9">Allowed Block Splits</TableCell>
                                <TableCell key="10">Blocks</TableCell>
                                <TableCell key="2">Notes</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {combinedData.map((row, index) => (
                                <TableRow key={index}>
                                    <TableCell key="0">{row.id}</TableCell>
                                    <TableCell key="1">{row.name}</TableCell>
                                    <TableCell key="3">{row.semester}</TableCell>
                                    <TableCell key="5">{row.courseName}</TableCell>
                                    <TableCell key="7">{row.courseNotes}</TableCell>
                                    <TableCell key="8">
                                        {row.approvedInstructors.map((instructor) => (
                                            <div key={instructor.id}>
                                                {getInstructorName(instructor.id)}
                                            </div>
                                        ))}
                                    </TableCell>
                                    <TableCell key="9">
                                        {row.allowedBlockSplits.map((blockSplit) => (
                                            <div key={blockSplit.id}>
                                                {getBlockSplitName(blockSplit.id)}
                                            </div>
                                        ))}
                                    </TableCell>
                                    <TableCell key="10">{JSON.stringify(row.blocks)}</TableCell>
                                    <TableCell key="2">{row.notes}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
            <label htmlFor="download-table">
                <CSVLink
                    id="download-table"
                    data={transformDataForCSV()}
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
}

export default ScheduleTable;