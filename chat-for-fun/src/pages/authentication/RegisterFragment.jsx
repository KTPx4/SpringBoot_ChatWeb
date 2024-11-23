import WaveFragment from "./components/Wave";
import {useState} from "react";
import {
    CheckCircleTwoTone,
    CloseCircleOutlined,

} from '@ant-design/icons';
import axios from "axios";
import Spinner from 'react-bootstrap/Spinner';
const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';

const RegisterFragment = ({afterRegister, openNotification, openForgot}) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const registerAction = async (event) =>{
        event.preventDefault()
        if(!username || !password || !email)
        {
            openNotification(2,
                `Please input: ${!username?"username" :""}  ${!password?", password" :""}  ${!email?", email" :""}`,

                "",  "error");
            return;
        }
        setIsLoading(true);
        var raw = {
            username: username,
            password: password,
            email: email,
        }

        const url=`${SERVER}/account/register`;

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

            var code = response.status;
            if(code === 201)
            {
                openNotification(1, "Register Success", "",  <CheckCircleTwoTone twoToneColor="#52c41a" />);
                afterRegister(username, password);
            }
            else{
                var mess = response.data.messages || "Create failed. Try again!";
                openNotification(1, "Register Failed", mess,  <CloseCircleOutlined twoToneColor="#eb2f96"/>);
            }
        })
        .catch((error) => {

            var mess = error?.response?.data?.message || "Create failed. Try again!";
            openNotification(1, "Register Failed", mess,  <CloseCircleOutlined twoToneColor="#eb2f96"/>);
        })
        setIsLoading(false);

    }


    return (
        <>
            <div className="login-form">
                <h1>REGISTER</h1>

                <form className="form-login" onSubmit={e=> registerAction(e)}>
                    <input value={username}
                        onChange={e => setUsername(e.target.value)}
                        placeholder="Username" className="input input-user input-register"/>
                    <input value={password}
                        onChange={e => setPassword(e.target.value)}
                        placeholder="Password" className="input input-pass input-register" type="password"/>
                    <input value={email}
                           onChange={e => setEmail(e.target.value)}
                           placeholder="Email" className="input input-email input-register" type="email"/>
                    {isLoading ?
                        <Spinner animation="border" variant="info" style={{margin: "0 auto"}}/>
                        :
                        <button style={{background: "#6CB4EE"}} type="submit" className="login-button">SEND</button>}
                </form>
                <button onClick={openForgot}  style={{border: "none", background: "transparent",color: "black", fontSize: 14, marginTop: 15}}>Forgot password</button>
            </div>
            {/* Container cho các lớp sóng */}
            <WaveFragment isLogin={false}/>
        </>
    )
}

export default RegisterFragment