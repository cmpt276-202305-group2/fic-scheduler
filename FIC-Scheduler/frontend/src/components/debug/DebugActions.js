import axios from "axios";
import React, { useState } from "react";

import styles from "../../pages/Common.module.css";

import { tokenConfig } from "../../utils";

export function DebugActions() {
  const [updateResponse, setUpdateResponse] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);
  const [reqNo, setReqNo] = useState(0);

  const ActionButton = ({ url, label }) => {
    return (
      <button
        onClick={(event) => {
          event.preventDefault();
          axios.post(url, "", tokenConfig()).then(
            (response) => {
              setReqNo((r) => r + 1);
              setUpdateResponse(response.data);
              setErrorMessage(null);
            },
            (error) => {
              setReqNo((r) => r + 1);
              setUpdateResponse(error.response.data);
              setErrorMessage(error.message);
            }
          );
        }}
      >
        {label}
      </button>
    );
  };

  return (
    <div
      className={styles.DebugComponent}
      data-testid="debug-populate-test-blocks"
    >
      <h1>Debug Actions</h1>
      {reqNo === 0 ? (
        ""
      ) : (
        <p>
          {errorMessage ? (
            <span style={{ color: "red" }}>
              #{reqNo} {errorMessage}:{" "}
            </span>
          ) : (
            "#" + reqNo + " Request OK: "
          )}
          {updateResponse
            ? updateResponse instanceof String
              ? updateResponse
              : JSON.stringify(updateResponse)
            : "<nothing>"}
        </p>
      )}
      <ActionButton
        url="debug/populate-test-blocks"
        label="Populate Test Blocks"
      />
      <ActionButton
        url="debug/populate-test-classrooms"
        label="Populate Test Classrooms"
      />
      <ActionButton
        url="debug/populate-test-course-offerings"
        label="Populate Test Course Offerings"
      />
      <ActionButton
        url="debug/populate-test-instructors"
        label="Populate Test Instructors"
      />
      <ActionButton
        url="debug/populate-test-semester-plan"
        label="Populate Test Semester Plan"
      />
      <ActionButton url="debug/clear-data" label="Clear All Data" />
    </div>
  );
}

export default DebugActions;
