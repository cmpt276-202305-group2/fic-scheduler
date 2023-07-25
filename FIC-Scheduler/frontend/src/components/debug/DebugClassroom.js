import axios from 'axios';
import React, { useEffect, useState } from "react";

import styles from "../../pages/Common.module.css";

import { tokenConfig } from "../../utils"

export function DebugClassroom() {
  const [allClassrooms, setAllClassrooms] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState('');
  const [formRoomNumber, setFormRoomNumber] = useState('');
  const [formRoomType, setFormRoomType] = useState('');
  const [formNotes, setFormNotes] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const clearForm = () => {
    setFormId('');
    setFormRoomNumber('');
    setFormRoomType('');
  }

  useEffect(() => {
    clearForm();
    axios.get("api/classrooms", tokenConfig()).then(
      (response) => { setAllClassrooms(response.data); },
      (_) => { setAllClassrooms(null); });
  }, [updateResponse, setAllClassrooms]);

  var data = (<div>No classrooms</div>);
  if (((allClassrooms ?? null) !== null) && (allClassrooms instanceof Array)) {
    data = (
      <table>
        <thead>
          <tr>
            <th key="0">id</th>
            <th key="1">roomNumber</th>
            <th key="2">roomType</th>
            <th key="3">notes</th>
          </tr>
        </thead>
        <tbody>
          {allClassrooms.map((row, rowIndex) => (
            <tr key={rowIndex}>
              <td key="0">{row.id}</td>
              <td key="1">{row.roomNumber}</td>
              <td key="2">{row.roomType}</td>
              <td key="3">{row.notes}</td>
            </tr>
          ))}
        </tbody>
      </table>);
  }
  return (
    <div className={styles.DebugComponent} data-testid="debug-classroom">
      <h1>Classrooms</h1>
      {data}
      <form onSubmit={(event) => {
        event.preventDefault();
        //const classroomIdStr = (formId !== '') ? ('/' + formId) : '';
        const classroomObj = {}
        try {
          if (formId) classroomObj.id = formId;
          if (formRoomNumber) classroomObj.roomNumber = formRoomNumber;
          if (formRoomType) classroomObj.roomType = formRoomType;
          classroomObj.notes = formNotes;
        }
        catch (error) {
          setErrorMessage("Couldn't make query: " + error.message);
          return;
        }

        axios.post("api/classrooms", [classroomObj], tokenConfig()).then(
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
              <td><label htmlFor="form-room-number">Room Number</label></td>
              <td><input id="form-room-number" type="text" name="formRoomNumber" value={formRoomNumber}
                onChange={(event) => setFormRoomNumber(event.target.value)}
                placeholder="Don't update" autoFocus /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-room-type">Room Type</label></td>
              <td><input id="form-room-type" type="text" name="formRoomType" value={formRoomType}
                onChange={(event) => setFormRoomType(event.target.value)}
                placeholder="Don't update" /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-notes">Notes</label></td>
              <td><input id="form-notes" type="text" name="formNotes" value={formNotes}
                onChange={(event) => setFormNotes(event.target.value)}
                placeholder="" /></td>
            </tr>
          </tbody>
        </table>
        <button type="submit">Create/Update</button>
        <button onClick={(event) => {
          event.preventDefault();
          if ((formId ?? '') !== '') {
            axios.delete("api/classrooms/" + formId, tokenConfig()).then(
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

export default DebugClassroom;
