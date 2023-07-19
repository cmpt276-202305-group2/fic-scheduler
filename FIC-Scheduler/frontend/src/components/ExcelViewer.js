import React, { useState } from "react";
import axios from "axios";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Paper from "@mui/material/Paper";
import readExcelFile from "./readExcelfile";
import styles from "./ExcelViewer.module.css";

const ExcelViewer = ({ spreadsheetData, setSpreadsheetData }) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [isPreviewVisible, setIsPreviewVisible] = useState(false);

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;
    const allowedFormats = ["xlsx", "csv"];
    const fileExtension = file.name.split(".").pop().toLowerCase();
    if (!allowedFormats.includes(fileExtension)) {
      setShowErrorMessage(true);
      return;
    }

    setShowErrorMessage(false);
    try {
      const data = await readExcelFile(file);
      setSpreadsheetData(data);
      setSelectedFile(file.name);
      setIsPreviewVisible(true);
    } catch (error) {
      console.error("Error reading Excel file:", error);
    }
  };
  

  const handleSendToBackend = async () => {
    if (spreadsheetData.length > 0) {
      const formData = new FormData();
      formData.append("file", selectedFile);
      
      try {
        const response = await axios.post("post_to_db", formData);

        if (response.status === 200) {
          const result = response.data;
          // console.log("File upload successful:", result);
          // Add any further actions or state updates upon successful backend upload
        } else {
          // console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        // console.error("Error uploading Excel file:", error);
      }
    }
  };

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "flex-start",
      }}
    >
      <header
        style={{
          color: "black",
          fontSize: 30,
          position: "sticky",
          left: 0,
          marginTop: -10,
          marginBottom: 10,
          fontWeight: "bold"
        }}
      >
        Upload Instructor Availability
      </header>
      <div style={{ display: "flex", alignItems: "center" }}>
        {selectedFile && (
          <div className={styles.fileUploadInput}>{selectedFile}</div>
        )}
        {!selectedFile && (
          <div className={styles.fileUploadInput}>No File Selected</div>
        )}
        <TextField
          type="file"
          id="file-upload"
          onChange={handleFileUpload}
          accept=".xlsx,.csv"
          style={{ display: "none" }}
        />
        <label htmlFor="file-upload">
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
      <h1
        style={{
          color: "black",
          fontSize: 30,
          position: "sticky",
          left: 0,
          marginTop: 0,
        }}
      >
        Preview
      </h1>
      {spreadsheetData.length > 0 ? (
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
      )}
    </div>
  );
};

export default ExcelViewer;