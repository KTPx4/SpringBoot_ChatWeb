import {Badge, Menu} from "antd";
import { Card, Avatar, List } from 'antd'
import React, {useState, useContext} from 'react';
import {ThemeContext} from "../../ThemeContext";


var data = [{
    id: '1',
    name: 'Kieu Thanh Phat',
    messages: [
        {
            id: 'mess1',
            contentType: "text",
            content: 'Hi, this is message 1',
            from: "2",
            reply: "",
            status: "sent"
        },
        {
            id: 'mess2',
            contentType: "text",

            content: 'message 2',
            from: "Px4",
            reply: "",
            status: "sent"
        },
    ],
        avatar: 'https://avatars.githubusercontent.com/u/128300163?s=96&v=4',
        selected: false,
        count: 1
    },
    {
        id: '2',
        name: 'Nguyen Van A',
        messages: [
            {
                id: 'mess3',
                contentType: "text",

                content: 'Hi, this is message 1 of 2',
                from: "2",
                reply: "",
                status: "sent"
            },
            {
                id: 'mess4',
                contentType: "image",
                content: 'https://picsum.photos/800/900',
                from: "2",
                reply: "",
                status: "sent"
            },
        ],
        avatar: 'https://api.dicebear.com/9.x/adventurer/svg?seed=Ryan',
        selected: false,
        count: 1
    },
];

const CardS = ({clickUser})=> {
    const { currentTheme } = useContext(ThemeContext);
    const background = currentTheme.getBackground();
    const contentColor = currentTheme.getContent()
    const cardColor = currentTheme.getCard();
    const textColor = currentTheme.getText();
    const textStyle = {color: textColor}
    const sliderColor = currentTheme.getKey().split("_")[1];
    const colorName = sliderColor === "light" ? "#f8f9fa    " : "black";
    const selectedCard = currentTheme.getCardSelected();
    const [dataSet, setDataSet] = useState(data);

    const onClickUser = (itemClick) =>{
        data = data.map(item => {
            return (item.id === itemClick.id ? {...item, selected: true} : {...item, selected: false});
        });
        setDataSet(data)
        console.log("index: ",itemClick)
        clickUser(itemClick);
    }
    if(dataSet.length == 0)
    {
        return <i className="d-flex justify-content-center" style={{color:textColor}}>Make friends to chat!!!!</i>
    }
    return (

        <List itemLayout="vertical" dataSource={dataSet} split={false}
              style={{
               height: '85%',
              overflowY: 'auto',
                  padding: "0px 10px 10px 10px"
              }}
              renderItem={item =>
                  (
                      <List.Item style={{paddingBottom: 0,  } }>
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

                                  description={<span style={textStyle}>{item.messages.at(item.messages.length-1).content}</span>} // Áp dụng style cho

                              />
                          </Card>
                      </List.Item>
                  )} />
    )
}

export default CardS;