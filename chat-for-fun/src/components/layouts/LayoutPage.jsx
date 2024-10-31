import React from 'react';
import {Outlet} from "react-router-dom";
import Header from "./Header";

const LayoutPage = ({ children }) => {
    return(
        <div className="layout-container">

            <Outlet />
        </div>
    )
}
export default LayoutPage;