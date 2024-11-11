import React, {useContext, useEffect, useRef, useState} from "react";
import {Helmet, HelmetProvider} from "react-helmet-async";
import { Button, Input, Menu, message, Modal, } from "antd";
import {TeamOutlined,SendOutlined, UserAddOutlined,UsergroupAddOutlined} from "@ant-design/icons";
import useStore from "../../store/useStore";
import {ThemeContext} from "../../ThemeContext";
import Sider from "antd/es/layout/Sider";

import FriendContent from "./FriendContent";
import RequestContent from "./RequestContent";
import SuggestContent from "./SuggestContent";

function getItem(label, key, icon, children) {
    return {
        key,
        icon,
        children,
        label,
    };
}

const items = [

    getItem('All Friends', 'all',  <TeamOutlined />),
    getItem('Friend Requests', 'request',<UserAddOutlined />),
    getItem('Suggests', 'suggest', <UsergroupAddOutlined />),

    // getItem('Files', '9', <FileOutlined />),
];
const FriendComponent = ({userId, socketHandler}) =>{
    const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';
    const [token ,setToken] = useState(localStorage.getItem('token-auth') || '');
    const {id, setId} = useStore()
    const [myId, setMyId] = useState(id||"px4");
    const [searchId, setSearchId] = useState(userId)
    const { currentTheme } = useContext(ThemeContext);
    const themeName = currentTheme.getKey();
    const contentColor = currentTheme.getContent()
    const textColor = currentTheme.getText();
    const sliderColor = currentTheme.getKey().split("_")[1];
    const borderColor = currentTheme.getBorder()
    const cardColor = currentTheme.getCard();

    const [messageApi, contextHolder2] = message.useMessage();

    const [collapsed, setCollapsed] = useState(false);
    const [selectedMenu, setSelectedMenu] = useState("all"); // Quản lý các key được chọn


    //modal fast message
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [inputValue, setInputValue] = useState('');
    const [idSendMess, setIdSendMess] = useState('');
    const focusChatRef = useRef(false);
    const [fastMessContent, setFastMessContent] = useState('');
    const fastMessContentRef = useRef(null);
    const [notify, setNotify] = useState(false);
    const [updateUser, setUpdateUser] = useState(null);
    const [listSearch, setListSearch] = useState([])
    const showModal = (userId) => {
        setIsModalVisible(true);
        setIdSendMess(userId);
    };

    useEffect(() => {
        fastMessContentRef.current = fastMessContent;
    }, [fastMessContent]);

    useEffect(() => {
        console.log("Modal :")
        if (isModalVisible) {
            setTimeout(() => { focusChatRef?.current?.focus(); }, 0);
        }
    }, [isModalVisible]);

    // const [content, setContent] = useState(<FriendContent userId={searchId} openModal={showModal} messageApi={messageApi}/>);

    useEffect(() => {
        if (!socketHandler) return;
        const token = localStorage.getItem('token-auth');


        // Chỉ thiết lập callback nếu `socketHandler` đã tồn tại
        socketHandler.setOnMessageReceived((newMessage) => {
            console.log("get mess: ", fastMessContentRef?.current)
            if (newMessage.sender === myId && newMessage.content === fastMessContentRef?.current)
            {
                setNotify(true)
            }


        });

        socketHandler.setOnAddFriend((friendItem)=>{
            if(friendItem)
            {
                try{

                    var dataUser = JSON.parse(friendItem)
                    console.log(dataUser)
                    setUpdateUser(dataUser)

                }
                catch (e)
                {
                    console.log(e)
                }

            }
            //setUpdateUser
        })
        socketHandler.setOnSearch((listSearch)=>{
            if(listSearch)
            {
                try{
                    var list = JSON.parse(listSearch);
                    console.log("search: " ,list)
                    setListSearch(list)
                }
                catch (e){
                    console.log(e)
                }
            }
        })

        return () => {
            socketHandler.setOnMessageReceived(null);
        };
    }, []);

    useEffect(()=>{
        if(notify)
        {

            messageApi.open({
                type: "success", // success, error , warning
                content: "Message has been sent. Please check chat!",
                className: 'custom-class',
                style: {
                    marginTop: '10vh',
                },
            });
            setNotify(false)
        }
    }, [notify])

    const sendAction = (userId) =>{
        // console.log("sendAction:", userId)
        if(socketHandler)socketHandler.sendAction(userId)
    }

    // search
    const sendSearch =(search)=>{

        if(socketHandler)socketHandler.sendSearch(search)

    }

    // modal fast message
    const handleOk = () => {
        if (inputValue && socketHandler && idSendMess) {
            setFastMessContent(inputValue)
            socketHandler.sendMessage( idSendMess, inputValue);
            setInputValue('');
            setIsModalVisible(false);
            setIdSendMess("")

        }
    };

    const handleCancel = () => {
        setIsModalVisible(false);

    };

// click slider menu
    const handleClickMenu =({key})=>{
        setSelectedMenu(key)
        window.history.pushState({}, null, "/")
        setSearchId("")
        switch (key)
        {
            case "all":
                // setContent(<FriendContent userId={searchId} openModal={showModal} messageApi={messageApi}/>);
                break

            case "request":
                // setContent(<RequestContent openModal={showModal} messageApi={messageApi}/>);
                break
            case "suggest":
                // setContent(<SuggestContent messageApi={messageApi} sendAction={sendAction} updateUser={updateUser}/>)
                break

        }
    }



    return(
        <HelmetProvider>
            <Helmet>
                <link href="/css/friend/friend.css" rel="stylesheet"/>
            </Helmet>
            {contextHolder2}

            <Modal
                title="Nhắn tin"
                open={isModalVisible}
                onOk={handleOk}
                onCancel={handleCancel}
                footer={[
                    <Button key="back" onClick={handleCancel}>
                        Đóng
                    </Button>,
                    <Button
                        key="submit"
                        type="primary"
                        icon={<SendOutlined />}
                        onClick={handleOk}
                    >
                        Gửi
                    </Button>,
                ]}
            >
                <Input
                    ref={focusChatRef}
                    placeholder="Nhập tin nhắn..."
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    onPressEnter={handleOk}
                />
            </Modal>
            <div style={{ height: "100vh", width: "100%"}} className="container-body mx-4">
                <div style={{
                    display: "flex",
                    flexDirection: "row",
                    alignItems: "center",
                    justifyContent: "start"
                }}
                     className="header">
                <h4 style={{color: textColor, visibility:"hidden"}}> ....</h4>


                </div>

                <div className="body " style={{display: "flex", flexDirection: "row", alignItems: "flex-start", height: "90%"}}>

                    <div className="body-left" style={{}}>
                        <Sider style={{ padding: "30px 0", height: "100%", minWidth: 175, width: 175}}
                               theme={sliderColor}
                               width={175}
                                trigger={null}
                               collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>


                            <Menu
                                selectedKeys={selectedMenu}
                                style={{ }}
                                theme={sliderColor}
                                onClick={handleClickMenu}   mode="inline" items={items}/>

                        </Sider>
                    </div>

                    <div className="body-rigth" style={{display: "flex", flexDirection:"row", width: "100%", height: "100%" }}>
                        {/*Place this*/}
                        {/*{content}*/}
                        {selectedMenu === "all" && (
                            <FriendContent userId={searchId} openModal={showModal} messageApi={messageApi}/>
                        )}
                        {selectedMenu === "request" && (
                            <RequestContent openModal={showModal} messageApi={messageApi}/>
                        )}
                        {selectedMenu === "suggest" && (
                            <SuggestContent openModal={showModal} messageApi={messageApi} sendAction={sendAction} updateUser={updateUser} searchName={sendSearch} listSearch={listSearch}/>
                        )}

                    </div>

                </div>
            </div>


        </HelmetProvider>
    )

}

export default FriendComponent