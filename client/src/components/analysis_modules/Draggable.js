import { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { activeMoving } from "../../actions";

export default function({entity, setEntity, children, style}) {
    const dragging = useRef(false);
    const startPoint = useRef([]);
    const { movingId, point } = useSelector(state => state.classDiagram);
    const dispatch = useDispatch();

    const handleMouseDown = (e) => {
        e.stopPropagation();
        e.preventDefault();

        // 이벤트 타겟이 entity가 아닌 child로 잡히기 때문에 조정해줘야함.
        // 조정하지 않고 offset를 쓰면 child element기준으로 offset이 된다.
        // 예) className="content"를 클릭했을 때
        dragging.current = true;
        dispatch(activeMoving(entity.id));
        const child = e.target.getBoundingClientRect();
        const parent = document.getElementById(entity.id).getBoundingClientRect();

        startPoint.current = [child.left - parent.left + e.nativeEvent.offsetX,
            child.top - parent.top + e.nativeEvent.offsetY];
    };

    const processMoving = () => {
        const [curX, curY] = getPoint(...point);
        const [offX, offY] = startPoint.current;

        const newX = curX - offX;
        const newY = curY - offY;

        return [newX, newY];
    }

    useEffect(() => {
        if(movingId == entity.id) {
            const [newX, newY] = processMoving();

            entity.move(newX, newY)
            setEntity(entity);
        }
    }, [point]);

    const handleMouseUp = (e) => {
        e.stopPropagation();
        e.preventDefault();

        if(movingId == entity.id) {
            const [newX, newY] = processMoving();

            entity.moveEnd(newX, newY);
            setEntity(entity);
            dragging.current = false;
            dispatch(activeMoving(-1));
        }
    }
    const getPoint = (clientX, clientY) => {
        var bounds = document.getElementById("classdiagram-content").getBoundingClientRect();
        return [clientX - bounds.left, clientY - bounds.top];
    }
    const handleClick = (e) => {
        e.stopPropagation();
        e.preventDefault();
    }

    return (
    <div className={`${dragging.current? 'dragging': ''}`} id={entity.id} style={style}
    onMouseDown={handleMouseDown} 
    onMouseUp={handleMouseUp}
    onClick={handleClick}>
        {children}
    </div>
    );
}