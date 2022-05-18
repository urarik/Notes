import axios from "axios";
import { useEffect, useState } from "react";
import { get } from "../../api/api";
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';


export default function({name, url, setVisibility}) {
    const [code, setCode] = useState("");

    useEffect(async () => {
        const response = await get('analyze/code', {url});
        if(response.status == 200)
            setCode(response.data);
    });

    return (
        <div className="codeview-container">
            <div className="codeview">
                <div className="name">{name}</div>
                <SyntaxHighlighter language="java">
                    {(code === "")? ". . .": code}
                </SyntaxHighlighter>
                <div className="close-button" onClick={() => setVisibility(false)}>X</div>
            </div>
        </div>
    );
}