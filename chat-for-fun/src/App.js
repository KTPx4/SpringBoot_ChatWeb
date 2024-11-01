import React from "react";

import {BrowserRouter as Router} from "react-router-dom";
import AppRoutes from "./routes/AppRoutes";
import 'bootstrap/dist/css/bootstrap.min.css';
function App() {
    return(
        <div>
            <Router>
                <AppRoutes />
            </Router>
        </div>
    )
}
export default App;