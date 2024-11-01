import {Badge, Menu} from "antd";
import { Card, Avatar, List } from 'antd'
import React, {useState, useContext} from 'react';
import {ThemeContext} from "../../ThemeContext";


var data = [
    {
        id: '1',
        title: 'Kieu Thanh Phat',
        description: 'Hi, 111111111111111111111111111111111111111111111111111111111111111111',
        avatar: 'path-to-your-avatar-image',
        selected: true,
        count: 2
    },
    {
        id: '2',

        title: 'Nguyen Van A',
        description: 'Hi, 2222',
        avatar: 'path-to-your-avatar-image',
        selected: false,
        count: 0
    },
    {
        id: '3',
        title: 'Kieu Thanh Phat',
        description: 'Hi, 111111111111111111111111111111111111111111111111111111111111111111',
        avatar: 'path-to-your-avatar-image',
        selected: false,
        count: 1
    },
     {
         id: '4',
        title: 'Kieu Thanh Phat',
        description: 'Hi, 111111111111111111111111111111111111111111111111111111111111111111',
        avatar: 'path-to-your-avatar-image',
         selected: false,
        count: 8
    },

     {
         id: '5',
        title: 'Kieu Thanh Phat',
        description: 'Hi, 111111111111111111111111111111111111111111111111111111111111111111',
        avatar: 'path-to-your-avatar-image',
         selected: false,
        count: 8
    },

     {
         id: '6',
        title: 'Kieu Thanh Phat',
        description: 'Hi, 111111111111111111111111111111111111111111111111111111111111111111',
        avatar: 'path-to-your-avatar-image',
         selected: false,
        count: 8
    },

     {
         id: '7',
        title: 'Kieu Thanh Phat',
        description: 'Hi, 111111111111111111111111111111111111111111111111111111111111111111',
        avatar: 'path-to-your-avatar-image',
     selected: false,
        count: 8
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
                                                            background: "grey"
                                                        }}
                                                    />
                                        </Badge>
                                        }
                                    title={<span style={textStyle}>{item.title}</span>} // Áp dụng style cho title
                                   description={<span style={textStyle}>{item.description}</span>} // Áp dụng style cho

                              />
                          </Card>
                      </List.Item>
                  )} />
    )
}

export default CardS;