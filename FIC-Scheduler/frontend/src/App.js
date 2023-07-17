import React, { useState, useEffect } from 'react';
import "./App.css";
import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import InstructorHomePage from "./pages/InstructorHomePage";
import CoordinatorHomePage from "./pages/CoordinatorHomePage";
import { createContext } from 'react';
import LogoutPage from "./pages/LogoutPage";
import CheckAuth from "./components/CheckAuth";
import axios from 'axios';

export const UserRoleContext = createContext();

function App() {
  const [userRole, setUserRole] = useState(null);
  const [userInfoFetched, setUserInfoFetched] = useState(false); 

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axios.get("http://localhost:8080/auth/userinfo", { withCredentials: true });
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
          <Route path="/*" element={<Navigate to={`/${userRole ? userRole.toLowerCase() : "login"}`} replace />} />
        </Routes>
      </UserRoleContext.Provider>
    </div>
  );
}

export default App;
