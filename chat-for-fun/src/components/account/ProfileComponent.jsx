import React, {useContext, useEffect, useState} from 'react';
import useStore from "../../store/useStore";
import {ThemeContext} from "../../ThemeContext";
import {Helmet, HelmetProvider} from "react-helmet-async";
import {Avatar, Upload, message, Form, Input, Button, Modal, Popover, List} from "antd";
import {
    LoadingOutlined,
    PlusOutlined,
    EditOutlined,
    RedoOutlined, CheckCircleTwoTone,
} from '@ant-design/icons';
import axios from "axios";
import Line from "antd/lib/progress/Line";
import listImages from "../../store/linkEmoji";
import linkEmoji from "../../store/linkEmoji";

const beforeUpload = (file) => {
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
    if (!isJpgOrPng) {
        message.error('You can only upload JPG/PNG file!');
    }
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
        message.error('Image must smaller than 2MB!');
    }
    return isJpgOrPng && isLt2M;
};

const getBase64 = (img, callback) => {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result));
    reader.readAsDataURL(img);
};

const Profile = ({openNotification, changeAvt})  =>{
    const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';
    const [token ,setToken] = useState(localStorage.getItem('token-auth') || '');
    const {id, setId} = useStore()
    const [myId, setMyId] = useState(id||"px4");

    const { currentTheme } = useContext(ThemeContext);
    const key = currentTheme.getKey();
    const themeName = currentTheme.getKey();
    const contentColor = currentTheme.getContent()
    const textColor = currentTheme.getText();
    const sliderColor = currentTheme.getKey().split("_")[1];
    const borderColor = currentTheme.getBorder()

    const [loading, setLoading] = useState(false);
    const [imageUrl, setImageUrl] = useState();
    const [Server_Upload, setServer_Upload] = useState(SERVER +"/upload?token=" + token);
    const [name, setName] = useState("");
    const [user, setUser] = useState("");
    const [email,setEmail] = useState("")

    // modal
    const [title, setTitle] = useState("Modal");
    const [content, setContent] = useState("");
    const [err, setErr] = useState("");

    //password
    const [oldPass, setOldPass] = useState("")
    const [newPass, setNewPass] = useState("")
    const [confirm, setConfirm] = useState("")

    useEffect(() => {
        loadProfile()
    }, []);

    const loadProfile = async () => {
        const url = `${SERVER}/account/${myId}`;

        try {
            const res =  await axios({
                url: url,
                method: "get",
                headers:{
                    authorization: `Bearer ${token}`,
                    // "Content-Type": "application/json",
                },

            })
            const status = res.status;
            if(status === 200)
            {
                const data = res?.data?.data
                if(data)
                {

                    setImageUrl(data.image)
                    setName(data.name)
                    setUser(data.id)
                    setEmail(data.email)
                    setNewName(data.name)
                    setNewUsername(data.userProfile)
                    setNewEmail(data.email)
                }
            }

        } catch (err) {
            alert("Failed to connect Server")
        } finally {
            setLoading(false);
        }
    }

    // upload image
    const handleChange = (info) => {
        if (info.file.status === 'uploading') {
            setLoading(true);
            return;
        }
        if (info.file.status === 'done') {
            // Get this url from response in real world.
            getBase64(info.file.originFileObj, (url) => {
                setLoading(false);
                setImageUrl(url);
                changeAvt(url)

            });
            messageApi.open({
                type: "success", // success, error , warning
                content: "Update Success",
                className: 'custom-class',
                style: {
                    marginTop: '10vh',
                },
            });
        }
    };

    const uploadButton =
        (
            <button
                style={{
                    border: 0,
                    background: 'none',
                }}
                type="button"
            >
                {loading ?
                    <LoadingOutlined /> :
                    <Avatar
                        style={{}}
                        size={98}
                        shape="circle"
                        src={imageUrl}
                    />}

            </button>
        );

    const [messageApi, contextHolder2] = message.useMessage();

    // modal
    const [typeModal, setTypeModal] = useState("");
    // modal change pass
    const [open, setOpen] = useState(false);
    const [confirmLoading, setConfirmLoading] = useState(false);
    const [modalText, setModalText] = useState('Content of the modal');
    const showModal = () => {
        setOpen(true);
    };

    //modal change info
     const [newName, setNewName] = useState("");
     const [newUsername, setNewUsername] = useState("");
     const [newEmail, setNewEmail] = useState("");




    const sendRequest =async (type, method, datas) =>{
        if(!type || !method) {
            setErr("Not have value to send request Server")
            return
        }
        const url = `${SERVER}/account/${type}`;
        const getToken = localStorage.getItem("token-auth")
        try {
            const res =  await axios({
                url: url,
                method: method,
                headers:{
                    authorization: `Bearer ${getToken}`,

                },
                data: datas

            })
            const status = res.status;
            const resData = res.data?.data

            if(status === 200)
            {

                console.log(resData)
                if(typeModal === "pass")
                {
                    localStorage.setItem("token-auth", resData)
                    setToken(resData)
                }
                else if(typeModal === "info")
                {
                    setImageUrl(resData.image)
                    setName(resData.name)
                    setUser(resData.userProfile)
                    setEmail(resData.email)
                    setNewName(resData.name)
                    setNewUsername(resData.userProfile)
                    setNewEmail(resData.email)
                }
                else if(typeModal === "avatar")
                {

                }


                if(resData)
                {
                    setOpen(false)
                    messageApi.open({
                        type: "success", // success, error , warning
                        content: "Update Success",
                        className: 'custom-class',
                        style: {
                            marginTop: '10vh',
                        },
                    });

                }
            }
            else{
                setErr(resData?.message)
            }

        } catch (err) {

            if(err?.status === 400)
            {
                setErr(err?.response?.data?.message)

            }
        }
        setConfirmLoading(false);
    }

    // modal
    const handleOk = () => {
        var type, method, data =null
        if(typeModal === "pass")
        {
            if(!oldPass || !newPass || !confirm)
            {
                setErr("Please input full value")
                return;
            }
            else if(confirm !== newPass)
            {
                setErr("Confirm not match!")
                return
            }
            setConfirmLoading(true);
            type="password"
            method="post"
            data = {
                oldPass: oldPass,
                newPass: newPass
            }
        }
        else
        {
            if(!newName || !newUsername || !newEmail)
            {
                setErr("Please input full value")
                return;
            }
            type=myId
            method = "put"
            data={
                name: newName,
                email: newEmail,
                userProfile: newUsername
            }
        }


        sendRequest(type, method, data)

    };

    const handleCancel = () => {

        setOpen(false);
    };


    const openChangePass =()=>{
        setTypeModal("pass")
        setTitle("Change Password")
        setOpen(true);
    }

    const  openChangeInfo =()=>{
        setTypeModal("info")
        setTitle("Change Info")
        setOpen(true);
    }

    // modal select image
    const [isModalVisible, setIsModalVisible] = useState(false);
    const listImages = linkEmoji
    const handleUploadChange = info => {
        if (info.file.status === 'done') {
            setImageUrl(info.file.response.url);
        }
    };

    const selectImage = (url) => {
        setImageUrl(url);
        changeAvt(url)

        setTypeModal("avatar")
        var type=myId
        var method = "put"
        var data={
            avatar: url
        }
        sendRequest(type, method, data)
        setIsModalVisible(false);
    };

    return (
        <HelmetProvider>
            <Helmet>
                <link href="/css/profile/settings.css" rel="stylesheet" />
            </Helmet>
            {contextHolder2}
            <Modal
                className={`modal-${key === "theme_dark" ? "dark":"light"}`}
                title={<p style={{color: textColor}}>Select an Image</p>}

                open={isModalVisible}
                onCancel={() => setIsModalVisible(false)}
                footer={null}
                style={{

                    maxHeight: "500px"
                }}
            >

                <div  style={{ overflowY: "auto",overflowX: "hidden", maxHeight: "500px"}}>

                    <List
                        grid={{ gutter: 16, column: 3 }}
                        dataSource={listImages}
                        renderItem={(item) => (
                            <List.Item>
                                <img
                                    src={item}
                                    alt="img"
                                    style={{ width: '100%', cursor: 'pointer' }}
                                    onClick={() => selectImage(item)}
                                />
                            </List.Item>
                        )}
                    />
                </div>
            </Modal>
            <Modal
                className={`modal-${key === "theme_dark" ? "dark":"light"}`}

                title={<p style={{color: textColor}}>{title}</p>}
                open={open}
                onOk={handleOk}
                confirmLoading={confirmLoading}
                onCancel={handleCancel}
            >
                {typeModal === "pass" && (
                    <div id="content-pass">
                        <Input
                            type="password"
                            defaultValue={null}
                            onChange={(e) => {
                                setOldPass(e.target.value)
                                setErr("")
                            }}  style={{margin: 5, background: "#dadada", border: "none"}}  placeholder="Old Password" />

                        <Input
                            type="password"
                            defaultValue={null}
                            onChange={(e) => {
                                setNewPass(e.target.value)
                                setErr("")
                            }} style={{margin: 5, background: "#dadada", border: "none"}} placeholder="New Password" />

                        <Input
                            type="password"
                            defaultValue={null}
                            onChange={(e) => {
                                setConfirm(e.target.value)
                                setErr("")
                            }} style={{margin: 5, background: "#dadada", border: "none"}} placeholder="Confirm Password" />
                    </div>
                )}

                {typeModal === "info" && (
                    <div id="content-info">
                        <Input
                            defaultValue={newName}
                            onChange={(e) => {
                                setNewName(e.target.value)
                                setErr("")
                            }}  style={{margin: 5, background: "#dadada", border: "none"}}  placeholder="Name" />

                        {/*<Input*/}
                        {/*    defaultValue={newUsername}*/}
                        {/*    onChange={(e) => {*/}
                        {/*        setNewUsername(e.target.value)*/}
                        {/*        setErr("")*/}
                        {/*    }} style={{margin: 5}} placeholder="UserProfile" />*/}

                        <Input
                            defaultValue={newEmail}
                            onChange={(e) => {
                                setNewEmail(e.target.value)
                                setErr("")
                            }} style={{margin: 5, background: "#dadada", border: "none"}} placeholder="Email" />
                    </div>
                )}
                {err && <p  style={{margin: 5}}  className="text-danger"><i>{err}</i></p>}
            </Modal>

            <div style={{}} className="container-body w-100 mx-3">
                <div style={{display: "flex", flexDirection: "row", alignItems: "center", borderBottom: `1px solid ${borderColor}`}}
                     className="mt-5 header">

                    <Popover content={
                        <div style={{
                            display: "flex",
                            flexDirection: "column",
                        }}>
                            <Button style={{margin: "10px 0 0"}}>

                                <Upload
                                    name="file"
                                    listType="text"
                                    className="avatar-uploader"
                                    showUploadList={false}
                                    action={Server_Upload}

                                    beforeUpload={beforeUpload}
                                    onChange={handleChange}
                                >
                                    Up load
                                </Upload>
                            </Button>
                            <Button style={{margin: "10px 0"}} onClick={() => setIsModalVisible(true)}>Emoji</Button>
                        </div>
                    }>
                        <Avatar
                            style={{width: 140, height: 80}}
                            shape="circle"
                            src={imageUrl}
                        />
                    </Popover>



                    <div className="mx-4 w-50 display">
                        <h4 style={{color: textColor}} className="">{name}</h4>
                        <p style={{color: textColor}}><i>Email: {email}</i></p>
                        <p style={{color: textColor}}><i>User Profile: {user}</i></p>
                    </div>
                    <div className="w-100 mt-4 "
                         style={{display: "flex", flexDirection: "row", alignItems: "end" , justifyContent: "end"}}>
                        <Button
                                icon={<RedoOutlined spin={true}/>}
                                style={{background: contentColor, color: textColor}}
                                className="btn-profile btn-change-pass mx-3"
                                onClick={openChangePass} >

                            <b>Change Password</b>
                        </Button>

                        <Button
                                icon={<EditOutlined />}
                                    style={{background: contentColor, color: textColor}}
                                 className="btn-profile btn-change-info"
                                 onClick={openChangeInfo}
                        >
                            <b>Change Info</b></Button>
                    </div>
                </div>

                <div className="body mt-3" style={{display: "flex", flexDirection: "row", alignItems: "center"}}>
                    <div className="body-left" style={{borderRight: `1px none ${borderColor}`}}>

                    </div>

                    <div className="body-rigth">

                    </div>

                </div>
            </div>
        </HelmetProvider>
    )
}
export default Profile;