import axios from 'axios';
import React, { createContext, useState, useEffect } from 'react';
import { Routes, Route, Navigate } from "react-router-dom";

import "./App.css";

import CheckAuth from "./components/CheckAuth";

import CoordinatorHomePage from "./pages/CoordinatorHomePage";
import GenerateSchedule from "./pages/GenerateSchedule";
import InstructorHomePage from "./pages/InstructorHomePage";
import LoginPage from "./pages/LoginPage";
import LogoutPage from "./pages/LogoutPage";
import ViewFullSchedule from "./pages/ViewFullSchedule";

export const UserRoleContext = createContext();

function App() {
  axios.defaults.baseURL = "http://localhost:8080/";
  axios.defaults.headers.post["Content-Type"] = "application/json;charset=utf-8";

  const [userRole, setUserRole] = useState(null);
  const [userInfoFetched, setUserInfoFetched] = useState(false);

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axios.get("auth/userinfo", { withCredentials: true });
        const { roles } = response.data;
        setUserRole(roles[0]);
      } catch (error) {
        setUserRole(null);
      } finally {
        setUserInfoFetched(true);
      }
    };

    fetchUserInfo();
  }, []);

  if (!userInfoFetched) {
    return <div>Loading...</div>;
  }

  return (
    <div className="App">
      <UserRoleContext.Provider value={{ userRole, setUserRole }}>
        <Routes>
          <Route
            path="/login"
            element={
              userRole ? <Navigate to={`/${userRole.toLowerCase()}`} replace /> : <LoginPage />
            }
          />
          <Route path="/logout" element={<LogoutPage />} />
          <Route
            path="/instructor"
            element={
              <CheckAuth roles={["INSTRUCTOR"]}>
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
          <Route path="/*" element={<Navigate to={`/${userRole ? userRole.toLowerCase() : "login"}`} replace />} />
        </Routes>
      </UserRoleContext.Provider>
    </div>
  );
}

export default App;
