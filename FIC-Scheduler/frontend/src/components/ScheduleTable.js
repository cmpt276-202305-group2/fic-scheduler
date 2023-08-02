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
    const [instructorsMap, setInstructorsMap] = useState({});
    const [classroomMap, setClassroomMap] = useState({});
    const [courseOfferingMap, setCourseOfferingMap] = useState({});
    useEffect(() => {
        const fetchData = async () => {
            try {
                const [scheduleResponse, instructorsResponse, classroomResponse, courseOfferingResponse] = await Promise.all([
                    axios.get("api/schedules", tokenConfig()),
                    axios.get("api/instructors", tokenConfig()),
                    axios.get("api/classrooms", tokenConfig()),
                    axios.get("api/course-offerings", tokenConfig()),
                ]);
                setScheduleData(scheduleResponse.data);
                const instructorsData = instructorsResponse.data;
                const instructorsById = instructorsData.reduce((acc, instructor) => {
                    acc[instructor.id] = instructor;
                    return acc;
                }, {});
                setInstructorsMap(instructorsById);

                const classroomData = classroomResponse.data;
                const classroomById = classroomData.reduce((acc, classroom) => {
                    acc[classroom.id] = classroom;
                    return acc;
                }, {});
                setClassroomMap(classroomById);

                const courseOfferingData = courseOfferingResponse.data;
                const courseOfferingById = courseOfferingData.reduce((acc, courseOffering) => {
                    acc[courseOffering.id] = courseOffering;
                    return acc;
                }, {});
                setCourseOfferingMap(courseOfferingById);

            } catch (error) {
                console.error("Error fetching schedule data:", error);
                setScheduleData(null);
                setInstructorsMap({});
                setClassroomMap({});
                setCourseOfferingMap({});
            }
        };

        fetchData();
    }, []);


    const getInstructorName = (instructorId) => {
        return instructorsMap[instructorId]?.name || "N/A";
    };

    const getClassroomNumber = (classroomId) => {
        return classroomMap[classroomId]?.roomNumber || "N/A";
    };

    const getCourseOfferingName = (courseOfferingId) => {
        return courseOfferingMap[courseOfferingId]?.name || "N/A";
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
            return courseObj.blocks.map((blockObj) => {
                return {
                    courses: courseObj.course,
                    instructor: blockObj.instructor,
                    classroom: blockObj.classroom,
                    day: blockObj.day,
                    time: blockObj.time,
                };
            });
        });
    });

    const transformDataForCSV = () => {
        return combinedData.map((row) => ({
            courses: getCourseOfferingName(row.courses.id),
            instructor: getInstructorName(row.instructor.id),
            classroom: getClassroomNumber(row.classroom.id),
            day: row.day,
            time: row.time,
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
                                <TableCell
                                    key="0"
                                    sx={{
                                        fontWeight: "bold",
                                        backgroundColor: "#f0f0f0",
                                    }}
                                >
                                    Course
                                </TableCell>
                                <TableCell
                                    key="1"
                                    sx={{
                                        fontWeight: "bold",
                                        backgroundColor: "#f0f0f0",
                                    }}
                                >
                                    Instructor
                                </TableCell>
                                <TableCell
                                    key="2"
                                    sx={{
                                        fontWeight: "bold",
                                        backgroundColor: "#f0f0f0",
                                    }}
                                >
                                    Classroom
                                </TableCell>
                                <TableCell
                                    key="3"
                                    sx={{
                                        fontWeight: "bold",
                                        backgroundColor: "#f0f0f0",
                                    }}
                                >
                                    Day
                                </TableCell>
                                <TableCell
                                    key="4"
                                    sx={{
                                        fontWeight: "bold",
                                        backgroundColor: "#f0f0f0",
                                    }}
                                >
                                    Time
                                </TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {combinedData.map((row, index) => (
                                <TableRow key={index}>
                                    <TableCell key="0">
                                        {getCourseOfferingName(row.courses.id)}
                                    </TableCell>
                                    <TableCell key="1">
                                        {getInstructorName(row.instructor.id)}
                                    </TableCell>
                                    <TableCell key="2">
                                        {getClassroomNumber(row.classroom.id)}
                                    </TableCell>
                                    <TableCell key="3">{row.day}</TableCell>
                                    <TableCell key="4">{row.time}</TableCell>
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