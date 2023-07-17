import React, { useState } from "react";
import readExcelFile from "./readExcelfile";
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
        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                {spreadsheetData[0].map((header, index) => (
                  <th key={index}>{header}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {spreadsheetData.slice(1).map((row, rowIndex) => (
                <tr key={rowIndex}>
                  {row.map((cell, cellIndex) => (
                    <td key={cellIndex}>{cell}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <div className={styles.emptyMessage}>No data to display</div>
      )}
    </div>
  );
};

export default ExcelViewer;
