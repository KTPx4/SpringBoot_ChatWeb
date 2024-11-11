import React, {useEffect, useState, useContext, useMemo} from "react";
import ProfileComponent from "../../components/account/ProfileComponent";
import ChatComponent from "../../components/chat/ChatComponent";
import {Helmet, HelmetProvider} from "react-helmet-async";
import {

    LogoutOutlined,
    WechatOutlined,
    TeamOutlined,
    UserOutlined,
    IeOutlined,
    WindowsOutlined, CloseCircleOutlined
} from '@ant-design/icons';
import { ThemeContext } from '../../ThemeContext';
import ThemeManager from "../../ThemeManager";
import {Avatar, Breadcrumb, Button, Layout, Menu, Modal, Spin} from 'antd';
import themeManager from "../../ThemeManager";
import axios from "axios";
import FriendComponent from "../../components/friend/FriendComponent";
import useStore from "../../store/useStore";
import WebSocketHandler from "../../components/chat/WebSocketHandler";
import {useParams} from "react-router-dom";

const { Header, Content, Footer, Sider } = Layout;

function getItem(label, key, icon, children) {
    return {
        key,
        icon,
        children,
        label,
    };
}

const items = [
    getItem('User', 'sub1', <UserOutlined />, [
        getItem('Settings', 'settings'),
        getItem('Logout', 'logout'),
    ]),
    getItem('Chats', 'chats', <WechatOutlined />),
    getItem('Groups', 'groups', <TeamOutlined />),
    getItem('Social', 'sub2',<IeOutlined />, [
        getItem('Friends', 'friends'),
        getItem('Threads', 'threads')
    ]),
    // getItem('Files', '9', <FileOutlined />),
];
const items2 = [
    getItem('Themes', 'sub1', <WindowsOutlined />, [
        getItem('Light', 'theme_light'),
        // getItem('Light Pink', 'theme_light_pink'),
        getItem('Dark', 'theme_dark'),
        // getItem('Dark Pink', 'theme_dark_pink'),
    ]),
    getItem('Logout', 'logout', <LogoutOutlined />),
    // getItem('Files', '9', <FileOutlined />),
];

const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';

const HomePage = ({openNotification})  =>{
    const {myAccount} = useStore()
    const [collapsed, setCollapsed] = useState(false);
    const { currentTheme, changeTheme } = useContext(ThemeContext);
    const [selectedTheme, setSelectedTheme] = useState(currentTheme.getKey); // Quản lý các key được chọn
    const [selectedMenu, setSelectedMenu] = useState("chats"); // Quản lý các key được chọn
    // const [bodyComponent,setBodyComponent] = useState(<ChatComponent />);
    const [bodyComponent,setBodyComponent] = useState(null);

    const sliderColor = currentTheme.getKey().split("_")[1];
    const background = currentTheme.getBackground();
    const contentColor = currentTheme.getContent()

    const [open, setOpen] = useState(false);
    const [confirmLoading, setConfirmLoading] = useState(false);
    const [modalText, setModalText] = useState('Content of the modal');
    const [avt, setAvt] =useState(myAccount?.image)

    const [webSocketHandler, setWebSocketHandler] = useState(null);
    const { id } = useParams(); // Lấy giá trị id từ URL




    useEffect(() => {
        const token = localStorage.getItem('token-auth');
        const socketHandler = new WebSocketHandler(token);

        socketHandler.connect();
        setWebSocketHandler(socketHandler);
        // return () => { socketHandler.disconnect(); };
    }, []);
// Sử dụng useMemo để khởi tạo các component chỉ một lần


    useEffect(() => {
        if(webSocketHandler)
        {
            if(id)
            {
                console.log("run time")
                setSelectedMenu("friends")
                setBodyComponent(<FriendComponent userId={id} socketHandler={webSocketHandler}/>)
            }
            else
            {
                setBodyComponent(<ChatComponent socketHandler={webSocketHandler} />)
            }
        }
    }, [webSocketHandler])

    const showModal = () => {
        setOpen(true);
    };
    const handleOk = () => {
        setModalText('Please wait!!');
        setConfirmLoading(true);
        setTimeout(async() => {
            await logOutAction()
            setOpen(false);
            setConfirmLoading(false);
        }, 1000);
    };
    const handleCancel = () => {
        setOpen(false);
    };

    const logOutAction = async()=>{
        const url=`${SERVER}/account/logout`;
        const token = localStorage.getItem('token-auth') || '';
        localStorage.setItem('token-auth', '');
        await axios({
            url: url,
            method: "post",
            headers:{
                authorization: `Bearer ${token}`,
                // "Content-Type": "ap
                // plication/json",
            },

        })
            .then((response) => {
                console.log(response)
                var code = response.status;
                if(code === 200)
                {
                    window.location.replace("/login")
                }
                else{
                }
            })
            .catch((error) => {

            })
    }
    const changeAvt = (link) =>{
        if(link) setAvt(link)
    }
    const handleClick = ({key}) =>{
        if(key !== "logout") setSelectedMenu(key)
        window.history.pushState({},null, "/" )
        switch (key)
        {
            case "chats":
                setBodyComponent(<ChatComponent socketHandler={webSocketHandler} />)
                break

            case "settings":
                setBodyComponent(<ProfileComponent openNotification={openNotification} changeAvt={changeAvt}/>)
                break

            case "logout":
                showModal()

                break

            case "groups":
                break

            case "friends":
                setBodyComponent(<FriendComponent userId={id} socketHandler={webSocketHandler}/>)
                    break

            case "threads":
                break
            case 'theme_light':
                changeTheme(key);
                setSelectedTheme(key)
                break
            case 'theme_dark':
                changeTheme(key);
                setSelectedTheme(key)
                break;
        }
    }

    return (
        <HelmetProvider>
            <Helmet>
                <link href="/css/home.css"
                      rel="stylesheet"
                      type="text/css" />
            </Helmet>
            <Modal
                title="Log Out"
                open={open}
                onOk={handleOk}
                confirmLoading={confirmLoading}
                onCancel={handleCancel}
            >
                <p>Do you want to logout?</p>
            </Modal>
            <Layout   style={{ minHeight: '100vh', background: contentColor, overflowX: "auto" }}>
                <Sider style={{ padding: "30px 0", }}
                       theme={sliderColor}
                       collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>

                    {/*<div className="demo-logo-vertical" />*/}
                    <div style={{ display: "flex", flexDirection:"column", width: "100%", alignItems: "center"}}>
                        { avt ? <Avatar size={50} src={avt} onError={()=> setAvt("")}/> : <Spin /> }

                    </div>

                    <Menu
                        selectedKeys={selectedMenu}
                            style={{height: "50%", }}
                          theme={sliderColor}
                          onClick={handleClick}   mode="inline" items={items}/>

                    <Menu
                        selectedKeys={selectedTheme}
                        style={{

                            height: "40%",
                            display: "flex",
                            flexDirection: "column",
                            justifyContent: "flex-end",
                        }}
                        onClick={handleClick} theme={sliderColor} mode="inline" items={items2}/>

                </Sider>
                {bodyComponent}

            </Layout>
        </HelmetProvider>
    );
}
export default HomePage;