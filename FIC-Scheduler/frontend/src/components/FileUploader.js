import React from "react";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import Paper from "@mui/material/Paper";
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import PropTypes from "prop-types";

export const FileUploader = ({
  selectedFile,
  handleFileUpload,
  showErrorMessage,
  isPreviewVisible,
  handleSendToBackend,
  id,
  styles,
}) => (
  <div style={{ display: "flex", alignItems: "center" }}>
    {selectedFile ? (
      <div className={styles.fileUploadInput}>{selectedFile}</div>
    ) : (
      <div className={styles.fileUploadInput}>No File Selected</div>
    )}
    <TextField
      type="file"
      id={id}
      onChange={handleFileUpload}
      accept=".xlsx,.csv"
      style={{ display: "none" }}
    />
    <label htmlFor={id}>
      <Button
        variant="contained"
        component="span"
        color="primary"
        sx={{
          color: "white",
          backgroundColor: "#417A1A",
          "&:hover": { backgroundColor: "#417A1A" },
        }}
        style={{ marginBottom: 10 }}
      >
        Preview File
      </Button>
    </label>
    {showErrorMessage && (
      <div
        style={{
          color: "red",
          fontSize: "13px",
          marginLeft: "5px",
          marginBottom: "12px",
        }}
      >
        Please Upload Valid .csv, .xlsx File
      </div>
    )}
    {isPreviewVisible && (
      <Button
        variant="contained"
        color="primary"
        sx={{
          color: "white",
          backgroundColor: "#417A1A",
          "&:hover": { backgroundColor: "#417A1A" },
        }}
        style={{ marginBottom: 10, marginLeft: 10 }}
        onClick={handleSendToBackend}
      >
        Upload File
      </Button>
    )}
  </div>
);

FileUploader.propTypes = {
  selectedFile: PropTypes.string,
  handleFileUpload: PropTypes.func.isRequired,
  showErrorMessage: PropTypes.bool,
  isPreviewVisible: PropTypes.bool,
  handleSendToBackend: PropTypes.func.isRequired,
  id: PropTypes.string.isRequired,
  styles: PropTypes.object,
};

FileUploader.defaultProps = {
  selectedFile: "",
  showErrorMessage: false,
  isPreviewVisible: false,
  styles: {},
};

export const SpreadsheetTable = ({ spreadsheetData, styles }) =>
  spreadsheetData.length > 0 ? (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "flex-start",
      }}
    >
      <Box sx={{ width: "100%" }}>
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
                  {spreadsheetData[0].map((header, index) => (
                    <TableCell
                      key={index}
                      sx={{
                        fontWeight: "bold",
                        backgroundColor: "#f0f0f0",
                      }}
                    >
                      {header}
                    </TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {spreadsheetData.slice(1).map((row, rowIndex) => (
                  <TableRow key={rowIndex}>
                    {row.map((cell, cellIndex) => (
                      <TableCell key={cellIndex}>{cell}</TableCell>
                    ))}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
      </Box>
    </div>
  ) : (
    <div className={styles.emptyMessage}>No data to display</div>
  );

SpreadsheetTable.propTypes = {
  spreadsheetData: PropTypes.array.isRequired,
  styles: PropTypes.object,
};

SpreadsheetTable.defaultProps = {
  styles: {},
};
