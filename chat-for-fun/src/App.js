import logo from './logo.svg';
import './App.css';
import React, { useEffect, useState } from 'react';

function App() {
    const [message, setMessage] = useState('');
    const [receivedMessages, setReceivedMessages] = useState([]);
    const [socket, setSocket] = useState(null);
    const [username, setUsername] = useState('');
    const [ID, setID] = useState('');

    useEffect(() => {       
    }, []);

    // Hàm gửi tin nhắn đến WebSocket server
    const sendMessage = () => {

      if (socket && socket.readyState === WebSocket.OPEN && message) 
        {
        const messageObject = {
            type: 'chat', // Loại tin nhắn
            content: message,
            sender: username // Tên người dùng, có thể thay đổi thành một giá trị động
        };
        socket.send(JSON.stringify(messageObject));
        setMessage('');
     }
    };

    const connectToWebSocket = () => {
        try{
            const ws = new WebSocket('ws://localhost:8080/ws');
            setSocket(ws);
            //const stompClient = Stomp.over(ws);

            ws.onopen = () => {
                console.log('Đã kết nối đến WebSocket server');
                const connectMessage = {
                    type: 'connect',
                    sender: username // Sử dụng tên người dùng
                };
                ws.send(JSON.stringify(connectMessage));
            };
        
                    // Xử lý khi nhận được tin nhắn từ server
            ws.onmessage = (event) => {
              console.log(event);
              
              const msg = JSON.parse(event.data);
              console.log('Nhận tin nhắn từ server: ', msg.content);
              console.log( msg);
    
              if (msg.type === 'yourid') 
              {
                setID(msg.content)
              }
              else if (msg.type === 'chat') {
                  setReceivedMessages((prevMessages) => [...prevMessages, msg.content]);
              }
              else if (msg.type === 'connect') {
                setReceivedMessages((prevMessages) => [...prevMessages, `${msg.sender} đã tham gia`]);
              } 
              else if (msg.type === 'disconnect') {
                setReceivedMessages((prevMessages) => [...prevMessages, `${msg.sender} đã rời khỏi`]);
            }
            };
        
            ws.onclose = () => {
                console.log('Kết nối WebSocket đã bị đóng');
            };
            ws.onerror = (error) => {
                console.error('WebSocket error', error);
            };
        }
        catch(e)
        {
            console.log(e);            
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