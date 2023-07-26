import axios from 'axios';
import React, { useEffect, useState } from "react";

import styles from "../../pages/Common.module.css";

import { tokenConfig } from "../../utils"

export function DebugCourseOffering() {
  const [allCourseOfferings, setAllCourseOfferings] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState('');
  const [formName, setFormName] = useState('');
  const [formCourseNumber, setFormCourseNumber] = useState('');
  const [formNotes, setFormNotes] = useState('');
  const [formApprovedInstructors, setFormApprovedInstructors] = useState('');
  const [formAllowedBlockSplits, setFormAllowedBlockSplits] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const clearForm = () => {
    setFormId('');
    setFormName('');
    setFormCourseNumber('');
    setFormNotes('');
    setFormApprovedInstructors('');
    setFormAllowedBlockSplits('');
  }

  useEffect(() => {
    clearForm();
    axios.get("api/course-offerings", tokenConfig()).then(
      (response) => { setAllCourseOfferings(response.data); },
      (_) => { setAllCourseOfferings(null); });
  }, [updateResponse, setAllCourseOfferings]);

  var data = (<div>No course offerings</div>);
  if (((allCourseOfferings ?? null) !== null) && (allCourseOfferings instanceof Array)) {
    data = (
      <table>
        <thead>
          <tr>
            <th key="0">id</th>
            <th key="1">name</th>
            <th key="2">course number</th>
            <th key="3">notes</th>
            <th key="4">approved instructors</th>
            <th key="5">allowed block splits</th>
          </tr>
        </thead>
        <tbody>
          {allCourseOfferings.map((row, rowIndex) => (
            <tr key={rowIndex}>
              <td key="0">{row.id}</td>
              <td key="1">{row.name}</td>
              <td key="2">{row.courseNumber}</td>
              <td key="3">{row.notes}</td>
              <td key="4">{JSON.stringify(row.approvedInstructors)}</td>
              <td key="5">{JSON.stringify(row.allowedBlockSplits)}</td>
            </tr>
          ))}
        </tbody>
      </table>);
  }
  return (
    <div className={styles.DebugComponent} data-testid="debug-course-offering">
      <h1>Course Offerings</h1>
      {data}
      <form onSubmit={(event) => {
        event.preventDefault();
        //const courseOfferingIdStr = (formId !== '') ? ('/' + formId) : '';
        const courseOfferingObj = {}
        try {
          if (formId) courseOfferingObj.id = formId;
          if (formName) courseOfferingObj.name = formCourseNumber;
          if (formCourseNumber) courseOfferingObj.courseNumber = formCourseNumber;
          if (formNotes) courseOfferingObj.notes = formNotes;
          if (formApprovedInstructors) courseOfferingObj.approvedInstructors = JSON.parse(formApprovedInstructors);
          if (formAllowedBlockSplits) courseOfferingObj.allowedBlockSplits = JSON.parse(formAllowedBlockSplits);
        }
        catch (error) {
          setErrorMessage("Couldn't make query: " + error.message);
          return;
        }

        axios.post("api/course-offerings", [courseOfferingObj], tokenConfig()).then(
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
              <td><label htmlFor="form-course-number">Course Number</label></td>
              <td><input id="form-course-number" type="text" name="formCourseNumber" value={formCourseNumber}
                onChange={(event) => setFormCourseNumber(event.target.value)}
                placeholder="Don't update" autoFocus /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-notes">Notes</label></td>
              <td><input id="form-notes" type="text" name="formNotes" value={formNotes}
                onChange={(event) => setFormNotes(event.target.value)}
                placeholder="Don't update" size="50" /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-approved-instructors">Approved Instructors</label></td>
              <td><textarea id="form-approved-instructors" name="formApprovedInstructors" value={formApprovedInstructors}
                onChange={(event) => setFormApprovedInstructors(event.target.value)}
                placeholder="Don't update" rows="10" cols="50" /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-allowed-block-splits">Allowed Block Splits</label></td>
              <td><textarea id="form-allowed-block-splits" name="formAllowedBlockSplits" value={formAllowedBlockSplits}
                onChange={(event) => setFormAllowedBlockSplits(event.target.value)}
                placeholder="Don't update" rows="10" cols="50" /></td>
            </tr>
          </tbody>
        </table>
        <button type="submit">Create/Update</button>
        <button onClick={(event) => {
          event.preventDefault();
          if ((formId ?? '') !== '') {
            axios.delete("api/course-offerings/" + formId, tokenConfig()).then(
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

export default DebugCourseOffering;
