import React, {useEffect} from "react";

import {BrowserRouter as Router} from "react-router-dom";
import AppRoutes from "./routes/AppRoutes";
import 'bootstrap/dist/css/bootstrap.min.css';
import {GlobalDebug} from "./hooks/GlobalDebug";
function App() {
    useEffect(() => {
        (process.env.REACT_APP_MODE_ENV === "production" ||
            process.env.REACT_APP_MODE_ENV === "STAGING") &&
        GlobalDebug(false);
    }, []);
    return(
        <div>
            <Router>
                <AppRoutes />
            </Router>
        </div>
    )
}
export default App;