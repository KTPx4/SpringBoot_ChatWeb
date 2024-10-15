# Chat Application

This project is a chat application consisting of an API server (`ChatAPI` - Spring Boot) and a client application (`chat-for-fun`- Reactjs). 
## API Description

### Account

- **Register Account**
    - **Endpoint:** `POST /api/v1/account/register`
    - **Header:**

    - **Response:** <i>Success(200) , Failed(400), Error(500)</i>.
    - Requires `username`, `password`, and `email`.
    - The account is considered valid if the `username` does not already exist in the database.
    - Users can create multiple accounts with the same email address.
    - **Request Body Example:**
   ```json
   {
     "username": "user1",
     "password": "password123",
     "email": "user1@example.com"
   }
   ```

- **Login**
    - **Endpoint:** `POST /api/v1/account/login`
    - **Header:** 
    - Requires `username` and `password`.
    - **Response**: JWT token if the credentials are valid.
    - **Request Body Example:**
      ```json
      {
        "username": "user1",
        "password": "password123"
      }
      ```  

- **Logout:**
  - This use for add jwt token to black list (security purpose). After that the jwt token can't use. 
  - **Endpoint:** `POST /api/v1/account/logout`
  - **Header:** `Authorization`
  - **Response**: 
  - **Request Body Example:**  


- **Change Password**
  - **Endpoint:** `POST /api/v1/account/password`
  - **Header:** `Authorization`
  - Requires `oldPass` and `newPass`.
  - **Response**: New password will update if oldpass validated. Response JWT token after update success.
  - **Request Body Example:**
    ```json
    {
      "oldPass": "12345",
      "newPass": "password123"
    }
    ```   

- **Reset Password**
  - Reset password via email. An url will send to email of user, they can use it for change new password
  - **Endpoint:** `GET /api/v1/account/{id}/reset`
  - **Header:**
  - Requires: `Id`
  - **Response**: 
  - **Request Body Example:**
    ```json
    {
      "oldPass": "12345",
      "newPass": "password123"
    }
    ```  


- **Get By ID:**
  - User only can access infomation account from their id. They can't find other account (security purpose).
  - **Endpoint:** `GET /api/v1/account/{id}`
  - **Header:** `Authorization`
  - **Response**: Account hava `id`
  - **Request Body Example:**


- **Put By ID:**
  - User only can access infomation account from their id. They can't find other account (security purpose).
  - **Endpoint:** `PUT /api/v1/account/{id}`
  - **Header:** `Authorization`
  - **Response**: Account hava `id`
  - **Request Body Example:** `name`, `email`, `avatar` Can be null field but can't null for all field
    ```json
    {
      "name": "Phat",
      "email": "px4.vnd@gmail.com",
      "avatar": "/"
    }
    ```  

      

