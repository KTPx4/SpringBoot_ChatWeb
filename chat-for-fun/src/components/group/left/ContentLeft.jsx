import {Avatar, Badge, Button, Card, Input, List, Modal, Tooltip} from "antd";
import {
    SearchOutlined,
    PlusOutlined
} from "@ant-design/icons";
import React, {useContext, useEffect, useState} from "react";
import {ThemeContext} from "../../../ThemeContext";
import useStore from "../../../store/useStore";

const ContentLeft = ({dataSet, dataFriend, clickGroup, messageApi, createAction})=>{
    const {id, setId} = useStore()
    const [myId, setMyId] = useState(id||"px4");

    const { currentTheme } = useContext(ThemeContext);
    const key = currentTheme.getKey();
    const background = currentTheme.getBackground();
    const contentColor = currentTheme.getContent()
    const cardColor = currentTheme.getCard();
    const textColor = currentTheme.getText();
    const textStyle = {color: textColor}
    const sliderColor = currentTheme.getKey().split("_")[1];
    const colorName = sliderColor === "light" ? "#f8f9fa    " : "black";
    const selectedCard = currentTheme.getCardSelected();
    const borderColor = currentTheme.getBorder()

    const [addData, setAddData] = useState([])
    const [nameNewGr, setNameNewGr] = useState("")

    const [isModalOpen, setIsModalOpen] = useState(false);


    const showModal = () => {
        setIsModalOpen(true);
    };

    const handleOk = () => {
        if(addData.length < 1)
        {
            messageApi.open({
                type: "error", // success, error , warning
                content: "List user has null",
                className: 'custom-class',
                style: {
                    marginTop: '10vh',
                },
            });
            return
        }
        else if(!nameNewGr)
        {
            messageApi.open({
                type: "error", // success, error , warning
                content: "Please input name group",
                className: 'custom-class',
                style: {
                    marginTop: '10vh',
                },
            });
            return
        }

        var newGr ={
            name: nameNewGr,
            users: addData.map(item=>item.id)
        }
        createAction(newGr, (response, mess) => {
            if(response === "success")
            {
                setIsModalOpen(false);
                messageApi.open({
                    type: "success", // success, error , warning
                    content: "Create group success",
                    className: 'custom-class',
                    style: {
                        marginTop: '10vh',
                    },
                });
            }
            else{
                messageApi.open({
                    type: "error", // success, error , warning
                    content: mess ??"Create group failed!",
                    className: 'custom-class',
                    style: {
                        marginTop: '10vh',
                    },
                });
            }
        })

    };

    const handleCancel = () => {
        setIsModalOpen(false);
    };

    const handleClickGroup = (group)=>{
        clickGroup(group)
    }



    return(
        <>
            <Modal
                className={`modal-${key === "theme_dark" ? "dark":"light"}`}
                title={<p style={{color:textColor}}>Create group</p>} open={isModalOpen} onOk={handleOk} onCancel={handleCancel}>
                <div style={{display: "flex",  flexDirection: "row", overflow: "auto", width: "100%"}}>
                    <div>
                        <Input
                            key={"input-add"}
                            placeholder={"Search name..."}
                            suffix={<SearchOutlined />}
                            style={{width: "90%", margin: "10px 10px", borderRadius: 50,}}
                        />
                        <div style={{margin: "0 10px", overflowY: "auto", maxHeight: 400}}>
                            {dataFriend.map((item, index) => {
                                return(
                                    <>
                                        <Tooltip id={item.id} title={item.name} >
                                            <Card style={{width: "200px", marginTop: "10px", background: cardColor, border: `1px solid ${borderColor}`}}>
                                                <Card.Meta
                                                    avatar={<Avatar size={40} src={item.avatar} />}
                                                    title={<p style={{color: textColor}}>{item.name}</p>}
                                                    description={<p style={{color: textColor}}>{item.id}</p>}
                                                />
                                                <div style={{marginTop: 8,display: "flex", justifyContent: "center"}}>
                                                    <Button onClick={()=>{
                                                        var add = addData.filter((it) => it.id === item.id)
                                                        if(add.length > 0 ) return;
                                                        setAddData([...addData, item])
                                                    }}>+</Button>
                                                </div>
                                            </Card>
                                        </Tooltip>
                                    </>
                                )
                            })}
                        </div>
                    </div>
                    <div style={{width: "50%"}}>
                        <Input
                                onChange={(e)=> {setNameNewGr(e.target.value)}}
                                className="mt-2 "
                               style={{width: "100%"}} placeholder={"Name group"}/>
                        <b className={" d-flex justify-content-center"}>Count: {addData.length}</b>
                        <div style={{margin: "0 10px", overflowX: "hidden",overflowY: "auto", maxHeight: 400}}>
                            {addData.map((item, index) => {
                                return (
                                    <>
                                        <Tooltip id={item.id + "add"} title={item.name}>
                                            <Card style={{width: "200px", marginTop: "10px"}}>
                                                <Card.Meta
                                                    avatar={<Avatar size={40} src={item.avatar}/>}
                                                    title={item.name}
                                                    description={item.id}
                                                />
                                                <div style={{marginTop: 8, display: "flex", justifyContent: "center"}}>
                                                    <Button onClick={() => {
                                                        var newData = addData.filter(it => it.id !== item.id)
                                                        setAddData(newData)
                                                    }}>-</Button>
                                                </div>
                                            </Card>
                                        </Tooltip>
                                    </>
                                )
                            })}
                        </div>
                    </div>
                </div>
            </Modal>

            <div style={{width: '100%', height: '100%', display: 'flex', flexDirection: "column"}}>
                <div className="mt-3"></div>
                <div className="header d-flex" style={{flexDirection: "row", padding: "20px 10px"}}>
                    <Input
                        key={"input-search"}
                        placeholder={"Search name..."}
                        suffix={<SearchOutlined/>}
                        style={{margin: "0 10px", borderRadius: 50,}}
                    />
                    <Button onClick={showModal} title={"New group"} icon={<PlusOutlined/>}
                            style={{width: 40, borderRadius: 50}}/>
                </div>
                <div
                    style={{maxHeight: "90vh"}}
                    className="list-group"
                >
                    <List
                        className="gr-card"
                        style={{
                            overflowY: "auto",
                            padding: 10
                        }}
                        split={false}
                        itemLayout="vertical"
                        dataSource={dataSet}

                        renderItem={(item, index) => (
                            <>

                                <Card
                                    key={item.id}
                                    onClick={() => handleClickGroup(item)}
                                    style={{color: textColor, background:  item.selected ? selectedCard : cardColor, border: `1px solid ${borderColor}`}}>
                                        <Card.Meta
                                            avatar={
                                                <Badge showZero={false}  count={item.count ?? 0}>
                                                    <Avatar src={item.avatar}
                                                                    style={{
                                                                        height: 50, width: 50,
                                                                        background: "lightgrey"
                                                                    }}
                                                    />
                                                </Badge>
                                            }
                                            title={<span style={{color:textColor}}>{item.name}</span>}
                                            description={
                                                <span
                                                    style={{color: textColor, fontWeight: ((item.count ?? 0) > 0 ? "bold" : "normal")}}>{item?.messages?.at(item.messages?.length - 1)?.content}
                                                </span>
                                            }
                                        />
                                    </Card>
                                    <div className="mt-2"></div>
                            </>
                        )}
                    />
                </div>

            </div>

        </>
    )
}
export default ContentLeft;