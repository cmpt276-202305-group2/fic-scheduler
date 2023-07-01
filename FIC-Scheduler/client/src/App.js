import "./App.css";
import {Routes, Route} from 'react-router-dom';
import LoginPage from "./pages/LoginPage";
import InstructorHomePage from "./pages/InstructorHomePage";

function App() {
  return <div className="App">
    <>
      <Routes>
        <Route path="/InstructorHomePage" element={<InstructorHomePage />} />
        <Route path="/" element={<LoginPage />} />
      </Routes>
    </>
  </div>;
}

export default App;
