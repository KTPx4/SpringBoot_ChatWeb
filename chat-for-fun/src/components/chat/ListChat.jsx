import {Badge, Menu, message} from "antd";
import { Card, Avatar, List } from 'antd'
import React, {useState, useContext, useEffect} from 'react';
import {ThemeContext} from "../../ThemeContext";
import axios from "axios";
import Spinner from "react-bootstrap/Spinner";
import data from "bootstrap/js/src/dom/data";
import * as item from "date-fns/locale";
const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';



const CardS = ({updateCard, searchName, clickUser, newMessage, oldMessage})=> {
    const { currentTheme } = useContext(ThemeContext);
    const background = currentTheme.getBackground();
    const contentColor = currentTheme.getContent()
    const cardColor = currentTheme.getCard();
    const textColor = currentTheme.getText();
    const textStyle = {color: textColor}
    const sliderColor = currentTheme.getKey().split("_")[1];
    const colorName = sliderColor === "light" ? "#f8f9fa    " : "black";
    const selectedCard = currentTheme.getCardSelected();

    const [dataSet, setDataSet] = useState([]);
    const [tempDataSet, setTempDataSet] = useState([]);

    const [isLoading, setIsLoading] = useState(true);

    const onClickUser = (itemClick) =>{
        var it = {...itemClick, selected: true, count: 0}

        var data = dataSet.map(item => {
            return (item.id === itemClick.id ? it : {...item, selected: false});
        });

        setDataSet(data)
        // console.log("index: ",itemClick)

        clickUser(it);
    }

    useEffect(() => {
        loadListFriend();
    }, []);

    useEffect(() => {

        if(dataSet && dataSet.length > 0)
        {
            console.log("it run")
            setDataSet(sortedList)
        }

    }, [dataSet]);

    const sortedList = (data) =>{

        return data.sort((a, b) => {
            // Sắp xếp theo `count`, count càng lớn thì xếp càng lên đầu
            if (a.count !== b.count) {
                return b.count - a.count;
            }
            // Nếu count bằng nhau, sắp xếp theo `messages[length - 1].createdAt`
            const dateA = new Date(a.messages[a.messages.length - 1]?.createdAt);
            const dateB = new Date(b.messages[b.messages.length - 1]?.createdAt);

            return dateB - dateA;
        })

    };

    useEffect(()=>{
        if(searchName && dataSet.length > 0)
        {
            const data = dataSet.filter(item => item.name.toLowerCase().includes(searchName.toLowerCase()));
            setTempDataSet(data);
        }
    }, [searchName])

    useEffect(()=>{
        if(updateCard)
        {
            var data = dataSet.map(item => (item.id === updateCard.id ? updateCard : item))
            setDataSet(data)
        }
    }, [updateCard])

    useEffect(()=>{
        if(newMessage)
        {
            var data = []
            if(!dataSet || dataSet.length < 1)
            {
                loadListFriend();
            }
            else{
              data = dataSet.map(item => {

                  var it = item
                  if(item.id === newMessage.sender)
                  {
                      it = {...item, messages: [...item.messages, newMessage], count: item.selected ?  0 : item.count+1 }
                  }
                  else if(item.id === newMessage.to)
                  {
                      it  = {...item, messages: [...item.messages, newMessage]}
                  }

                  return (it)
              })
            setDataSet(data);

            }
        }
    }, [newMessage])


    useEffect(() => {
        if (oldMessage) {
            const updatedData = dataSet.map(item => ({
                ...item,
                messages: [...oldMessage, ...item.messages],
            }));

            setDataSet([...updatedData]);  // Cung cấp mảng mới hoàn toàn
            console.log("dataset:", updatedData);  // In mảng mới
        }
    }, [oldMessage]);

    const loadListFriend = async()=>{
        const token = localStorage.getItem("token-auth")
        setIsLoading(true);

        const url = `${SERVER}/chat`;

        try {
            const res =  await axios({
                url: url,
                method: "get",
                headers:{
                    authorization: `Bearer ${token}`,
                    // "Content-Type": "application/json",
                },

            })
            console.log(res)
            if(res.status === 200)
            {
                const listFriend = res.data.data.friends
                setDataSet(listFriend)
            }

        } catch (err) {

        } finally {
            setIsLoading(false);

        }
    }


    if(!dataSet ||( dataSet?.length == 0 && !isLoading))
    {
        return <i className="d-flex justify-content-center" style={{color:textColor, marginTop: 30}}>Make friends to chat!!!!</i>
    }
    else if(isLoading) return  <div className="d-flex justify-content-center" ><Spinner className="mt-3" animation="border" variant="info" /></div>

    return (

        <List itemLayout="vertical" dataSource={searchName ? tempDataSet : dataSet} split={false}
              style={{
               height: '85%',
              overflowY: 'auto',
                  padding: "0px 10px 10px 10px"
              }}
              renderItem={item =>
              {
                  return (
                      <List.Item style={{paddingBottom: 0,  } } key={item.id}>
                          <Card style={{ width: '100%', padding: 0, background:  item.selected ? selectedCard : cardColor,   border: `1px ${colorName} solid`,      }}
                                onClick={()=>onClickUser(item)}
                          >
                              <Card.Meta

                                  style={{
                                      alignItems:'center',
                                      display: "flex",
                                      justifyContent: "center",

                                  }}

                                  avatar={
                                      <Badge count={item.count}>
                                          <Avatar src={item.avatar}
                                                  style={{
                                                      height: 40, width: 40,
                                                      background: "lightgrey"
                                                  }}
                                          />
                                      </Badge>
                                  }
                                  title={<span style={textStyle}>{item.name}</span>} // Áp dụng style cho title

                                  description={<span className={item.count >0 ? "fw-bold":""} style={textStyle}>{item.messages?.at(item.messages.length-1)?.content}</span>} // Áp dụng style cho

                              />
                          </Card>
                      </List.Item>
                      )

              }
                }
        />
    )
}

export default CardS;