import React, {useState} from "react";
import ProfileComponent from "../../components/account/ProfileComponent";
import ChatComponent from "../../components/chat/ChatComponent";

const HomePage = () =>{
    const [component, setComponent] = useState(null);
    const HomeComponent = <div><h1>Home Page</h1></div>

    return(
        <>
            <button
            title="Chat"
            style={{width:50, height:50}}
            onClick={()=> {setComponent(ChatComponent)}}
            />
            <button
            title="Profile"

            style={{width:50, height:50}}
            onClick={()=> {setComponent(ProfileComponent)}}
            />
            <h1>Show component</h1>
            <div>{component}</div>
        </>
    )
}
export default HomePage;