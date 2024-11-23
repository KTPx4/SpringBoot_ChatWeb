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
    ApartmentOutlined,
    OpenAIOutlined,
    CommentOutlined,
    WindowsOutlined, QqOutlined, BranchesOutlined
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
import GroupConponent from "../../components/group/GroupConponent";

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
    // getItem('User', 'settings', <UserOutlined />),
    getItem('Chats', 'chats', <CommentOutlined />),
    getItem('Groups', 'groups', <TeamOutlined />),
    getItem('Friends', 'friends', <ApartmentOutlined />),
    // getItem('AI', 'ai', <OpenAIOutlined />)
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
    const [collapsed, setCollapsed] = useState(localStorage.getItem('collapsed-main') ?? false);
    const { currentTheme, changeTheme } = useContext(ThemeContext);
    const key = currentTheme.getKey();
    const textColor = currentTheme.getText()
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

        // Cleanup khi component unmount
        return () => {
            if (socketHandler) {
                socketHandler.disconnect();
            }
        };
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
    const  clickAvt = () =>{
        setBodyComponent(<ProfileComponent openNotification={openNotification} changeAvt={changeAvt}/>)
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
                break

            case "logout":
                showModal()

                break

            case "groups":
                setBodyComponent(<GroupConponent socketHandler={webSocketHandler}/>)
                break

            case "friends":
                setBodyComponent(<FriendComponent userId={id} socketHandler={webSocketHandler}/>)
                    break

            case "ai":
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
                className={`modal-${key === "theme_dark" ? "dark":"light"}`}
                title={<span style={{color: textColor}}>Log Out</span>}
                open={open}
                onOk={handleOk}
                confirmLoading={confirmLoading}
                onCancel={handleCancel}
            >
                <p style={{color: textColor}}>Do you want to logout?</p>
            </Modal>
            <Layout className="layout-main"  style={{ minHeight: '100vh', background: contentColor, overflowX: "auto" }}>
                <Sider className="main-sider" style={{ padding: "30px 0", border: "none !important"}}
                       theme={sliderColor}

                       collapsible collapsed={collapsed} onCollapse={(value) => {
                            localStorage.setItem('collapsed-main', value )
                           setCollapsed(value)
                }}>


                    <div style={{ marginBottom: 10,display: "flex", flexDirection:"column", width: "100%", alignItems: "center"}}>
                        { avt ?
                            <Avatar size={50} src={avt}
                                    onClick={clickAvt}
                                    onError={()=> setAvt("")}/>
                            :
                            <Spin /> }

                    </div>

                    <Menu
                        selectedKeys={selectedMenu}
                            style={{height: "50%", border: "none"}}
                          theme={sliderColor}
                          onClick={handleClick}   mode="inline" items={items}/>

                    <Menu
                        selectedKeys={selectedTheme}
                        style={{
                            height: "40%",
                            display: "flex",
                            flexDirection: "column",
                            justifyContent: "flex-end",
                            border: "none"
                        }}
                        onClick={handleClick} theme={sliderColor} mode="inline" items={items2}/>

                </Sider>
                {bodyComponent}

            </Layout>
        </HelmetProvider>
    );
}
export default HomePage;