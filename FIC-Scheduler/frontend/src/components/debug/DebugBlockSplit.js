import axios from 'axios';
import React, { useEffect, useState } from "react";

import styles from "../../pages/Common.module.css";

import { tokenConfig } from "../../utils"

export function DebugBlockSplit() {
  const [allBlockSplits, setAllBlockSplits] = useState(null);
  const [updateResponse, setUpdateResponse] = useState(null);
  const [formId, setFormId] = useState('');
  const [formName, setFormName] = useState('');
  const [formBlocks, setFormBlocks] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const clearForm = () => {
    setFormId('');
    setFormName('');
    setFormBlocks('');
  }

  useEffect(() => {
    clearForm();
    axios.get("api/block-splits", tokenConfig()).then(
      (response) => { setAllBlockSplits(response.data); },
      (_) => { setAllBlockSplits(null); });
  }, [updateResponse, setAllBlockSplits]);

  var data = (<div>No block splits</div>);
  if (((allBlockSplits ?? null) !== null) && (allBlockSplits instanceof Array)) {
    data = (
      <table>
        <thead>
          <tr>
            <th key="0">id</th>
            <th key="1">name</th>
            <th key="2">blocks</th>
          </tr>
        </thead>
        <tbody>
          {allBlockSplits.map((row, rowIndex) => (
            <tr key={rowIndex}>
              <td key="0">{row.id}</td>
              <td key="1">{row.name}</td>
              <td key="2">{JSON.stringify(row.blocks)}</td>
            </tr>
          ))}
        </tbody>
      </table>);
  }
  return (
    <div className={styles.DebugComponent} data-testid="debug-block-split">
      <h1>Block Splits</h1>
      {data}
      <form onSubmit={(event) => {
        event.preventDefault();
        //const blockSplitIdStr = (formId !== '') ? ('/' + formId) : '';
        const blockSplitObj = {}
        try {
          if (formId) blockSplitObj.id = formId;
          if (formName) blockSplitObj.name = formName;
          if (formBlocks) blockSplitObj.blocks = JSON.parse(formBlocks);
        }
        catch (error) {
          setErrorMessage("Couldn't make query: " + error.message);
          return;
        }

        axios.post("api/block-splits", [blockSplitObj], tokenConfig()).then(
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
              <td><label htmlFor="form-name">Room Number</label></td>
              <td><input id="form-name" type="text" name="formName" value={formName}
                onChange={(event) => setFormName(event.target.value)}
                placeholder="Don't update" autoFocus /></td>
            </tr>
            <tr>
              <td><label htmlFor="form-blocks">Room Name</label></td>
              <td><input id="form-blocks" type="text" name="formBlocks" value={formBlocks}
                onChange={(event) => setFormBlocks(event.target.value)}
                placeholder="Don't update" /></td>
            </tr>
          </tbody>
        </table>
        <button type="submit">Create/Update</button>
        <button onClick={(event) => {
          event.preventDefault();
          if ((formId ?? '') !== '') {
            axios.delete("api/block-splits/" + formId, tokenConfig()).then(
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

export default DebugBlockSplit;
