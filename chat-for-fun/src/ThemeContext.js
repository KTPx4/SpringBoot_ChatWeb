// ThemeContext.js
import React, { createContext, useState, useEffect } from 'react';
import ThemeManager from './ThemeManager';

export const ThemeContext = createContext();

export const ThemeProvider = ({ children }) => {
    const [currentTheme, setCurrentTheme] = useState(ThemeManager);

    // Lấy theme từ localStorage khi ứng dụng khởi động
    useEffect(() => {
        const savedTheme = localStorage.getItem('app_theme');
        if (savedTheme) {
            ThemeManager.setTheme(savedTheme);
            setCurrentTheme({ ...ThemeManager });
        }
    }, []);

    // Hàm để thay đổi theme và cập nhật vào localStorage
    const changeTheme = (themeName) => {
        ThemeManager.setTheme(themeName);
        setCurrentTheme({ ...ThemeManager }); // Cập nhật để re-render
    };

    return (
        <ThemeContext.Provider value={{ currentTheme, changeTheme }}>
            {children}
        </ThemeContext.Provider>
    );
};
