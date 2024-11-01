
import {Route, Routes} from "react-router-dom"
import UserRouter from "./UserRouter";
import LayoutPage from "../components/layouts/LayoutPage";
import HomePage from "../pages/home/HomePage";
import Header from "../components/layouts/Header";
import Auth from "../pages/authentication/Index"
import NotFoundPage from "../pages/notfound/NotFoundPage";
const AppRoutes = () =>{
    return(
        <>
            <Routes>

                <Route path="/" element={<UserRouter><LayoutPage/></UserRouter>} >
                    <Route index element={<HomePage/>} />
                </Route>

                <Route path="/login" element={<UserRouter><LayoutPage/></UserRouter>} >
                    <Route index element={<Auth/>} />
                </Route>

                <Route path="*" element={<NotFoundPage/>}/>

            </Routes>
        </>
    )
}

export default AppRoutes;