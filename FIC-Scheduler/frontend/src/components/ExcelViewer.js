import React, { useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@material-ui/core";
import readExcelFile from "./readExcelFile";
import styles from "./ExcelViewer.module.css"; // Import CSS module

const ExcelViewer = () => {
  const [spreadsheetData, setSpreadsheetData] = useState([]);

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];

    try {
      const data = await readExcelFile(file);
      setSpreadsheetData(data);

      const formData = new FormData();
      formData.append("file", file);

      const response = await fetch("post_to_db", {
        method: "POST",
        body: formData,
      });

      if (response.ok) {
        const result = await response.json();
        console.log("File upload successful:", result);
      } else {
        console.error("Error uploading Excel file:", response.statusText);
      }
    } catch (error) {
      console.error("Error reading Excel file:", error);
    }
  };

  return (
    <div className={styles.container}>
      <input
        type="file"
        onChange={handleFileUpload}
        accept=".xlsx"
        className={styles.fileInput}
      />
      {spreadsheetData.length > 0 ? (
        <TableContainer component={Paper} className={styles.tableWrapper}>
          <Table className={styles.table} aria-label="Excel sheet table">
            <TableHead>
              <TableRow>
                {spreadsheetData[0].map((header, index) => (
                  <TableCell key={index}>{header}</TableCell>
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
      ) : (
        <div className={styles.emptyMessage}>No data to display</div>
      )}
    </div>
  );
};

export default ExcelViewer;
