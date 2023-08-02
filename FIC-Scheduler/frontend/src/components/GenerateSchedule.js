import React, { useState, useEffect } from "react";
import Button from "@mui/material/Button";
import axios from "axios";
import { tokenConfig } from "../utils";
import ScheduleTable from "../components/ScheduleTable";

function GenerateSchedule() {
  const [latestSemesterPlanId, setLatestSemesterPlanId] = useState(null);
  const [hasFetchedLatestId, setHasFetchedLatestId] = useState(false);
  const [showScheduleTable, setShowScheduleTable] = useState(false);
  const [scheduleExists, setScheduleExists] = useState(false);
  const [showErrorMessage, setShowErrorMessage] = useState(false);

  useEffect(() => {
    if (!hasFetchedLatestId) {
      axios
        .get("api/semester-plans/latest", tokenConfig())
        .then((response) => {
          const data = response.data;
          if (data.error) {
            console.log(data.error);
          } else {
            setLatestSemesterPlanId(data.id);
            setHasFetchedLatestId(true);
            setScheduleExists(data.scheduleExists);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    }
    if (latestSemesterPlanId === null) {
      setShowErrorMessage(true);
    } else {
      setShowErrorMessage(false);
    }
    axios
      .get("api/schedules", tokenConfig())
      .then((response) => {
        const data = response.data;
        if (data.length > 0) {
          setScheduleExists(true);
        }
      })
      .catch((error) => {
        console.log(error);
      });
  }, [hasFetchedLatestId, latestSemesterPlanId]);

  const handleGenerateSchedule = (event) => {
    event.preventDefault();

    if (scheduleExists) {
      console.log("A schedule already exists.");
      setShowScheduleTable(true);
      return;
    }

    const postData = { semesterPlan: { id: latestSemesterPlanId } };
    // console.log("this is postData:", postData);
    axios.post("api/generate-schedule", postData, tokenConfig()).then(
      (response) => {
        setShowScheduleTable(true);
        setScheduleExists(true);
      },
      (error) => {
        console.log(error);
      }
    );
  };
  return (
    <div>
      <Button
        onClick={handleGenerateSchedule}
        variant="contained"
        color="primary"
        sx={{
          color: "white",
          backgroundColor: "#417A1A",
          "&:hover": { backgroundColor: "#417A1A" },
        }}
        style={{ marginBottom: 15 }}
      >
        Generate Schedule
      </Button>
      {showErrorMessage && (
        <div style={{ fontSize: 20 }}>Please Import all the Files Properly</div>
      )}
      {showScheduleTable && <ScheduleTable />}
    </div>
  );
}

export default GenerateSchedule;
