import React, {useState, useEffect} from "react";
const UserRouter = ({children}) =>{
    const [result, setResult] = useState(null);

    useEffect(() => {

    }, []);

    if (result)
    {
        console.log("User Router - result run")
        return result;
    }

    return children;
}
export default UserRouter;
