import "./App.css";
import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import InstructorHomePage from "./pages/InstructorHomePage";
import CoordinatorHomePage from "./pages/CoordinatorHomePage";
import React from "react";
import LogoutPage from "./pages/LogoutPage";
import CheckAuth from "./components/CheckAuth";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/logout" element={<LogoutPage />} />

        <Route
          path="/instructor"
          element={
            <CheckAuth roles={["INSTRUCTOR", "COORDINATOR"]}>
              <InstructorHomePage />
            </CheckAuth>
          }
        />

        <Route
          path="/coordinator"
          element={
            <CheckAuth roles={["COORDINATOR"]}>
              <CoordinatorHomePage />
            </CheckAuth>
          }
        />

        <Route path="/*" element={<Navigate to="/login" replace />} />
      </Routes>
    </div>
  );
}

export default App;
