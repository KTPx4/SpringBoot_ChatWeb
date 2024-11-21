import { useState, useEffect } from 'react';
import axios from 'axios';
import useStore from "../store/useStore";

const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';

const useAuth = (token) => {
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const {id, setId, myAccount, setMyAccount} = useStore()

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
                const status = res.status;
                if(status === 200)
                {
                    const data = res.data.data
                    const id = data.id
                    setMyAccount(data)
                    setId(id)
                }
                setIsAuthenticated(res.status === 200);

            } catch (err) {
                console.log(err)
                if(err?.status === 401)
                {
                    alert(err?.response?.data)

                }
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
