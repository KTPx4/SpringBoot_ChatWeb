import React, {useContext, useEffect, useState} from "react";
import {Helmet, HelmetProvider} from "react-helmet-async";
import ContentLeft from "./left/ContentLeft";
import ContentRight from "./right/ContentRight";
import ContentCenter from "./center/ContentCenter";
import {ThemeContext} from "../../ThemeContext";
import axios from "axios";
import useStore from "../../store/useStore";
import {message} from "antd";
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
    const sliderColor = currentTheme.getKey().split("_")[1];
    const borderColor = currentTheme.getBorder()
    const cardSelectedColor = currentTheme.cardSelected
    const [searchValue, setSearchValue] = useState("")

    const [dataGroup, setDataGroup] = useState([]);
    const [currentSelected, setCurrentSelected] = useState(null);
    const [newMessage, setNewMessage] = useState(null)
    const [dataFriend, setDataFriend] = useState([]);

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
            if(currentSelected && newMess.to === currentSelected.id)
            {
                socketHandler.sendSeen(currentSelected.id)
            }

            setNewMessage(newMess)

            // Sử dụng hàm cập nhật để đảm bảo giá trị mới nhất của `dataGroup`
            setDataGroup((prevDataGroup) => {
                var updatedDataGroup = prevDataGroup.map((gr) => {
                        if(gr.id === newMess.to)
                        {
                            var rs = {
                                ...gr,
                                count: currentSelected?.id !== gr.id ? (gr.count || 0) + 1 : 0,
                            }
                            rs.messages.push(newMess)
                            return rs
                        }
                        return gr
                    }
                );

                console.log(updatedDataGroup); // Log giá trị mới nhất
                return updatedDataGroup; // Cập nhật state
            });


        })

    }, []);

    const loadListFriend = async() =>
    {
        const action = "friend"
        const method = "get"
        const data = null
        var res = await sendServer(action, method, data)
        if(res.status && res.status === 200)
        {
            var list = res.data.data.friends;
            console.log(list)
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

        socketHandler.sendSeen(group.id)

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

    return(
        <HelmetProvider>
            <Helmet>
                <link href="/css/group/group.css" rel="stylesheet" />
            </Helmet>
            {contextHolder2}
            <div className="container-fluid"
                 style={{display:"flex", flexDirection:"row",minHeight:750, minWidth: 750, overflow: "auto", padding: "5px 0"}}>

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
                    <ContentCenter messageApi={messageApi} currentSelected={currentSelected} sendMess={sendMessage} newMessage={newMessage}/>
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