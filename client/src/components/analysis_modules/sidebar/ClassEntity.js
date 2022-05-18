import { useState } from "react";
import { useDispatch } from "react-redux";
import { activeClass } from "../../../actions";
import CodeView from "../CodeView";

export default function({ classEntity }) {
    const dispatch = useDispatch();
    const [hiding, setHiding] = useState("hiding");
    const [code, setCode] = useState(false);
    const hide = () => {
        setHiding("hiding");
    }
    const display = () => {
        setHiding("")
    }
    const displayCode = () => {
        setCode(true);
    }
    const active = () => {
        dispatch(activeClass({
            id: classEntity.id,
            name: classEntity.properties.name
        }));
    }

    const renderIcon = (label) => {
        switch(label) {
            case 'Class':
                 return <i className='bx bx-copyright'></i>;
            case 'Interface':
                return <i className='bx bx-info-circle' ></i>;
            default:
                return "";
        }
    };

    return (
        <div>
            <div className="classEntity" onMouseEnter={() => display()} onMouseLeave={() => hide()} onClick={() => active()}>
                <div>
                    {renderIcon(classEntity.label[0])}  
                    <span className="name">{classEntity.properties.name}</span>
                </div>
                <i className={`bx bx-dots-horizontal-rounded ${hiding}`} onClick={() => displayCode()}></i>
            </div>

            {
                code && <CodeView 
                            name={classEntity.properties.name} 
                            url={classEntity.properties.url}
                            setVisibility={setCode}></CodeView>
            }
        </div>
    );
}