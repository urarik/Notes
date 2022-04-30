import { post } from "../api/api";

export default function({msg, toggle}) {
    const {id, title, type, sender, content} = msg;
 
    console.log(type);

    const decide = async (decision) => {
        const response = await post('/messages/decide', {
            iid: id,
            accept: decision
        });

        if(response.status == 200) {
            toggle();
        } else console.log(response);
    };

    const renderDecision = () => {
        return(
            <div className="decision">
                <button className="btn btn-primary" onClick={() => decide(true)}>수락</button>
                <button className="btn btn-danger" onClick={() => decide(true)}>거절</button>
            </div>
        )
    };

    return (
        <div className="msg-container">
            <table className="table">
                <tbody>
                    <tr>
                        <td>제목</td>
                        <td>{title}</td>
                    </tr>
                    <tr>
                        <td>보낸사람</td>
                        <td>{sender}</td>
                    </tr>
                    <tr>
                        <td colSpan={2} className="content">
                            <div>{content}</div>
                            {
                                type == 'Invitation'? renderDecision(): null
                            }
                        </td>
                    </tr>
                </tbody>
            </table>
            <div className="footer">
                <button className="btn btn-primary" onClick={() => toggle()}>뒤로</button>
            </div>
        </div>
    )
}