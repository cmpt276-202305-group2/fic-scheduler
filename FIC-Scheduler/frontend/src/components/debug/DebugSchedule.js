import axios from 'axios';
import React, { useEffect, useState } from "react";

import styles from "../../pages/Common.module.css";

import { tokenConfig } from "../../utils"

export function DebugSchedule() {
  const [latestSchedule, setLatestSchedule] = useState(null);
  const [schedules, setSchedules] = useState(null);

  useEffect(() => {
    axios.get("api/schedules/latest", tokenConfig()).then(
      (response) => { setLatestSchedule(response.data); },
      (_) => { setLatestSchedule(null); });
    axios.get("api/schedules", tokenConfig()).then(
      (response) => { setSchedules(response.data); },
      (_) => { setSchedules(null); });
  }, [setLatestSchedule, setSchedules]);

  if ((latestSchedule === null) || !(latestSchedule.classScheduleAssignments instanceof Array)) {
    return (
      <div className={styles.PageContent} data-testid="debug-schedule">
        <div>No schedules</div>
      </div>);
  }

  return (
    <div className={styles.PageContent} data-testid="debug-schedule">
      <div>
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
            {latestSchedule.classScheduleAssignments.map((row, rowIndex) => (
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
      </div>
      <div>
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
            {schedules.map((row, rowIndex) => (
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
      </div>
    </div>);
};

export default DebugSchedule;
