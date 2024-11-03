import React, {useState, useContext, useEffect, useRef} from 'react';
import ListFriend from "./ListFriend";
import {Avatar, Button, Image, Input, Layout, Popover, Result} from "antd";
import SliderHead from "./SliderHead";

import {
    PhoneOutlined,
    FileUnknownOutlined,
    MoreOutlined, SmileOutlined,PlusOutlined,SendOutlined
} from '@ant-design/icons';

import {ThemeContext} from "../../ThemeContext";
import {Helmet, HelmetProvider} from "react-helmet-async";
import EmojiPicker from "emoji-picker-react";

const { Header, Content, Footer, Sider } = Layout;


const ChatComponent = () =>{
    const [collapsedChat, setCollapsedChat] = useState(false);

    const SLid = <SliderHead collapsed={collapsedChat}/>

    const { currentTheme } = useContext(ThemeContext);
    const themeName = currentTheme.getKey();
    const background = currentTheme.getBackground();
    const contentColor = currentTheme.getContent()
    const textColor = currentTheme.getText();

    const sliderColor = currentTheme.getKey().split("_")[1];
    const [listMessage, setListMessage] = useState([{}]);
    const [myId, setMyId] = useState("Px4");
    const [userName, setUserName] = useState("...");
    const [selectedCard, setSelectedCard] = useState(null);
    const [inputValue, setInputValue] = useState('');
    const [showPicker, setShowPicker] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    // Đảm bảo input giữ focus sau khi cập nhật

    // const inputRef = useRef(null); // Tạo ref cho input

    const onEmojiClick = ( emojiObject) => {


        setInputValue(prevInput => prevInput + emojiObject.emoji);
       // setShowPicker(false); // Ẩn emoji picker sau khi chọn emoji
        // Đảm bảo input giữ focus sau khi cập nhật
        // inputRef.current?.focus();
    };

    const handleButtonClick = () => {
        setShowPicker(prevState => !prevState); // Toggle emoji picker khi bấm nút
    };

    const clickUser = (card) =>{

        setSelectedCard(card);
        setListMessage(card.messages)
    }

    const handleLoad = () => {
        setLoading(false); // Ảnh đã tải xong
    };

    const handleError = () => {
        setLoading(false); // Ảnh không tải được
        setError(true);
    };

    const HeaderContent = () =>{
        if(selectedCard !== null)
        {
            return(
                <>
                    <Avatar
                        shape="circle"
                        src={selectedCard.avatar}
                        style={{

                            background: "lightgrey",
                            marginLeft:15,
                            marginRight:17,
                            width: 55, height: 50
                        }}
                    />
                    <div style={{width: "100%", display: "flex", justifyContent: "space-between", padding: "10px 25px 5px 0px"}}>
                        <h3 style={{color: textColor, display: "flex", justifyContent: "center", alignItems:"center"}}>{selectedCard.name}</h3>
                        <div>
                            <PhoneOutlined className="btn-call" style={{ color: textColor,fontSize: 30, marginRight: 30 }} />
                            <MoreOutlined className="btn-more" style={{ color: textColor,fontSize: 30 }} />
                        </div>
                    </div>
                </>
            )
        }
    }
    const contentAddFile = (
        <div style={{
            display: "flex",
            flexDirection: "column",
        }}>
            <Button className="m-1">File</Button>
            <Button className="m-1">Image</Button>
        </div>
    )
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
                    <HeaderContent/>

                </Header>

                <Content style={{
                    /* display: flex;
                        flex-direction: column;
                        justify-content: flex-end;
                        padding: 20px;
                        overflow-y: auto;
                    * */
                    display: "flex",
                    flexDirection: selectedCard? "column":"column-reverse",
                    justifyContent: "flex-end",
                    padding: "10px 35px",
                    overflowY: "auto",

                }}>
                    {!selectedCard ?
                        <Result
                            className={`wait-conversation ${themeName}`}
                            status="403"
                            title={<h4>Welcome</h4>}
                            subTitle={<p>Let choose a friend to start chatting......</p>}
                        />
                        :
                        listMessage.map((item, index) => {
                            if (item.contentType === "text") {
                                return (
                                    <div style={{color: textColor}} key={item.id} className={item.from === myId ? "message message-me" : "message message-friend"}>
                                        <p>{item.content}</p>
                                    </div>
                                );
                            }
                            else if (item.contentType === "image") {
                                return (

                                    <div
                                        style={{
                                            backgroundColor: loading || error ? 'lightgrey' : 'transparent',
                                            maxWidth: 300,
                                            maxHeight: 400,
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center'
                                        }}
                                        className={item.from === myId ? "message message-me" : "message message-friend"}
                                    >
                                        <Image
                                            alt="Image"
                                            src={item.content}
                                            style={{
                                                display: loading ? 'none' : 'block', // Ẩn ảnh khi đang tải
                                            }}
                                            onLoad={handleLoad}
                                            onError={handleError}
                                            key={item.id}
                                        />
                                        {loading && <span style={{height: 300, width: 200, textAlign:"center", display: "flex",alignItems: "center"}}>Loading...</span>} {/* Hiển thị thông báo tải, nếu cần */}
                                        {error &&
                                            <span style={{height: 300, width: 200, textAlign:"center",display: "flex", alignItems: "center"}} >Error loading image</span>} {/* Hiển thị thông báo lỗi, nếu cần */}
                                    </div>

                                );
                            }

                            return null; // Trả về null nếu không khớp với bất kỳ điều kiện nào
                        })
                    }

                </Content>


                {selectedCard && (
                    <Footer style={{background: "transparent", textAlign: 'center', display: "flex",}}>
                        <Popover content={contentAddFile} trigger="hover">
                            <Button className="btn-addFile" style={{color: textColor}} icon={<PlusOutlined/>}/>
                        </Popover>
                        <div className="input-mess"
                             style={{position: 'relative', display: "inline-block", width: "100%"}}>
                            {showPicker && (
                                <div key="picker" style={{position: 'absolute', bottom: "100%", right: 0, zIndex: 10}}>
                                    <EmojiPicker onEmojiClick={onEmojiClick}/>
                                </div>
                            )}
                            <Input
                                // ref={inputRef} // Tham chiếu đến input
                                value={inputValue}
                                onChange={(e) => {
                                    setInputValue(e.target.value)
                                    // inputRef.current?.focus();
                                }}
                                suffix={
                                    <Popover content={  <EmojiPicker onEmojiClick={onEmojiClick}/>} trigger="hover">
                                        <Button icon={<SmileOutlined/>} onClick={null}/>
                                    </Popover>
                                }
                            />
                        </div>
                        <Button className="btn-sendMess" style={{color: textColor}} icon={<SendOutlined/>}/>

                    </Footer>
                )}

            </Layout>
        </HelmetProvider>
    )
}
export default ChatComponent;
