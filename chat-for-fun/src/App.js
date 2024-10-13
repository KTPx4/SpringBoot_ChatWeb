import logo from './logo.svg';
import './App.css';
import React, { useEffect, useState } from 'react';

function App() {
    const [message, setMessage] = useState('');
    const [receivedMessages, setReceivedMessages] = useState([]);
    const [socket, setSocket] = useState(null);

    useEffect(() => {
        // Khởi tạo WebSocket và kết nối đến server
        const ws = new WebSocket('ws://localhost:8080/ws');
        setSocket(ws);

        // Xử lý khi kết nối được mở
        ws.onopen = () => {
            console.log('Đã kết nối đến WebSocket server');
            const connectMessage = {
              type: 'connect',
              sender: 'User' // Tên người dùng, có thể thay đổi thành một giá trị động
          };
          ws.send(JSON.stringify(connectMessage));
        };

        // Xử lý khi nhận được tin nhắn từ server
        ws.onmessage = (event) => {
          console.log(event);
          
          const msg = JSON.parse(event.data);
          console.log('Nhận tin nhắn từ server: ', msg.content);
          console.log( msg);
          
          if (msg.type === 'chat') {
              setReceivedMessages((prevMessages) => [...prevMessages, msg.content]);
          }
          else if (msg.type === 'connect') {
            setReceivedMessages((prevMessages) => [...prevMessages, `${msg.sender} đã tham gia`]);
          } 
          else if (msg.type === 'disconnect') {
            setReceivedMessages((prevMessages) => [...prevMessages, `${msg.sender} đã rời khỏi`]);
        }
        };

        // Xử lý khi kết nối bị đóng
        ws.onclose = () => {
            console.log('Kết nối WebSocket đã bị đóng');
        };

        // Cleanup khi component bị huỷ
        return () => {
            if (ws) {
                ws.close();
            }
        };
    }, []);

    // Hàm gửi tin nhắn đến WebSocket server
    const sendMessage = () => {

      if (socket && socket.readyState === WebSocket.OPEN && message) {
        const messageObject = {
            type: 'chat', // Loại tin nhắn
            content: message,
            sender: 'User' // Tên người dùng, có thể thay đổi thành một giá trị động
        };
        socket.send(JSON.stringify(messageObject));
        setMessage('');
    }

  };

    return (
        <div className="App">
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