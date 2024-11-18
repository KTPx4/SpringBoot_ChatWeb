
import React, {useContext, useEffect, useRef, useState} from "react";
import useStore from "../../../store/useStore";
import {ThemeContext} from "../../../ThemeContext";
import {Avatar, Button, Card, Image, Input, Layout, List, Popover, Tooltip} from "antd";
import {
    CheckCircleTwoTone,
    EyeTwoTone,
    MoreOutlined,
    PlusOutlined,
    SendOutlined,
    SmileOutlined
} from "@ant-design/icons";
import EmojiPicker from "emoji-picker-react";
import useHCMTime from "../../../hooks/useHCMTime";

const { Header, Content, Footer, Sider } = Layout;

const ContentCenter = ({messageApi, currentSelected, sendMess, newMessage })=>{
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

    const [showPicker, setShowPicker] = useState(false);
    const [inputValue, setInputValue] = useState('');
    const inputRef = useRef(null);
    const listMessRef = useRef(null);
    const messagesEndRef = useRef(null);
    const [page, setPage] = useState(1);
    const {convertToHCMTime} = useHCMTime()
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [listWaitSend, setlistWaitSend] = useState([]);
    const [waitScroll, setWaitScroll] = useState(false);
    const [dataMess, setDataMess] = useState([]);

    useEffect(() => {
        if(newMessage && newMessage.sender === myId)
        {

            const content = newMessage.content;

            // Lọc ra các phần tử không khớp với điều kiện
            const updatedList = listWaitSend.filter((item) => item !== content);

            // Cập nhật state với mảng mới
            setlistWaitSend(updatedList);

        }
        else if(newMessage && !waitScroll)
        {
            if (messagesEndRef.current ) {
                messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
            }

        }


    }, [newMessage])

    useEffect(()=>{
        if(currentSelected )
        {
            setDataMess(currentSelected.messages)
        }
    }, [currentSelected])
    useEffect(()=>{
        if(dataMess && messagesEndRef.current )
        {
            messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });

        }
    }, [dataMess])

    const handleLoad = () => {
        setLoading(false); // Ảnh đã tải xong
    };

    const handleError = () => {
        setLoading(false); // Ảnh không tải được
        setError(true);
    };

    const onEmojiClick = ( emojiObject) => {
        setInputValue(prevInput => prevInput + emojiObject.emoji);
        inputRef.current?.focus()

    };


    const onSendMess = () =>{
        if(!inputValue)
        {
            return
        }

        var lsWait = listWaitSend
        lsWait.push(inputValue)
        setlistWaitSend(lsWait)

        sendMess(inputValue , (type, mess)=>{
            if(type === "error")
            {
                messageApi.open({
                    type: "error", // success, error , warning
                    content: mess,
                    className: 'custom-class',
                    style: {
                        marginTop: '10vh',
                    },
                });
            }
            else{

            }

        })

        setTimeout(() => {
            if (messagesEndRef.current) {
                messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
            }
        }, 100);
        setInputValue("")
    }

    const handleScroll =()=>{
        console.log("Scroll")
        if(currentSelected && listMessRef?.current.scrollTop === 0)
        {

            setWaitScroll(true)
            var friendId = currentSelected.id
            var nextPage = page + 1
            setPage(nextPage)

            const scrollPosition = listMessRef.current.scrollHeight - listMessRef.current.scrollTop;

            //loadMess(friendId, nextPage)
            // Khôi phục vị trí cuộn
            setTimeout(() => {
                listMessRef.current.scrollTop = listMessRef.current.scrollHeight - scrollPosition;
            }, 0);

        }
        else{
            setWaitScroll(false)
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

    return (
        <>
            {currentSelected && (
                <Layout style={{
                    height: "100%",

                    background: "transparent",
                }}>
                    <Header className="content-header" style={{background: "transparent", display: "flex", flexDirection: "row", alignItems: "center",justifyContent: "space-between", marginTop: 10}}>

                        <div style={{display: "flex", flexDirection: "row"}}>
                            <Avatar src={currentSelected.avatar} size={50}/>

                            <Card style={{height: "100%", padding: "0 !important", background: "transparent", border: "none"}}>
                                <Card.Meta
                                    title={
                                        <h4 style={{color: textColor, margin: 0, padding: "2px 0"}}>
                                            {currentSelected.name}
                                        </h4>}
                                    description={<i style={{color: textColor}}>{`${currentSelected.members?.length??0} members`}</i>}/>
                            </Card>
                        </div>
                        <Tooltip title={<></>} placement="bottomLeft" color={"white"}>
                            <MoreOutlined className="btn-more" style={{ color: textColor,fontSize: 30 }} />
                        </Tooltip>
                    </Header>
                    <Content
                        ref={listMessRef}
                        onScroll={handleScroll}
                        style={{padding: 30,height: "80vh", overflowY: "auto", borderRight:`1px solid ${borderColor}`}}>
                        {currentSelected && (
                            <>
                                {dataMess.map((item, index)=>{
                                        if (item.contentType === "text") {
                                            return (
                                                <div
                                                    key={item.id + Date.now()}
                                                    className={ `message 
                                                    ${(item.sender === myId ) ? "message-me" : "message message-friend"} 
                                                     ${(item.system === true) ? "message-server" : ""} 
                                                    ${(page > 1 &&  index % (15) === 0 ) ? "message-border" : ""}
                                                `}
                                                >
                                                    <Tooltip  title={item.createdAt && convertToHCMTime(item.createdAt)}>
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


                                {dataMess[dataMess.length - 1]?.sender === myId && listWaitSend.length < 1 &&
                                    <div style={{display: "flex", justifyContent: "end"}}>
                                        <Tooltip
                                             title={dataMess.at(dataMess.length - 1).whoSeen.length + " people seen"}>
                                            {dataMess.at(dataMess.length - 1).whoSeen.length < 1 ?
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

                    <Footer style={{height: "7%", width: "100%", background: "transparent", display: "flex", alignItems: "center"}}>

                        <Popover content={contentAddFile} trigger="hover">
                            <Button className="btn-addFile" style={{color: "grey"}} icon={<PlusOutlined/>}/>
                        </Popover>

                        <div className="input-mess"
                             style={{position: 'relative', display: "inline-block", width: "100%" , marginLeft: 10}}>
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
                        <Button onClick={onSendMess} className="btn-sendMess" style={{color: "grey", marginLeft: 10}} icon={<SendOutlined/>}/>

                    </Footer>
                </Layout>
            )}
        </>
    )
}
export default ContentCenter