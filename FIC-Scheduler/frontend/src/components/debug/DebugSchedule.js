import axios from 'axios';
import React, { useEffect, useState } from "react";

import styles from "../../pages/Common.module.css";

import { tokenConfig } from "../../utils"

export function DebugSchedule() {
  const [allSchedules, setAllSchedules] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState('');
  const [formName, setFormName] = useState('');
  const [formNotes, setFormNotes] = useState('');
  const [formCourses, setFormCourses] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const clearForm = () => {
    setFormId('');
    setFormName('');
    setFormNotes('');
    setFormCourses('');
  }

  useEffect(() => {
    clearForm();
    axios.get("api/schedules", tokenConfig()).then(
      (response) => { setAllSchedules(response.data); },
      (_) => { setAllSchedules(null); });
  }, [updateResponse, setAllSchedules]);

  var data = (<div>No schedules</div>);
  if (((allSchedules ?? null) !== null) && (allSchedules instanceof Array)) {
    data = (
      <table className={styles.DebugDataTable}>
        <thead>
          <tr>
            <th key="0">id</th>
            <th key="1">name</th>
            <th key="2">notes</th>
            <th key="3">assignments</th>
          </tr>
        </thead>
        <tbody>
          {allSchedules.map((row, rowIndex) => (
            <tr key={rowIndex}>
              <td key="0">{row.id}</td>
              <td key="1">{row.name}</td>
              <td key="2"><pre>{row.notes}</pre></td>
              <td key="3"><pre>{JSON.stringify(row.courses, null, 2)}</pre></td>
            </tr>
          ))}
        </tbody>
      </table>);
  }
  return (
    <div className={styles.DebugComponent} data-testid="debug-schedule">
      <h1>Schedules</h1>
      {data}
      <form onSubmit={(event) => {
        event.preventDefault();
        //const scheduleIdStr = (formId !== '') ? ('/' + formId) : '';
        const scheduleObj = {}
        try {
          if (formId) scheduleObj.id = formId;
          if (formName) scheduleObj.name = formName;
          if (formNotes) scheduleObj.notes = formNotes;
        }
        catch (error) {
          setErrorMessage("Couldn't make query: " + error.message);
          return;
        }

        axios.post("api/schedules", [scheduleObj], tokenConfig()).then(
          (response) => { setUpdateResponse(response); setErrorMessage(''); },
          (error) => {
            setErrorMessage(error.response ?
              error.response.status + ' ' + error.response.data : error.message);
          });
      }}>
        <h2>Create/Update</h2>
        <p style={{ color: 'red' }}>{errorMessage}</p>
        <table className={styles.DebugFormTable}>
          <tbody>
            <tr>
              <td><label htmlFor="form-id">ID</label></td>
              <td><input id="form-id" type="text" name="formId" value={formId}
                onChange={(event) => setFormId(event.target.value)}
                placeholder="Create new" /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-name">Name</label></td>
              <td><input id="form-name" type="text" name="formName" value={formName}
                onChange={(event) => setFormName(event.target.value)}
                placeholder="Don't update" autoFocus /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-notes">Notes</label></td>
              <td><input id="form-notes" type="text" name="formNotes" value={formNotes}
                onChange={(event) => setFormNotes(event.target.value)}
                placeholder="Don't update" size="50" /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-courses">Courses</label></td>
              <td><textarea id="form-courses" name="formCourses" value={formCourses}
                onChange={(event) => setFormCourses(event.target.value)}
                placeholder="Don't update" rows="10" cols="50" /></td>
            </tr>
          </tbody>
        </table>
        <button type="submit">Create/Update</button>
        <button onClick={(event) => {
          event.preventDefault();
          if ((formId ?? '') !== '') {
            axios.delete("api/schedules/" + formId, tokenConfig()).then(
              (response) => { setUpdateResponse(response); setErrorMessage(''); },
              (error) => {
                setErrorMessage(error.response ?
                  error.response.status + ' ' + error.response.data : error.message);
              });
          }
        }}>Delete</button>
      </form>
    </div>);
};

export default DebugSchedule;
