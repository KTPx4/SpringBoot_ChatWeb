
import React, {useContext, useEffect, useRef, useState} from "react";
import useStore from "../../../store/useStore";
import {ThemeContext} from "../../../ThemeContext";
import {
    Avatar,
    Button,
    Card,
    Drawer,
    Image,
    Input,
    Layout,
    List, message,
    Modal,
    Popconfirm,
    Popover,
    Switch,
    Tooltip
} from "antd";
import {
    CheckCircleTwoTone,
    EyeTwoTone,
    MoreOutlined,
    PlusOutlined,
    SendOutlined,
    SmileOutlined,
    FileOutlined, MinusCircleTwoTone
} from "@ant-design/icons";
import EmojiPicker from "emoji-picker-react";
import useHCMTime from "../../../hooks/useHCMTime";
import {Alert} from "react-bootstrap";
import axios from "axios";

const { Header, Content, Footer, Sider } = Layout;

const ContentCenter = ({
                           Scroll,
                           messageApi,
                           currentSelected,
                           sendMess,
                           newMessage,
                           sendPermis,
                           openModal ,
                           loadMess,
                           handleRemove
})=>{
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

    const [open, setOpen] = useState(false);
    const [valuePermiss, setValuePermiss] = useState(true)

    useEffect(() => {
        if(newMessage && newMessage.sender === myId)
        {
            // var dtM = dataMess
            // dtM.push(newMessage)
             //setDataMess( dtM)
            const content = newMessage.content;

            // Lọc ra các phần tử không khớp với điều kiện
            const updatedList = listWaitSend.filter((item) => item !== content);

            // Cập nhật state với mảng mới
            setlistWaitSend(updatedList);

        }
        // if(newMessage)
        // {
        //     setDataMess((prev) => [...prev, newMessage]);
        // }
        if(newMessage && !waitScroll)
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
            setValuePermiss(currentSelected.allPermit)
            if(dataMess && messagesEndRef.current && !waitScroll )
            {
                messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
            }
        }
    }, [currentSelected])

    useEffect(()=>{
        if(dataMess && messagesEndRef.current && !waitScroll )
        {
            messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
        }
    }, [dataMess])

    useEffect(()=>{
        if(loadMess && currentSelected )
        {
            setWaitScroll(true);
            var newDataMess = [...loadMess, ...dataMess]

            setDataMess(newDataMess)
        }
    }, [loadMess])

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
        setWaitScroll(false)
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

    const handleScroll = async()=>{

        if(currentSelected && listMessRef?.current.scrollTop === 0)
        {
            setWaitScroll(true)
            var friendId = currentSelected.id
            var nextPage = page + 1

            setPage(nextPage)

            const scrollPosition = listMessRef.current.scrollHeight - listMessRef.current.scrollTop;

            //loadMess(friendId, nextPage)


            await Scroll(currentSelected.id, nextPage)

        }
        else{
            setWaitScroll(false)
        }
    }

    const setPermit = async () =>{
        var permis= !valuePermiss
        setValuePermiss(permis)

        sendPermis(currentSelected.id, permis)


    }

    // drawer
    const showDrawer = () => {
        setOpen(true);
    };

    const onClose = () => {
        setOpen(false);
    };


    const contentAddFile =
        (
            <div style={{
                display: "flex",
                flexDirection: "column",
                }}>
                <Button className="m-1" >File</Button>
                <Button className="m-1">Image</Button>
            </div>
    )


    const confirmDelMember = (id) => {

        handleRemove(currentSelected?.id, [id])

        // message.success('Click on Yes');
    };
    const ContentDrawer =(
        <div style={{width: "100%", display: "flex", justifyContent: "center", flexDirection: "column"}}>
            {(currentSelected?.allPermit === true || currentSelected?.deputy?.includes(myId) || currentSelected?.leaderId === myId) && (
                <>
                    {(currentSelected?.deputy?.includes(myId) || currentSelected?.leaderId === myId) && (
                        <div style={{display: "flex", flexDirection: "row", justifyContent: "center"}}>
                            <Switch className="mb-3 mx-2" value={valuePermiss} onClick={setPermit} title="Permission"/>
                            <p style={{color: textColor}}>All Permission</p>
                        </div>
                    )}
                    <Button
                        onClick={()=> openModal.Upload()}
                        style={{background: "transparent", border: "none", color: textColor}}
                        className="mb-3 group-btn-drawer"
                    >Change Avatar</Button>
                    <Button
                        onClick={() => openModal.Name()}
                        style={{background: "transparent", border: "none", color: textColor}}
                        className="mb-3 group-btn-drawer"
                    >Change Name</Button>


                </>
            )}
            <Button
                onClick={()=> openModal.People()}
                style={{background: "transparent", border: "none", color: textColor}}
                className="mb-3 group-btn-drawer"
            >Members</Button>

            <Popconfirm
                title="Leave group"
                description="Are you sure to leave this group?"
                onConfirm={() => confirmDelMember(myId)}
                // onCancel={cancelDelMember}
                okText="Yes"
                cancelText="No"
            >
                <Button
                    onClick={()=> openModal.Leave()}
                    style={{background: "transparent", border: "none", color: "red"}}
                    className="mb-3 group-btn-drawer"
                >Leave</Button>
            </Popconfirm>

        </div>
    )

    var canSend = true;
    if (currentSelected && currentSelected?.canSend?.length > 0) {
        canSend = currentSelected.canSend.includes(myId)
    }
    if( currentSelected && !currentSelected?.members.includes(myId))
    {
        return (
            <>
                <Alert variant="warning" className="d-flex justify-content-center"> You has been leave this group!</Alert>
            </>
        )
    }

    return (
        <>
            <Drawer title={<p style={{color: textColor}}>Settings</p>} onClose={onClose} open={open} drawerStyle={{
                backgroundColor: borderColor, // Thay đổi màu nền
                color: '#333', // Màu chữ
            }}>
                {ContentDrawer}
            </Drawer>


            {currentSelected && (
                <Layout style={{
                    height: "100%",

                    background: "transparent",
                }}>
                    <Header className="content-header"
                            style={{
                                background: "transparent",
                                display: "flex", flexDirection: "row", alignItems: "center",justifyContent: "space-between",
                                paddingTop: 20}}
                    >

                        <div style={{display: "flex", flexDirection: "row"}}>
                            <Avatar src={currentSelected.avatar} size={50}/>

                            <Card style={{top: -10,height: "100%", padding: "0 !important", background: "transparent", border: "none"}}>
                                <Card.Meta
                                    title={
                                        <h4 style={{color: textColor, margin: 0, padding: "2px 0"}}>
                                            {currentSelected.name}
                                        </h4>}
                                    description={<i style={{color: textColor}}>{`${currentSelected.members?.length??0} members`}</i>}/>
                            </Card>
                        </div>
                        <MoreOutlined onClick={showDrawer} className="btn-more" style={{ color: textColor,fontSize: 30 }} />
                    </Header>
                    <Content
                        ref={listMessRef}
                        onScroll={handleScroll}
                        style={{padding: 30,height: "68vh", overflowY: "auto", borderRight:`1px solid ${borderColor}`}}>
                        {currentSelected && (
                            <>
                                {dataMess.map((item, index)=>{
                                    console.log(item)
                                    var isSystem = (item.system === true || item.isSystem === true)
                                        if (item.contentType === "text") {
                                            return (
                                                <div
                                                    key={item.id}
                                                    className={ `message 
                                                        ${( isSystem ? "message-server" : (item.sender === myId  ? "message-me" : "message-friend") ) } 
                                                        ${(page > 1 &&  index % (15) === 0 ) ? "message-border" : ""} `
                                                    }
                                                >
                                                    <div style={{display: "flex", flexDirection: "row", alignItems: "center"}}>
                                                        {!isSystem && (
                                                            <Tooltip title={item.senderName} >
                                                                <Avatar style={{width: 25, height: 25, visibility: item.sender === myId? "hidden" : "visible"}} src={item.avatar} />
                                                            </Tooltip>
                                                        )}
                                                        <Tooltip  title={item.createdAt && convertToHCMTime(item.createdAt)}>
                                                            <p style={{color: isSystem ? (item.content.includes("deleted") ?  "red" : hintColor) : textColor}}>{item.content}</p>
                                                        </Tooltip>
                                                    </div>
                                                </div>

                                            );
                                        }
                                        else if (item.contentType === "image") {
                                            var link = `${SERVER}/file?token=${token}&group=${currentSelected.id}&id=${item.id}`
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
                                                        margin: "5px 0 "
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
                                            var fileLink = `${SERVER}/file?token=${token}&group=${currentSelected.id}&id=${item.id}`;
                                            return (
                                                <div
                                                    key={item.id}
                                                    className={`file message ${item.sender === myId ? "message-me" : "message-friend"}`}
                                                    style={{marginTop: 10 ,display: "flex", flexDirection: "row",  alignItems: "flex-end"}}
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
                                                                color: textColor,
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
                                            key={item + "wait"}
                                            className="message-wait message message-me"
                                        >
                                            <p key={item + "mess"}>{item}</p>
                                        </div>)
                                })}


                                {dataMess && dataMess[dataMess.length - 1]?.sender === myId && listWaitSend.length < 1 &&
                                    <div style={{display: "flex", justifyContent: "end"}}>

                                        <Tooltip
                                             title={`${dataMess.at(dataMess?.length - 1).whoSeen?.length ?? "0"} seen`}>
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
                    {!canSend && (<>
                            <Alert variant="warning" title="Only leader and deuty can send message"/>
                    </>)}
                    {canSend && (
                        <Footer style={{height: "7%", width: "100%", background: "transparent", display: "flex", alignItems: "center"}}>

                            {/*<Popover content={contentAddFile} trigger="hover">*/}
                            {/*</Popover>*/}
                            <Button  onClick={() => openModal.File()} className="btn-addFile" style={{color: "grey"}} icon={<PlusOutlined/>}/>

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

                    )}
                </Layout>
            )}
        </>
    )
}
export default ContentCenter