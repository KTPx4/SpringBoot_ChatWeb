import { useState, useEffect } from 'react';
import axios from 'axios';

const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';

const useAuth = (token) => {
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        const verifyToken = async () => {
            if (!token) {
                setIsAuthenticated(false);
                setLoading(false);
                return;
            }

            const url = `${SERVER}/account/verify`;

            try {
                const res =  await axios({
                    url: url,
                    method: "get",
                    headers:{
                        authorization: `Bearer ${token}`,
                        // "Content-Type": "application/json",
                    },

                })

                setIsAuthenticated(res.status === 200);

            } catch (err) {
                setIsAuthenticated(false);
            } finally {
                setLoading(false);
            }
        };

        verifyToken();
    }, [token]);

    return { isAuthenticated, loading };
};

export default useAuth;
