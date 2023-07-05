import "./App.css";
import { Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import InstructorHomePage from "./pages/InstructorHomePage";
import CoordinatorHomePage from "./pages/CoordinatorHomePage";
import React from "react";
import LogoutPage from "./pages/LogoutPage";
import { Navigate } from "react-router-dom";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/InstructorHomePage" element={<InstructorHomePage />} />
        <Route path="/CoordinatorHomePage" element={<CoordinatorHomePage />} />
        <Route path="/LogoutPage" element={<LogoutPage />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </div>
  );
}

export default App;
