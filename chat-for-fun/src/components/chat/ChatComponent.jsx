import React, {useState, useContext, useEffect, useRef} from 'react';
import ListChat from "./ListChat";
import {Avatar, Button, Image, Input, Layout, Popover, Result, Tooltip} from "antd";
import SliderHead from "./SliderHead";

import {
    PhoneOutlined,
    FileUnknownOutlined,EyeTwoTone,
    MoreOutlined, SmileOutlined, PlusOutlined, SendOutlined, CheckCircleOutlined, EyeOutlined, CheckCircleTwoTone
} from '@ant-design/icons';

import {ThemeContext} from "../../ThemeContext";
import {Helmet, HelmetProvider} from "react-helmet-async";
import EmojiPicker from "emoji-picker-react";
import useStore from "../../store/useStore";
import WebSocketHandler from "./WebSocketHandler";
import useHCMTime from "../../hooks/useHCMTime";
import axios from "axios";
import {Alert} from "react-bootstrap";

const { Header, Content, Footer, Sider } = Layout;

const TYPE_FRIEND = {
    none: "non",
    accept: "response",
    waiting: "waiting"
}
const STATUS_FRIEND ={
    normal: "normal",
    blocked: "blocked",
    blockedBy: "blockedby"
}

const ChatComponent = ({socketHandler}) =>{
    const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';
    const token = localStorage.getItem('token-auth') || '';
    const {id, setId} = useStore()
    const [myId, setMyId] = useState(id||"px4");

    const { currentTheme } = useContext(ThemeContext);
    const themeName = currentTheme.getKey();
    const contentColor = currentTheme.getContent()
    const textColor = currentTheme.getText();
    const sliderColor = currentTheme.getKey().split("_")[1];
    const borderColor = currentTheme.getBorder()
    const cardSelectedColor = currentTheme.cardSelected
    const [searchValue, setSearchValue] = useState("")

    const [collapsedChat, setCollapsedChat] = useState(false);
    const SLid = <SliderHead searchName={setSearchValue} collapsed={collapsedChat}/>


    const {convertToHCMTime} = useHCMTime()
    const [showPicker, setShowPicker] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    const [page, setPage] = useState(1);

    const [isSeen, setIsSeen] = useState(true);


    const [listMessage, setListMessage] = useState([{}]);
    const [listWaitSend, setlistWaitSend] = useState([]);
    const [newMess, setNewMess] = useState(null);
    const [oldMess, setOldMess] = useState(null);

    const [waitScroll, setWaitScroll] = useState(false);

    const [selectedCard, setSelectedCard] = useState(null);
    const selectedCardRef = useRef(null); // tạo ref để lưu selectedCard

    const [inputValue, setInputValue] = useState('');
    const inputRef = useRef(null);
    const listMessRef = useRef(null);
    const messagesEndRef = useRef(null);

    const [webSocketHandler, setWebSocketHandler] = useState(socketHandler);

    const [isCanSend, setCanSend] = useState(true);


    useEffect(() => {
        const token = localStorage.getItem('token-auth');


        // Chỉ thiết lập callback nếu `socketHandler` đã tồn tại
        socketHandler.setOnMessageReceived((newMessage) => {

            if (newMessage.sender === myId)
            {
                var content = newMessage.content;
                var ls = listWaitSend
                for(var i =0; i< ls.length; i++)
                {
                    if(ls[0] === content)
                    {
                        ls.splice(i,1);
                        break
                    }
                }
                setlistWaitSend(ls)
            }

            if (selectedCardRef?.current && selectedCardRef.current.id === newMessage.sender) {
                socketHandler.sendSeen(selectedCardRef.current.id);
            }
            else if(!selectedCard && newMessage.sender !== myId)
            {
            }
            setNewMess(newMessage)
            setListMessage((prevMessages) => [...prevMessages, newMessage]);
        });

        socketHandler.setOnGetSeen((userSeen) => {

            if (selectedCardRef.current && userSeen === selectedCardRef.current.id) {
                setIsSeen(true);
            }
        });

    }, []);


// cập nhật ref mỗi khi selectedCard thay đổi
    useEffect(() => {
        selectedCardRef.current = selectedCard;
        inputRef.current?.focus()
    }, [selectedCard]);


    // Cuộn tới cuối danh sách mỗi khi listMessage thay đổi
    useEffect(() => {
        if (messagesEndRef.current && !waitScroll) {
            messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
        }
        const hasUnSeen = listMessage.some(mess=>mess.seen === false)
        if(hasUnSeen) setIsSeen(false);


    }, [listMessage]); // Mỗi khi listMessage thay đổi, hiệu ứng sẽ được kích hoạt


    const onEmojiClick = ( emojiObject) => {
        setInputValue(prevInput => prevInput + emojiObject.emoji);
        inputRef.current?.focus()

    };


    const handleButtonClick = () => {
        setShowPicker(prevState => !prevState); // Toggle emoji picker khi bấm nút
    };

    const loadMess = async(id, page)=>{
        const url = `${SERVER}/chat/friend/${id}?page=${page}`;

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
                const data = res.data.data
                if(data)
                {
                    setOldMess(data)
                    setListMessage((prevMessages) => [...data, ...prevMessages]);

                }
            }

        } catch (err) {
            alert("Failed to connect Server")
        } finally {
            setLoading(false);
        }
    }

    const handleScroll =()=>{
        if(selectedCardRef?.current && listMessRef?.current.scrollTop === 0)
        {

            setWaitScroll(true)
            var friendId = selectedCardRef?.current.id
            var nextPage = page + 1
            setPage(nextPage)

            const scrollPosition = listMessRef.current.scrollHeight - listMessRef.current.scrollTop;

            loadMess(friendId, nextPage)
            // Khôi phục vị trí cuộn
            setTimeout(() => {
                listMessRef.current.scrollTop = listMessRef.current.scrollHeight - scrollPosition;
            }, 0);

        }
        else{
            setWaitScroll(false)
        }
    }

    const clickUser = (card) =>{
        console.log(card)
        if(card.status?.toLowerCase() === STATUS_FRIEND.blockedBy || card.status?.toLowerCase() === STATUS_FRIEND.blocked)
        {
            setCanSend(false)
        }
        setSelectedCard(card);
        setListMessage(card.messages)
        if(webSocketHandler)webSocketHandler.sendSeen(card.id)
    }

    const onSendMess = () =>{
        if(inputValue === null || !inputValue || !selectedCard) return;
        if(webSocketHandler)webSocketHandler.sendMessage(selectedCard.id, inputValue)
        setIsSeen(false)

        var ls = listWaitSend
        ls.push(inputValue)
        setlistWaitSend(ls)

        setTimeout(() => {
            if (messagesEndRef.current) {
                messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
            }
        }, 100);
        setInputValue("")
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
                 width={350}
                   style={{height: "100vh",  background: "transparent !important" }}
            >

                {SLid}
                <ListChat searchName={searchValue} clickUser={clickUser} newMessage={newMess} oldMessage={oldMess} />
            </Sider>

            <Layout
                style={{
                    border: `1px solid ${borderColor}`,

                    borderStartStartRadius: 25,
                    borderEndStartRadius: 25,
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

                    <Content
                        ref={listMessRef}
                        onScroll={handleScroll}
                        style={{

                            display: "flex",
                            flexDirection: selectedCard ? "column" : "column-reverse",
                            justifyContent: selectedCard ? null : "flex-end",
                            padding: "10px 35px",
                            overflowY: "auto",

                        }}>
                        {!selectedCard ? (
                            <Result
                                className={`wait-conversation ${themeName}`}
                                status="403"
                                title={<h4>Welcome</h4>}
                                subTitle={<p>Let choose a friend to start chatting......</p>}
                            />
                        ) : (
                            <>
                                {listMessage.map((item, index) => {

                                    if (item.contentType === "text") {
                                        return (

                                            <div

                                                // key={item.id + Date.now()}
                                                className={ `message 
                                                    ${item.sender === myId ?"message-me" : "message message-friend"} 
                                                    ${(page > 1 &&  index % (15) === 0 ) ? "message-border" : ""}
                                                `}
                                            >
                                                <Tooltip  title={convertToHCMTime(item.createdAt??"")}>
                                                    <p>{item.content}</p>
                                                </Tooltip>
                                            </div>

                                        );
                                    } else if (item.contentType === "image") {
                                        return (
                                            <div
                                                style={{
                                                    backgroundColor: loading || item.error ? 'lightgrey' : 'transparent',
                                                    maxWidth: 300,
                                                    maxHeight: 400,
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    justifyContent: 'center'
                                                }}
                                                key={item.id + "img"}
                                                className={item.sender === myId ? "message message-me" : "message message-friend"}
                                            >
                                                <Tooltip  title={convertToHCMTime(item.createdAt??"")}>
                                                    <Image
                                                        alt="Image"
                                                        src={item.content}
                                                        style={{ display: loading ? 'none' : 'block' }}
                                                        onLoad={handleLoad}
                                                        onError={handleError}
                                                    />
                                                    {loading && <span style={{ height: 300, width: 200, textAlign: "center", display: "flex", alignItems: "center" }}>Loading...</span>}
                                                    {error && <span style={{ height: 300, width: 200, textAlign: "center", display: "flex", alignItems: "center" }}>Error loading image</span>}
                                                </Tooltip>
                                            </div>
                                        );
                                    }
                                    return null;
                                })}
                                {listWaitSend.map((item, index) => {
                                    return (
                                        <div
                                            style={{
                                                color: textColor,
                                            }}
                                            key={item+"wait"}
                                            className="message-wait message message-me"
                                        >
                                            <p    key={item+"mess"}>{item}</p>
                                        </div>)
                                })}

                                {listMessage[listMessage.length - 1]?.sender === myId && listWaitSend.length < 1 &&
                                    <div style={{display: "flex", justifyContent: "end"}}>
                                        <Tooltip
                                            title={(listMessage[listMessage.length - 1]?.seen === true) ? "Seen" : "Sent"}>
                                            {!isSeen ?
                                                <CheckCircleTwoTone className="message message-me"
                                                                    twoToneColor="#2f99f0"/> :

                                                <EyeTwoTone   className="message message-me" alt="Seen"
                                                             twoToneColor={cardSelectedColor}/>}
                                        </Tooltip>
                                    </div>}

                                {/* Element dùng để cuộn xuống cuối */}
                                <div ref={messagesEndRef}/>
                            </>
                        )}
                    </Content>
                    {!isCanSend && (
                        <Alert variant="warning" >You has been blocked or blocked by this user!</Alert>
                    )}
                    {isCanSend && selectedCard && (
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

                                    ref={inputRef} // Tham chiếu đến input
                                    value={inputValue}
                                    onChange={(e) => {

                                        setInputValue(e.target.value)
                                        // inputRef.current?.focus();
                                    }}
                                    onKeyDown={(e) => {
                                        if (e.key === 'Enter') {
                                            onSendMess();  // Gọi hàm gửi tin nhắn khi nhấn Enter
                                        }
                                    }}
                                    suffix={
                                        <Popover content={  <EmojiPicker onEmojiClick={onEmojiClick}/>} trigger="hover">
                                            <Button icon={<SmileOutlined/>} onClick={null}/>
                                        </Popover>
                                    }
                                />
                            </div>
                            <Button onClick={onSendMess} className="btn-sendMess" style={{color: textColor}} icon={<SendOutlined/>}/>

                        </Footer>
                    )}


            </Layout>
        </HelmetProvider>
    )
}
export default ChatComponent;
