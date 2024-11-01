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
    WindowsOutlined
} from '@ant-design/icons';
import { ThemeContext } from '../../ThemeContext';
import ThemeManager from "../../ThemeManager";
import {Breadcrumb, Button, Layout, Menu, theme} from 'antd';
import themeManager from "../../ThemeManager";

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


const HomePage = () =>{

    const [collapsed, setCollapsed] = useState(false);
    const [bodyComponent,setBodyComponent] = useState(<ChatComponent />);
    const { currentTheme, changeTheme } = useContext(ThemeContext);
    const [selectedTheme, setSelectedTheme] = useState(currentTheme.getKey); // Quản lý các key được chọn

    const sliderColor = currentTheme.getKey().split("_")[1];
    const background = currentTheme.getBackground();
    const handleClick = ({key}) =>{
        console.log(key)
        switch (key)
        {
            case "chats":
                setBodyComponent(<ChatComponent />)
                break

            case "settings":
                break

            case "logout":
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
            <Layout style={{ minHeight: '100vh', }}>

                <Sider style={{ padding: "30px 0",}}
                       theme={sliderColor}
                       collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>

                    <div className="demo-logo-vertical" />

                    <Menu style={{height: "50%", }}
                          theme={sliderColor}
                          onClick={handleClick}  defaultSelectedKeys={['chats']} mode="inline" items={items}/>

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