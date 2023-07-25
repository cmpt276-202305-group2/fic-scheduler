import axios from 'axios';
import React, { useEffect, useState } from "react";

import styles from "../../pages/Common.module.css";

import { tokenConfig } from "../../utils"

export function DebugInstructor() {
  const [allInstructors, setAllInstructors] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState('');
  const [formName, setFormName] = useState('');
  const [formNotes, setFormNotes] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const clearForm = () => {
    setFormId('');
    setFormName('');
    setFormNotes('');
  }

  useEffect(() => {
    clearForm();
    axios.get("api/instructors", tokenConfig()).then(
      (response) => { setAllInstructors(response.data); },
      (_) => { setAllInstructors(null); });
  }, [updateResponse, setAllInstructors]);

  var data = (<div>No instructors</div>);
  if (((allInstructors ?? null) !== null) && (allInstructors instanceof Array)) {
    data = (
      <table>
        <thead>
          <tr>
            <th key="0">id</th>
            <th key="1">name</th>
            <th key="2">notes</th>
          </tr>
        </thead>
        <tbody>
          {allInstructors.map((row, rowIndex) => (
            <tr key={rowIndex}>
              <td key="0">{row.id}</td>
              <td key="1">{row.name}</td>
              <td key="2">{row.notes}</td>
            </tr>
          ))}
        </tbody>
      </table>);
  }
  return (
    <div className={styles.DebugComponent} data-testid="debug-instructor">
      <h1>Instructors</h1>
      {data}
      <form onSubmit={(event) => {
        event.preventDefault();
        //const instructorIdStr = (formId !== '') ? ('/' + formId) : '';
        const instructorObj = {}
        try {
          if (formId) instructorObj.id = formId;
          if (formName) instructorObj.name = formName;
          if (formNotes) instructorObj.notes = formNotes;
        }
        catch (error) {
          setErrorMessage("Couldn't make query: " + error.message);
          return;
        }

        axios.post("api/instructors", [instructorObj], tokenConfig()).then(
          (response) => { setUpdateResponse(response); setErrorMessage(''); },
          (error) => {
            setErrorMessage(error.response ?
              error.response.status + ' ' + error.response.data : error.message);
          });
      }}>
        <h2>Create/Update</h2>
        <p style={{ color: 'red' }}>{errorMessage}</p>
        <table>
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
                placeholder="Don't update" /></td>
            </tr>
          </tbody>
        </table>
        <button type="submit">Create/Update</button>
        <button onClick={(event) => {
          event.preventDefault();
          if ((formId ?? '') !== '') {
            axios.delete("api/instructors/" + formId, tokenConfig()).then(
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

export default DebugInstructor;
