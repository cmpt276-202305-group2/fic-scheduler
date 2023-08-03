import { Box, Button, Container, TextField, Typography } from "@mui/material";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import axios from "axios";
import React, { useEffect, useState } from "react";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";

import { boxStyles, headerStyles, formStyles } from "./DebugStyles";
import { tokenConfig } from "../../utils";

export function DebugBlockSplit() {
  const [allBlockSplits, setAllBlockSplits] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState("");
  const [formName, setFormName] = useState("");
  const [formBlocks, setFormBlocks] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const clearForm = () => {
    setFormId("");
    setFormName("");
    setFormBlocks("");
  };

  useEffect(() => {
    clearForm();
    axios.get("api/block-splits", tokenConfig()).then(
      (response) => {
        setAllBlockSplits(response.data);
      },
      (_) => {
        setAllBlockSplits(null);
      }
    );
  }, [updateResponse]);

  let data = <div>No block splits</div>;
  if ((allBlockSplits ?? null) !== null && allBlockSplits instanceof Array) {
    data = (
      <Paper sx={{ width: "100%", overflow: "hidden" }}>
        <TableContainer
          sx={{
            width: "100%",
            maxHeight: 300,
          }}
        >
          <Table stickyHeader aria-label="sticky table">
            <TableHead>
              <TableRow>
                <TableCell key="0">ID</TableCell>
                <TableCell key="1">Name</TableCell>
                <TableCell key="2">Blocks</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {allBlockSplits.map((row, rowIndex) => (
                <TableRow key={rowIndex}>
                  <TableCell key="0">{row.id}</TableCell>
                  <TableCell key="1">{row.name}</TableCell>
                  <TableCell key="2">{JSON.stringify(row.blocks)}</TableCell>
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
      <Box {...boxStyles} data-testid="debug-block-split">
        <Container maxWidth="lg">
          <Box {...headerStyles}>
            <Typography component="h1" variant="h4" color="white">
              Block Splits
            </Typography>
          </Box>

          {data}

          <Box
            {...formStyles}
            onSubmit={(event) => {
              event.preventDefault();
              const blockSplitObj = {};
              try {
                if (formId) blockSplitObj.id = formId;
                if (formName) blockSplitObj.name = formName;
                if (formBlocks) blockSplitObj.blocks = JSON.parse(formBlocks);
              } catch (error) {
                setErrorMessage("Couldn't make query: " + error.message);
                return;
              }

              axios
                .post("api/block-splits", [blockSplitObj], tokenConfig())
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
              label="Room Number"
              value={formName}
              onChange={(event) => setFormName(event.target.value)}
              margin="normal"
              fullWidth
              placeholder="Don't update"
              autoFocus
            />

            <TextField
              id="form-blocks"
              label="Room Name"
              value={formBlocks}
              onChange={(event) => setFormBlocks(event.target.value)}
              margin="normal"
              fullWidth
              multiline
              rows="10"
              placeholder="Don't update"
            />

            <Button type="submit" variant="contained" color="primary" fullWidth>
              Create/Update
            </Button>

            <Button
              sx={{ mb: 3, mt: 1 }}
              onClick={(event) => {
                event.preventDefault();
                if ((formId ?? "") !== "") {
                  axios
                    .delete("api/block-splits/" + formId, tokenConfig())
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

export default DebugBlockSplit;
