import React from 'react';
import {Outlet} from "react-router-dom";
import Header from "./Header";
import {notification} from "antd";

const LayoutPage = ({ children }) => {

    return(
        <>
            <div className="layout-container">

                <Outlet />
            </div>
        </>
    )
}
export default LayoutPage;