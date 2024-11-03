import React, {useEffect, useState,useContext} from "react";
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
import {Breadcrumb, Button, Layout, Menu, Modal} from 'antd';
import themeManager from "../../ThemeManager";
import axios from "axios";

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

const HomePage = () =>{

    const [collapsed, setCollapsed] = useState(false);
    const [bodyComponent,setBodyComponent] = useState(<ChatComponent />);
    const { currentTheme, changeTheme } = useContext(ThemeContext);
    const [selectedTheme, setSelectedTheme] = useState(currentTheme.getKey); // Quản lý các key được chọn
    const [selectedMenu, setSelectedMenu] = useState("chats"); // Quản lý các key được chọn

    const sliderColor = currentTheme.getKey().split("_")[1];
    const background = currentTheme.getBackground();
    const [open, setOpen] = useState(false);
    const [confirmLoading, setConfirmLoading] = useState(false);
    const [modalText, setModalText] = useState('Content of the modal');
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
                // "Content-Type": "application/json",
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

    const handleClick = ({key}) =>{
        if(key !== "logout") setSelectedMenu(key)
        switch (key)
        {
            case "chats":
                setBodyComponent(<ChatComponent />)
                break

            case "settings":
                break

            case "logout":
                showModal()

                break

            case "groups":
                break

            case "friends":
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
            <Layout style={{ minHeight: '100vh', }}>

                <Sider style={{ padding: "30px 0",}}
                       theme={sliderColor}
                       collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>

                    <div className="demo-logo-vertical" />

                    <Menu
                        selectedKeys={selectedMenu}
                            style={{height: "50%", }}
                          theme={sliderColor}
                          onClick={handleClick}   mode="inline" items={items}/>

                    <Menu
                        selectedKeys={selectedTheme}
                        style={{
                            paddingBottom: 20,
                            height: "50%",
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