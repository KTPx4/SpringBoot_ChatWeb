import {Badge, Button, Menu, message, Popover} from "antd";
import { Card, Avatar, List } from 'antd'
import React, {useState, useContext, useEffect} from 'react';
import {ThemeContext} from "../../ThemeContext";
import axios from "axios";
import Spinner from "react-bootstrap/Spinner";
import {
    CheckCircleTwoTone,
    CloseCircleTwoTone,
} from "@ant-design/icons"
const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';



const CardS = ({TYPE="all",searchName, currentUser, clickUser, afterLoad, updateUser, newUser, handleAccept, handleReject})=> {
    const { currentTheme } = useContext(ThemeContext);
    const background = currentTheme.getBackground();
    const contentColor = currentTheme.getContent()
    const cardColor = currentTheme.getCard();
    const textColor = currentTheme.getText();
    const textStyle = {color: textColor}
    const sliderColor = currentTheme.getKey().split("_")[1];
    const colorName = sliderColor === "light" ? "#f8f9fa" : "black";
    const selectedCard = currentTheme.getCardSelected();
    const [dataSet, setDataSet] = useState([]);
    const [tempDataSet, setTempDataSet] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    const onClickUser = (itemClick) =>{

        console.log("index: ",itemClick)
        var data = dataSet.map(item => {
            return (item.id === itemClick.id ? {...item, selected: true} : {...item, selected: false});
        });
        setDataSet(data)
        clickUser(itemClick);
    }

    useEffect(() => {
        loadListFriend();
    }, []);

    useEffect(()=>{
        if(searchName && dataSet.length > 0)
        {
            const data = dataSet.filter(item => item.name.toLowerCase().includes(searchName.toLowerCase()));
            setTempDataSet(data);
        }
    }, [searchName])


    useEffect(()=>{
        afterLoad(dataSet.length)
    }, [dataSet])

    useEffect(()=>{
        if(currentUser && currentUser?.id)
        {
            var data = dataSet.map(item => {
                return (item.id === currentUser.id ? {...item, selected: true} : {...item, selected: false});
            })
            setDataSet(data);
        }
    }, [currentUser])

    useEffect(()=>{
        if(updateUser)
        {
            console.log("update", updateUser)
            var data = []
            if(updateUser.friend === false && TYPE === "all")
            {
                data = dataSet.filter(item => item?.id !== updateUser.id);
            }
            else if(updateUser.type === "non" && TYPE === "response")
            {
                data = dataSet.filter(item => item?.id !== updateUser.id);
            }
            else{
                data = dataSet.map(item => {
                    return (item?.id === updateUser?.id ? updateUser : item);
                });
            }
            setDataSet(data)



        }
    },[updateUser])

    useEffect(()=>{
        if(newUser && !dataSet.some(value => value.id === newUser.id))
        {
            var data = dataSet
            data.push(newUser)
            setDataSet(data)
        }
    }, [newUser])

    const loadListFriend = async()=>{
        const token = localStorage.getItem("token-auth")
        setIsLoading(true);

        var url = `${SERVER}/friend`;

        if(TYPE === "response") url = url +"/request/all"
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
                if(!res.data?.data)
                {
                    setDataSet([])
                };
                var listFriend = res.data.data.friends

                setDataSet(listFriend)
            }

        } catch (err) {

        } finally {
            setIsLoading(false);

        }
    }


    if(dataSet.length == 0 && !isLoading)
    {
        return <i className="d-flex justify-content-center" style={{color:textColor}}>You are alone :)))</i>
    }

    else if(isLoading) return  <div className="d-flex justify-content-center" ><Spinner animation="border" variant="info" /></div>
    return (

        <List itemLayout="vertical" dataSource={searchName ? tempDataSet : dataSet} split={false}
              style={{
                  height: '85%',
                  overflowY: 'auto',
                  padding: "0px 10px"
              }}
              renderItem={item =>
              {
                  return (
                      <Popover placement={"right"} content={item?.name ?? ""}>
                          <List.Item style={{paddingBottom: 0,  } }>
                              <Card style={{
                                  overflowX: "auto",
                                  width: '100%',
                                  padding: 0,
                                  background:  item.selected ? selectedCard : cardColor,
                                  border: `1px ${colorName} solid`,      }}
                                    onClick={()=>onClickUser(item)}
                              >
                                  <Card.Meta

                                      style={{
                                          alignItems:'center',
                                          display: "flex",
                                          justifyContent: "center",

                                      }}

                                      avatar={
                                          <Avatar src={item.avatar ?? "https://api.dicebear.com/9.x/fun-emoji/svg?seed=Sawyer"}
                                                  style={{
                                                      height: 40, width: 40,
                                                      background: "lightgrey"
                                                  }}
                                          />
                                      }
                                      title={<span style={textStyle}>{item.name??""}</span>} // Áp dụng style cho title

                                      description={<span style={textStyle}><i>{item.id ??""}</i></span>} // Áp dụng style cho

                                  />
                                  {TYPE === "response" && (
                                      <>
                                        <Button onClick={()=> handleAccept(item)} icon={<CheckCircleTwoTone twoToneColor="#52c41a" />} />
                                        <Button onClick={()=> handleReject(item)} icon={<CloseCircleTwoTone twoToneColor="#eb2f96"  />} />
                                      </>
                                  )}
                              </Card>
                          </List.Item>
                      </Popover>
                  )

              }
              }
        />
    )
}

export default CardS;