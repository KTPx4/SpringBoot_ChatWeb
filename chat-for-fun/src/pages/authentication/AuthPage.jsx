import React, { useState, useMemo } from 'react';
import { Helmet, HelmetProvider } from "react-helmet-async";
import { Button } from "antd";
import { Navigate } from "react-router-dom";
import useAuth from "../../hooks/useAuth";
import LoginFragment from "./LoginFragment";
import RegisterFragment from "./RegisterFragment";
import {notification} from "antd";
import {CheckCircleTwoTone} from "@ant-design/icons";

const AuthPage = ({openNotification}) => {
    const [isLogin, setLogin] = useState(true);
    const token = localStorage.getItem('token-auth') || "";
    const { isAuthenticated } = useAuth(token);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");


    if (isAuthenticated) {
        return <Navigate to="/" />;
    }


    const changeForm = (value) => {
        setLogin(value);
    };

    const afterRegister = (user, pass)=>{
        setUsername(user);
        setPassword(pass);
        setLogin(true)
    }



    return (
        <HelmetProvider>
            <Helmet>
                <link href="/css/auth/auth.css" rel="stylesheet" type={"text/css"} />
            </Helmet>
            <div className="container-body">

                <div className="button-select">
                    <Button
                        style={{ color: isLogin ? "#f9629f" : "black" }}
                        onClick={() => changeForm(true)}
                        className={isLogin ? "btn btn-login-screen selected" : "btn btn-login-screen"}
                    >
                        Login
                    </Button>
                    <Button
                        style={{ color: !isLogin ? "#6CB4EE" : "black" }}
                        onClick={() => changeForm(false)}
                        className={isLogin ? "btn btn-register-screen" : "btn btn-register-screen selected"}
                    >
                        Register
                    </Button>
                </div>
                <div className="login-container">
                    <div style={{display: isLogin ? 'block' : 'none'}}>
                        <LoginFragment user={username} pass={password} openNotification={openNotification}  />
                    </div>
                    <div style={{display: !isLogin ? 'block' : 'none'}}>
                        <RegisterFragment afterRegister={afterRegister} openNotification={openNotification} />
                    </div>
                </div>
            </div>
        </HelmetProvider>
    );
};

export default AuthPage;
