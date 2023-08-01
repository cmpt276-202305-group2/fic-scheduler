import axios from "axios";
import React, { useEffect, useState } from "react";
import {
  Box,
  Button,
  Container,
  TextField,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";
import { createTheme, ThemeProvider } from "@mui/material/styles";

import { boxStyles, headerStyles, formStyles } from "./DebugStyles";
import { tokenConfig } from "../../utils";

export function DebugCourseOffering() {
  const [allCourseOfferings, setAllCourseOfferings] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState("");
  const [formName, setFormName] = useState("");
  const [formCourseNumber, setFormCourseNumber] = useState("");
  const [formNotes, setFormNotes] = useState("");
  const [formApprovedInstructors, setFormApprovedInstructors] = useState("");
  const [formAllowedBlockSplits, setFormAllowedBlockSplits] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const clearForm = () => {
    setFormId("");
    setFormName("");
    setFormCourseNumber("");
    setFormNotes("");
    setFormApprovedInstructors("");
    setFormAllowedBlockSplits("");
  };

  useEffect(() => {
    clearForm();
    axios.get("api/course-offerings", tokenConfig()).then(
      (response) => {
        setAllCourseOfferings(response.data);
      },
      (_) => {
        setAllCourseOfferings(null);
      }
    );
  }, [updateResponse]);

  let data = <div>No course offerings</div>;
  if (allCourseOfferings && allCourseOfferings instanceof Array) {
    data = (
      <Paper sx={{ width: "100%", overflow: "hidden" }}>
        <TableContainer sx={{ width: "100%", maxHeight: 300 }}>
          <Table stickyHeader aria-label="sticky table">
            <TableHead>
              <TableRow>
                <TableCell key="0">ID</TableCell>
                <TableCell key="1">Name</TableCell>
                <TableCell key="2">Course Number</TableCell>
                <TableCell key="3">Notes</TableCell>
                <TableCell key="4">Approved Instructors</TableCell>
                <TableCell key="5">Allowed Block Splits</TableCell>
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
                    {JSON.stringify(row.approvedInstructors)}
                  </TableCell>
                  <TableCell key="5">
                    {JSON.stringify(row.allowedBlockSplits)}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    );
  }

  const theme = createTheme({
    palette: {
      primary: {
        main: "#0a5e28",
      },
    },
  });

  return (
    <ThemeProvider theme={theme}>
      <Box {...boxStyles} data-testid="debug-course-offering">
        <Container maxWidth="lg">
          <Box {...headerStyles}>
            <Typography component="h1" variant="h4" color="white">
              Course Offerings
            </Typography>
          </Box>

          {data}

          <Box
            {...formStyles}
            onSubmit={(event) => {
              event.preventDefault();
              const courseOfferingObj = {};
              try {
                if (formId) courseOfferingObj.id = formId;
                if (formName) courseOfferingObj.name = formName;
                if (formCourseNumber)
                  courseOfferingObj.courseNumber = formCourseNumber;
                if (formNotes) courseOfferingObj.notes = formNotes;
                if (formApprovedInstructors)
                  courseOfferingObj.approvedInstructors = JSON.parse(
                    formApprovedInstructors
                  );
                if (formAllowedBlockSplits)
                  courseOfferingObj.allowedBlockSplits = JSON.parse(
                    formAllowedBlockSplits
                  );
              } catch (error) {
                setErrorMessage("Couldn't make query: " + error.message);
                return;
              }

              axios
                .post(
                  "api/course-offerings",
                  [courseOfferingObj],
                  tokenConfig()
                )
                .then(
                  (response) => {
                    setUpdateResponse(response);
                    setErrorMessage("");
                  },
                  (error) => {
                    setErrorMessage(
                      error.response
                        ? error.response.status + " " + error.response.data
                        : error.message
                    );
                  }
                );
            }}
          >
            <Typography component="h2" variant="h5">
              Create/Update
            </Typography>

            <Typography component="p" variant="body1" color="error">
              {errorMessage}
            </Typography>

            <TextField
              id="form-id"
              label="ID"
              value={formId}
              onChange={(event) => setFormId(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Create new"
            />

            <TextField
              id="form-name"
              label="Name"
              value={formName}
              onChange={(event) => setFormName(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
              autoFocus
            />

            <TextField
              id="form-course-number"
              label="Course Number"
              value={formCourseNumber}
              onChange={(event) => setFormCourseNumber(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
            />

            <TextField
              id="form-notes"
              label="Notes"
              value={formNotes}
              onChange={(event) => setFormNotes(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
            />

            <TextField
              id="form-approved-instructors"
              label="Approved Instructors"
              value={formApprovedInstructors}
              onChange={(event) =>
                setFormApprovedInstructors(event.target.value)
              }
              margin="normal"
              fullWidth
              placeholder="Don't update"
            />

            <TextField
              id="form-allowed-block-splits"
              label="Allowed Block Splits"
              value={formAllowedBlockSplits}
              onChange={(event) =>
                setFormAllowedBlockSplits(event.target.value)
              }
              margin="normal"
              fullWidth
              placeholder="Don't update"
            />

            <Button
              type="submit"
              variant="contained"
              color="primary"
              sx={{ mt: 3, mb: 2 }}
            >
              Submit
            </Button>
          </Box>
        </Container>
      </Box>
    </ThemeProvider>
  );
}

export default DebugCourseOffering;
