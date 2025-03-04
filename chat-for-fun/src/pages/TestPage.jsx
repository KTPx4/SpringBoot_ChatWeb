import React, { useEffect, useState } from 'react';
import { Stomp } from '@stomp/stompjs';

const TestPage = () =>{
    const [message, setMessage] = useState('');
    const [receivedMessages, setReceivedMessages] = useState([]);
    const [username, setUsername] = useState(localStorage.getItem('token-auth'));
    const [stompClient, setStomClient] = useState(null);
    const [toUser, setToUser] = useState("");
    const [listOnline, setListOnline] = useState([]);

    useEffect(() => {
        if (stompClient) {
            // Kết nối sau khi stompClient được khởi tạo
            stompClient.connect(
                { Authorization: `Bearer ${username}` }, // JWT
                (frame) => {
                    console.log('Đã kết nối đến WebSocket server: ' + frame);

                    // Đăng ký một endpoint với client
                    stompClient.subscribe('/topic/messages', (message) => {
                        const msg = JSON.parse(message.body);
                        console.log('Nhận tin nhắn từ server: ', msg.content);
                        setReceivedMessages((prevMessages) => [...prevMessages, msg.content]);
                    });

                    stompClient.subscribe('/list/online', (message) => {
                        console.log("List online: ", message)
                        const msg = JSON.parse(message.body);
                        console.log(msg)
                        var lists  = JSON.parse(msg.content);
                        console.log(lists)
                        setListOnline([])
                        lists.forEach(list =>  setListOnline((pre) => [...pre, list.userId]))


                    });

                    stompClient.subscribe("/user/topic/messages", function (message) {
                        console.log("Received private message: ", message.body);
                        const msg = JSON.parse(message.body);
                        if(msg.type  === ("error") && msg.sender === ("server"))
                        {
                            alert(msg.content);
                        }
                        else{
                            setReceivedMessages((prevMessages) => [...prevMessages,`Message from : ${msg.sender}: ${msg.content}` ]);
                        }
                    });

                    const connectMessage = {
                        type: 'connect',
                        content: 'User đã tham gia',
                        sender: username,
                        to: "server"
                    };
                    //  stompClient.send('/app/chat', {}, JSON.stringify(connectMessage));
                },
                (error) => {
                    console.log(error);
                    console.error('Connection error: ' + error);
                }
            );

        }
    }, [stompClient, username]); // Kết nối khi stompClient và username đã được thiết lập

    const connectToWebSocket = () => {
        const ws = new WebSocket('ws://localhost:8080/ws');
        const client = Stomp.over(ws);
        setStomClient(client); // Thiết lập stompClient
        ws.onclose = (event) => {
            console.log(event)
            if (event.wasClean) {
                console.log('Connection closed cleanly');
            } else {
                console.log('Connection error:', event.reason || 'Unknown reason');
            }
        };
    };

    // Hàm gửi tin nhắn đến WebSocket server
    const sendMessage = () => {
        if (stompClient && stompClient.connected && message) {
            const Mess = {

                sender: username,
                to: toUser,
                content: message,
                contentType: "text",

            };
            stompClient.send('/app/chat', {}, JSON.stringify(Mess));
            setMessage('');
        } else {
            console.log("Chưa kết nối WebSocket hoặc chưa nhập tin nhắn.");
        }
    };

    return (
        <div className="App">
            <div>
                <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Nhập token của bạn"
                />
                <button onClick={connectToWebSocket}>Kết nối</button>
            </div>

            <h1>WebSocket Client</h1>
            <input
                type="text"
                value={toUser}
                onChange={(e) => setToUser(e.target.value)}
                placeholder="Id muốn gửi"
            />
            <input
                type="text"
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder="Nhập tin nhắn"
            />
            <button onClick={sendMessage}>Gửi tin nhắn</button>
            <div>
                <h2>List online</h2>
                <ul>
                    {listOnline.map((msg, index) => (
                        <li key={index}>{msg}</li>
                    ))}
                </ul>
                <h2>Tin nhắn đã nhận:</h2>
                <ul>
                    {receivedMessages.map((msg, index) => (
                        <li key={index}>{msg}</li>
                    ))}
                </ul>
            </div>
        </div>
    );

}
export default TestPage;