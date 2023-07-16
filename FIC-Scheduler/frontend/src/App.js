import axios from 'axios';
import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";

import "./App.css";

import CheckAuth from "./components/CheckAuth";

import CoordinatorHomePage from "./pages/CoordinatorHomePage";
import GenerateSchedule from "./pages/GenerateSchedule";
import InstructorHomePage from "./pages/InstructorHomePage";
import LoginPage from "./pages/LoginPage";
import LogoutPage from "./pages/LogoutPage";
import ViewFullSchedule from "./pages/ViewFullSchedule";

function App() {
  axios.defaults.baseURL = "http://localhost:8080/";
  axios.defaults.headers.post["Content-Type"] = "application/json;charset=utf-8";

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

        <Route
          path="/generateSchedule"
          element={
            <CheckAuth roles={["COORDINATOR"]}>
              <GenerateSchedule />
            </CheckAuth>
          }
        />

        <Route
          path="/viewFullSchedule"
          element={
            <CheckAuth roles={["COORDINATOR"]}>
              <ViewFullSchedule />
            </CheckAuth>
          }
        />

        <Route path="/*" element={<Navigate to="/login" replace />} />
      </Routes>
    </div>
  );
}

export default App;
