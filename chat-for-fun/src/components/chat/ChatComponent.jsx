import React, {useState, useContext, useEffect} from 'react';
import ListFriend from "./ListFriend";
import {Avatar, Button, Input, Layout} from "antd";
import SliderHead from "./SliderHead";

import {
    PhoneOutlined,
    FileUnknownOutlined,
    MoreOutlined, SmileOutlined,PlusOutlined,SendOutlined
} from '@ant-design/icons';

import {ThemeContext} from "../../ThemeContext";
import {Helmet, HelmetProvider} from "react-helmet-async";

const { Header, Content, Footer, Sider } = Layout;


const ChatComponent = () =>{
    const [collapsedChat, setCollapsedChat] = useState(false);

    const SLid = <SliderHead collapsed={collapsedChat}/>

    const { currentTheme } = useContext(ThemeContext);
    const background = currentTheme.getBackground();
    const contentColor = currentTheme.getContent()
    const textColor = currentTheme.getText();

    const sliderColor = currentTheme.getKey().split("_")[1];
    const [listMessage, setListMessage] = useState([]);
    const [myId, setMyId] = useState("Px4");
    const clickUser = (card) =>{
        console.log("clickUser", card)
        var mess = card.description
        var l = [
            {
                id: "1",
                content: mess,
                contentType: "text",
                from: "Px4",
                reply: "",
            },
            {
                id: "1",
                content: "friend:"+mess,
                contentType: "text",
                from: "Px4k1",
                reply: "",
            },     {
                id: "1",
                content: "friend:"+mess,
                contentType: "text",
                from: "Px4k1",
                reply: "",
            },     {
                id: "1",
                content: "friend:"+mess,
                contentType: "text",
                from: "Px4k1",
                reply: "",
            },     {
                id: "1",
                content: "friend:"+mess,
                contentType: "text",
                from: "Px4k1",
                reply: "",
            },     {
                id: "1",
                content: "friend:"+mess,
                contentType: "text",
                from: "Px4k1",
                reply: "",
            },     {
                id: "1",
                content: "friend:"+mess,
                contentType: "text",
                from: "Px4k1",
                reply: "",
            },     {
                id: "1",
                content: "friend:"+mess,
                contentType: "text",
                from: "Px4k1",
                reply: "",
            },     {
                id: "1",
                content: "friend:"+mess,
                contentType: "text",
                from: "Px4k1",
                reply: "",
            },     {
                id: "1",
                content: "friend:"+mess,
                contentType: "text",
                from: "Px4k1",
                reply: "",
            },
        ]
        setListMessage(l)
    }
    return(
        <HelmetProvider>
            <Helmet>
                <link href="/css/chat/chat.css"
                      rel="stylesheet"
                      type="text/css"/>
            </Helmet>
            <Sider
                theme={sliderColor}
                trigger={null}
                width={350}
                   style={{height: "100vh",  background: "transparent !important" }}
            >

                {SLid}
                <ListFriend clickUser={clickUser}/>
            </Sider>

            <Layout
                style={{
                    minWidth:500,
                    height: "100vh",
                    background: contentColor,
                }}
            >

                <Header
                    style={{
                        height: "12.7%",
                        minHeight: 85,
                        padding: 0,
                        background: "transparent",
                        display: "flex",
                        flexDirection: "row",
                        justifyContent: "flex-start",
                        alignItems: "center",
                    }}
                >
                    <Avatar
                        style={{
                            background:"grey",

                            marginLeft:15,
                            marginRight:17,
                            width: 50, height: 50
                        }}
                    />
                    <div style={{width: "100%", display: "flex", justifyContent: "space-between", padding: "10px 25px 5px 0px"}}>
                        <h3 style={{color: textColor, display: "flex", justifyContent: "center", alignItems:"center"}}>Kieu Thanh Phat</h3>
                        <div>
                            <PhoneOutlined style={{ color: textColor,fontSize: 30, marginRight: 30 }} />
                            <MoreOutlined  style={{ color: textColor,fontSize: 30 }} />
                        </div>
                    </div>

                </Header>

                <Content style={{}}>
                    {listMessage.map((item, index) => {
                        if (item.contentType === "text") {
                            return (
                                <div style={{color: textColor}} key={index} className={item.from === myId ? "message message-me" : "message message-friend"}>
                                    <p>{item.content}</p>
                                </div>
                            );
                        } else if (item.contentType === "image") {
                            return (
                                <div style={{color: textColor}} key={index} className={item.from === myId ? "message message-me" : "message message-friend"}>
                                    <FileUnknownOutlined />
                                </div>
                            );
                        }
                        return null; // Trả về null nếu không khớp với bất kỳ điều kiện nào
                    })}
                </Content>

                <Footer
                    style={{ background: "transparent", textAlign: 'center', display: "flex" ,}} >
                    <Button className="btn-addFile" style={{color: textColor}} icon={<PlusOutlined />} />
                    <Input
                        suffix={<Button icon={<SmileOutlined />}  />}
                    />
                    <Button className="btn-sendMess" style={{color: textColor}} icon={<SendOutlined />} />


                </Footer>
            </Layout>
        </HelmetProvider>
    )
}
export default ChatComponent;
