import axios from 'axios';
import React, { useEffect, useState } from "react";

import styles from "../../pages/Common.module.css";

import { tokenConfig } from "../../utils"

export function DebugUser() {
  const [fetchResult, setFetchResult] = useState(null);

  useEffect(() => {
    axios.get("api/users", tokenConfig()).then(
      (response) => { setFetchResult(response.data); },
      (_) => { setFetchResult(null); });
  }, [setFetchResult]);

  var data = (<div>No users</div>);
  if (((fetchResult ?? null) !== null) && (fetchResult instanceof Array)) {
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
          {fetchResult.map((row, rowIndex) => (
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
      {data}
      {/* {() => {
        axios.get("api/users", tokenConfig()).then(
          (response) => { setFetchResult(response.data); },
          (_) => { setFetchResult(null); });
      }} */}
    </div>);

};

export default DebugUser;
