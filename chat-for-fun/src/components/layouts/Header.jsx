import React from 'react';
import { Link } from 'react-router-dom';

function Header() {

    return (
        <nav>
            <ul>
                <li><Link to="/">Home</Link></li>
                <li><Link to="/login">login</Link></li>
            </ul>
        </nav>
    );
}

export default Header;
