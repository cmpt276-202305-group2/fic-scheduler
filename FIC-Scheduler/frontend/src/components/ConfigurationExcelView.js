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
import { tokenConfig } from "../utils";

const ConfigurationExcelView = ({configurationSpreadsheetDataOne, setConfigurationSpreadsheetDataOne, configurationSpreadsheetDataTwo, setConfigurationSpreadsheetDataTwo, configurationSpreadsheetDataThree, setConfigurationSpreadsheetDataThree, configurationSpreadsheetDataFour, setConfigurationSpreadsheetDataFour, configurationSpreadsheetDataFive, setConfigurationSpreadsheetDataFive}) => {
  const [selectedFileOne, setSelectedFileOne] = useState(null);
  const [selectedFileTwo, setSelectedFileTwo] = useState(null);
  const [selectedFileThree, setSelectedFileThree] = useState(null);
  const [selectedFileFour, setSelectedFileFour] = useState(null);
  const [selectedFileFive, setSelectedFileFive] = useState(null);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [showErrorMessageTwo, setShowErrorMessageTwo] = useState(false);
  const [showErrorMessageThree, setShowErrorMessageThree] = useState(false);
  const [showErrorMessageFour, setShowErrorMessageFour] = useState(false);
  const [showErrorMessageFive, setShowErrorMessageFive] = useState(false);
  const [isPreviewVisibleOne, setIsPreviewVisibleOne] = useState(false);
  const [isPreviewVisibleTwo, setIsPreviewVisibleTwo] = useState(false);
  const [isPreviewVisibleThree, setIsPreviewVisibleThree] = useState(false);
  const [isPreviewVisibleFour, setIsPreviewVisibleFour] = useState(false);
  const [isPreviewVisibleFive, setIsPreviewVisibleFive] = useState(false);

  const handleFileUploadOne = async (event) => {
    const file = event.target.files[0];
    if(!file) return;
    const allowedFormats = ["xlsx", "csv"];
    const fileExtension = file.name.split(".").pop().toLowerCase();
    if(!allowedFormats.includes(fileExtension)) {
      setShowErrorMessage(true);
      return;
    }
    setShowErrorMessage(false);
    try {
      const data = await readExcelFile(file);
      setConfigurationSpreadsheetDataOne(data);
      setSelectedFileOne(file.name);
      setIsPreviewVisibleOne(true);
    } catch (error) {
      console.error("Error reading Excel file:", error);
    }
  };
  const handleSendToBackEndOne = async () => {
    if (configurationSpreadsheetDataOne.length > 0) {
      const formData = new FormData();
      formData.append("file", selectedFileOne);

      try {
        const response = await axios.post("post_to_db", formData);

        if (response.status === 200) {
          const result = response.data;
          console.log("File upload successful:", result);
        } else {
          console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        console.error("Error reading Excel file:", error);
      }
    }
  };
  const handleFileUploadTwo = async (event) => {
    const file = event.target.files[0];
    if(!file) return;
    const allowedFormats = ["xlsx", "csv"];
    const fileExtension = file.name.split(".").pop().toLowerCase();
    if(!allowedFormats.includes(fileExtension)) {
      setShowErrorMessageTwo(true);
      return;
    }
    setShowErrorMessageTwo(false);
    try {
      const data = await readExcelFile(file);
      setConfigurationSpreadsheetDataTwo(data);
      setSelectedFileTwo(file.name);
      setIsPreviewVisibleTwo(true);
    } catch (error) {
      console.error("Error reading Excel file:", error);
    }
  };

  const handleSendToBackEndTwo = async () => {
    if (configurationSpreadsheetDataTwo.length > 0) {
      const formData = new FormData();
      formData.append("file", selectedFileTwo);

      try {
        const response = await axios.post("post_to_db", formData);

        if (response.status === 200) {
          const result = response.json();
          console.log("File upload successful:", result);
        } else {
          console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        console.error("Error reading Excel file:", error);
      }
    }
  };

  const handleFileUploadThree = async (event) => {
    const file = event.target.files[0];
    if(!file) return;
    const allowedFormats = ["xlsx", "csv"];
    const fileExtension = file.name.split(".").pop().toLowerCase();
    if(!allowedFormats.includes(fileExtension)) {
      setShowErrorMessageThree(true);
      return;
    }
    setShowErrorMessageThree(false);
    try {
      const data = await readExcelFile(file);
      setConfigurationSpreadsheetDataThree(data);
      setSelectedFileThree(file.name);
      setIsPreviewVisibleThree(true);
    } catch (error) {
      console.error("Error reading Excel file:", error);
    }
  };
  const handleSendToBackEndThree = async () => {
    if (configurationSpreadsheetDataThree.length > 0) {
      const formData = new FormData();
      formData.append("file", selectedFileThree);

      try {
        const response = await axios.post("post_to_db", formData);

        if (response.status === 200) {
          const result = response.data;
          console.log("File upload successful:", result);
        } else {
          console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        console.error("Error reading Excel file:", error);
      }
    }
  };

  const handleFileUploadFour = async (event) => {
    const file = event.target.files[0];
    if(!file) return;
    const allowedFormats = ["xlsx", "csv"];
    const fileExtension = file.name.split(".").pop().toLowerCase();
    if(!allowedFormats.includes(fileExtension)) {
      setShowErrorMessageFour(true);
      return;
    }
    setShowErrorMessageFour(false);
    try {
      const data = await readExcelFile(file);
      setConfigurationSpreadsheetDataFour(data);
      setSelectedFileFour(file.name);
      setIsPreviewVisibleFour(true);
    } catch (error) {
      console.error("Error reading Excel file:", error);
    }
  };
  const handleSendToBackEndFour = async () => {
    if (configurationSpreadsheetDataThree.length > 0) {
      const formData = new FormData();
      formData.append("file", selectedFileFour);

      try {
        const response = await axios.post("post_to_db", formData);

        if (response.status === 200) {
          const result = response.data;
          console.log("File upload successful:", result);
        } else {
          console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        console.error("Error reading Excel file:", error);
      }
    }
  };

  const handleFileUploadFive = async (event) => {
    const file = event.target.files[0];
    if(!file) return;
    const allowedFormats = ["xlsx", "csv"];
    const fileExtension = file.name.split(".").pop().toLowerCase();
    if(!allowedFormats.includes(fileExtension)) {
      setShowErrorMessageFive(true);
      return;
    }
    setShowErrorMessageFive(false);
    try {
      const data = await readExcelFile(file);
      setConfigurationSpreadsheetDataFive(data);
      setSelectedFileFive(file.name);
      setIsPreviewVisibleFive(true);
    } catch (error) {
      console.error("Error reading Excel file:", error);
    }
  };
  const handleSendToBackEndFive = async () => {
    if (configurationSpreadsheetDataThree.length > 0) {
      const formData = new FormData();
      formData.append("file", selectedFileFive);

      try {
        const response = await axios.post("post_to_db", formData, tokenConfig());

        if (response.status === 200) {
          const result = response.data;
          console.log("File upload successful:", result);
        } else {
          console.error("Error uploading Excel file:", response.statusText);
        }
      } catch (error) {
        console.error("Error reading Excel file:", error);
      }
    }
  };
  return (
    <>
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'flex-start',
        marginBottom: 30
        }}
    >
      <header
        style={{
          color: "black",
          fontSize: 30,
          position: "sticky",
          left: 0,
          marginTop: -10,
          marginBottom: 5,
          fontWeight: "bold"
        }}
      >
        Upload Courses
      </header>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {selectedFileOne && (
          <div className={styles.fileUploadInput}>{selectedFileOne}</div>
        )}
        {!selectedFileOne && (
          <div className={styles.fileUploadInput}>No File Selected</div>
        )}
        <TextField
          type="file"
          id="file-upload"
          onChange={handleFileUploadOne}
          accept=".xlsx,.csv"
          style={{ display: 'none'}}
        />
        <label htmlFor="file-upload">
          <Button
            variant="contained"
            component="span"
            color = "primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              '&:hover': { backgroundColor: '#417A1A' }
            }}
            style={{ marginBottom: 10}}
            >
            Preview File
          </Button>
        </label>
        {showErrorMessage && (
          <div style={{
            color: 'red',
            fontSize: "13px",
            marginLeft: "5px",
            marginBottom: "12px"
            }}
          >
          Please Upload Valid .csv, .xlsx File
          </div>
        )}
        {isPreviewVisibleOne && (
          <Button
            variant="contained"
            color="primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              "&:hover": { backgroundColor: "#417A1A" },
            }}
            style={{ marginBottom: 10, marginLeft: 10 }}
            onClick={handleSendToBackEndOne}
            >
              Upload File
            </Button>
        )}
      </div>
      <h1
        style={{
          color: 'black',
          fontSize: 30,
          position: 'sticky',
          left: 0,
          marginTop: 0
        }}
      >
        Preview
      </h1>
      {configurationSpreadsheetDataOne.length > 0 ? (
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'flex-start'
          }}
        >
          <Box sx={{width: '100%'}}>
            <Paper sx={{width: '100%', overflow: 'hidden'}}>
              <TableContainer
                sx={{
                  width: '100%',
                  maxHeight: window.innerWidth >= 600 ? 700 : 350
                }}
              >
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      {configurationSpreadsheetDataOne[0].map((header, index) => (
                        <TableCell
                          key={index}
                          sx={{
                            fontWeight: 'bold',
                            backgroundColor: '#f0f0f0'
                          }}
                        >
                          {header}
                        </TableCell>
                      ))}
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {configurationSpreadsheetDataOne.slice(1).map((row, rowIndex) => (
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
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'flex-start',
        marginBottom: 30
      }}
    >
      <header
        style={{
          color: "black",
          fontSize: 30,
          position: "sticky",
          left: 0,
          marginTop: 10,
          marginBottom: -25,
          fontWeight: "bold"
        }}
      >
        Upload Classrooms
      </header>
      <div style={{ display: 'flex', alignItems: 'center', marginTop: '30px'}}>
        {selectedFileTwo && (
          <div className={styles.fileUploadInput}>{selectedFileTwo}</div>
        )}
        {!selectedFileTwo && (
          <div className={styles.fileUploadInput}>No File Selected</div>
        )}
        <TextField
          type="file"
          id="file-upload-two"
          onChange={handleFileUploadTwo}
          accept=".xlsx,.csv"
          style={{ display: 'none'}}
        />
        <label htmlFor="file-upload-two">
          <Button
            variant="contained"
            component="span"
            color = "primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              '&:hover': { backgroundColor: '#417A1A' }
            }}
            style={{ marginBottom: 10}}
            >
            Preview File
          </Button>
        </label>
        {showErrorMessageTwo && (
          <div
            style={{
              color: 'red',
              fontSize: "13px",
              marginLeft: "5px",
              marginBottom: "12px"
            }}
          >
            Please Upload Valid .csv, .xlsx File
          </div>
        )}
        {isPreviewVisibleTwo && (
          <Button
            variant="contained"
            color="primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              "&:hover": { backgroundColor: "#417A1A" },
            }}
            style={{ marginBottom: 10, marginLeft: 10 }}
            onClick={handleSendToBackEndTwo}
          >
            Upload File
          </Button>
        )}
      </div>
      <h1
        style={{
          color: 'black',
          fontSize: 30,
          position: 'sticky',
          left: 0,
          marginTop: 0
        }}
      >
        Preview
      </h1>
      {configurationSpreadsheetDataTwo.length > 0 ? (
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'flex-start'
          }}
        >
          <Box sx={{width: '100%'}}>
            <Paper sx={{width: '100%', overflow: 'hidden'}}>
              <TableContainer
                sx={{
                  width: '100%',
                  maxHeight: window.innerWidth >= 600 ? 700 : 350
                }}
              >
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      {configurationSpreadsheetDataTwo[0].map((header, index) => (
                        <TableCell
                          key={index}
                          sx={{
                            fontWeight: 'bold',
                            backgroundColor: '#f0f0f0'
                          }}
                        >
                          {header}
                        </TableCell>
                      ))}
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {configurationSpreadsheetDataTwo.slice(1).map((row, rowIndex) => (
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
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'flex-start',
        marginTop: '5px',
        marginBottom: 30
        }}
    >
      <header
        style={{
          color: "black",
          fontSize: 30,
          position: "sticky",
          left: 0,
          marginTop: 10,
          marginBottom: 5,
          fontWeight: "bold"
        }}
      >
        Upload Instructors
      </header>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {selectedFileThree && (
          <div className={styles.fileUploadInput}>{selectedFileOne}</div>
        )}
        {!selectedFileThree && (
          <div className={styles.fileUploadInput}>No File Selected</div>
        )}
        <TextField
          type="file"
          id="file-upload-three"
          onChange={handleFileUploadThree}
          accept=".xlsx,.csv"
          style={{ display: 'none'}}
        />
        <label htmlFor="file-upload-three">
          <Button
            variant="contained"
            component="span"
            color = "primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              '&:hover': { backgroundColor: '#417A1A' }
            }}
            style={{ marginBottom: 10}}
            >
            Preview File
          </Button>
        </label>
        {showErrorMessageThree && (
          <div style={{
            color: 'red',
            fontSize: "13px",
            marginLeft: "5px",
            marginBottom: "12px"
            }}
          >
          Please Upload Valid .csv, .xlsx File
          </div>
        )}
        {isPreviewVisibleThree && (
          <Button
            variant="contained"
            color="primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              "&:hover": { backgroundColor: "#417A1A" },
            }}
            style={{ marginBottom: 10, marginLeft: 10 }}
            onClick={handleSendToBackEndThree}
            >
              Upload File
            </Button>
        )}
      </div>
      <h1
        style={{
          color: 'black',
          fontSize: 30,
          position: 'sticky',
          left: 0,
          marginTop: 0
        }}
      >
        Preview
      </h1>
      {configurationSpreadsheetDataThree.length > 0 ? (
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'flex-start'
          }}
        >
          <Box sx={{width: '100%'}}>
            <Paper sx={{width: '100%', overflow: 'hidden'}}>
              <TableContainer
                sx={{
                  width: '100%',
                  maxHeight: window.innerWidth >= 600 ? 700 : 350
                }}
              >
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      {configurationSpreadsheetDataThree[0].map((header, index) => (
                        <TableCell
                          key={index}
                          sx={{
                            fontWeight: 'bold',
                            backgroundColor: '#f0f0f0'
                          }}
                        >
                          {header}
                        </TableCell>
                      ))}
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {configurationSpreadsheetDataThree.slice(1).map((row, rowIndex) => (
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
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'flex-start',
        marginBottom: 35
        }}
    >
      <header
        style={{
          color: "black",
          fontSize: 30,
          position: "sticky",
          left: 0,
          marginTop: 15,
          marginBottom: 5,
          fontWeight: "bold"
        }}
      >
        Upload Facilities
      </header>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {selectedFileFour && (
          <div className={styles.fileUploadInput}>{selectedFileOne}</div>
        )}
        {!selectedFileFour && (
          <div className={styles.fileUploadInput}>No File Selected</div>
        )}
        <TextField
          type="file"
          id="file-upload-four"
          onChange={handleFileUploadFour}
          accept=".xlsx,.csv"
          style={{ display: 'none'}}
        />
        <label htmlFor="file-upload-four">
          <Button
            variant="contained"
            component="span"
            color = "primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              '&:hover': { backgroundColor: '#417A1A' }
            }}
            style={{ marginBottom: 10}}
            >
            Preview File
          </Button>
        </label>
        {showErrorMessageFour && (
          <div style={{
            color: 'red',
            fontSize: "13px",
            marginLeft: "5px",
            marginBottom: "12px"
            }}
          >
          Please Upload Valid .csv, .xlsx File
          </div>
        )}
        {isPreviewVisibleFour && (
          <Button
            variant="contained"
            color="primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              "&:hover": { backgroundColor: "#417A1A" },
            }}
            style={{ marginBottom: 10, marginLeft: 10 }}
            onClick={handleSendToBackEndFour}
            >
              Upload File
            </Button>
        )}
      </div>
      <h1
        style={{
          color: 'black',
          fontSize: 30,
          position: 'sticky',
          left: 0,
          marginTop: 0
        }}
      >
        Preview
      </h1>
      {configurationSpreadsheetDataFour.length > 0 ? (
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'flex-start'
          }}
        >
          <Box sx={{width: '100%'}}>
            <Paper sx={{width: '100%', overflow: 'hidden'}}>
              <TableContainer
                sx={{
                  width: '100%',
                  maxHeight: window.innerWidth >= 600 ? 700 : 350
                }}
              >
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      {configurationSpreadsheetDataFour[0].map((header, index) => (
                        <TableCell
                          key={index}
                          sx={{
                            fontWeight: 'bold',
                            backgroundColor: '#f0f0f0'
                          }}
                        >
                          {header}
                        </TableCell>
                      ))}
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {configurationSpreadsheetDataFour.slice(1).map((row, rowIndex) => (
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

    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'flex-start'
        }}
    >
      <header
        style={{
          color: "black",
          fontSize: 30,
          position: "sticky",
          left: 0,
          marginTop: 10,
          marginBottom: 5,
          fontWeight: "bold"
        }}
      >
        Upload Accreditations
      </header>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {selectedFileFive && (
          <div className={styles.fileUploadInput}>{selectedFileOne}</div>
        )}
        {!selectedFileFive && (
          <div className={styles.fileUploadInput}>No File Selected</div>
        )}
        <TextField
          type="file"
          id="file-upload-five"
          onChange={handleFileUploadFive}
          accept=".xlsx,.csv"
          style={{ display: 'none'}}
        />
        <label htmlFor="file-upload-five">
          <Button
            variant="contained"
            component="span"
            color = "primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              '&:hover': { backgroundColor: '#417A1A' }
            }}
            style={{ marginBottom: 10}}
            >
            Preview File
          </Button>
        </label>
        {showErrorMessageFive && (
          <div style={{
            color: 'red',
            fontSize: "13px",
            marginLeft: "5px",
            marginBottom: "12px"
            }}
          >
          Please Upload Valid .csv, .xlsx File
          </div>
        )}
        {isPreviewVisibleFive && (
          <Button
            variant="contained"
            color="primary"
            sx={{
              color: "white",
              backgroundColor: "#417A1A",
              "&:hover": { backgroundColor: "#417A1A" },
            }}
            style={{ marginBottom: 10, marginLeft: 10 }}
            onClick={handleSendToBackEndFive}
            >
              Upload File
            </Button>
        )}
      </div>
      <h1
        style={{
          color: 'black',
          fontSize: 30,
          position: 'sticky',
          left: 0,
          marginTop: 0
        }}
      >
        Preview
      </h1>
      {configurationSpreadsheetDataFive.length > 0 ? (
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'flex-start'
          }}
        >
          <Box sx={{width: '100%'}}>
            <Paper sx={{width: '100%', overflow: 'hidden'}}>
              <TableContainer
                sx={{
                  width: '100%',
                  maxHeight: window.innerWidth >= 600 ? 700 : 350
                }}
              >
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      {configurationSpreadsheetDataFive[0].map((header, index) => (
                        <TableCell
                          key={index}
                          sx={{
                            fontWeight: 'bold',
                            backgroundColor: '#f0f0f0'
                          }}
                        >
                          {header}
                        </TableCell>
                      ))}
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {configurationSpreadsheetDataFive.slice(1).map((row, rowIndex) => (
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
    </>
  );
};

export default ConfigurationExcelView;
