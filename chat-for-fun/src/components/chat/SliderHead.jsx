import React, {useContext, useEffect, useState} from 'react';
import {Divider, Input} from "antd";
import {ThemeContext} from "../../ThemeContext";
import {
    SearchOutlined,
} from '@ant-design/icons'

const SliderHead = ({searchName, collapsed}) =>{
    const { currentTheme } = useContext(ThemeContext);
    const background = currentTheme.getBackground();
    const textColor = currentTheme.getText();

    const [inputValue, setInputValue] = useState("")

    useEffect(() => {
        // Thiết lập một `timer` để cập nhật `debouncedValue` sau 500ms
        const timer = setTimeout(() => {
            searchName(inputValue);
        }, 500);

        // Xóa `timer` nếu `inputValue` thay đổi trước khi hết 500ms
        return () => clearTimeout(timer);
    }, [inputValue]);


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
                onChange={e => setInputValue(e.target.value)}
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
            {/*<Divider style={{margin: "15px 0"}}/>*/}
        </div>
    )
}
export default SliderHead;