import React, {useState} from 'react';
import {Helmet, HelmetProvider} from "react-helmet-async";
import {Button} from "antd";
const Authen = () =>{
    const [isLogin, setLogin] = useState(true);
    const LoginForm = () => {
        return (
            <>
                    <div className="login-form">
                        <h1>LOGIN</h1>

                        <form className="form-login">
                            <input placeholder="Username" className="input input-user input-login"/>
                            <input placeholder="Password" className="input input-pass input-login" type="password"/>
                            <button style={{background: "#f9629f"}} type="submit" className="login-button">LOG IN</button>
                        </form>
                    </div>

                    {/* Container cho các lớp sóng */}
                    <div className="wave-container">
                        <svg className="wave wave1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
                            <path fill="#f9629f" fill-opacity="0.2"
                                  d="M0,32L40,37.3C80,43,160,53,240,80C320,107,400,149,480,170.7C560,192,640,192,720,160C800,128,880,64,960,48C1040,32,1120,64,1200,90.7C1280,117,1360,139,1400,149.3L1440,160L1440,320L1400,320C1360,320,1280,320,1200,320C1120,320,1040,320,960,320C880,320,800,320,720,320C640,320,560,320,480,320C400,320,320,320,240,320C160,320,80,320,40,320L0,320Z"></path>
                        </svg>

                        <svg className="wave wave1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
                            <path fill="#F9629F" fillOpacity="0.7"
                                  d="M0,256L40,229.3C80,203,160,149,240,149.3C320,149,400,203,480,192C560,181,640,107,720,85.3C800,64,880,96,960,133.3C1040,171,1120,213,1200,229.3C1280,245,1360,235,1400,229.3L1440,224L1440,320L1400,320C1360,320,1280,320,1200,320C1120,320,1040,320,960,320C880,320,800,320,720,320C640,320,560,320,480,320C400,320,320,320,240,320C160,320,80,320,40,320L0,320Z">
                            </path>
                        </svg>


                        <svg className="wave wave2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
                            <path fill="#F9629F" fillOpacity="0.9"
                                  d="M0,288L48,272C96,256,192,224,288,213.3C384,203,480,213,576,224C672,235,768,245,864,234.7C960,224,1056,192,1152,176C1248,160,1344,160,1392,160L1440,160L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z">
                            </path>
                        </svg>
                    </div>

            </>
        )
    }
    const RegisterForm =() =>{
        return(
            <>
                <div className="login-form">
                    <h1>REGISTER</h1>

                    <form className="form-login">
                        <input placeholder="Username" className="input input-user input-register"/>
                        <input placeholder="Password" className="input input-pass input-register" type="password"/>
                        <input placeholder="Email" className="input input-email input-register" type="email"/>
                        <button style={{background: "#6CB4EE"}} type="submit" className="login-button">SEND</button>
                    </form>
                </div>

                {/* Container cho các lớp sóng */}
                <div className="wave-container">
                    <svg className="wave wave1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
                        <path fill="#6CB4EE" fill-opacity="0.2"
                              d="M0,32L40,37.3C80,43,160,53,240,80C320,107,400,149,480,170.7C560,192,640,192,720,160C800,128,880,64,960,48C1040,32,1120,64,1200,90.7C1280,117,1360,139,1400,149.3L1440,160L1440,320L1400,320C1360,320,1280,320,1200,320C1120,320,1040,320,960,320C880,320,800,320,720,320C640,320,560,320,480,320C400,320,320,320,240,320C160,320,80,320,40,320L0,320Z"></path>
                    </svg>

                    <svg className="wave wave1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
                        <path fill="#6CB4EE" fillOpacity="0.7"
                              d="M0,256L40,229.3C80,203,160,149,240,149.3C320,149,400,203,480,192C560,181,640,107,720,85.3C800,64,880,96,960,133.3C1040,171,1120,213,1200,229.3C1280,245,1360,235,1400,229.3L1440,224L1440,320L1400,320C1360,320,1280,320,1200,320C1120,320,1040,320,960,320C880,320,800,320,720,320C640,320,560,320,480,320C400,320,320,320,240,320C160,320,80,320,40,320L0,320Z">
                        </path>
                    </svg>


                    <svg className="wave wave2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
                        <path fill="#6CB4EE" fillOpacity="0.9"
                              d="M0,288L48,272C96,256,192,224,288,213.3C384,203,480,213,576,224C672,235,768,245,864,234.7C960,224,1056,192,1152,176C1248,160,1344,160,1392,160L1440,160L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z">
                        </path>
                    </svg>
                </div>
            </>
        )
    }
    const changeForm = (value) => {
        setLogin(value);
    }
    const RenderForm = isLogin ? LoginForm : RegisterForm
    return (
        <HelmetProvider>
            <Helmet>
                <link href="/css/auth/auth.css" rel="stylesheet" type={"text/css"}/>

            </Helmet>
            <div className="container-body">
                <div className="button-select">
                    <Button style={{color: isLogin? "#f9629f" : "black"}}
                            onClick={()=>changeForm(true)}
                            className={isLogin ? "btn btn-login-screen selected" : "btn btn-login-screen"}>Login</Button>
                    <Button style={{color: !isLogin? "#6CB4EE" : "black"}}
                            onClick={()=>changeForm(false)}
                            className={isLogin ? "btn btn-register-screen" : "btn btn-register-screen selected"}>Register</Button>
                </div>
                <div className="login-container">
                    {<RenderForm/>}
                </div>
            </div>

        </HelmetProvider>
    )
}
export default Authen