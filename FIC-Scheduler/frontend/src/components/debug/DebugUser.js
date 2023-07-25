import axios from 'axios';
import React, { useEffect, useState } from "react";

import styles from "../../pages/Common.module.css";

import { tokenConfig } from "../../utils"

export function DebugUser() {
  const [allUsers, setAllUsers] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState('');
  const [formUsername, setFormUsername] = useState('');
  const [formPassword, setFormPassword] = useState('');
  const [formRoles, setFormRoles] = useState('');
  const [formFullName, setFormFullName] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const clearForm = () => {
    setFormId('');
    setFormUsername('');
    setFormPassword('');
    setFormRoles('');
    setFormFullName('');
  }

  useEffect(() => {
    clearForm();
    axios.get("api/users", tokenConfig()).then(
      (response) => { setAllUsers(response.data); },
      (_) => { setAllUsers(null); });
  }, [updateResponse, setAllUsers]);

  var data = (<div>No users</div>);
  if (((allUsers ?? null) !== null) && (allUsers instanceof Array)) {
    data = (
      <table>
        <thead>
          <tr>
            <th key="0">id</th>
            <th key="1">username</th>
            <th key="2">password</th>
            <th key="3">roles</th>
            <th key="4">fullName</th>
          </tr>
        </thead>
        <tbody>
          {allUsers.map((row, rowIndex) => (
            <tr key={rowIndex}>
              <td key="0">{row.id}</td>
              <td key="1">{row.username}</td>
              <td key="2">{row.password ?? '*'}</td>
              <td key="3">{'[' + row.roles.join(', ') + ']'}</td>
              <td key="4">{row.fullName}</td>
            </tr>
          ))}
        </tbody>
      </table>);
  }
  return (
    <div className={styles.DebugComponent} data-testid="debug-user">
      <h1>Users</h1>
      {data}
      <form onSubmit={(event) => {
        event.preventDefault();
        //const userIdStr = (formId !== '') ? ('/' + formId) : '';
        const userObj = {}
        try {
          if (formId) userObj.id = formId;
          if (formUsername) userObj.username = formUsername;
          if (formPassword) userObj.password = formPassword;
          if (formRoles) {
            userObj.roles =
              formRoles.split(' ').filter((o) => !!o)
                .map((o) => ('' + o).trim().toUpperCase());
          }
          if (formFullName) userObj.fullName = formFullName;
        }
        catch (error) {
          setErrorMessage("Couldn't make query: " + error.message);
          return;
        }

        axios.post("api/users", [userObj], tokenConfig()).then(
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
              <td><label htmlFor="form-username">Username</label></td>
              <td><input id="form-username" type="text" name="formUsername" value={formUsername}
                onChange={(event) => setFormUsername(event.target.value)}
                placeholder="Don't update" autoFocus /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-password">Password</label></td>
              <td><input id="form-password" type="text" name="formPassword" value={formPassword}
                onChange={(event) => setFormPassword(event.target.value)}
                placeholder="Don't update" /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-roles">Roles</label></td>
              <td><input id="form-roles" type="text" name="formRoles" value={formRoles}
                onChange={(event) => setFormRoles(event.target.value)}
                placeholder="Don't update" /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-full-name">Full Name</label></td>
              <td><input id="form-full-name" type="text" name="formFullName" value={formFullName}
                onChange={(event) => setFormFullName(event.target.value)}
                placeholder="Don't update" /></td>
            </tr>
          </tbody>
        </table>
        <button type="submit">Create/Update</button>
        <button onClick={(event) => {
          event.preventDefault();
          if ((formId ?? '') !== '') {
            axios.delete("api/users/" + formId, tokenConfig()).then(
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

export default DebugUser;
