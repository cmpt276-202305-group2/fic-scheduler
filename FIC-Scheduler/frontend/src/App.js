import axios from "axios";
import React, { createContext, useEffect, useState } from "react";
import { Routes, Route, Navigate } from "react-router-dom";

import "./App.css";

import { ToastContainer } from "react-toastify";
import DebugMenuPage from "./pages/DebugMenuPage";
import GenerateSchedulePage from "./pages/GenerateSchedulePage";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import LogoutPage from "./pages/LogoutPage";
import ManageInstructorPage from "./pages/ManageInstructorPage";
import ManageClassroomPage from "./pages/ManageClassroomPage";
import ManageCoursePage from "./pages/ManageCoursePage";
import ViewFullSchedulePage from "./pages/ViewFullSchedulePage";
import { isJwtTokenExpired, tokenConfig } from "./utils";

export const UserInfoContext = createContext();
export const validRoles = new Set("ADMIN", "COORDINATOR", "INSTRUCTOR");

function App() {
  // console.log("App render");

  axios.defaults.baseURL = process.env.REACT_APP_BACKEND_URL;
  axios.defaults.headers.post["Content-Type"] =
    "application/json;charset=utf-8";

  const lsUserInfoItem = (() => {
    try {
      return JSON.parse(localStorage.getItem("userInfo")) ?? null;
    } catch (_) {
      return null;
    }
  })();
  const lsUserInfo =
    lsUserInfoItem === null
      ? null
      : {
          username: lsUserInfoItem.username ?? "nouser",
          roles: Array.from(lsUserInfoItem.roles ?? []),
          fullName: lsUserInfoItem.fullName ?? "No User",
        };

  const [userInfo, setUserInfo] = useState(lsUserInfo);
  const persistUserInfo = (userInfo) => {
    localStorage.setItem("userInfo", JSON.stringify(userInfo));
    setUserInfo(userInfo);
  };
  // console.log("App userinfo:", userInfo);

  const [userInfoRefreshSeq, setUserInfoRefreshSeq] = useState(0);
  useEffect(() => {
    if (!isJwtTokenExpired(localStorage.getItem("jwtToken"))) {
      axios.get("auth/current-user", tokenConfig()).then(
        (response) => {
          if (response.status === 200 && response.data && response.data.roles) {
            persistUserInfo(response.data);
          }
        },
        (_) => {
          // error? maybe logout? -- or just let the user figure it out
        }
      );
    }
    // after 10s, refresh again
    setTimeout(() => {
      setUserInfoRefreshSeq((seq) => seq + 1);
    }, 10000);
  }, [userInfo, userInfoRefreshSeq]);

  return (
    <div className="App">
      <ToastContainer
        position="top-right"
        autoClose={6000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
      <UserInfoContext.Provider
        value={{ userInfo, setUserInfo: persistUserInfo }}
      >
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route
            path="/debugMenu"
            element={<Navigate to="/debugMenu/actions" />}
          />
          <Route
            path="/debugMenu/actions"
            element={<DebugMenuPage subpage="actions" />}
          />
          <Route
            path="/debugMenu/blockSplit"
            element={<DebugMenuPage subpage="blockSplit" />}
          />
          <Route
            path="/debugMenu/classroom"
            element={<DebugMenuPage subpage="classroom" />}
          />
          <Route
            path="/debugMenu/courseOffering"
            element={<DebugMenuPage subpage="courseOffering" />}
          />
          <Route
            path="/debugMenu/instructor"
            element={<DebugMenuPage subpage="instructor" />}
          />
          <Route
            path="/debugMenu/schedule"
            element={<DebugMenuPage subpage="schedule" />}
          />
          <Route
            path="/debugMenu/semesterPlan"
            element={<DebugMenuPage subpage="semesterPlan" />}
          />
          <Route
            path="/debugMenu/user"
            element={<DebugMenuPage subpage="user" />}
          />
          <Route path="/generateSchedule" element={<GenerateSchedulePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/logout" element={<LogoutPage />} />
          <Route path="/manageInstructor" element={<ManageInstructorPage />} />
          <Route path="/manageClassroom" element={<ManageClassroomPage />} />
          <Route path="/manageCourse" element={<ManageCoursePage />} />
          <Route path="/viewFullSchedule" element={<ViewFullSchedulePage />} />
          <Route path="/*" element={<Navigate to="/" replace />} />
        </Routes>
      </UserInfoContext.Provider>
    </div>
  );
}

export default App;
