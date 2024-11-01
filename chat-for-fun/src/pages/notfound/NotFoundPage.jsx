import React from 'react';

import { Helmet , HelmetProvider } from "react-helmet-async";

const NotFoundPage = () => {
    return (
        <HelmetProvider>
            <Helmet>
                <link href="/css/notfound.css" rel="stylesheet" />
            </Helmet>

        </HelmetProvider>
    )
}

export default NotFoundPage;