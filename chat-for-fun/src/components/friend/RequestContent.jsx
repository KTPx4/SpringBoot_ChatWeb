import Sider from "antd/es/layout/Sider";
import SliderHead from "./SliderHead";
import ListFriend from "./ListFriend";
import {Avatar, Button, message} from "antd";
import {UserAddOutlined, UserDeleteOutlined,
    AliwangwangOutlined,
    CloseCircleOutlined,
    RedoOutlined,
    CheckCircleOutlined,InfoCircleOutlined,
} from "@ant-design/icons";
import React, {useContext, useEffect, useState} from "react";
import {ThemeContext} from "../../ThemeContext";
import {Alert} from "react-bootstrap";
import axios from "axios";
import Spinner from "react-bootstrap/Spinner";
import useStore from "../../store/useStore";


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

const RequestContent = ({userId, openModal, messageApi})=>{
    const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';
    const [token,setToken]= useState(localStorage.getItem('token-auth'));
    const {id, setId} = useStore()
    const [myId, setMyId] = useState(id||"px4");
    //theme
    const { currentTheme } = useContext(ThemeContext);
    const themeName = currentTheme.getKey();
    const contentColor = currentTheme.getContent()
    const textColor = currentTheme.getText();
    const sliderColor = currentTheme.getKey().split("_")[1];
    const borderColor = currentTheme.getBorder()
    const cardColor = currentTheme.getCard();

    // notify
    const [loading, setIsLoading] = useState(false);

    const [countFriend, setCountFriend] = useState(0);

    const [currentUser, setCurrentUser] = useState(null)
    const [updateUser, setUpdateUser] = useState(null)

    const [stateFriend, setStateFriend] = useState(null);
    const [stateStatus, setStateStatus] = useState(currentUser?.status.toLowerCase());

    const [searchValue, setSearchValue] = useState("")



    // Update button state when currentUser changes
    useEffect(() => {
        if (currentUser) {
            setStateStatus(currentUser.status.toLowerCase());
            setStateFriend(getButton(currentUser.friend, currentUser.type, currentUser.status));
        }
    }, [currentUser]);




    const getButton = (friend, type, status) =>{
        var btn =null
        if(friend === true)
        {
            btn = (
                <Button
                    onClick={handleActionFriend}
                    icon={<UserDeleteOutlined/>}
                    style={{color: textColor, width: 120, background: cardColor}}>
                    Unfriend
                </Button>
            )

        }
        else if(friend === false && status.toLowerCase() !== STATUS_FRIEND.blockedBy)
        {
            switch (type.toLowerCase())
            {
                case TYPE_FRIEND.none:
                    btn = (
                        <Button
                            onClick={handleActionFriend}

                            icon={<UserAddOutlined/>}
                            style={{color: textColor, width: 120, background: cardColor}}>
                            Make Friend
                        </Button>
                    )
                    break

                case TYPE_FRIEND.accept:
                    btn = (
                        <Button
                            onClick={handleActionFriend}
                            icon={<CheckCircleOutlined/>}
                            style={{color: textColor, width: 120, background: cardColor}}>
                            Accept
                        </Button>
                    )
                    break

                case TYPE_FRIEND.waiting:
                    btn = (
                        <Button
                            onClick={handleActionFriend}
                            icon={<InfoCircleOutlined />}
                            style={{color: textColor, width: 130, background: cardColor}}>
                            Cancel Request
                        </Button>
                    )
                    break
            }
        }
        else{

        }
        return btn
    }



    const clickUser = (user)=>{
        setCurrentUser(user)
        window.history.pushState({}, null, `/friend/${user.id}`)
    }


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
            setIsLoading(false)

            return res

        } catch (err) {
            console.log(err?.response)
            setIsLoading(false)
            alert(err?.response.data.message ?? "Failed to connect Server")
            return err?.response
        }
    }


    const handleAfterLoad = (count)=>{
        setCountFriend(count);
    }

    const handleActionFriend = async () =>{
        if(loading) return

        if(currentUser)
        {
            setIsLoading(true);
            const action = `friend/${currentUser.id}`
            const method = "post"
            const response = await sendServer(action, method, null)
            if(response?.status && response.status === 200)
            {
                const data = response.data.data
                setUpdateUser(data)
                var btn = getButton(data.friend, data.type, data.status)

                setStateFriend(btn)

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
    }

    const handleActionStatus = async()=>{
        if(loading) return
        if(currentUser)
        {
            setIsLoading(true);
            const action = `friend/status/${currentUser.id}`
            const method = "post"
            const response = await sendServer(action, method, null)
            if(response?.status && response.status === 200)
            {
                const data = response.data.data
                setUpdateUser(data)
                setStateStatus(data.status.toLowerCase());

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
    }

    const handleChat = () =>{
        if(currentUser)
        {
            openModal(currentUser.id)
        }
    }

    const handleSearch = (searchName) =>{
        setSearchValue(searchName)
    }

    const handleAccept = async(item) =>{
        if(item && item.id)
        {
            if(loading) return

            setIsLoading(true);
            const action = `friend/${item.id}`
            const method = "post"
            const response = await sendServer(action, method, null)
            if(response?.status && response.status === 200)
            {
                setIsLoading(false)
                const data = response.data.data

                setUpdateUser(data)

                setCurrentUser(null)

                setCountFriend(countFriend-1)

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
    }

    const handleReject = async(item) =>{
        if(item && item.id)
        {
            if(loading) return

            setIsLoading(true);
            const action = `friend/unfriend/${item.id}`
            const method = "post"
            const response = await sendServer(action, method, null)
            if(response?.status && response.status === 200)
            {
                setIsLoading(false)
                const data = response.data.data

                setUpdateUser(data)

                setCurrentUser(null)

                setCountFriend(countFriend-1)
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
    }

    return(
        <>

            <Sider
                theme={sliderColor}
                trigger={null}
                width={"30%"}

                style={{ maxWidth: 350,height: "100%", background: "transparent !important" }}
            >
                <SliderHead count={countFriend} searchName={setSearchValue}/>
                <ListFriend TYPE={"response"} searchName={searchValue}
                            currentUser={currentUser}
                            clickUser={clickUser}
                            afterLoad={handleAfterLoad}
                            handleAccept={handleAccept}
                            handleReject={handleReject}
                            updateUser={updateUser}/>

            </Sider>
            {currentUser && (
                <div className="content"
                     style={{
                         height: "100%",
                         width: "70%",
                         marginLeft: 7,
                         marginTop: 5,
                         background: `${contentColor}`,
                         display: "flex",
                         flexDirection: "column",
                         overflowY: "auto",
                         alignItems: "center",
                         padding: "30px",
                         border: `1px solid ${borderColor}`,
                         borderRadius: 20
                     }}
                >
                    {stateStatus === "blockedby" && <Alert key="alert-warning" variant="warning">You has been blocked by this user</Alert>}
                    <Avatar
                        size={90}
                        src={currentUser?.avatar ?? "https://api.dicebear.com/9.x/fun-emoji/svg?seed=Sarah"}
                        className="mb-2"
                    />

                    {loading && <Spinner variant="info" />}

                    <h4 style={{color: textColor}}>{currentUser?.name}</h4>
                    <i style={{color: textColor}}>{currentUser?.id}</i>

                    <div
                        className="mt-2"
                        style={{
                            display: "flex",
                            flexDirection: "row",
                        }}>

                        {/*Button add/un/accep friend*/}
                        {stateFriend}

                        <div className="mx-1"/>

                        {stateStatus !== STATUS_FRIEND.blockedBy && (
                            <Button
                                onClick={handleActionStatus}
                                icon={stateStatus === STATUS_FRIEND.normal ? <CloseCircleOutlined /> : <RedoOutlined />}
                                style={{color: textColor, width: 90, background: cardColor}}> {stateStatus === STATUS_FRIEND.normal ? "Block" : "Unblock"}
                            </Button>)
                        }

                        <div className="mx-1"/>

                        <Button
                            onClick={handleChat}
                            icon={<AliwangwangOutlined/>}
                            style={{color: textColor, width: 90, background: cardColor}}>Fast Chat
                        </Button>
                    </div>
                </div>
            )}
        </>
    )
}
export default RequestContent;
