// import Header from './components/Header';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Collections from './pages/Collections';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

import './App.scss';

function App() {
  return (
    <div className="App">
      <Router>
        <Navbar />
        <div className="app-default-body">
          <Routes>
            <Route
              path="/"
              element={<Home />}
            />
            <Route
              path="collections"
              element={<Collections />}
            />
          </Routes>
        </div>
      </Router>
    </div>
  );
}

export default App;
