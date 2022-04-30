import { useEffect, useState } from "react";
import NewWindow from "react-new-window";
import { get } from "../api/api";
import { Link } from "react-router-dom";
import Msg from "./Msg";

export default function({ setVisible }) {
    const [msgList, setMsgList] = useState({});
    const [isList, setIsList] = useState(true);
    const [msg, setMsg] = useState({});

    useEffect(async () => {
        const response = await get('/messages', {});
        console.log(response);
        if(response.status == 200)
        setMsgList(response.data);
    }, []);

    const renderMsgList = () => {
        return (Object.keys(msgList)
        .flatMap((k) => {
            if(k == "Invitation") {
                return msgList[k].map((msg) => {
                    const {id, title, sender, content} = msg;
                    const onClick = () => {
                        setIsList(false);
                        setMsg({...msg, type: "Invitation"});
                    }
                    return (
                        <tr key={id} className="item" onClick={() => onClick()}>
                            <td>{title}</td>
                            <td>{sender}</td>
                        </tr>
                    )
                });
            }
        }));
    };

    return (
        <NewWindow 
            onUnload={() => setVisible(false)}
            name="message"
            >
            <div className="msg-list-container">
                <h3>메시지</h3>
                {
                    isList
                    ? (
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>제목</th>
                                    <th>보낸이</th>
                                </tr>
                            </thead>
                            <tbody>
                                {renderMsgList()}
                            </tbody>

                        </table>
                    )
                    : <Msg msg={msg} toggle={() => setIsList(true)}/>
                }
            </div>
        </NewWindow>
    )
}