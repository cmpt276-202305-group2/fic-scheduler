import {
  Box,
  Button,
  TextField,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  Container,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import { boxStyles, headerStyles, formStyles } from "./DebugStyles";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { tokenConfig } from "../../utils";

const theme = createTheme({
  components: {
    MuiTableCell: {
      styleOverrides: {
        root: {
          verticalAlign: "top",
        },
      },
    },
  },
  palette: {
    primary: {
      main: "#0a5e28",
    },
  },
});

export function DebugSemesterPlan() {
  const [allSemesterPlans, setAllSemesterPlans] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState("");
  const [formName, setFormName] = useState("");
  const [formNotes, setFormNotes] = useState("");
  const [formSemester, setFormSemester] = useState("");
  const [formCoursesOffered, setFormCoursesOffered] = useState("");
  const [formInstructorsAvailable, setFormInstructorsAvailable] = useState("");
  const [formClassroomsAvailable, setFormClassroomsAvailable] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const clearForm = () => {
    setFormId("");
    setFormName("");
    setFormNotes("");
    setFormSemester("");
    setFormCoursesOffered("");
    setFormInstructorsAvailable("");
    setFormClassroomsAvailable("");
  };

  useEffect(() => {
    clearForm();
    axios.get("api/semester-plans", tokenConfig()).then(
      (response) => {
        setAllSemesterPlans(response.data);
      },
      (_) => {
        setAllSemesterPlans(null);
      }
    );
  }, [updateResponse, setAllSemesterPlans]);

  var data = <div>No semester plans</div>;
  if (
    (allSemesterPlans ?? null) !== null &&
    allSemesterPlans instanceof Array
  ) {
    data = (
      <Paper sx={{ width: "100%", overflow: "hidden" }}>
        <TableContainer sx={{ width: "100%", maxHeight: 300 }}>
          <Table stickyHeader aria-label="sticky table">
            <TableHead>
              <TableRow>
                <TableCell key="generate"></TableCell>
                <TableCell key="0">id</TableCell>
                <TableCell key="1">name</TableCell>
                <TableCell key="2">notes</TableCell>
                <TableCell key="3">semester</TableCell>
                <TableCell key="4">courses offered</TableCell>
                <TableCell key="5">instructors available</TableCell>
                <TableCell key="6">classrooms available</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {allSemesterPlans.map((row, rowIndex) => {
                const rowId = row.id;
                return (
                  <TableRow key={rowIndex}>
                    <TableCell key="generate">
                      <Button
                        style={{
                          background: "none",
                          border: "none",
                          padding: 0,
                          /*optional*/
                          // fontFamily: ["arial", "sans-serif"],
                          color: "#069",
                          textDecoration: "underline",
                          cursor: "pointer",
                        }}
                        onClick={(event) => {
                          event.preventDefault();
                          const postData = { semesterPlan: { id: rowId } };
                          console.log(JSON.stringify(postData));
                          axios
                            .post(
                              "api/generate-schedule",
                              postData,
                              tokenConfig()
                            )
                            .then(
                              (response) => {
                                navigate("/debugMenu/schedule");
                              },
                              (error) => {
                                setErrorMessage(
                                  "Generate for id=" +
                                    rowId +
                                    " failed: " +
                                    error.message
                                );
                              }
                            );
                        }}
                      >
                        gen
                      </Button>
                    </TableCell>
                    <TableCell key="0">{row.id}</TableCell>
                    <TableCell key="1">{row.name}</TableCell>
                    <TableCell key="2">
                      <pre>{row.notes}</pre>
                    </TableCell>
                    <TableCell key="3">{row.semester}</TableCell>
                    <TableCell key="4">
                      <pre>{JSON.stringify(row.coursesOffered, null, 2)}</pre>
                    </TableCell>
                    <TableCell key="5">
                      <pre>
                        {JSON.stringify(row.instructorsAvailable, null, 2)}
                      </pre>
                    </TableCell>
                    <TableCell key="6">
                      <pre>
                        {JSON.stringify(row.classroomsAvailable, null, 2)}
                      </pre>
                    </TableCell>
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
    <ThemeProvider theme={theme}>
      <Box {...boxStyles} data-testid="debug-semester-plan">
        <Container maxWidth="lg">
          <Box {...headerStyles}>
            <Typography component="h1" variant="h4" color="white">
              Semester Plans
            </Typography>
          </Box>

          {data}
          <Box
            {...formStyles}
            onSubmit={(event) => {
              event.preventDefault();
              //const semesterPlanIdStr = (formId !== '') ? ('/' + formId) : '';
              const semesterPlanObj = {};
              try {
                if (formId) semesterPlanObj.id = formId;
                if (formName) semesterPlanObj.name = formName;
                if (formNotes) semesterPlanObj.notes = formNotes;
                if (formSemester) semesterPlanObj.semester = formSemester;
                if (formCoursesOffered)
                  semesterPlanObj.coursesOffered =
                    JSON.parse(formCoursesOffered);
                if (formInstructorsAvailable)
                  semesterPlanObj.instructorsAvailable = JSON.parse(
                    formInstructorsAvailable
                  );
                if (formClassroomsAvailable)
                  semesterPlanObj.classroomsAvailable = JSON.parse(
                    formClassroomsAvailable
                  );
              } catch (error) {
                setErrorMessage("Couldn't make query: " + error.message);
                return;
              }

              axios
                .post("api/semester-plans", [semesterPlanObj], tokenConfig())
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
              type="text"
              name="formId"
              margin="normal"
              fullWidth
              value={formId}
              onChange={(event) => setFormId(event.target.value)}
              placeholder="Create new"
            />

            <TextField
              id="form-name"
              type="text"
              name="formName"
              label="Form Name"
              margin="normal"
              fullWidth
              value={formName}
              onChange={(event) => setFormName(event.target.value)}
              placeholder="Don't update"
              autoFocus
            />

            <TextField
              id="form-notes"
              type="text"
              name="formNotes"
              label="Form Notes"
              fullWidth
              value={formNotes}
              onChange={(event) => setFormNotes(event.target.value)}
              placeholder="Don't update"
              margin="normal"
            />

            <TextField
              id="form-semester"
              label="Semester"
              margin="normal"
              fullWidth
              type="text"
              name="formSemester"
              value={formSemester}
              onChange={(event) => setFormSemester(event.target.value)}
              placeholder="Don't update"
            />
            <TextField
              id="form-courses-offered"
              label="Courses Offered"
              margin="normal"
              type="text"
              fullWidth
              name="formCoursesOffered"
              value={formCoursesOffered}
              onChange={(event) => setFormCoursesOffered(event.target.value)}
              placeholder="Don't update"
            />

            <TextField
              id="form-instructors-available"
              name="formInstructorsAvailable"
              label="Instructors Available"
              margin="normal"
              fullWidth
              value={formInstructorsAvailable}
              onChange={(event) =>
                setFormInstructorsAvailable(event.target.value)
              }
              placeholder="Don't update"
            />

            <TextField
              id="form-classrooms-available"
              label="Classrooms Available"
              margin="normal"
              fullWidth
              type="text"
              name="formClassroomsAvailable"
              value={formClassroomsAvailable}
              onChange={(event) =>
                setFormClassroomsAvailable(event.target.value)
              }
              placeholder="Don't update"
            />

            <Button type="submit" variant="contained" color="primary" fullWidth>
              Create/Update
            </Button>
            <Button
              onClick={(event) => {
                event.preventDefault();
                if ((formId ?? "") !== "") {
                  axios
                    .delete("api/semester-plans/" + formId, tokenConfig())
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
                }
              }}
              variant="outlined"
              color="error"
              fullWidth
            >
              Delete
            </Button>
          </Box>
        </Container>
      </Box>
    </ThemeProvider>
  );
}

export default DebugSemesterPlan;
