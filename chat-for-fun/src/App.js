import logo from './logo.svg';
import './App.css';
import React, { useEffect, useState } from 'react';
import { Stomp } from '@stomp/stompjs';


function App() {
    const [message, setMessage] = useState('');
    const [receivedMessages, setReceivedMessages] = useState([]);
    const [socket, setSocket] = useState(null);
    const [username, setUsername] = useState('');
    const [ID, setID] = useState('');
    const [stompClient, setStomClient] = useState(null);
    useEffect(() => {       
    }, []);

    // Hàm gửi tin nhắn đến WebSocket server
    const sendMessage = () => {

    //   if (socket && socket.readyState === WebSocket.OPEN && message) 
      if (stompClient && stompClient.readyState === WebSocket.OPEN && message) 

        {
        // const messageObject = {
        //     type: 'chat', // Loại tin nhắn
        //     content: message,
        //     sender: username // Tên người dùng, có thể thay đổi thành một giá trị động
        // };
        // socket.send(JSON.stringify(messageObject));
        const Mess = {
            type: 'chat',
            content: message,
            sender: username, // Sử dụng tên người dùng
        };
        stompClient.send('/app/chat', {}, JSON.stringify(Mess));
        setMessage('');
     }
    };

  
    const connectToWebSocket = async () => {
      try {
          const ws = new WebSocket('ws://localhost:8080/ws');
          const client = Stomp.over(ws);
  
          client.connect(
              { Authorization: `Bearer .eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBZmlRNzI0MVdqIiwiaWF0IjoxNzI5ODY3MTc3LCJleHAiOjE3MzA0NzE5Nzd9.fIFE0-UxZX0xGbSVG5VKxaexfyPrdSHtbpRHY0i7zcs` }, // JWT
              (frame) => {
                  console.log('Đã kết nối đến WebSocket server: ' + frame);
                  
                  setStomClient(client); // Cập nhật lại giá trị `stompClient`
                  
                  // Đăng ký một endpoint với client
                  client.subscribe('/topic/messages', (message) => {
                    console.log(message);
                    console.log("......");
                    
                      const msg = JSON.parse(message.body);
                      console.log('Nhận tin nhắn từ server: ', msg.content);
                      setReceivedMessages((prevMessages) => [...prevMessages, msg.content]);
                  });
  
                  const connectMessage = {
                      type: 'connect',
                      content: 'User đã tham gia',
                      sender: username,
                  };
                  client.send('/app/chat', {}, JSON.stringify(connectMessage));
              },
              (error) => {
                  console.error('Connection error: ' + error);
              }
          );
      } catch (e) {
          console.log('Error connecting to WebSocket:', e);
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