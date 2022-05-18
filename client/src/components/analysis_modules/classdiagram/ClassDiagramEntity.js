import { useMemo, useRef, useState } from "react";
import useDrag from "../../../hooks/useDrag";
import Draggable from "../Draggable";
import Resizable from "../Resizable";

export default function({entity, setEntity}) {
    const focusRef = useRef(null);

    const style = {
        position: 'absolute',
        top: entity.rel_top,
        left: entity.rel_left,
        width: entity.rel_w,
        height: entity.rel_h,
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

                return <div key={member.id} className={memberClass} style={{fontSize: entity.font_size}}>
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
                let methodClass = "contents ";
                if(method.isStatic) methodClass += "under-line ";

                return <div key={method.id} className={methodClass} style={{fontSize: entity.font_size}}>
                            {getModifierSymbol(method.visibility)}
                            &nbsp;
                            {method.name}
                            ({method.parameters})
                            &nbsp; : &nbsp;
                            {method.returnType}
                        </div>});
        }
        else return (<div className="empty-content"></div>);
    };

    const renderStereoType = () => {
        if(entity.type === "Interface") 
            return <div className="contents text-center" style={{fontSize: entity.font_size*0.9}}>
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
        <Draggable entity={entity} setEntity={setEntity} style={style}>
        <Resizable entity={entity} setEntity={setEntity} style={style}>
            <div className="entity" style={{width: entity.rel_w, height: entity.rel_h}} onKeyDown={handleKeyDown} tabIndex="1" onClick={handleOnClick} ref={focusRef}>
                {renderStereoType()}
                <div className={nameClass} style={{fontSize: entity.font_size * 1.25}}>{entity.name}</div>
                <hr className="line"/>
                {renderMembers()}
                <hr className="line"/>
                {renderMethods()}
            </div>
        </Resizable>
        </Draggable>
    );
}