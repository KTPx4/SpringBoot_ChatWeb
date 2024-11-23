import WaveFragment from "./components/Wave";
import React , { useEffect, useState} from "react";
import axios from "axios";
import {CheckCircleTwoTone, CloseCircleOutlined} from "@ant-design/icons";
import {Navigate} from "react-router-dom";
import Spinner from "react-bootstrap/Spinner";

const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';

const LoginFragment = ({user, pass, openNotification, openForgot}) => {
    const [username, setUsername] = useState(user || '');
    const [password, setPassword] = useState(pass || '');
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        setUsername(user);
        setPassword(pass);
    }, [user, pass]);

    const loginAction = async (event) =>{
        event.preventDefault()

        if(!username || !password )
        {
            openNotification(2,
                `Please input: ${!username?"username" :""}  ${!password?", password" :""} `,

                "",  "error");
            return;
        }
        setIsLoading(true);
        var raw = {
            username: username,
            password: password,
        }

        const url=`${SERVER}/account/login`;

        await axios({
            url: url,
            method: "post",
            headers:{
                // authorization: `bearer ${token}`,
                // "Content-Type": "application/json",
            },
            data: raw,
        })
            .then((response) => {
                console.log(response)
                var code = response.status;
                if(code === 200)
                {
                    var token = response.data.data
                    loginSuccess(token)
                }
                else{
                    var mess = response.data.messages || "Create failed. Try again!";
                    openNotification(1, "Login Failed", mess,  <CloseCircleOutlined twoToneColor="#eb2f96"/>);
                }
            })
            .catch((error) => {

                var mess = error?.response?.data?.message || "Login failed. Try again!";
                openNotification(1, "Login Failed", mess,  <CloseCircleOutlined twoToneColor="#eb2f96"/>);
            })
        setIsLoading(false)
    }

    const loginSuccess= (token) =>{
        localStorage.setItem("token-auth", token)
        openNotification(1, "Login Success", "",  <CheckCircleTwoTone twoToneColor="#52c41a" />);
        window.location.replace("/");
    }
    return (
        <>
            <div className="login-form">
                <h1>LOGIN</h1>

                <form className="form-login" onSubmit={loginAction}>
                    <input value={username}
                           onChange={e => setUsername(e.target.value)}
                           placeholder="Username" className="input input-user input-login"/>
                    <input value={password}
                           onChange={e => setPassword(e.target.value)}
                           placeholder="Password" className="input input-pass input-login" type="password"/>
                    {isLoading ?
                        <Spinner animation="border" variant="danger" style={{margin: "0 auto"}}/>
                        :
                        <button style={{background: "#f9629f"}} type="submit" className="login-button">LOG IN</button>
                    }
                </form>
                <button onClick={openForgot} style={{
                    border: "none",
                    background: "transparent",
                    color: "black",
                    fontSize: 14,
                    marginTop: 15
                }}>Forgot password
                </button>

            </div>

            {/* Container cho các lớp sóng */}
            <WaveFragment isLogin={true}/>
        </>
    )
}
export default LoginFragment;