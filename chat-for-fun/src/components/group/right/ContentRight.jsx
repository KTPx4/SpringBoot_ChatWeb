import {MoreOutlined, PlusOutlined, SendOutlined, SmileOutlined} from "@ant-design/icons";
import {Button} from "antd";


const ContentRight =() =>{
    return (
        <>
            <div style={{width: "100%", padding: 0, display: "flex", justifyContent: "space-evenly"}} >
                <h4>Notify</h4>
                <Button icon={<PlusOutlined />}/>
            </div>
            <div style={{width: "100%", height: "90%", overflow: "auto", padding: 0, display: "flex", flexDirection: "column"}}>

            </div>
        </>
    )
}
export default ContentRight