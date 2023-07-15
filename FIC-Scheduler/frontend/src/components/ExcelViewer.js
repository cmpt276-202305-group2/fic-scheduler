import React, { useEffect, useState } from "react";
import readExcelFile from "./readExcelfile";

const ExcelViewer = () => {
  const [spreadsheetData, setSpreadsheetData] = useState([]);

  useEffect(() => {
    const fetchSpreadsheetData = async () => {
      try {
        const data = await readExcelFile("../schedules/schedule.xlsx");
        setSpreadsheetData(data);
      } catch (error) {
        console.error("Error reading Excel file:", error);
      }
    };

    fetchSpreadsheetData();
  }, []);

  return (
    <div>
      <table>
        <thead>
          <tr>
            {spreadsheetData.length > 0 &&
              spreadsheetData[0].map((header, index) => (
                <th key={index}>{header}</th>
              ))}
          </tr>
        </thead>
        <tbody>
          {spreadsheetData.map((row, rowIndex) => (
            <tr key={rowIndex}>
              {row.map((cell, cellIndex) => (
                <td key={cellIndex}>{cell}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ExcelViewer;
