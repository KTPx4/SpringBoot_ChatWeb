import './App.css';
import React, { useEffect, useState } from 'react';
import { Stomp } from '@stomp/stompjs';

function App() {
    const [message, setMessage] = useState('');
    const [receivedMessages, setReceivedMessages] = useState([]);
    const [username, setUsername] = useState('');
    const [stompClient, setStomClient] = useState(null);

    useEffect(() => {
        if (stompClient) {
            // Kết nối sau khi stompClient được khởi tạo
            stompClient.connect(
                { Authorization: `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBZmlRNzI0MVdqIiwiaWF0IjoxNzI5ODY3MTc3LCJleHAiOjE3MzA0NzE5Nzd9.fIFE0-UxZX0xGbSVG5VKxaexfyPrdSHtbpRHY0i7zcs` }, // JWT
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
                        console.log('Nhận tin nhắn từ server: ', msg.content);

                    });

                    const connectMessage = {
                        type: 'connect',
                        content: 'User đã tham gia',
                        sender: username,
                    };
                    stompClient.send('/app/chat', {}, JSON.stringify(connectMessage));
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
                type: 'chat',
                content: message,
                sender: username,
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
            placeholder="Nhập tên của bạn"
          />
          <button onClick={connectToWebSocket}>Kết nối</button>
        </div>

        <h1>WebSocket Client</h1>
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="Nhập tin nhắn"
        />
        <button onClick={sendMessage}>Gửi tin nhắn</button>
        <div>
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

export default App;
