import { useMemo, useRef, useState } from "react";
import { useLocation, useParams } from "react-router";
import Draggable from "../Draggable";
import Resizable from "../Resizable";
import arrow from '../../../images/arrow-right.svg';
import { Link } from "react-router-dom";
import { activeCdMoving, activeCdResizing } from "../../../actions";

export default function({entity, setEntity}) {
    const focusRef = useRef(null);
    const {id} = useParams();
    const location = useLocation();

    const style = {
        position: 'absolute',
        top: entity.relTop,
        left: entity.relLeft,
        width: entity.relW,
        height: entity.relH,
        cursor: 'pointer',
        zIndex: 9999
    };
    
    const getModifierSymbol = (modifier) => {
        if(modifier === "public") return "+";
        if(modifier === "private") return "-";
        if(modifier === "package-private") return "~";
        if(modifier === "protected") return "#";
    };
    
    const renderMembers = () => {
        if(entity.members.length !== 0) {
            return entity.members.map(member => {
                let memberClass = "contents ";
                if(member.isStatic) memberClass += "under-line ";

                return <div key={member.id} className={memberClass} style={{fontSize: entity.plane.fontSize}}>
                            {getModifierSymbol(member.visibility)}
                            &nbsp;
                            {member.name}
                            &nbsp; : &nbsp;
                            {member.type}
                        </div>});
        }
        else return (<div className="empty-content"></div>);
    };
    const renderMethods = () => {
        if(entity.methods.length !== 0) {
            return entity.methods.map(method => {
                const [hiding, setHiding] = useState("hiding");
                const ref = useRef();
                let methodClass = "contents ";
                if(method.isStatic) methodClass += "under-line ";
                
                const style = {
                    width: (ref.current)? ref.current.offsetHeight: 16,
                    borderRadius: (ref.current)? ref.current.offsetHeight: 16,
                    height: (ref.current)? ref.current.offsetHeight: 16
                };

                // const goSd = e => {
                //     e.preventDefault();
                //     e.stopPropagation();
                // };

                return (
                    <div key={method.id} className="method-container" 
                        onMouseEnter={_ => setHiding("")}
                        onMouseLeave={_ => setHiding("hiding")}>
                        <div className={methodClass} style={{fontSize: entity.plane.fontSize}} ref={ref}>
                                {getModifierSymbol(method.visibility)}
                                &nbsp;
                                {method.name}
                                ({method.parameters})
                                &nbsp; : &nbsp;
                                {method.returnType}
                        </div>
                        <Link className="go-sd-button" style={style}
                              to={`/project/${id}/sequencediagram/${method.id}`}>
                            <img src={arrow} className={`img ${hiding}`}></img>
                        </Link>
                    </div>
                    )
            });
        }
        else return (<div className="empty-content"></div>);
    };

    const renderStereoType = () => {
        if(entity.type === "Interface") 
            return <div className="contents text-center" style={{fontSize: entity.plane.fontSize*0.9}}>
                        {'<<interface>>'}
                    </div>
        return <></>;
    };

    const handleKeyDown = e => {
        if(e.key === "Delete") {
            setEntity(null);
        }
    }
    const handleOnClick = e => {
        focusRef.current.focus();
    };

    let nameClass = "name ";
    if(entity.isStatic) nameClass += "under-line ";
    if(entity.isAbstract) nameClass += "italic ";
    return (
        <Draggable entity={entity} 
                   setEntity={setEntity} 
                   style={style} 
                   content={"classdiagram-content"}
                   selector={state => state.classDiagram}
                   activeMoving={activeCdMoving}>
        <Resizable entity={entity} 
                   setEntity={setEntity}
                   selector={state => state.classDiagram}
                   activeResizing={activeCdResizing}>
            <div className="entity" style={{width: entity.relW, height: entity.relH}} onKeyDown={handleKeyDown} tabIndex="1" onClick={handleOnClick} ref={focusRef}>
                {renderStereoType()}
                <div className={nameClass} style={{fontSize: entity.plane.fontSize * 1.25}}>{entity.name}</div>
                <hr className="line"/>
                {renderMembers()}
                <hr className="line"/>
                {renderMethods()}
            </div>
        </Resizable>
        </Draggable>
    );
}