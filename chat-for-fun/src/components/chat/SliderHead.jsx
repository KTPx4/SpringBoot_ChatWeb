import React, {useContext} from 'react';
import {Divider, Input} from "antd";
import {ThemeContext} from "../../ThemeContext";
import {
    SearchOutlined,
} from '@ant-design/icons'

const SliderHead = ({collapsed}) =>{
    const { currentTheme } = useContext(ThemeContext);
    const background = currentTheme.getBackground();
    const textColor = currentTheme.getText();
    return(
        <div style={{
            height: "15%",

        }}>
            <h4 style={{
                height:collapsed ? (43+26) : 35,
                alignItems: "center",
                display: "flex",
                justifyContent: "center",
                color: textColor,
            }}
            >Chats</h4>
            <Input
                placeholder="Search name..."
                hidden={collapsed}
                title="Search"
                suffix={<SearchOutlined />}
                style={{
                    // visibility: collapsed? "hidden" : "visible",
                    borderRadius: 50,
                    height: 35,
                    margin: "0 15px",
                    width: "90%",
                    padding: "0 20px",


                }}
            />
            <Divider style={{margin: "15px 0"}}/>
        </div>
    )
}
export default SliderHead;