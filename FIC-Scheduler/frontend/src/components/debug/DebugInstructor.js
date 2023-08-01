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

export function DebugInstructor() {
  const [allInstructors, setAllInstructors] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState("");
  const [formName, setFormName] = useState("");
  const [formNotes, setFormNotes] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const clearForm = () => {
    setFormId("");
    setFormName("");
    setFormNotes("");
  };

  useEffect(() => {
    clearForm();
    axios.get("api/instructors", tokenConfig()).then(
      (response) => {
        setAllInstructors(response.data);
      },
      (_) => {
        setAllInstructors(null);
      }
    );
  }, [updateResponse]);

  var data = <div>No instructors</div>;
  if (allInstructors && allInstructors instanceof Array) {
    data = (
      <Paper sx={{ width: "100%", overflow: "hidden" }}>
        <TableContainer sx={{ width: "100%", maxHeight: 500 }}>
          <Table stickyHeader aria-label="sticky table">
            <TableHead>
              <TableRow>
                <TableCell key="0">ID</TableCell>
                <TableCell key="1">Name</TableCell>
                <TableCell key="2">Notes</TableCell>
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

  const theme = createTheme({
    palette: {
      primary: {
        main: "#0a5e28",
      },
    },
  });

  return (
    <ThemeProvider theme={theme}>
      <Box {...boxStyles} data-testid="debug-instructor">
        <Container maxWidth="lg">
          <Box {...headerStyles}>
            <Typography component="h1" variant="h4" color="white">
              Instructors
            </Typography>
          </Box>

          {data}

          <Box
            {...formStyles}
            onSubmit={(event) => {
              event.preventDefault();
              const instructorObj = {};
              try {
                if (formId) instructorObj.id = formId;
                if (formName) instructorObj.name = formName;
                if (formNotes) instructorObj.notes = formNotes;
              } catch (error) {
                setErrorMessage("Couldn't make query: " + error.message);
                return;
              }

              axios
                .post("api/instructors", [instructorObj], tokenConfig())
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
              id="form-notes"
              label="Notes"
              value={formNotes}
              onChange={(event) => setFormNotes(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
            />

            <Button type="submit" variant="contained" color="primary" fullWidth>
              Create/Update
            </Button>

            <Button
              onClick={(event) => {
                event.preventDefault();
                if (formId) {
                  axios.delete("api/instructors/" + formId, tokenConfig()).then(
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

export default DebugInstructor;
