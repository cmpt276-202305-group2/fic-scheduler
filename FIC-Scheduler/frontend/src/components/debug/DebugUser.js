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

export function DebugUser() {
  const [allUsers, setAllUsers] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState("");
  const [formUsername, setFormUsername] = useState("");
  const [formPassword, setFormPassword] = useState("");
  const [formRoles, setFormRoles] = useState("");
  const [formFullName, setFormFullName] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const clearForm = () => {
    setFormId("");
    setFormUsername("");
    setFormPassword("");
    setFormRoles("");
    setFormFullName("");
  };

  useEffect(() => {
    clearForm();
    axios.get("api/users", tokenConfig()).then(
      (response) => {
        setAllUsers(response.data);
      },
      (_) => {
        setAllUsers(null);
      }
    );
  }, [updateResponse]);

  let data = <div>No users</div>;
  if (allUsers && allUsers instanceof Array) {
    data = (
      <Paper sx={{ width: "100%", overflow: "hidden" }}>
        <TableContainer sx={{ width: "100%", maxHeight: 300 }}>
          <Table stickyHeader aria-label="sticky table">
            <TableHead>
              <TableRow>
                <TableCell key="0">ID</TableCell>
                <TableCell key="1">Username</TableCell>
                <TableCell key="2">Password</TableCell>
                <TableCell key="3">Roles</TableCell>
                <TableCell key="4">Full Name</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {allUsers.map((row, rowIndex) => (
                <TableRow key={rowIndex}>
                  <TableCell key="0">{row.id}</TableCell>
                  <TableCell key="1">{row.username}</TableCell>
                  <TableCell key="2">{row.password ?? "*"}</TableCell>
                  <TableCell key="3">{JSON.stringify(row.roles)}</TableCell>
                  <TableCell key="4">{row.fullName}</TableCell>
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
      <Box {...boxStyles} data-testid="debug-user">
        <Container maxWidth="lg">
          <Box {...headerStyles}>
            <Typography component="h1" variant="h4" color="white">
              Users
            </Typography>
          </Box>

          {data}

          <Box
            {...formStyles}
            onSubmit={(event) => {
              event.preventDefault();
              const userObj = {};
              try {
                if (formId) userObj.id = formId;
                if (formUsername) userObj.username = formUsername;
                if (formPassword) userObj.password = formPassword;
                if (formRoles)
                  userObj.roles = formRoles
                    .split(" ")
                    .filter((o) => !!o)
                    .map((o) => ("" + o).trim().toUpperCase());
                if (formFullName) userObj.fullName = formFullName;
              } catch (error) {
                setErrorMessage("Couldn't make query: " + error.message);
                return;
              }

              axios.post("api/users", [userObj], tokenConfig()).then(
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
              id="form-username"
              label="Username"
              value={formUsername}
              onChange={(event) => setFormUsername(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
              autoFocus
            />

            <TextField
              id="form-password"
              label="Password"
              value={formPassword}
              onChange={(event) => setFormPassword(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
            />

            <TextField
              id="form-roles"
              label="Roles"
              value={formRoles}
              onChange={(event) => setFormRoles(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
            />

            <TextField
              id="form-full-name"
              label="Full Name"
              value={formFullName}
              onChange={(event) => setFormFullName(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
            />

            <Button type="submit" variant="contained" color="primary" fullWidth>
              Create/Update
            </Button>

            <Button
              sx={{ mb: 3, mt: 1 }}
              onClick={(event) => {
                event.preventDefault();
                if (formId !== "") {
                  axios.delete("api/users/" + formId, tokenConfig()).then(
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

export default DebugUser;
