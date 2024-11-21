import React, {useContext, useEffect, useState} from "react";
import {Helmet, HelmetProvider} from "react-helmet-async";
import ContentLeft from "./left/ContentLeft";
import ContentRight from "./right/ContentRight";
import ContentCenter from "./center/ContentCenter";
import {ThemeContext} from "../../ThemeContext";
import axios, {get} from "axios";
import useStore from "../../store/useStore";
import {Input, message, Modal, Upload} from "antd";
import { InboxOutlined } from '@ant-design/icons';
import data from "bootstrap/js/src/dom/data";
const { Dragger } = Upload;

const dataTest = [
    {
        id: "1234",
        avatar: "https://api.dicebear.com/9.x/fun-emoji/svg?seed=Aidan",
        name: "Group 01",
        messages:[
            {
                id: "0123a",
                content: "messages test",
                sender: "000002",
                to: "1234",
                contentType: "text",
                whoSeen:[ // added
                    "12345", "6789", "123456"
                ],
            },{
                id: "0123s",
                content: "messages test",
                sender: "000002",
                to: "1234",
                contentType: "text",
                whoSeen:[ // added
                    "12345", "6789", "123456"
                ],
            },{
                id: "0123d",
                content: "from me",
                sender: "Sh5WMqlBIW",
                to: "1234",
                contentType: "text",
                whoSeen:[ // added
                    "12345", "6789", "123456"
                ],
            },{
                id: "0123f",
                content: "messages test",
                sender: "000002",
                to: "1234",
                contentType: "text",
                whoSeen:[ // added
                    "12345", "6789", "123456"
                ],
            },{
                id: "0123g",
                content: "messages test",
                sender: "000002",
                to: "1234",
                contentType: "text",
                whoSeen:[ // added
                    "12345", "6789", "123456"
                ],
            },{
                id: "0123h",
                content: "messages test",
                sender: "000002",
                to: "1234aa",
                contentType: "text",
                whoSeen:[ // added
                    "12345", "6789", "123456"
                ],
            },{
                id: "0123ss",
                content: "messages test",
                sender: "000002",
                to: "1234",
                contentType: "text",
                whoSeen:[ // added
                    "12345", "6789", "123456"
                ],
            },{
                id: "ddd",
                content: "messages test",
                sender: "000002",
                to: "1234",
                contentType: "text",
                whoSeen:[ // added
                    "12345", "6789", "123456"
                ],
            },
        ],
        count: 2,
        members:[
            {
                id: "12345ddd",
                name: "Px4",
                avatar: "https://api.dicebear.com/9.x/fun-emoji/svg?seed=Aidan"
            },
            {
                id: "12345",
                name: "Px4",
                avatar: "https://api.dicebear.com/9.x/fun-emoji/svg?seed=Aidan"
            },
        ],
        selected: false
    },
    {
        id: "12345",
        avatar: "https://api.dicebear.com/9.x/fun-emoji/svg?seed=Aidan",
        name: "Group 02",
        messages:[
            {
                id: "0123hjh",
                content: "messages test 2",
                sender: "000002",
                to: "1234",
                contentType: "text",
                whoSeen:[ // added
                    "12345", "6789", "123456"
                ],
            },
        ],
        count: 3,
        members:[
            {
                id: "12345",
                name: "Px4",
                avatar: "https://api.dicebear.com/9.x/fun-emoji/svg?seed=Aidan"
            },
            {
                id: "12345",
                name: "Px4",
                avatar: "https://api.dicebear.com/9.x/fun-emoji/svg?seed=Aidan"
            },
            {
                id: "12345",
                name: "Px4",
                avatar: "https://api.dicebear.com/9.x/fun-emoji/svg?seed=Aidan"
            },
        ],
        selected: false
    },

]

const GroupConponent = ({ socketHandler }) =>{
    const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';
    const token = localStorage.getItem('token-auth') || '';
    const {id, setId} = useStore()
    const [myId, setMyId] = useState(id||"px4");
    const [messageApi, contextHolder2] = message.useMessage();

    const { currentTheme } = useContext(ThemeContext);

    const themeName = currentTheme.getKey();
    const contentColor = currentTheme.getContent()
    const textColor = currentTheme.getText();
    const hintColor = currentTheme.getHint();
    const sliderColor = currentTheme.getKey().split("_")[1];
    const borderColor = currentTheme.getBorder()
    const cardSelectedColor = currentTheme.cardSelected
    const [searchValue, setSearchValue] = useState("")
    const [updateGroup, setUpdateGroup] = useState(null);
    const [getSeen, setSeen] = useState(null);
    const [sendSeen, setSendSeen] = useState(false)
    const [dataGroup, setDataGroup] = useState([]);
    const [currentSelected, setCurrentSelected] = useState(null);
    const [newMessage, setNewMessage] = useState(null)
    const [dataFriend, setDataFriend] = useState([]);
    const [isModalName, setIsModalName] = useState(false);
    const [isModalUpload, setIsModalUpload] =useState(false);
    const [inputNewName, setInputNewName] = useState("")
    const beforeUpload = (file) => {
        const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
        if (!isJpgOrPng) {
            message.error('You can only upload JPG/PNG file!');
        }
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isLt2M) {
            message.error('Image must smaller than 2MB!');
        }
        return isJpgOrPng && isLt2M;
    };
    const props = {
        name: 'file',
        multiple: false,
        action:  `${SERVER}/upload?token=${token}&group=${currentSelected?.id ?? "-1"}`,
        beforeUpload: (file) =>{
            const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
            if (!isJpgOrPng) {
                message.error('You can only upload JPG/PNG file!');
            }
            const isLt2M = file.size / 1024 / 1024 < 2;
            if (!isLt2M) {
                message.error('Image must smaller than 2MB!');
            }
            return isJpgOrPng && isLt2M;
        },
        onChange(info) {
            const { status } = info.file;
            if (status !== 'uploading') {
            }
            if (status === 'done') {
                message.success(`${info.file.name} file uploaded successfully.`);
                const newLink = info.file.response + `?t=${Date.now()}`
                var curr = {...currentSelected, avatar: newLink}
                console.log(curr)
                setCurrentSelected(curr);
                var dt = dataGroup.map((gr)=> gr.id === curr.id ? curr : gr)
                console.log(dt)
                console.log(dataGroup)
                setDataGroup(dt)

            } else if (status === 'error') {
                message.error(`${info.file.name} file upload failed.`);
            }
        },

        onDrop(e) {
            console.log('Dropped files', e.dataTransfer.files);
        },
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

    useEffect(() => {
        loadListFriend()
        loadListGroup()

        socketHandler.setOnMessageReceived((newMess)=>{
            // send - to

            setSendSeen(true)

            setNewMessage(newMess)

        })
        socketHandler.setOnGetSeen((msg)=>{
            setSeen(msg)
        })
        socketHandler.setOnUpdateGroup((updateGroup)=>{
            console.log(updateGroup)
            setUpdateGroup(updateGroup)
        })

    }, []);


    useEffect(() => {
        if(newMessage )
        {
            // Sử dụng hàm cập nhật để đảm bảo giá trị mới nhất của `dataGroup`
            setDataGroup((prevDataGroup) => {
                var updatedDataGroup = prevDataGroup.map((gr) => {
                        if(gr.id === newMessage.to)
                        {
                            var rs = { ...gr, count: (newMessage.sender !== myId && newMessage.to !== currentSelected?.id) ? (gr.count || 0) + 1 : 0}
                            rs.messages.push(newMessage)
                            return rs
                        }
                        return gr
                    }
                );
                return updatedDataGroup; // Cập nhật state
            });

            if( currentSelected && newMessage.to === currentSelected?.id)
            {
                // handleSendSeen(currentSelected.id)
            }
        }



    }, [newMessage ]);
    useEffect(() => {
        if(sendSeen === true && currentSelected)
        {
            setSendSeen(false)
            handleSendSeen()
        }
    }, [sendSeen]);

    useEffect(() => {
        if(getSeen)
        {
            var to = getSeen.to
            var sender = getSeen.sender
            if (currentSelected && currentSelected.id === to && sender !== myId) {
                const curr = { ...currentSelected }; // Tạo bản sao để tránh sửa trực tiếp
                const lastMessage = curr.messages.at(curr.messages.length - 1);

                if (lastMessage && !lastMessage.whoSeen.includes(sender)) {
                    // Chỉ push nếu `sender` chưa có trong `whoSeen`
                    lastMessage.whoSeen.push(sender);
                }

                setCurrentSelected(curr);
                setDataGroup((prev) =>
                    prev.id === curr.id ? curr : prev
                );
            }

        }
    }, [getSeen])

    useEffect(()=>{
        if(currentSelected)
        {
            setInputNewName(currentSelected.name)
        }
    }, [currentSelected])

    useEffect(() => {
        if(updateGroup)
        {
            var updateGr = updateGroup
            setCurrentSelected((prev)=> (
                {...prev,
                    name: updateGr.name,
                    allPermit: updateGr.allPermit,
                    avatar: updateGr.avatar,
                    canSend: updateGr.canSend,
                    deputy: updateGr.deputy,
                    leaderId: updateGr.leaderId,
                    members: updateGr.members,

                }
            ))

            var newDt = dataGroup.map((group) =>
                group.id === updateGr.id ?
                    {...group,
                        name: updateGr.name,
                        allPermit: updateGr.allPermit,
                        avatar: updateGr.avatar,
                        canSend: updateGr.canSend,
                        deputy: updateGr.deputy,
                        leaderId: updateGr.leaderId,
                        members: updateGr.members,
                    }
                    : group)
            setDataGroup(newDt)
            //message.success("Change name success")
            setIsModalName(false)
        }

    }, [updateGroup]);

    useEffect(()=>{
        if(getSeen)
        {
            var to = getSeen.to
            var sender = getSeen.sender
            if (currentSelected && currentSelected.id === to && sender !== myId) {
                const curr = { ...currentSelected }; // Tạo bản sao để tránh sửa trực tiếp
                const lastMessage = curr.messages.at(curr.messages.length - 1);

                if (lastMessage && !lastMessage.whoSeen.includes(sender)) {
                    // Chỉ push nếu `sender` chưa có trong `whoSeen`
                    lastMessage.whoSeen.push(sender);
                }

                setCurrentSelected(curr);
                setDataGroup((prev) =>
                    prev.id === curr.id ? curr : prev
                );
            }

        }
    }, [getSeen])

    const loadListFriend = async() =>
    {
        const action = "friend"
        const method = "get"
        const data = null
        var res = await sendServer(action, method, data)
        if(res.status && res.status === 200)
        {
            var list = res.data.data.friends;
            setDataFriend(list);
        }
        else{
            messageApi.open({
                type: "error", // success, error , warning
                content: res?.data?.message ?? "Error when load list friend from server!",
                className: 'custom-class',
                style: {
                    marginTop: '10vh',
                },
            });
        }
    }


    const loadListGroup = async() =>{
        const action = "chat/group"
        const method = "get"
        const data = null
        var res = await sendServer(action, method, data)
        if(res.status && res.status === 200)
        {
            var list = res.data.data.groups ?? [];
            console.log(list)
            setDataGroup(list);
        }
        else{
            messageApi.open({
                type: "error", // success, error , warning
                content: res?.data?.message ?? "Error when load list friend from server!",
                className: 'custom-class',
                style: {
                    marginTop: '10vh',
                },
            });
        }
    }

    const handleClickGroup = (group) =>{


        var data = dataGroup.map(item => item.id === group.id ? {...group, count: 0, selected: true} : {...item, selected: false});

        setDataGroup(data)

        var newGr = {...group}

        setCurrentSelected(newGr)

        // handleSendSeen(group.id)
        handleSendSeen()

    }

    const handleSendSeen = () =>{
     if(socketHandler && currentSelected)
     {
         socketHandler.sendSeen(currentSelected.id)
     }
    }

    const handleCreateGroup = async (createData , callBack) =>{
        const action = "group"
        const method = "post"

        const data ={
            name: createData.name,
            users: [...createData.users, myId],
        }
        var res = await  sendServer(action, method, data)
        console.log(res)

        if(res.status && res.status === 200)
        {
            var newGr = res.data.data
                console.log(newGr)

            newGr.selected = true

            const updatedDataGroup = [
                newGr, // Nhóm mới ở đầu danh sách
                ...dataGroup.map((group) => ({
                    ...group, // Sao chép từng nhóm cũ
                    selected: false, // Gán selected = false
                })),
            ];
            setDataGroup(updatedDataGroup)

            // setDataGroup(newGr)
            callBack("success", "")
            setCurrentSelected(newGr)

        }
        else{
            callBack("error", res.data?.message ?? "Failed to connect Server")
        }
    }

    const sendMessage = async(mess, calback) =>{
        // sendMessage(toUser, content, contentType = 'text', replyMessageId ='')
        if(currentSelected)
        {
            socketHandler.sendMessage(currentSelected.id, mess)
        }
    }

    const handleScroll = async(id, page) =>{
        await loadMess(id, page)
    }
    const handlePermis = async(id, permis) =>{
        const action = `group/${currentSelected.id}?permis=${permis}`
        const method = "put"
        const data = {}
        const datas = {
            id: currentSelected.id,
            permis: `${permis}`
        }
        socketHandler.sendUpdateGroup(datas)
        var res = await sendServer(action, method, data)

        if(res.status && res.status === 200)
        {
            var dt = res.data.data
            var newDt = dataGroup.map((group) => group.id === dt.id ? {...group, allPermit: dt.allPermit} : group)
            setDataGroup(newDt)
            setCurrentSelected((prev) =>({...prev, allPermit: dt.allPermit }))
        }
    }

    const loadMess = async(id, page)=>{
        const url = `${SERVER}/chat/group/${id}?page=${page}`;

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
                    setDataGroup((prevGroups) =>
                        {
                            return prevGroups.map((group) =>
                                group.id === id
                                    ? { ...group, messages: [...data, ...group.messages] }
                                    : group
                            )
                        }
                    );

                    var curr = {...currentSelected}
                    curr.messages = [...data, ...curr.messages]
                    console.log(curr)
                    setCurrentSelected(curr)
                }
            }

        } catch (err) {
            alert("Failed to connect Server")
        } finally {
        }
    }

    const okModalName = async ()=>{
        if(!inputNewName)
        {
            message.error('New name must not null');
            return
        }

        if(currentSelected && socketHandler)
        {
            const action =`group/${currentSelected.id}`
            const method = "put"
            const datas = {
                id: currentSelected.id,
                name: inputNewName
            }
            socketHandler.sendUpdateGroup(datas)
            // var res = await sendServer(action, method, datas)

            // if(res.status && res.status === 200)
            // {
            //     var updateGr = res.data.data
            //     setCurrentSelected((prev)=> ({...prev, name: updateGr.name}))
            //     var newDt = dataGroup.map((group) => group.id === updateGr.id ? {...group, name: updateGr.name} : group)
            //     setDataGroup(newDt)
            //     message.success("Change name success")
            //     setIsModalName(false)
            // }
            // else{
            //     message.error(res.data?.message ?? "Failed to connect Server")
            // }

        }
    }
    const okModalUpload =()=>{

    }

    return(
        <HelmetProvider>
            <Helmet>
                <link href="/css/group/group.css" rel="stylesheet" />
            </Helmet>
            {contextHolder2}

            <Modal
                    className={`modal-${themeName === "theme_dark" ? "dark" : "light"}`}
                    title={<p style={{color: textColor}}>Change name</p>}
                   open={isModalName} onOk={okModalName} onCancel={()=> setIsModalName(false)}>
                <Input placeholder={"Name"} value={inputNewName}
                       onKeyDown={(e)=>{
                           if(e.key === "Enter"){
                               okModalName()
                           }
                       }}
                       onChange={(e)=> {
                    setInputNewName(e.target.value)
                }}/>
            </Modal>
            <Modal
                    className={`modal-${themeName === "theme_dark" ? "dark" : "light"}`}
                    onCancel={()=>setIsModalUpload(false)}
                   open={isModalUpload} footer={null} >
                <Dragger className="p-2" {...props}>
                    <p className="ant-upload-drag-icon">
                        <InboxOutlined />
                    </p>
                    <p style={{color: textColor}}  className="ant-upload-text">Click or drag file to this area to upload</p>
                    <p style={{color: hintColor}} className="ant-upload-hint">
                        Select an image less than 2mb
                    </p>
                </Dragger>
            </Modal>

            <div className="container-fluid"
                 style={{display:"flex", flexDirection:"row",minHeight:720, minWidth: 750, overflow: "auto", padding: "5px 0"}}>

                <div style={{ width: "26%", height: "100%", background: "transparent"}}>
                    {/*content left*/}
                    <ContentLeft  createAction={handleCreateGroup} messageApi={messageApi} dataFriend={dataFriend} dataSet={dataGroup} clickGroup={handleClickGroup} />
                </div>

                <div style={{ width: "74%", height: "100%", background: "transparent",
                    borderLeft: `1px solid ${borderColor}`,
                    borderTop:  `1px solid ${borderColor}`,
                    borderBottom: `1px solid ${borderColor}`,
                    borderStartStartRadius: 25,
                    borderEndStartRadius: 25,
                    overflow: "hidden"

                }}>
                    {/*content center*/}
                    <ContentCenter
                                    openModal={{Name: ()=> setIsModalName(true), Upload: ()=>setIsModalUpload((true))}}
                                    Scroll={handleScroll}
                                    messageApi={messageApi}
                                    currentSelected={currentSelected}
                                    sendMess={sendMessage}
                                    newMessage={newMessage}
                                    sendPermis={handlePermis}/>
                </div>

                {/*<div style={{ visibility: "hidden" ,width: "20%", height: "100%", padding: 25, background: "transparent",*/}
                {/*            borderEndEndRadius: 25 ,*/}
                {/*            borderStartEndRadius: 25,*/}
                {/*            borderTop:  `1px solid ${borderColor}`,*/}
                {/*            borderBottom:  `1px solid ${borderColor}`,*/}
                {/*            borderRight:`1px solid ${borderColor}`}}>*/}
                {/*    /!*content right*!/*/}
                {/*    <ContentRight />*/}
                {/*</div>*/}

            </div>
        </HelmetProvider>
    )
}
export default GroupConponent;