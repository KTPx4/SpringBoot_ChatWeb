import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from "react-router-dom";
import { Alert } from "react-bootstrap";
import Spinner from "react-bootstrap/Spinner";
import axios from "axios";
import { message } from "antd";

const ResetPage = () => {
    const SERVER = process.env.REACT_APP_SERVER || 'http://localhost:8080/api/v1';
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token") || "";
    const [isLoading, setIsLoading] = useState(true);
    const [AlertCustom, setAlertCustom] = useState({
        title: "",
        variant: "warning",
    });
    const [second, setSecond] = useState(10);

    const navigate = useNavigate(); // React Router hook để chuyển hướng

    useEffect(() => {
        if (!token) {
            setIsLoading(false);
            setAlertCustom({
                title: "Invalid Token",
                variant: "danger",
            });
        } else {
            validToken(token);
        }
    }, []);

    const sendServer = async (action, method, data) => {
        try {
            const url = `${SERVER}/${action}`;
            const res = await axios({
                url: url,
                method: method,
                headers: {
                    authorization: `Bearer ${token}`,
                },
                data: data,
            });

            return res;

        } catch (err) {
            console.log(err?.response);
            return err?.response;
        }
    };

    const validToken = async (token) => {
        var action = `account/reset?token=${token}`;
        var method = "get";
        var data = null;
        var res = await sendServer(action, method, data);
        if (res.status && res.status === 200) {
            setIsLoading(false);
            setAlertCustom({
                title: "Reset Success",
                variant: "success",
            });
        } else if (res.status && res.status !== 200) {
            message.error(res.data?.message ?? "Failed to connect Server");
            setIsLoading(false);
            setAlertCustom({
                title: "Reset Failed - " + res.data?.message ?? "",
                variant: "danger",
            });
        }
    };

    // Countdown logic and redirection
    useEffect(() => {
        if (!isLoading) {
            const timer = setInterval(() => {
                setSecond((prev) => {
                    if (prev <= 1) {
                        clearInterval(timer);
                        navigate("/"); // Redirect to "/"
                        return 0;
                    }
                    return prev - 1;
                });
            }, 1000);

            return () => clearInterval(timer); // Clear timer when component unmounts
        }
    }, [isLoading, navigate]);

    return (
        <>
            {isLoading && (
                <div className="d-flex justify-content-center">
                    <Spinner animation="border" variant="info" />
                </div>
            )}
            {!isLoading && (
                <div className={"d-flex justify-content-center"} style={{ flexDirection: "column" }}>
                    <Alert className={"d-flex justify-content-center"} variant={AlertCustom.variant}>{AlertCustom.title}</Alert>
                    <i className={"d-flex justify-content-center"}>{`Redirect after: ${second} seconds`}</i>
                </div>
            )}
        </>
    );
};

export default ResetPage;
