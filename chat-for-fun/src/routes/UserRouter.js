import React from 'react';
import { Navigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

const UserRouter = ({ children }) => {
    const token = localStorage.getItem('token-auth') || '';

    const { isAuthenticated, loading } = useAuth(token);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" />;
    }

    return children;
};

export default UserRouter;
