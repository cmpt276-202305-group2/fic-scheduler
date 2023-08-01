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

export function DebugClassroom() {
  const [allClassrooms, setAllClassrooms] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState("");
  const [formRoomNumber, setFormRoomNumber] = useState("");
  const [formRoomType, setFormRoomType] = useState("");
  const [formNotes, setFormNotes] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const clearForm = () => {
    setFormId("");
    setFormRoomNumber("");
    setFormRoomType("");
    setFormNotes("");
  };

  useEffect(() => {
    clearForm();
    axios.get("api/classrooms", tokenConfig()).then(
      (response) => {
        setAllClassrooms(response.data);
      },
      (_) => {
        setAllClassrooms(null);
      }
    );
  }, [updateResponse]);

  let data = <div>No classrooms</div>;
  if (allClassrooms && allClassrooms instanceof Array) {
    data = (
      <Paper sx={{ width: "100%", overflow: "hidden" }}>
        <TableContainer sx={{ width: "100%", maxHeight: 500 }}>
          <Table stickyHeader aria-label="sticky table">
            <TableHead>
              <TableRow>
                <TableCell key="0">ID</TableCell>
                <TableCell key="1">Room Number</TableCell>
                <TableCell key="2">Room Type</TableCell>
                <TableCell key="3">Notes</TableCell>
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

  const theme = createTheme({
    palette: {
      primary: {
        main: "#0a5e28",
      },
    },
  });

  return (
    <ThemeProvider theme={theme}>
      <Box {...boxStyles} data-testid="debug-classroom">
        <Container maxWidth="lg">
          <Box {...headerStyles}>
            <Typography component="h1" variant="h4" color="white">
              Classrooms
            </Typography>
          </Box>

          {data}

          <Box
            {...formStyles}
            onSubmit={(event) => {
              event.preventDefault();
              const classroomObj = {};
              try {
                if (formId) classroomObj.id = formId;
                if (formRoomNumber) classroomObj.roomNumber = formRoomNumber;
                if (formRoomType) classroomObj.roomType = formRoomType;
                classroomObj.notes = formNotes;
              } catch (error) {
                setErrorMessage("Couldn't make query: " + error.message);
                return;
              }

              axios.post("api/classrooms", [classroomObj], tokenConfig()).then(
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
              id="form-room-number"
              label="Room Number"
              value={formRoomNumber}
              onChange={(event) => setFormRoomNumber(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
              autoFocus
            />

            <TextField
              id="form-room-type"
              label="Room Type"
              value={formRoomType}
              onChange={(event) => setFormRoomType(event.target.value)}
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

            <Button type="submit" variant="contained" color="primary" fullWidth>
              Create/Update
            </Button>

            <Button
              onClick={(event) => {
                event.preventDefault();
                if (formId) {
                  axios.delete("api/classrooms/" + formId, tokenConfig()).then(
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
              color="error"
              fullWidth
              variant="outlined"
            >
              Delete
            </Button>
          </Box>
        </Container>
      </Box>
    </ThemeProvider>
  );
}

export default DebugClassroom;
