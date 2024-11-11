import Sider from "antd/es/layout/Sider";
import SliderHead from "./SliderHead";
import ListFriend from "./ListFriend";
import {Avatar, Button, Card, Input, List, message, Popover} from "antd";
import {
    UserAddOutlined, UserDeleteOutlined,
    AliwangwangOutlined,
    CloseCircleOutlined,
    RedoOutlined,
    CheckCircleOutlined, InfoCircleOutlined, SearchOutlined, CheckCircleTwoTone, CloseCircleTwoTone,
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

const SuggestContent = ({ openModal, messageApi, sendAction, updateUser, searchName, listSearch})=>{
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
    const textStyle = {color: textColor}
    const colorName = sliderColor === "light" ? "#f8f9fa" : "black";

    const [page, setPage] = useState(1)
    const [waitLoading, setWaitLoading] = useState(false)
    const [dataSuggests, setDataSuggests] = useState([])
    const [dataSearch, setDataSearch] = useState([])
    const [buttonLoading, setButtonLoading] = useState("")
    const [inputValue, setInputValue] = useState("")

    useEffect(() => {
        // Thiết lập một `timer` để cập nhật `debouncedValue` sau 500ms
        const timer = setTimeout(() => {
            if(inputValue)
            {
                searchName(inputValue);

            }
            else{
                setDataSearch([])
            }

        }, 500);

        // Xóa `timer` nếu `inputValue` thay đổi trước khi hết 500ms
        return () => clearTimeout(timer);
    }, [inputValue]);

    useEffect(() => {

            if(listSearch && listSearch.length > 0 )
            {
                setDataSearch(listSearch)
                // setDataSuggests(listSearch);
            }
    }, [listSearch]);

    useEffect(()=>{

        if(waitLoading === false)
        {
            loadData()
        }

    },[])

    const loadData = async () => {
        const action = `friend/suggest/all?page=${page}`
        const method = "get"
        setWaitLoading(true)
        const res = await sendServer(action, method, null)
        console.log(res)
        setWaitLoading(false)
        if(res.data?.data )
        {
            var response = res.data.data
            if(response.count > 0)
            {
                setDataSuggests(response.friends)
            }
        }
        else{
            messageApi.open({
                type: "error", // success, error , warning
                content: res?.data?.message ?? "Error when load data from server!",
                className: 'custom-class',
                style: {
                    marginTop: '10vh',
                },
            });
        }
    }
    useEffect(() => {
        if(updateUser)
        {
            console.log("SuggestContent update user: ", updateUser)
            var data = dataSuggests.map(item=>{
                return item?.id === updateUser?.id ? updateUser : item
            })
            setButtonLoading("")
            setDataSuggests(data)
        }
    }, [updateUser]);


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

    const actionButton = (id) =>{
        if(!id || buttonLoading === id) return;
        setButtonLoading(id)
        sendAction(id)
    }

    var data = ( inputValue) ? dataSearch : dataSuggests;

    return(
        <>

            <div className="content"
                 style={{
                     height: "100%",
                     width: "100%",
                     marginLeft: 7,
                     marginTop: 5,
                     background: `${contentColor}`,
                     overflow: "auto",
                     alignItems: "center",
                     padding: "30px",
                     border: `1px solid ${borderColor}`,
                     borderRadius: 20
                 }}
            >
                <Input
                    onChange={(e)=>setInputValue(e.target.value)}
                    placeholder="Search name..."
                    suffix={<SearchOutlined />}
                    style={{
                        // visibility: collapsed? "hidden" : "visible",
                        borderRadius: 50,
                        height: 35,
                        margin: "0",
                        width: "100%",
                        padding: "0 20px",


                    }}
                />
                {waitLoading && (<Spinner style={{justifySelf: "center"}} className="mt-5 d-flex" variant="info"/>)}
                {!waitLoading && (
                    <List className="list-suggest" itemLayout="vertical" dataSource={data} split={false}
                          style={{
                              height: '95%',
                              overflowY: 'auto',
                              padding: "0px 10px"
                          }}
                          renderItem={item =>
                          {
                              var displayButton =
                                  item.type === TYPE_FRIEND.none ? "Add" :
                                      (item.type === TYPE_FRIEND.waiting ? "Cancel" : "Accept")

                              if(buttonLoading === item.id) displayButton = (<Spinner style={{width: 20, height: 20}} variant={"info"}/>)
                              var displayColor =  item.type === TYPE_FRIEND.none ? textColor : (item.type === TYPE_FRIEND.waiting ? "#D91656" : "#0D92F4")
                              if(item.friend === true){
                                  displayButton = "Unfriend"
                                  displayColor= "#D91656"
                              }
                              return (
                                      <List.Item style={{paddingBottom: 0,  } }>
                                          <Card
                                                className="card-suggest"
                                              style={{
                                              overflowX: "auto",
                                              width: '100%',
                                              padding: 0,
                                              background:   cardColor,
                                              border: `1px ${colorName} solid`,
                                              display: "flex",
                                              alignItems: "center"
                                            }}
                                          >
                                              <Card.Meta

                                                  style={{
                                                      alignItems:'center',
                                                      display: "flex",
                                                      justifyContent: "center",
                                                      overflow: "auto"
                                                  }}

                                                  avatar={
                                                      <Avatar src={item.avatar ?? "https://api.dicebear.com/9.x/fun-emoji/svg?seed=Sawyer"}
                                                              style={{
                                                                  height: 40, width: 40,
                                                                  background: "lightgrey"
                                                              }}
                                                      />
                                                  }
                                                  title={<span style={textStyle}>
                                                      <Popover placement={"bottom"} content={item?.name ?? ""}>
                                                            {item.name??""}
                                                    </Popover>
                                                    </span>} // Áp dụng style cho title

                                                  description={<span style={textStyle}><i>{item.id ??""}</i></span>} // Áp dụng style cho

                                              />
                                              <Button
                                                  onClick={()=>actionButton(item.id)}
                                                  style={{width: 80,background: contentColor, color: displayColor, border: `1px solid ${borderColor}`}}
                                                      className="suggest-btn-add mt-3">
                                                  {displayButton}
                                              </Button>
                                              <Button
                                                  onClick={()=>openModal(item.id)}
                                                  style={{width: 80,background: contentColor, color: textColor, border: `1px solid ${borderColor}`}}
                                                      className="suggest-btn-add mt-3">
                                                  Fast Chat
                                              </Button>
                                          </Card>
                                      </List.Item>
                              )

                          }
                          }
                    />
                )}

            </div>
        </>
    )
}
export default SuggestContent;
