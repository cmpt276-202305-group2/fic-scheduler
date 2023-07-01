import "./App.css";
import {Routes, Route} from 'react-router-dom';
import LoginPage from "./pages/LoginPage";
import InstructorHomePage from "./pages/InstructorHomePage";
import CoordinatorHomePage from "./pages/CoordinatorHomePage";

function App() {
  return <div className="App">
    <>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/InstructorHomePage" element={<InstructorHomePage />} />
        <Route path="/CoordinatorHomePage" element={<CoordinatorHomePage />} />
      </Routes>
    </>
  </div>;
}

export default App;
