import axios from 'axios';
import React, { createContext, useState, useEffect, useContext } from 'react';
import { Routes, Route, Navigate, useNavigate } from "react-router-dom";

import "./App.css";

import GenerateSchedulePage from "./pages/GenerateSchedulePage";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import LogoutPage from "./pages/LogoutPage";
import ManageCoursesPage from "./pages/ManageCoursesPage";
import ManageInstructorsPage from "./pages/ManageInstructorsPage";
import UploadInstructorAvailabilityPage from "./pages/UploadInstructorAvailabilityPage";
import ViewFullSchedulePage from "./pages/ViewFullSchedulePage";
import ViewInstructorSchedulePage from "./pages/ViewInstructorSchedulePage";

var setUserInfo = (_) => { console.log('dummy setUserInfo'); };

export const UserInfoContext = createContext();
export const validRoles = new Set('ADMIN', 'COORDINATOR', 'INSTRUCTOR')
export const storeUserInfo = async (userInfoDto) => {
  // console.log("storeUserInfo");
  const userInfo = {
    username: '' + (userInfoDto.username ?? 'nouser'),
    roles: Array.from(userInfoDto.roles ?? []),
    fullName: '' + (userInfoDto.fullName ?? 'No User')
  };
  localStorage.setItem('userInfo', JSON.stringify(userInfoDto));
  setUserInfo(userInfo);
  // console.log("storeUserInfo done");
};
export const fetchUserInfo = async () => {
  // console.log("fetchUserInfo");
  try {
    const response = await axios.get("auth/userinfo", { withCredentials: true });
    if (response.status == 200) {
      storeUserInfo(response.data);
    } else {
      console.log("fetchUserInfo bad status", response.status);
      localStorage.removeItem('userInfo');
    }
  } catch (error) {
    console.log("fetchUserInfo exception", error);
    localStorage.removeItem('userInfo');
  }
};

function App() {
  // console.log("App render");

  axios.defaults.baseURL = "http://localhost:8080/";
  axios.defaults.headers.post["Content-Type"] = "application/json;charset=utf-8";

  const lsUserInfoItem = localStorage.getItem('userInfo') ?? null;
  const lsUserInfo = (lsUserInfoItem === null) ? null : {
    username: lsUserInfoItem.username ?? 'nouser',
    roles: Array.from(lsUserInfoItem.roles ?? []),
    fullName: lsUserInfoItem.fullName ?? 'No User'
  };

  var userInfo;
  [userInfo, setUserInfo] = useState(lsUserInfo);
  const navigate = useNavigate();

  useEffect(() => { fetchUserInfo(); }, []);

  if (lsUserInfo === null) {
    navigate('/login');
  }

  return (
    <div className="App">
      <UserInfoContext.Provider value={{ userInfo, setUserInfo }}>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/generateSchedule" element={<GenerateSchedulePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/logout" element={<LogoutPage />} />
          <Route path="/manageCourses" element={<ManageCoursesPage />} />
          <Route path="/manageInstructors" element={<ManageInstructorsPage />} />
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
