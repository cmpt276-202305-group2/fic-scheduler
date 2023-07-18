import { useState } from "react";
import { Navigate } from "react-router-dom";
import styles from "./Common.module.css";
import CheckAuth from "../components/CheckAuth";
import Sidebar from "../components/Sidebar";
import ConfigurationExcelView from "../components/ConfigurationExcelView";

function ConfigurationPage() {
    const [configurationSpreadsheetDataOne, setConfigurationSpreadsheetDataOne] = useState([]); 
    const [configurationSpreadsheetDataTwo, setConfigurationSpreadsheetDataTwo] = useState([]); 
  return (
    <CheckAuth permittedRoles={["ADMIN", "COORDINATOR"]} fallback={<Navigate to="/" replace />}>
      <div className={styles.Container}>
        <div className={styles.Sidebar}>
          <Sidebar />
        </div>
        <div className={styles.PageContent} data-testid="schedule">
          <ConfigurationExcelView 
            configurationSpreadsheetDataOne={configurationSpreadsheetDataOne} 
            setConfigurationSpreadsheetDataOne={setConfigurationSpreadsheetDataOne}
            configurationSpreadsheetDataTwo={configurationSpreadsheetDataTwo}
            setConfigurationSpreadsheetDataTwo={setConfigurationSpreadsheetDataTwo}
          />
        </div>
      </div>
    </CheckAuth>
  );
}

export default ConfigurationPage;
