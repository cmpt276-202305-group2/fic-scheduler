import React, { useState } from "react";
import axios from "axios";
import readExcelFile from "./readExcelfile";
import styles from "./FileImport.module.css";
import Button from "@mui/material/Button";
import { ViewUploadedCourseOfferingList } from "./ViewUploadedCourseOfferingList";
import { tokenConfig } from "../utils";
import { FileUploader, SpreadsheetTable } from "./FileUploader";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const ImportCourses = ({
  coursesSpreadsheetData,
  setCoursesSpreadsheetData,
}) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [isPreviewVisible, setIsPreviewVisible] = useState(false);
  const [incorrectFormat, setIncorrectFormat] = useState(null);
  //   const [showDeleteButton, setShowDeleteButton] = useState(false);
  const [isCourseOfferingListVisible, setIsCourseOfferingListVisible] =
    useState(false);

  const handleShowCourseOfferingList = () => {
    setIsCourseOfferingListVisible(
      (prevIsCourseOfferingListVisible) => !prevIsCourseOfferingListVisible
    );
  };

  const createFileUploadHandler =
    (setFile, setErrorMessage, setData, setIsPreviewVisible) =>
      async (event) => {
        const file = event.target.files[0];
        if (!file) return;
        const allowedFormats = ["xlsx", "csv"];
        const fileExtension = file.name.split(".").pop().toLowerCase();
        if (!allowedFormats.includes(fileExtension)) {
          setErrorMessage(true);
          return;
        }
        setErrorMessage(false);
        try {
          const data = await readExcelFile(file);
          setData(data);
          setFile(file.name);
          setIsPreviewVisible(true);
        } catch (error) {
          console.error("Error reading Excel file:", error);
        }
      };

  const handleFileUpload = createFileUploadHandler(
    setSelectedFile,
    setShowErrorMessage,
    setCoursesSpreadsheetData,
    setIsPreviewVisible
  );

  const handleDelete = async () => {
    let response = null;
    try {
      response = await axios.get("api/semester-plans", tokenConfig());
      const semesterPlan = response.data[response.data.length - 1];
      semesterPlan.coursesOffered = [];
      await axios.post("api/semester-plans", [semesterPlan], tokenConfig());

      response = await axios.get("api/course-offerings", tokenConfig());
      const currentCourses = response.data;

      if (currentCourses.length > 0) {
        for (const course of currentCourses) {
          await axios.delete(
            `api/course-offerings/${course.id}`,
            tokenConfig()
          );
        }
      }
      setIsCourseOfferingListVisible(
        (prevIsCourseOfferingListVisible) => !prevIsCourseOfferingListVisible
      );
      toast.info("All Uploaded Courses have been deleted.", {
        position: "top-right",
        autoClose: 6000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "light",
      });
    } catch (error) {
      console.error("Error deleting uploaded courses:", error);
    }
  };

  const handleSendToBackEnd = async () => {
    if (coursesSpreadsheetData.length === 0) {
      return;
    }
    const jsonData = coursesSpreadsheetData.filter((row) => !!row[0].trim()).map((row) => {
      return {
        id: null,
        name: row[0].trim(),
        courseNumber: row[1].trim(),
        approvedInstructors: [
          row[2].trim(),
          row[3].trim(),
          row[4].trim(),
          row[5].trim(),
          row[6].trim(),
          row[7].trim(),
        ].filter(Boolean),
        allowedBlockSplits: [
          [row[8].trim(), row[9].trim().toUpperCase()],
          [row[10].trim(), row[11].trim().toUpperCase()],
          [row[12].trim(), row[13].trim().toUpperCase()],
        ],
        notes: row[14].trim(),
      };
    }).filter((c) => !!(c.name && c.courseNumber && c.approvedInstructors.length && c.allowedBlockSplits));

    console.log("jsonData:", jsonData);

    try {
      const uploadedCourses = [];
      uploadedCourses.push(...jsonData.slice(1));
      let response = null;
      const importedBlockSplits = new Map();
      const roomTypes = new Map();
      let roomTypeNum = 1;

      for (let row of uploadedCourses) {
        const blocks = [];
        let blockSplitStr = "";
        let nameStr = "";
        const { allowedBlockSplits } = row;
        for (const [roomType, duration] of allowedBlockSplits) {
          // Check input format
          if (!roomType && !duration) {
            continue;
          }
          if (!roomType) {
            setIncorrectFormat("bad roomType format in the excel file");
            continue; // or bail?
          }

          // Convert unsafe roomType string to safer numeric ID
          if (!roomTypes.has(roomType)) {
            roomTypes.set(roomType, roomTypeNum++);
          }
          const roomTypeId = roomTypes.get(roomType);

          // Parse duration
          const halfDuration = duration === "HALF";
          const fullDuration = duration === "FULL";
          if (!halfDuration && !fullDuration) {
            setIncorrectFormat("bad duration format in the excel file");
            continue; // or bail?
          }

          // The first part of this tuple is a string identifier like H3 or F7
          blocks.push([duration[0] + roomTypeId, { duration, roomType }]);

          if (nameStr.length > 0) {
            nameStr += " + ";
          }
          nameStr += roomType + "/" + duration;
        }
        // The leading string element in tuple makes the list sortable
        blocks.sort();

        // blockSplitStr should be something like H2 or F2 or F5H2
        blockSplitStr += blocks.map(([blockStr, _block]) => blockStr).join("");

        const blockSplit = {
          id: null,
          name: nameStr,
          blocks: blocks.map(([_blockStr, block]) => block),
        };

        importedBlockSplits.set(blockSplitStr, blockSplit);
        row.allowedBlockSplits = [nameStr];
      }
      console.log("importedBlockSplits: ", importedBlockSplits);

      response = await axios.get("api/block-splits", tokenConfig());
      let oldBlocks = response.data;
      if (oldBlocks.length > 0) {
        for (const block of oldBlocks) {
          console.log("block: ", block);
          await axios.delete(`api/block-splits/${block.id}`, tokenConfig());
        }
      }
      const blockSplitsArray = [];
      for (const blockSplit of importedBlockSplits.values()) {
        blockSplitsArray.push(blockSplit);
      }

      response = await axios.post(
        "api/block-splits",
        blockSplitsArray,
        tokenConfig()
      );
      const blockSplitIdsHolder = response.data;
      //   console.log("blockSplitIdsHolder: ", blockSplitIdsHolder);

      response = await axios.get("api/instructors", tokenConfig());
      const currentInstructors = response.data;

      uploadedCourses.forEach((course) => {
        // Mapping approvedInstructors
        course.approvedInstructors = course.approvedInstructors.map(
          (instructorName) => {
            const instructor = currentInstructors.find(
              (instructorObj) => instructorObj.name === instructorName
            );
            return instructor ? { id: instructor.id } : null;
          }
        );

        // Replacing allowedBlockSplits value with corresponding id
        blockSplitIdsHolder.forEach((blockSplit) => {
          if (course.allowedBlockSplits[0] === blockSplit.name) {
            course.allowedBlockSplits = [{ id: blockSplit.id }];
          }
        });
      });

      //   console.log("uploadedCourses: ", uploadedCourses);

      response = await axios.post(
        "api/course-offerings",
        uploadedCourses,
        tokenConfig()
      );
      //   console.log("response data:", response.data);
      const finalArray = [];

      response.data.forEach((course) => {
        finalArray.push([{ id: course.id }]);
      });

      response = await axios.get("api/semester-plans", tokenConfig());
      const semesterPlan = response.data[response.data.length - 1];
      semesterPlan.coursesOffered = finalArray;

      response = await axios.post(
        "api/semester-plans",
        [semesterPlan],
        tokenConfig()
      );

      //   console.log("GET ME OUT: ", response.data);
      if (response.status === 200) {
        setShowSuccessMessage(true);
      }
    } catch (error) {
      console.error("Error sending JSON data to the backend:", error);
    }
  };

  return (
    <div className={styles.tableHolder}>
      <h2 className={styles.title}>Course Offering</h2>
      <FileUploader
        selectedFile={selectedFile}
        handleFileUpload={handleFileUpload}
        showErrorMessage={showErrorMessage}
        isPreviewVisible={isPreviewVisible}
        handleSendToBackend={handleSendToBackEnd}
        id={"4"}
        styles={styles}
      />
      {showSuccessMessage && (
        <h2 className={styles.successMessage}>
          List of Course Offering successfully uploaded!
        </h2>
      )}
      {incorrectFormat != null && (
        <h2 className={styles.errorMessage}>{incorrectFormat}</h2>
      )}

      <SpreadsheetTable
        spreadsheetData={coursesSpreadsheetData}
        styles={styles}
      />
      <Button
        onClick={handleShowCourseOfferingList}
        variant="contained"
        color="primary"
        sx={{
          color: "white",
          backgroundColor: "#417A1A",
          "&:hover": { backgroundColor: "#417A1A" },
        }}
        style={{ marginBottom: 10, marginTop: 10 }}
      >
        {isCourseOfferingListVisible ? "Hide" : "Show"} Current Course Offerings
      </Button>
      {isCourseOfferingListVisible && (
        <Button
          sx={{
            color: "white",
            backgroundColor: "#9f4141",
            "&:hover": { backgroundColor: "#742e2e" },
            marginBottom: 2,
          }}
          onClick={handleDelete}
          variant="contained"
        >
          {" "}
          Delete Uploaded Course Offerings{" "}
        </Button>
      )}
      {isCourseOfferingListVisible && <ViewUploadedCourseOfferingList />}
    </div>
  );
};

export default ImportCourses;
