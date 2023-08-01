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

export function DebugSchedule() {
  const [allSchedules, setAllSchedules] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState("");
  const [formName, setFormName] = useState("");
  const [formNotes, setFormNotes] = useState("");
  const [formCourses, setFormCourses] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const clearForm = () => {
    setFormId("");
    setFormName("");
    setFormNotes("");
    setFormCourses("");
  };

  useEffect(() => {
    clearForm();
    axios.get("api/schedules", tokenConfig()).then(
      (response) => {
        setAllSchedules(response.data);
      },
      (_) => {
        setAllSchedules(null);
      }
    );
  }, [updateResponse]);

  var data = <div>No schedules</div>;
  if (allSchedules && allSchedules instanceof Array) {
    data = (
      <Paper sx={{ width: "100%", overflow: "hidden" }}>
        <TableContainer sx={{ width: "100%", maxHeight: 300 }}>
          <Table stickyHeader aria-label="sticky table">
            <TableHead>
              <TableRow>
                <TableCell key="0">ID</TableCell>
                <TableCell key="1">Name</TableCell>
                <TableCell key="2">Notes</TableCell>
                <TableCell key="3">Courses</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {allSchedules.map((row, rowIndex) => (
                <TableRow key={rowIndex}>
                  <TableCell key="0">{row.id}</TableCell>
                  <TableCell key="1">{row.name}</TableCell>
                  <TableCell key="2">{row.notes}</TableCell>
                  <TableCell key="3">
                    <pre>{JSON.stringify(row.courses, null, 2)}</pre>
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
    <ThemeProvider theme={theme}>
      <Box {...boxStyles} data-testid="debug-schedule">
        <Container maxWidth="lg">
          <Box {...headerStyles}>
            <Typography component="h1" variant="h4" color="white">
              Schedules
            </Typography>
          </Box>

          {data}

          <Box
            {...formStyles}
            onSubmit={(event) => {
              event.preventDefault();
              const scheduleObj = {};
              try {
                if (formId) scheduleObj.id = formId;
                if (formName) scheduleObj.name = formName;
                if (formNotes) scheduleObj.notes = formNotes;
              } catch (error) {
                setErrorMessage("Couldn't make query: " + error.message);
                return;
              }

              axios.post("api/schedules", [scheduleObj], tokenConfig()).then(
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
              id="form-notes"
              label="Notes"
              value={formNotes}
              onChange={(event) => setFormNotes(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
            />

            <TextField
              id="form-courses"
              label="Courses"
              value={formCourses}
              onChange={(event) => setFormCourses(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
              multiline
              rows={4}
            />

            <Button type="submit" variant="contained" color="primary" fullWidth>
              Create/Update
            </Button>

            <Button
              sx={{ mb: 3, mt: 1 }}
              onClick={(event) => {
                event.preventDefault();
                if (formId) {
                  axios.delete("api/schedules/" + formId, tokenConfig()).then(
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

export default DebugSchedule;
