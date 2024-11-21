import React, {useContext, useEffect, useState} from 'react';
import {Badge, Divider, Input} from "antd";
import {ThemeContext} from "../../ThemeContext";
import {
    SearchOutlined,
} from '@ant-design/icons'

const SliderHead = ({ count = 0, searchName}) =>{
    const [inputValue, setInputValue] = useState("")

    const { currentTheme } = useContext(ThemeContext);
    const background = currentTheme.getBackground();
    const textColor = currentTheme.getText();

    useEffect(() => {
        // Thiết lập một `timer` để cập nhật `debouncedValue` sau 500ms
        const timer = setTimeout(() => {
            searchName(inputValue);
        }, 500);

        // Xóa `timer` nếu `inputValue` thay đổi trước khi hết 500ms
        return () => clearTimeout(timer);
    }, [inputValue]);

    // Hàm xử lý khi người dùng nhập vào ô input
    const handleChange = (e) => {
        setInputValue(e.target.value);
    };

    return(
        <div style={{
            display: "flex",
            flexDirection: "row",
            justifyContent: "space-evenly",
            alignItems: "center",
            height: "10%",
            padding: "20px 0"
        }}>
            <Badge showZero={true} color="blue"  count={count}></Badge>
            <Input
                onChange={handleChange}
                placeholder="Search name..."
                suffix={<SearchOutlined />}
                style={{
                    // visibility: collapsed? "hidden" : "visible",
                    borderRadius: 50,
                    height: 35,
                    margin: "0",
                    width: "80%",
                    padding: "0 20px",


                }}
            />
        </div>
    )
}
export default SliderHead;