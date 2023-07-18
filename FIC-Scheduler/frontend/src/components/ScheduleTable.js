import React, { useState } from "react";

import styles from "../pages/InstructorCoordinatorHomePage.module.css";

import { getAsync } from "../utils"

export function ScheduleTable() {
  const [fetchResult, setFetchResult] = useState(null);

  getAsync("schedules/latest", setFetchResult);

  if ((fetchResult == null) || !(fetchResult.classScheduleAssignments instanceof Array)) {
    return (
      <div className={styles.Schedule} data-testid="schedule">
        <div>No schedules generated!</div>
      </div>);
  }

  return (
    <div className={styles.Schedule} data-testid="schedule">
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
          {fetchResult.classScheduleAssignments.map((row, rowIndex) => (
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

export default ScheduleTable;