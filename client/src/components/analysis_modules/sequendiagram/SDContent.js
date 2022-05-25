import { useRef } from "react";
import { useDispatch } from "react-redux";
import { activeSdMoving, activeSdPoint, activeSdResizing } from "../../../actions";
import SDPlane from "./classes/SDPlane";

export default function({plane, setPlane, children}) {
    const dispatch = useDispatch();
    const moving = useRef(false);
    const startPoint = useRef([]);
    const startPosition = useRef([]);
    
    const handleClick = () => {
        dispatch(activeSdMoving(-1));
        dispatch(activeSdResizing(undefined));
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
        dispatch(activeSdPoint([e.clientX, e.clientY]));
        if(moving.current) {
            const [newLeft, newTop] = processMoving(e);

            plane.move(newLeft, newTop);
            // console.log(newLeft);
            // console.log(plane.lifeLines.UserRepository.relLeft);
            setPlane(SDPlane.getInstanceFromAnother(plane));
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

            plane.move(newLeft, newTop);
            setPlane(SDPlane.getInstanceFromAnother(plane));
            moving.current = false;
        }
    }


    const handleZoom = (_, type) => {
        if(type === "in") plane.zoomIn(0.1);
        else plane.zoomOut(0.1);

        setPlane(SDPlane.getInstanceFromAnother(plane));
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