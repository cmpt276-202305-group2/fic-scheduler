import axios from 'axios';
import React, { useEffect, useState } from "react";

import styles from "../../pages/Common.module.css";

import { tokenConfig } from "../../utils"

export function DebugSemesterPlan() {
  const [allSemesterPlans, setAllSemesterPlans] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState('');
  const [formName, setFormName] = useState('');
  const [formNotes, setFormNotes] = useState('');
  const [formSemester, setFormSemester] = useState('');
  const [formCoursesOffered, setFormCoursesOffered] = useState('');
  const [formInstructorsAvailable, setFormInstructorsAvailable] = useState('');
  const [formClassroomsAvailable, setFormClassroomsAvailable] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const clearForm = () => {
    setFormId('');
    setFormName('');
    setFormNotes('');
    setFormSemester('');
    setFormCoursesOffered('');
    setFormInstructorsAvailable('');
    setFormClassroomsAvailable('');
  }

  useEffect(() => {
    clearForm();
    axios.get("api/semester-plans", tokenConfig()).then(
      (response) => { setAllSemesterPlans(response.data); },
      (_) => { setAllSemesterPlans(null); });
  }, [updateResponse, setAllSemesterPlans]);

  var data = (<div>No semester plans</div>);
  if (((allSemesterPlans ?? null) !== null) && (allSemesterPlans instanceof Array)) {
    data = (
      <table className={styles.DebugDataTable}>
        <thead>
          <tr>
            <th key="generate"></th>
            <th key="0">id</th>
            <th key="1">name</th>
            <th key="2">notes</th>
            <th key="3">semester</th>
            <th key="4">courses offered</th>
            <th key="5">instructors available</th>
            <th key="6">classrooms available</th>
          </tr>
        </thead>
        <tbody>
          {
            allSemesterPlans.map((row, rowIndex) => {
              const rowId = row.Id;
              return (
                <tr key={rowIndex}>
                  <td key="generate"><a href="script:void;" onClick={
                    (_) => {
                      axios.post("api/generate-schedule", { semesterPlan: { id: rowId } }, tokenConfig()).then(
                        (response) => { setAllSemesterPlans(response.data); },
                        (_) => { setAllSemesterPlans(null); });
                    }
                  }>gen</a></td>
                  <td key="0">{row.id}</td>
                  <td key="1">{row.name}</td>
                  <td key="2">{row.notes}</td>
                  <td key="3">{row.semester}</td>
                  <td key="4"><pre>{JSON.stringify(row.coursesOffered, null, 2)}</pre></td>
                  <td key="5"><pre>{JSON.stringify(row.instructorsAvailable, null, 2)}</pre></td>
                  <td key="6"><pre>{JSON.stringify(row.classroomsAvailable, null, 2)}</pre></td>
                </tr>
              );
            })
          }
        </tbody>
      </table >);
  }
  return (
    <div className={styles.DebugComponent} data-testid="debug-semester-plan">
      <h1>Semester Plans</h1>
      {data}
      <form onSubmit={(event) => {
        event.preventDefault();
        //const semesterPlanIdStr = (formId !== '') ? ('/' + formId) : '';
        const semesterPlanObj = {}
        try {
          if (formId) semesterPlanObj.id = formId;
          if (formName) semesterPlanObj.name = formName;
          if (formNotes) semesterPlanObj.notes = formNotes;
          if (formSemester) semesterPlanObj.semester = formSemester;
          if (formCoursesOffered) semesterPlanObj.coursesOffered = JSON.parse(formCoursesOffered);
          if (formInstructorsAvailable) semesterPlanObj.instructorsAvailable = JSON.parse(formInstructorsAvailable);
          if (formClassroomsAvailable) semesterPlanObj.classroomsAvailable = JSON.parse(formClassroomsAvailable);
        }
        catch (error) {
          setErrorMessage("Couldn't make query: " + error.message);
          return;
        }

        axios.post("api/semester-plans", [semesterPlanObj], tokenConfig()).then(
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
              <td><label htmlFor="form-semester">Semester</label></td>
              <td><input id="form-semester" type="text" name="formSemester" value={formSemester}
                onChange={(event) => setFormSemester(event.target.value)}
                placeholder="Don't update" /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-courses-offered">Courses Offered</label></td>
              <td><textarea id="form-courses-offered" name="formCoursesOffered" value={formCoursesOffered}
                onChange={(event) => setFormCoursesOffered(event.target.value)}
                placeholder="Don't update" rows="10" cols="50" /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-instructors-available">Instructors Available</label></td>
              <td><textarea id="form-instructors-available" name="formInstructorsAvailable" value={formInstructorsAvailable}
                onChange={(event) => setFormInstructorsAvailable(event.target.value)}
                placeholder="Don't update" rows="10" cols="50" /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-classrooms-available">Classrooms Available</label></td>
              <td><textarea id="form-classrooms-available" name="formClassroomsAvailable" value={formClassroomsAvailable}
                onChange={(event) => setFormClassroomsAvailable(event.target.value)}
                placeholder="Don't update" rows="10" cols="50" /></td>
            </tr>
          </tbody>
        </table>
        <button type="submit">Create/Update</button>
        <button onClick={(event) => {
          event.preventDefault();
          if ((formId ?? '') !== '') {
            axios.delete("api/semester-plans/" + formId, tokenConfig()).then(
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

export default DebugSemesterPlan;
