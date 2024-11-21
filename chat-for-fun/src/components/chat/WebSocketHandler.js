// WebSocketHandler.js
import { Stomp } from '@stomp/stompjs';
const SERVER_DOMAIN = process.env.REACT_APP_SERVER_DOMAIN || "localhost:8080";
class WebSocketHandler {

    constructor(token) {
        this.token = token;
        this.stompClient = null;
        this.receivedMessages = null;
        this.listOnline = [];
        this.onMessageReceivedCallback = null; // Callback để nhận tin nhắn mới
        this.onGetSeen = null;
        this.onAddFriend = null;
        this.onSearch = null;
        this.onUpdateGroup = null;
    }
    // Thiết lập callback cho tin nhắn mới
    setOnMessageReceived(callback) {
        this.onMessageReceivedCallback = callback;
    }
    setOnGetSeen(callback) {
        this.onGetSeen = callback;
    }
    setOnAddFriend(callback) {
        this.onAddFriend = callback;
    }
    setOnSearch(callback) {
        this.onSearch = callback;
    }
    setOnUpdateGroup(callback) {
        this.onUpdateGroup = callback;
    }
    // Khởi tạo kết nối WebSocket
    connect() {
        const ws = new WebSocket(`ws://${SERVER_DOMAIN}/ws`);
        this.stompClient = Stomp.over(ws);

        this.stompClient.connect(
            { Authorization: `Bearer ${this.token}` },
            (frame) => {
                // console.log('Connected to WebSocket server:', frame);

                // Đăng ký các endpoint
                this.stompClient.subscribe('/topic/messages', (message) => {
                    const msg = JSON.parse(message.body);
                    // console.log('Received message from server:', msg.content);
                    this.receivedMessages.push(msg.content);
                });

                this.stompClient.subscribe('/list/online', (message) => {
                    // console.log("List online:", message);
                    const msg = JSON.parse(message.body);
                    const lists = JSON.parse(msg.content);
                    this.listOnline = lists.map(list => list.userId);
                });

                // Get message from server send private
                this.stompClient.subscribe('/user/topic/messages', (message) => {

                    const msg = JSON.parse(message.body);
                    // console.log(msg)
                    if (msg.type === 'error' && msg.sender === 'server') {
                        alert(msg.content);
                    }
                    else if (msg.type === 'chat') {
                        this.onMessageReceivedCallback(msg)
                    }
                    else if(msg.type === 'seen')
                    {
                        this.onGetSeen(msg)
                    }
                    else if(msg.type === 'friend')
                    {
                        this.onAddFriend(msg.content)
                    }
                    else if(msg.type === 'search')
                    {
                        this.onSearch(msg.content)
                    }
                });

                this.stompClient.subscribe('/user/update/group', (message) => {
                    const res = JSON.parse(message.body);
                    if (res.type && res.type === 'error' && res.sender === 'server') {
                        alert(res.content);
                    }
                    else{
                        this.onUpdateGroup(res)
                    }
                })

            },
            (error) => {
                console.error('Connection error:', error);
            }
        );

        ws.onclose = (event) => {
            alert("Server has been closed connect. Please try again!")
            console.log(event.wasClean ? 'Connection closed cleanly' : 'Connection error:', event.reason || 'Unknown reason');
        };
    }

    sendUpdateGroup(updateGroup)
    {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send('/app/update.group', {}, JSON.stringify(updateGroup));
        }else {
            console.log("Not connected to WebSocket or message is empty.");
        }
    }

    // Hàm gửi tin nhắn đến WebSocket server
    sendMessage(toUser, content, contentType = 'text', replyMessageId ='') {
        if (this.stompClient && this.stompClient.connected) {
            const message = {
                sender: this.token,
                to: toUser,
                content,
                contentType: contentType,
                replyMessageId: replyMessageId
            };
            this.stompClient.send('/app/chat', {}, JSON.stringify(message));
        } else {
            console.log("Not connected to WebSocket or message is empty.");
        }
    }
    sendAction(to)
    {
        if (this.stompClient && this.stompClient.connected) {
            const message = {

                to: to

            };
            this.stompClient.send('/app/friend', {}, JSON.stringify(message));
        } else {
            console.log("Not connected to WebSocket or message is empty.");
        }
    }
    sendSeen(to)
    {
        if (this.stompClient && this.stompClient.connected) {
            const message = {

                to: to

            };
            this.stompClient.send('/app/seen', {}, JSON.stringify(message));
        } else {
            console.log("Not connected to WebSocket or message is empty.");
        }
    }

    sendSearch(name)
    {
        if (this.stompClient && this.stompClient.connected) {
            const message = {
                to: name
            };
            this.stompClient.send('/app/friend.search', {}, JSON.stringify(message));
        } else {
            console.log("Not connected to WebSocket or message is empty.");
        }
    }
    // Hàm lấy danh sách tin nhắn nhận được
    getReceivedMessages() {
        return this.receivedMessages;
    }

    // Hàm lấy danh sách online
    getListOnline() {
        return this.listOnline;
    }

    disconnect() {
        if(this.stompClient !== null) {
            this.stompClient.disconnect(()=>{
                console.log("Disconnected!");
            });
        }
    }

}

export default WebSocketHandler;
