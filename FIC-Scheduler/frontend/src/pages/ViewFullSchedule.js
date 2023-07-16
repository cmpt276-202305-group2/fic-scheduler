import axios from 'axios';
import React, { Suspense, lazy } from "react";

import styles from "./InstructorCoordinatorHomePage.module.css";

import Sidebar from "../components/Sidebar";
import ErrorBoundary from "../components/ErrorBoundary"



function ViewFullSchedule() {
  async function LazyScheduleTable() {
    const response = await axios.get("schedules/latest");

    if (response.status === 200) {
      console.log("latest schedule", response.data);
    }

    return (<div className={styles.Schedule} data-testid="schedule">
      <table>
        <thead>
          <tr>
            <th key="0">id</th>
            <th key="1">courseNumber</th>
            <th key="2">partOfDay</th>
            <th key="3">classroom</th>
            <th key="4">instructor</th>
          </tr>
        </thead>
        <tbody>
          {response.data.classScheduleAssignments.map((row, rowIndex) => (
            <tr key={rowIndex}>
              <td key="0">{row.id}</td>
              <td key="1">{row.courseNumber}</td>
              <td key="2">{row.partOfDay}</td>
              <td key="3">{row.classroom.roomNumber}</td>
              <td key="4">{row.instructor.name}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>);
  };

  return (
    <React.Fragment>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar />
        </div>
        <ErrorBoundary fallback={<p>Could not fetch TV shows.</p>}>
          <Suspense fallback={<p>loading...</p>}>
            { lazy(LazyScheduleTable) }
          </Suspense>
        </ErrorBoundary>
      </div>
    </React.Fragment>
  );
}

export default ViewFullSchedule;
