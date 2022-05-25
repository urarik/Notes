import { useRef } from "react";
import { useParams } from "react-router";
import { activeSdMoving, activeSdResizing } from "../../../actions";
import Draggable from "../Draggable";
import Resizable from "../Resizable";

import chevron from '../../../images/chevron.svg';
import arrow from '../../../images/fill_arrow.svg';
import chevronLeft from '../../../images/chevron_left.svg';
import arrowLeft from '../../../images/fill_arrow_left.svg';
import { msgOffset } from "./initSequence";
import SpaceObject from "../SpaceObject";

export default function({lifeLine, setLifeLine, lifeLines, cid}) {
    const focusRef = useRef(null);
    const {id} = useParams();

    const style = {
        position: 'absolute',
        top: lifeLine.relTop,
        left: lifeLine.relLeft,
        width: lifeLine.relW,
        height: lifeLine.relH,
        cursor: 'pointer',
        zIndex: 9999
    };

    const lineStyle = {
        position: 'absolute',
        left: lifeLine.relLeft + lifeLine.relW / 2,
        top: lifeLine.relTop + lifeLine.relH,
        width: lifeLine.plane.relLineLength,
        height: 10,
        transform: `rotate(90deg)`,
        transformOrigin: '0% 0%'
    };

    const handleKeyDown = e => {
        if(e.key === "Delete") {
            setLifeLine(null);
        }
    }
    const handleOnClick = e => {
        focusRef.current.focus();
    };

    const renderSelfMessage = msg => {
        const left = lifeLine.relLeft + lifeLine.relW / 2;
        const relW = lifeLine.plane.msgOffset;

        const style = {
            position: 'absolute',
            left: left + lifeLine.relW / 8,
            top: msg.relHeight,
            width: relW,
            height: lifeLine.lineWidth/4,
            backgroundColor: 'black',
            transform: `rotate(90deg)`,
            transformOrigin: '0% 0%'
        };
        const topStyle = {
            position: 'absolute',
            left: left,
            top: msg.relHeight,
            width: lifeLine.relW / 8,
            height: lifeLine.lineWidth / 4,
            backgroundColor: 'black',
        };
        const bottomStyle = {
            position: 'absolute',
            left: left,
            top: msg.relHeight + relW,
            width: lifeLine.relW / 8,
            height: lifeLine.lineWidth / 4,
            backgroundColor: 'black',
        }
        const chevronStyle = {
            position: 'absolute',
            left: left,
            top: msg.relHeight + relW - relW / 5,
            width: relW / 2,
            height: relW / 2,
        }

        const msgStyle = {
            fontSize: lifeLine.plane.fontSize * 0.9,
            position: 'absolute',
            top: msg.relHeight + relW / 4,
            left: left + lifeLine.relW / 6,
            width: lifeLine.relW,
            height: lifeLine.plane.fontSize * 1.2
        };


        let msgStr;
        if(msg.methodName !== undefined)
            msgStr = `${msg.methodName}(${msg.msg})`;
        else msgStr = `${msg.msg}`;
        
        return (
            <div className="self-msg" key={msg.id}>
                <div style={style} ></div>
                <div style={topStyle}></div>
                <div style={bottomStyle}></div>
                <img src={chevronLeft} style={chevronStyle}/>
                <div style={msgStyle} className="no-drag sd-msg">{msgStr}</div>
            </div>
        )
    };

    const renderMessages = _ => {
        return lifeLine.messages.map(msg => {
            if(msg.toCid === cid && msg.fromCid == cid) return renderSelfMessage(msg);

            const to = lifeLines[msg.toCid];

            let width;
            if(msg.type !== "Reply")
                width = (to.relLeft + to.relW/2) - (lifeLine.relLeft + lifeLine.relW / 2);
            else width = (lifeLine.relLeft + lifeLine.relW / 2) - (to.relLeft + to.relW/2);

            let left;
            if(msg.type !== "Reply")
                left = lifeLine.relLeft + lifeLine.relW / 2;
            else left = to.relLeft + to.relW / 2;

            // if(msg.id < 0)
            // console.log(msg.relHeight);
            const style = {
                position: 'absolute',
                left,
                top: msg.relHeight,
                width,
                height: lifeLine.lineWidth/4
            };

            const pos = (msg.type === "Reply")? 'left': 'right';
            const arrowStyle = {
                position: 'absolute',
                [pos]: '0',
                top: -(lifeLine.lineWidth/(msg.type === "Reply"? 2: 2.5)),
                width: lifeLine.lineWidth,
                height: lifeLine.lineWidth
            };

            const msgStyle = {
                fontSize: lifeLine.plane.fontSize * 0.9,
                position: 'relative',
                top: -lifeLine.plane.fontSize * 1.3,
                height: lifeLine.plane.fontSize * 1.2
            };

            let src;
            if(msg.type === "Reply") src = chevronLeft;
            if(msg.type === "Sync") src = arrow;
            if(msg.type === "Async") src = chevron;

            let msgStr;
            if(msg.methodName !== undefined)
                msgStr = `${msg.methodName}(${msg.msg})`;
            else msgStr = `${msg.msg}`;
            

            return (
                <div style={style} key={msg.id} className={msg.type === "Reply"? 'dashed': 'black'} >
                    <div style={msgStyle} className="no-drag sd-msg">{msgStr}</div>
                    <img src={src} style={arrowStyle}/>
                </div>
            )
        })
    };

    let nameClass = "name ";
    return (
        <>
            <Draggable entity={lifeLine} 
                    setEntity={setLifeLine} 
                    style={style} 
                    content={"sd-content"}
                    selector={state => state.sd}
                    activeMoving={activeSdMoving}
                    onlyHorizontal={true}>
            <Resizable entity={lifeLine} 
                    setEntity={setLifeLine}
                    selector={state => state.sd}
                    activeResizing={activeSdResizing}
                    onlyHorizontal={true}>
                <div className="entity sd-entity" style={{width: lifeLine.relW, height: lifeLine.relH}} onKeyDown={handleKeyDown} tabIndex="1" onClick={handleOnClick} ref={focusRef}>
                    <div className={nameClass} style={{fontSize: lifeLine.plane.fontSize}}>{`:${lifeLine.name}`}</div>
                </div>
            </Resizable>
            </Draggable>
            <div className="dashed" style={lineStyle}></div>
            {renderMessages()}
        </>
    );
}