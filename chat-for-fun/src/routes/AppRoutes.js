
import {Route, Routes} from "react-router-dom"
import UserRouter from "./UserRouter";
import LayoutPage from "../components/layouts/LayoutPage";
import HomePage from "../pages/home/HomePage";
import Header from "../components/layouts/Header";
import AuthPage from "../pages/authentication/AuthPage"
import NotFoundPage from "../pages/notfound/NotFoundPage";
import {
    notification, message,
} from "antd";
import TestPage from "../pages/TestPage";
import ResetPage from "../pages/authentication/ResetPage";
const AppRoutes = () =>{
    const [api, contextHolder] = notification.useNotification();
    const [messageApi, contextHolder2] = message.useMessage();

    const openNotification = (type, mess, desc, icon) => {
        switch (type) {
            case 1:
                api.open({
                    message: mess,
                    description: desc,
                    icon:   icon,
                    showProgress: true,
                    pauseOnHover: true,
                });
                break
            case 2:
                messageApi.open({
                    type: icon, // success, error , warning
                    content: mess,
                    className: 'custom-class',
                    style: {
                        marginTop: '10vh',
                    },
                });
                break
        }
    };

    return(
        <>
            {contextHolder}
            {contextHolder2}

            <Routes>
                <Route path="/test" element={<TestPage/>} />
                <Route path="/" element={<UserRouter><LayoutPage/></UserRouter>} >
                    <Route index element={<HomePage openNotification={openNotification} />} />
                    <Route path="account/:id" element={<HomePage openNotification={openNotification} />} />
                    <Route path="friend/:id" element={<HomePage openNotification={openNotification} />} />
                </Route>

                <Route path="/login" element={<LayoutPage/>} >
                    <Route index element={<AuthPage openNotification={openNotification} />} />
                </Route>
                <Route path="/reset">
                    <Route index element={<ResetPage />} />
                </Route>
                <Route path="*" element={<NotFoundPage/>}/>

            </Routes>
        </>
    )
}

export default AppRoutes;