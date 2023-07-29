import axios from "axios";
import React, { useState } from "react";
import {
  Button,
  Container,
  Typography,
  Box,
  createTheme,
  ThemeProvider,
} from "@mui/material";

import { boxStyles, headerStyles } from "./DebugActionStyles";

import { tokenConfig } from "../../utils";

const theme = createTheme({
  palette: {
    primary: {
      main: "#0a5e28",
    },
  },
});

export function DebugActions() {
  const [updateResponse, setUpdateResponse] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);
  const [reqNo, setReqNo] = useState(0);

  const ActionButton = ({ url, label }) => {
    return (
      <Button
        variant="contained"
        color="primary"
        fullWidth
        sx={{ marginBottom: "10px" }}
        onClick={(event) => {
          event.preventDefault();
          axios.post(url, "", tokenConfig()).then(
            (response) => {
              setReqNo((r) => r + 1);
              setUpdateResponse(response.data);
              setErrorMessage(null);
            },
            (error) => {
              setReqNo((r) => r + 1);
              setUpdateResponse(error.response.data);
              setErrorMessage(error.message);
            }
          );
        }}
      >
        {label}
      </Button>
    );
  };

  return (
    <ThemeProvider theme={theme}>
      <Box {...boxStyles}>
        <Container maxWidth="sm">
          <Box {...headerStyles}>
            <Typography component="h1" variant="h4" color="white">
              Debug Actions
            </Typography>
          </Box>
          {reqNo === 0 ? (
            ""
          ) : (
            <Typography component="p" variant="body1">
              {errorMessage ? (
                <span style={{ color: "red" }}>
                  #{reqNo} {errorMessage}:{" "}
                </span>
              ) : (
                "#" + reqNo + " Request OK: "
              )}
              {updateResponse
                ? updateResponse instanceof String
                  ? updateResponse
                  : JSON.stringify(updateResponse)
                : "<nothing>"}
            </Typography>
          )}
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              mt: 2,
            }}
          >
            <ActionButton
              url="debug/populate-test-blocks"
              label="Populate Test Blocks"
            />
            <ActionButton
              url="debug/populate-test-classrooms"
              label="Populate Test Classrooms"
            />
            <ActionButton
              url="debug/populate-test-course-offerings"
              label="Populate Test Course Offerings"
            />
            <ActionButton
              url="debug/populate-test-instructors"
              label="Populate Test Instructors"
            />
            <ActionButton
              url="debug/populate-test-semester-plan"
              label="Populate Test Semester Plan"
            />
            <ActionButton url="debug/clear-data" label="Clear All Data" />
          </Box>
        </Container>
      </Box>
    </ThemeProvider>
  );
}

export default DebugActions;
