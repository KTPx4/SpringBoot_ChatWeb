import React, { useState, useMemo } from 'react';
import { Helmet, HelmetProvider } from "react-helmet-async";
import {Button, Input, message, Modal} from "antd";
import { Navigate } from "react-router-dom";
import useAuth from "../../hooks/useAuth";
import LoginFragment from "./LoginFragment";
import RegisterFragment from "./RegisterFragment";
import {notification} from "antd";
import {CheckCircleTwoTone} from "@ant-design/icons";
import axios from "axios";

const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';

const AuthPage = ({openNotification}) => {
    const [isLogin, setLogin] = useState(true);
    const token = localStorage.getItem('token-auth') || "";
    const { isAuthenticated } = useAuth(token);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isModalForgot, setIsModalForgot] = useState(false);
    const [userName, setUserName] = useState("");
    if (isAuthenticated) {
        return <Navigate to="/" />;
    }
    const sendServer= async (action, method, data)=>{
        try {
            const url = `${SERVER}/${action}`
            const res =  await axios({
                url: url,
                method: method,
                headers:{
                    authorization: `Bearer ${token}`,
                    // "Content-Type": "application/json",
                },
                data: data
            })

            return res

        } catch (err) {
            console.log(err?.response)
            // alert(err?.response.data.message ?? "Failed to connect Server")
            return err?.response
        }
    }


    const changeForm = (value) => {
        setLogin(value);
    };

    const afterRegister = (user, pass)=>{
        setUsername(user);
        setPassword(pass);
        setLogin(true)
    }

    const handleForgot = async()=>{
        if(!userName)
        {
            message.error("Please input userName")
            return
        }
        var link = `account/reset`
        var method = "post"
        var data ={
            username: userName
        }
        var res = await sendServer(link,method,data)
        if(res.status && res.status === 200)
        {
            message.success("An email has been sent to your email which is registered this account")
        }
        else{
            message.error(res?.data?.message ?? "Failed to connect Server")
        }
        setIsModalForgot(false)

    }

    return (
        <HelmetProvider>
            <Helmet>
                <link href="/css/auth/auth.css" rel="stylesheet" type={"text/css"} />
            </Helmet>
            <Modal
                open={isModalForgot}
                onCancel={() => setIsModalForgot(false)}
                onOk={handleForgot}
                title="Forgot Password?"
            >
             <Input placeholder={"Username"} onChange={(e)=> setUserName(e.target.value)} />
            </Modal>
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
                        <LoginFragment user={username} pass={password} openNotification={openNotification} openForgot={()=> setIsModalForgot(true)} />
                    </div>
                    <div style={{display: !isLogin ? 'block' : 'none'}}>
                        <RegisterFragment afterRegister={afterRegister} openNotification={openNotification} openForgot={()=> setIsModalForgot(true)}/>
                    </div>
                </div>
            </div>
        </HelmetProvider>
    );
};

export default AuthPage;
