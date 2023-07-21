import axios from 'axios';
import React, { createContext, useEffect, useState } from 'react';
import { Routes, Route, Navigate, useNavigate } from "react-router-dom";

import "./App.css";

import GenerateSchedulePage from "./pages/GenerateSchedulePage";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import LogoutPage from "./pages/LogoutPage";
import UploadInstructorAvailabilityPage from "./pages/UploadInstructorAvailabilityPage";
import ConfigurationPage from "./pages/ConfigurationPage";
import ViewFullSchedulePage from "./pages/ViewFullSchedulePage";
import ViewInstructorSchedulePage from "./pages/ViewInstructorSchedulePage";
import { isJwtTokenExpired, tokenConfig } from './utils';

export const UserInfoContext = createContext();
export const validRoles = new Set('ADMIN', 'COORDINATOR', 'INSTRUCTOR')

function App() {
  // console.log("App render");

  axios.defaults.baseURL = "https://ficbackend.onrender.com";
  axios.defaults.headers.post["Content-Type"] = "application/json;charset=utf-8";

  const lsUserInfoItem = (() => {
    try {
      return JSON.parse(localStorage.getItem('userInfo')) ?? null;
    }
    catch (_) {
      return null;
    }
  })();
  const lsUserInfo = (lsUserInfoItem === null) ? null : {
    username: lsUserInfoItem.username ?? 'nouser',
    roles: Array.from(lsUserInfoItem.roles ?? []),
    fullName: lsUserInfoItem.fullName ?? 'No User'
  };

  const [userInfo, setUserInfo] = useState(lsUserInfo);
  const persistUserInfo = (userInfo) => {
    localStorage.setItem("userInfo", JSON.stringify(userInfo));
    setUserInfo(userInfo);
  }
  // console.log("App userinfo:", userInfo);

  const [userInfoRefreshSeq, setUserInfoRefreshSeq] = useState(0);
  useEffect(
    () => {
      if (!isJwtTokenExpired(localStorage.getItem("jwtToken"))) {
        axios.get('auth/userinfo', tokenConfig()).then(
          (response) => {
            if ((response.status === 200) && response.data && response.data.roles) {
              persistUserInfo(response.data);
            }
          },
          (_) => {
            // error? maybe logout? -- or just let the user figure it out
          });
      }
      // after 10s, refresh again
      setTimeout(() => { setUserInfoRefreshSeq((seq) => seq + 1); }, 10000);
    },
    [userInfo, userInfoRefreshSeq]);

  return (
    <div className="App">
      <UserInfoContext.Provider value={{ userInfo, setUserInfo: persistUserInfo }}>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/generateSchedule" element={<GenerateSchedulePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/logout" element={<LogoutPage />} />
          <Route path="/configuration" element={<ConfigurationPage />} />
          <Route path="/uploadInstructorAvailability" element={<UploadInstructorAvailabilityPage />} />
          <Route path="/viewFullSchedule" element={<ViewFullSchedulePage />} />
          <Route path="/viewInstructorSchedule" element={<ViewInstructorSchedulePage />} />
          <Route path="/*" element={<Navigate to="/" replace />} />
        </Routes>
      </UserInfoContext.Provider>
    </div>
  );
}

export default App;
