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
  const [loading, setLoading] = useState(false);

  const deleteAllTables = async () => {
    let response = null;
    try {
      // delete schedules
      response = await axios.get("api/schedules", tokenConfig());
      for (const schedule of response.data) {
        await axios.delete(`api/schedules/${schedule.id}`, tokenConfig());
      }

      // delete semester plans
      response = await axios.get("api/semester-plans", tokenConfig());
      for (const semesterPlan of response.data) {
        await axios.delete(
          `api/semester-plans/${semesterPlan.id}`,
          tokenConfig()
        );
      }

      // delete classrooms
      response = await axios.get("api/classrooms", tokenConfig());
      for (const classroom of response.data) {
        await axios.delete(`api/classrooms/${classroom.id}`, tokenConfig());
      }

      // delete instructors
      response = await axios.get("api/instructors", tokenConfig());
      for (const instructor of response.data) {
        await axios.delete(`api/instructors/${instructor.id}`, tokenConfig());
      }

      // delete block splits
      response = await axios.get("api/block-splits", tokenConfig());
      for (const blockSplit of response.data) {
        await axios.delete(`api/block-splits/${blockSplit.id}`, tokenConfig());
      }

      alert("All tables have been deleted successfully");
    } catch (error) {
      console.log(error);
    }
  };

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
    setLoading(true);

    if (scheduleExists) {
      console.log("A schedule already exists.");
      setShowScheduleTable(true);
      return;
    }

    const postData = { semesterPlan: { id: latestSemesterPlanId } };
    // console.log("this is postData:", postData);
    axios.post("api/generate-schedule", postData, tokenConfig()).then(
      (response) => {
        if (response.data.courses.length === 0) {
          alert(
            "Generation failed. Please check your data, clear all the fields and try again."
          );
        }
        setLoading(false);
        setShowScheduleTable(true);
        setScheduleExists(true);
      },
      (error) => {
        console.log(error);
        setLoading(false);
        alert(
          "Error generating schedule either the tables are missing or a system problem has occurred."
        );
      }
    );
  };
  return (
    <div>
      {loading ? (
        <h1 style={{ textAlign: "center" }}>
          Generating schedule, please wait...
        </h1>
      ) : (
        <>
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
          <br></br>
          <Button
            sx={{
              color: "white",
              backgroundColor: "#9f4141",
              "&:hover": { backgroundColor: "#742e2e" },
              marginBottom: 2,
            }}
            onClick={deleteAllTables}
          >
            Delete all Tables and Schedule
          </Button>

          {showErrorMessage && (
            <div style={{ fontSize: 20 }}>
              Please Import all the Files Properly
            </div>
          )}
          {showScheduleTable && <ScheduleTable />}
        </>
      )}
    </div>
  );
}

export default GenerateSchedule;
