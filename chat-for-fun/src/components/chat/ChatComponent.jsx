import React, {useState, useContext, useEffect, useRef} from 'react';
import ListChat from "./ListChat";
import {Avatar, Button, Image, Input, Layout, message, Modal, Popover, Result, Tooltip, Upload} from "antd";
import SliderHead from "./SliderHead";

import {
    PhoneOutlined,
    FileUnknownOutlined,
    EyeTwoTone,
    MoreOutlined,
    SmileOutlined,
    PlusOutlined,
    SendOutlined,
    CheckCircleOutlined,
    EyeOutlined,
    CheckCircleTwoTone,
    FileOutlined, InboxOutlined
} from '@ant-design/icons';

import {ThemeContext} from "../../ThemeContext";
import {Helmet, HelmetProvider} from "react-helmet-async";
import EmojiPicker from "emoji-picker-react";
import useStore from "../../store/useStore";
import WebSocketHandler from "./WebSocketHandler";
import useHCMTime from "../../hooks/useHCMTime";
import axios from "axios";
import {Alert} from "react-bootstrap";
import Spinner from "react-bootstrap/Spinner";

const { Header, Content, Footer, Sider } = Layout;
const { Dragger } = Upload;

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
    const hintColor = currentTheme.getHint();
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
    const [messageApi, contextHolder2] = message.useMessage();

    const [loadingStatus, setStatusLoading] = useState(false)
    const [isModalFile, setIsModalFile] = useState(false)

    const openModal = {
        File: () => setIsModalFile(true)
    }
    const propsFile = {
        name: 'file',
        multiple: false,
        action: `${SERVER}/file?token=${token}&group=${selectedCard?.groupId ?? "-1"}`,
        beforeUpload: (file) => {
            // Các loại file được phép
            const allowedExtensions = [
                "jpg", "jpeg", "png",
                "zip", "rar", "txt",
                "docx", "xlsx", "ppt", "pptx"
            ];

            // Lấy phần mở rộng của file
            const fileExtension = file.name.split('.').pop().toLowerCase();

            // Kiểm tra loại file
            const isAllowedFileType = allowedExtensions.includes(fileExtension);
            if (!isAllowedFileType) {
                message.error(`You can only upload files with extensions: ${allowedExtensions.join(", ")}`);
                return false;
            }

            // Kiểm tra kích thước file
            const isLt30M = file.size / 1024 / 1024 < 30;
            if (!isLt30M) {
                message.error('File must be smaller than 30MB!');
                return false;
            }

            return isAllowedFileType && isLt30M;
        },
        onChange(info) {
            const { status } = info.file;
            console.log(status)
            if (status !== 'uploading') {
                console.log('Uploading file:', info.file);
            }
            if (status === 'done') {
                message.success(`${info.file.name} file uploaded successfully.`);
                var messId = info.file.response.data.id;
                socketHandler.sendFile(selectedCard?.id, messId)
                // const newLink = info.file.response + `?t=${Date.now()}`;
                // const curr = { ...currentSelected, avatar: newLink };
                // setCurrentSelected(curr);
                //
                // const updatedDataGroup = dataGroup.map((gr) =>
                //     gr.id === curr.id ? curr : gr
                // );
                // setDataGroup(updatedDataGroup);
                setIsModalFile(false)
            } else if (status === 'error') {
                message.error(`${info.file.name} file upload failed.`);
            }
        },
        onDrop(e) {
            console.log('Dropped files', e.dataTransfer.files);
        },
    };
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

            if (selectedCardRef?.current && selectedCardRef.current.id === newMessage.sender && isCanSend) {
                socketHandler.sendSeen(selectedCardRef.current.id);
            }
            else if(!selectedCard && newMessage.sender !== myId)
            {
            }
            setNewMess(newMessage)
            setListMessage((prevMessages) => [...prevMessages, newMessage]);
        });

        socketHandler.setOnGetSeen((msg) => {

            if (selectedCardRef.current && msg.sender === selectedCardRef.current.id) {
                setIsSeen(true);
            }
        });

    }, []);


// cập nhật ref mỗi khi selectedCard thay đổi
    useEffect(() => {
        if(selectedCard)
        {
            console.log("update: ", selectedCard)
            selectedCardRef.current = selectedCard;
            if(selectedCard.status?.toLowerCase() === STATUS_FRIEND.blockedBy || selectedCard.status?.toLowerCase() === STATUS_FRIEND.blocked)
            {
                console.log("run")
                setCanSend(false)
            }
            else{
                if(webSocketHandler) webSocketHandler.sendSeen(selectedCard.id)
                inputRef.current?.focus()
                setCanSend(true)
            }
            if(messagesEndRef && messagesEndRef.current)
            {
                messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
            }
        }
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
        setSelectedCard(card);
        setListMessage(card.messages)

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

    const sendServer= async (action, method, data)=>{
        try {
            const url = `${SERVER}/${action}`
            const res =  await axios({
                url: url,
                method: method,
                headers:{
                    authorization: `Bearer ${token}`,
                    // "Content-Type": "application/json",
                },
                data: data
            })

            return res

        } catch (err) {
            console.log(err?.response)
            alert(err?.response.data.message ?? "Failed to connect Server")
            return err?.response
        }
    }

    const handleStatusUser = async()=>{
        if(!selectedCard || loadingStatus) return;
        const action = `friend/status/${selectedCard.id}`
        const method = "post"
        setStatusLoading(true)
        const response = await sendServer(action, method, null)

        setStatusLoading(false)

        if(response?.status && response.status === 200)
        {
            const data = response.data.data
            var status = data.status.toLowerCase();
            var newUpdate = {...selectedCard, status: status}

            setSelectedCard(newUpdate)
        }
        else{
            messageApi.open({
                type: "error", // success, error , warning
                content: response?.data?.message ?? "Error, try again!",
                className: 'custom-class',
                style: {
                    marginTop: '10vh',
                },
            });
        }
    }

    const childMore = (<>
        <div style={{
            display: "flex",
            flexDirection: "column",
            background: "white",
        }}>
            <Button
                onClick={()=> window.location.replace(`/account/${selectedCard?.id}`)}
                className="m-1"
            >
                Profile
            </Button>
            <Button
                onClick={handleStatusUser}
                style={{color: selectedCard?.status === STATUS_FRIEND.normal ? "black" : "#2f99f0"}}
                className="m-1"
            >
                {loadingStatus && (<Spinner style={{width: 20, height: 20}} variant="info" />)}
                {!loadingStatus && (selectedCard?.status === STATUS_FRIEND.normal ? "Block" : "UnBlocked")}
            </Button>
        </div>
    </>)

    const HeaderContent = () => {
        if (selectedCard !== null) {
            return (
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
                            <PhoneOutlined className="btn-call" style={{display: "none", color: textColor,fontSize: 30, marginRight: 30 }} />
                            {selectedCard.status?.toLowerCase() !== STATUS_FRIEND.blockedBy && (
                                <Tooltip title={childMore} placement="bottomLeft" color={"white"}>
                                    <MoreOutlined className="btn-more" style={{ color: textColor,fontSize: 30 }} />
                                </Tooltip>
                            )}

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
            <Modal
                className={`modal-${themeName === "theme_dark" ? "dark" : "light"} Modal-File`}
                onCancel={()=>setIsModalFile(false)}
                open={isModalFile} footer={null}
            >
                <Dragger className="p-2" {...propsFile}>
                    <p className="ant-upload-drag-icon">
                        <InboxOutlined />
                    </p>
                    <p style={{color: textColor}}  className="ant-upload-text">Click or drag file to this area to upload</p>
                    <p style={{color: hintColor}} className="ant-upload-hint">
                        Select an image less than 30mb
                    </p>
                </Dragger>
            </Modal>
            <Sider
                theme={sliderColor}
                 width={350}
                   style={{height: "100vh",  background: "transparent !important" }}
            >

                {SLid}
                <ListChat updateCard={selectedCard} searchName={searchValue} clickUser={clickUser} newMessage={newMess} oldMessage={oldMess} />
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
                                    var isSystem = (item.system === true || item.isSystem === true)
                                    if (item.contentType === "text") {
                                        return (

                                            <div
                                                style={{
                                                    display: "flex",
                                                    flexDirection: "row",
                                                    alignItems: "center"
                                                }}
                                                key={item.id + Date.now()}
                                                className={ `message 
                                                    ${item.sender === myId ?"message-me" : "message message-friend"} 
                                                    ${(page > 1 &&  index % (15) === 0 ) ? "message-border" : ""}
                                                `}
                                            >
                                                {!isSystem && (
                                                    <Tooltip title={item.senderName}>
                                                        <Avatar style={{
                                                            width: 25,
                                                            height: 25,
                                                            visibility: item.sender === myId ? "hidden" : "visible"
                                                        }} src={item.avatar}/>
                                                    </Tooltip>
                                                )}
                                                <Tooltip  title={convertToHCMTime(item.createdAt??"")}>
                                                    <p>{item.content}</p>
                                                </Tooltip>
                                            </div>

                                        );
                                    }

                                    else if (item.contentType === "image") {
                                        var link = `${SERVER}/file?token=${token}&group=${selectedCard.groupId}&id=${item.id}`
                                        return (
                                            <div
                                                key={item.id}
                                                style={{
                                                    backgroundColor: loading || item.error ? 'lightgrey' : 'transparent',
                                                    width: "100%",
                                                    maxHeight: 400,
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    justifyContent: 'center',
                                                    margin: "10px 0 "
                                                }}

                                                className={item.sender === myId ? "message img message-me" : "message img message-friend"}
                                            >
                                                <div style={{
                                                    display: "flex",
                                                    flexDirection: "row",
                                                    alignItems: "flex-end"
                                                }}>
                                                    {!isSystem && (
                                                        <Tooltip title={item.senderName}>
                                                            <Avatar style={{
                                                                width: 25,
                                                                height: 25,
                                                                visibility: item.sender === myId ? "hidden" : "visible"
                                                            }} src={item.avatar}/>
                                                        </Tooltip>
                                                    )}
                                                    <Tooltip style={{}} title={convertToHCMTime(item.createdAt ?? "")}>
                                                        <Image
                                                            alt="Image"
                                                            src={link}
                                                            style={{display: loading ? 'none' : 'block'}}
                                                            onLoad={handleLoad}
                                                            onError={handleError}
                                                        />
                                                        {loading && <span style={{
                                                            height: 300,
                                                            width: 200,
                                                            textAlign: "center",
                                                            display: "flex",
                                                            alignItems: "center"
                                                        }}>Loading...</span>}
                                                        {/*{error && <span style={{ height: 300, width: 200, textAlign: "center", display: "flex", alignItems: "center" }}>Error loading image</span>}*/}
                                                    </Tooltip>
                                                </div>

                                            </div>
                                        );
                                    }
                                    // Xử lý loại nội dung "file"
                                    else if (item.contentType === "file") {
                                        var fileLink = `${SERVER}/file?token=${token}&group=${selectedCard.groupId}&id=${item.id}`;
                                        return (
                                            <div
                                                key={item.id}
                                                className={`file message ${item.sender === myId ? "message-me" : "message-friend"}`}
                                                style={{margin: "10px 0" ,display: "flex", flexDirection: "row",  alignItems: "flex-end"}}
                                            >
                                                {!isSystem && (
                                                    <Tooltip title={item.senderName}>
                                                        <Avatar style={{
                                                            width: 25,
                                                            height: 25,
                                                            visibility: item.sender === myId ? "hidden" : "visible"
                                                        }} src={item.avatar}/>
                                                    </Tooltip>
                                                )}
                                                <Tooltip title={convertToHCMTime(item.createdAt ?? "")}>
                                                    <a
                                                        href={fileLink}
                                                        target="_blank"
                                                        rel="noopener noreferrer"
                                                        style={{
                                                            color: "white",
                                                            textDecoration: "underline",
                                                            wordBreak: "break-word",
                                                        }}

                                                    >
                                                        <FileOutlined className="mx-2"/>
                                                        {item.content || "Download File"}
                                                    </a>
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

                    {/*    footer input message */}
                    </Content>
                    {!isCanSend && (
                        <Alert variant="warning" >You has been blocked or blocked by this user!</Alert>
                    )}
                    {isCanSend && selectedCard && (
                        <Footer style={{background: "transparent", textAlign: 'center', display: "flex",}}>
                            {/*<Popover content={contentAddFile} trigger="hover">*/}
                            {/*</Popover>*/}
                                <Button onClick={() => openModal.File()} className="btn-addFile" style={{color: textColor}} icon={<PlusOutlined/>}/>
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
                                            <Button style={{border: "none"}} icon={<SmileOutlined/>} onClick={null}/>
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
