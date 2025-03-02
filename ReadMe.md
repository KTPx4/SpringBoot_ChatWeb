# Web Chat Application

## Description
This project is a Web Chat Application built with Spring Boot for the backend API and ReactJS for the frontend. It includes essential chat functionalities such as user authentication, real-time messaging, file sharing, and group chat management.

## Features
### User Features
- **Authentication**: User registration, login, and password recovery via email.
- **Chat System**: Real-time messaging with friends, sending files, and blocking users.
- **Friend Management**: Search for friends, send friend requests, and block/unblock users.
- **Group Chat**: Create and manage group chats with role-based permissions.
- **User Profile**: Manage personal profile and settings.

## Installation & Setup
### Prerequisites
- Java 17+
- Spring Boot
- Node.js & npm
- MongoDB 

### Backend Setup (Spring Boot API)
```sh
# Clone the repository
./server

# Configure Environment variable, change 'x' by your key
```
```properties
GG_CLIENT_ID=xxxx;GG_CLIENT_SECRET=xxxx;GG_REFRESH_TOKEN=xxxx;GG_FROM_EMAIL=xxxx;
```
```sh
# Build and run the backend
mvn clean install
mvn spring-boot:run
```

### Database Setup
```sh
# Run the SQL script to initialize the database
mongorestore --host localhost --port 27017 -d ChatDB <folder store database>

```

### Frontend Setup (ReactJS)
```sh
cd ./client

# Install dependencies
npm install

# Start the React application
npm start
```

## Test Account
```sh
# User Account:
Username:  
Password:  
```

## API Documentation
The API endpoints are documented using Swagger and can be accessed at:
```
http://localhost:3001/api
```

