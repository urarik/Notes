import { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

const delta = 6;
const squareSize = 8;

export default function({entity, setEntity, children, selector, activeResizing, onlyHorizontal}) {
    const startPoint = useRef([]);
    const resizeStartPoint = useRef([]);
    const resizeStartSize = useRef([]);
    const resizeDir = useRef("");
    const resizing = useRef(false);
    const [resize, setResize] = useState(false);
    const { resizingId, point } = useSelector(selector);
    const dispatch = useDispatch();

    const squareStyle = {
        width: squareSize,
        height: squareSize,
        backgroundColor: 'white',
        border: '1px solid black'
    };

    const leftTopStyle = {
        position: 'absolute',
        top: 0 - squareSize / 2,
        left: 0 - squareSize / 2,
        cursor: 'nwse-resize',
        ...squareStyle  
    };

    const rightTopStyle = {
        position: 'absolute',
        top: 0 - squareSize / 2,
        left: entity.relW - squareSize / 2,
        cursor: 'nesw-resize',
        ...squareStyle  
    };

    const leftBottomStyle = {
        position: 'absolute',
        top: entity.relH - squareSize / 2,
        left: 0 - squareSize / 2,
        cursor: 'nesw-resize',
        ...squareStyle  
    };

    const rightBottomStyle = {
        position: 'absolute',
        top: entity.relH - squareSize / 2,
        left: entity.relW - squareSize / 2,
        cursor: 'nwse-resize',
        ...squareStyle  
    };

    const handleMouseDown = (e) => {
        startPoint.current = [e.pageX, e.pageY];
    };
    const handleMouseUp = (e) => {
        const diffX = Math.abs(e.pageX - startPoint.current[0]);
        const diffY = Math.abs(e.pageY - startPoint.current[1]);
      
        if (diffX < delta && diffY < delta) {
            setResize(true);
            dispatch(activeResizing(entity.id));
        }
    };

    const handleResizeStart = (e, dir) => {
        e.stopPropagation();
        e.preventDefault();

        if(onlyHorizontal === true && dir === 0b00 || dir === 0b10) return;

        resizeStartPoint.current = [e.clientX, e.clientY];
        resizeStartSize.current = [entity.relW, entity.relH];
        resizing.current = true;
        resizeDir.current = dir;
    };

    useEffect(() => {
        if(resizingId === undefined || resizingId !== entity.id) setResize(false);
    }, [resizingId]);

    const processResizing = () => {
        const dir = resizeDir.current;
        const [clientX, clientY] = point;
        const dWidth = ((dir & (1 << 1)) !== 0)?  // right
                            clientX - resizeStartPoint.current[0]:
                            resizeStartPoint.current[0] - clientX;

        const dHeight = ((dir & (1 << 0)) !== 0)?  // bottom
                            clientY - resizeStartPoint.current[1]:
                            resizeStartPoint.current[1] - clientY;

        const nWidth = resizeStartSize.current[0] + dWidth;
        const nHeight = resizeStartSize.current[1] + dHeight;

        return [nWidth, nHeight];
    }

    //dir - (0/1 - left/right) + (0/1 - top/bottom)
    useEffect(() => {
        if(resizing.current && resizingId !== undefined && resizingId === entity.id) {
            const dir = resizeDir.current;
            const [nWidth, nHeight] = processResizing();

            if(onlyHorizontal === true) entity.resize(nWidth, entity.relH, dir);
            else entity.resize(nWidth, nHeight, dir);
            setEntity(entity);
        }
    }, [point]);


    const handleResizingEnd = (e, dir) => {
        e.stopPropagation();
        e.preventDefault();

        if(resizingId === entity.id) {
            const dir = resizeDir.current;
            const [nWidth, nHeight] = processResizing();

            if(onlyHorizontal === true) entity.resizeEnd(nWidth, entity.relH, dir);
            else entity.resizeEnd(nWidth, nHeight, dir);
            setEntity(entity)
            resizing.current = false;
        }
    }

    const handleClick = (e) => {
        e.stopPropagation();
        e.preventDefault();
    }

    return (
        <div
             onMouseDown={handleMouseDown}
             onMouseUp={handleMouseUp}>
            {children}
            
            {
                resize && (<div>
                    <div style={leftTopStyle} 
                        onMouseDown={e => handleResizeStart(e, 0b00)}
                        onMouseUp={e => handleResizingEnd(e, 0b00)}
                        onClick={handleClick}> </div>
                    <div style={rightTopStyle} 
                        onMouseDown={e => handleResizeStart(e, 0b10)}
                        onMouseUp={e => handleResizingEnd(e, 0b10)}
                        onClick={handleClick}> </div>
                    <div style={leftBottomStyle} 
                        onMouseDown={e => handleResizeStart(e, 0b01)}
                        onMouseUp={e => handleResizingEnd(e, 0b01)}
                        onClick={handleClick}> </div>
                    <div style={rightBottomStyle} 
                        onMouseDown={e => handleResizeStart(e, 0b11)}
                        onMouseUp={e => handleResizingEnd(e, 0b11)}
                        onClick={handleClick}> </div>
                </div>)
            }
        </div>
    )
}