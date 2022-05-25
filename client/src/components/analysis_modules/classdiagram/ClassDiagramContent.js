import { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { activeCdMoving, activeCdResizing, activeCdPoint } from '../../../actions'

export default function({plane, editable, children, setEntities, entities}) {
    const dispatch = useDispatch();
    const moving = useRef(false);
    const startPoint = useRef([]);
    const startPosition = useRef([]);
    const { point } = useSelector(state => state.classDiagram);
    
    const handleClick = () => {
        dispatch(activeCdMoving(-1));
        dispatch(activeCdResizing(undefined));
    };

    const processMoving = e => {
        const [curX, curY] = [e.clientX, e.clientY];
        const [startX, startY] = startPoint.current;
        const [startLeft, startTop] = startPosition.current;
        const [ratioW, ratioH] = [plane.ratioW, plane.ratioH];

        const offsetX = startX - curX;
        const offsetY = startY - curY;

        const newLeft = startLeft + (offsetX / ratioW);
        const newTop = startTop + (offsetY / ratioH);

        return [newLeft, newTop];
    }

    const handleMove = e => {
        dispatch(activeCdPoint([e.clientX, e.clientY]));
        if(moving.current) {
            const [newLeft, newTop] = processMoving(e);

            plane.move(newLeft, newTop);
            setEntities({...plane.entities});
        }
    };

    const handleMouseDown = e => {
        if(plane !== null) {
            moving.current = true;
            startPoint.current = [e.clientX, e.clientY];
            startPosition.current = [plane.left, plane.top];
        }
    }

    const handleMouseUp = e => {
        if(moving.current) {
            const [newLeft, newTop] = processMoving(e);
            console.log(plane);
            plane.move(newLeft, newTop);
            setEntities({...plane.entities});
            moving.current = false;
        }
    }


    const handleZoom = (_, type) => {
        if(type === "in") plane.zoomIn(0.1);
        else plane.zoomOut(0.1);

        // 그냥 plane.entities를 넘기면 같은거라고 판단해서 수정이 안됨
        // 그래서 entities는 바뀐 상태인데 같은 거로 취급되기 때문에 entity를 수정해서
        //  entities가 바뀐 거라고 인식되면 그때 같이 확대/축소가 됨
        // -> 아마 setState에서 object equality를 검사할 때 deep comparison을 하지 않는듯.
        setEntities({...plane.entities});
    };

    return (
        <div className={`full ${moving.current? "dragging": ""}`}
             onClick={handleClick}
             onMouseMove={handleMove}
             onMouseDown={handleMouseDown}
             onMouseUp={handleMouseUp}>
            {children}

            <div className="adjust">
                <i className='bx bx-plus zoom-button'
                   onClick={e => handleZoom(e, "in")}></i>
                <i className='bx bx-minus zoom-button'
                   onClick={e => handleZoom(e, "out")}></i>
            </div>
        </div>
    );
}