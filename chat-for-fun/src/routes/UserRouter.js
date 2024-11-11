import React from 'react';
import { Navigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import Spinner from "react-bootstrap/Spinner";

const UserRouter = ({ children }) => {
    const token = localStorage.getItem('token-auth') || '';

    const { isAuthenticated, loading } = useAuth(token);

    if (loading) {
        return   <div className="w-100 d-flex justify-content-center align-items-center">
                    <Spinner className="d-flex justify-content-center" animation="border" variant="info" />

                </div>
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" />;
    }

    return children;
};

export default UserRouter;
