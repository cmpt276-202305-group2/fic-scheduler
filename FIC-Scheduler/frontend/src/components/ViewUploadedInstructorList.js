import axios from 'axios';
import React, { useEffect, useState } from "react";

import Paper from "@mui/material/Paper";
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import styles from "./FileImport.module.css";

import { tokenConfig } from "../utils"

export function ViewUploadedInstructorList() {
    const [allInstructors, setAllInstructors] = useState(null);

    useEffect(() => {
        axios.get("api/instructors", tokenConfig()).then(
            (response) => { setAllInstructors(response.data); },
            (_) => { setAllInstructors(null); });

    }, [setAllInstructors]);

    var data = (<div>No instructors</div>);
    if (((allInstructors ?? null) !== null) && (allInstructors instanceof Array)) {
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
                                    Instructor
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
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {allInstructors.map((row, rowIndex) => (
                                <TableRow key={rowIndex}>
                                    <TableCell key="0">{row.id}</TableCell>
                                    <TableCell key="1">{row.name}</TableCell>
                                    <TableCell key="2">{row.notes}</TableCell>
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
            <h2 className={styles.title}>Current Instructor List</h2>
            {data}
        </div>
    );
}

export default ViewUploadedInstructorList;