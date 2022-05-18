import { useRef } from "react";

export default function(entity, setAfter) {
    let startPoint = [];
    let dragging = useRef(false);

    const onMouseDown = (e) => {
        dragging.current = true;
        startPoint = [e.nativeEvent.offsetX, e.nativeEvent.offsetY];
        console.log(startPoint);

    };
    const onMouseMove = (e) => {
        console.log(dragging);
        console.log(`..${startPoint}`);

        if(dragging.current) { 
            const [curX, curY] = getPoint(e);
            const [offX, offY] = startPoint;

            const newX = curX - offX;
            const newY = curY - offY;

            entity.move(newX, newY)
            setAfter(entity);
        }
    };
    const onMouseUp = (e) => {
        const [curX, curY] = getPoint(e);
        const [offX, offY] = startPoint;
        console.log(`${offX}, ${offY}`);

        const newX = curX - offX;
        const newY = curY - offY;

        entity.moveEnd(newX, newY);
        setAfter(entity);
        dragging.current = false;

        e.stopPropagation();
        e.preventDefault();
    }

    const getPoint = (e) => {
        var bounds = document.getElementById("classdiagram-content").getBoundingClientRect();
        return [e.clientX - bounds.left, e.clientY - bounds.top];
    }

    return [onMouseDown, onMouseMove, onMouseUp];
}