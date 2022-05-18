import ClassDiagramRelationshipEdge from "./ClassDiagramRelationshipEdge";

function toDegrees (angle) {
    return angle * (180 / Math.PI);
}
function toRadians (angle) {
    return angle * (Math.PI / 180);
}

export default function({entities, relationship}) {
    const [id, fromId, toId, order, size, type, width] = relationship.getProperties();

    const fromEntity = entities[fromId];
    const toEntity = entities[toId];
    if(fromEntity === undefined || toEntity === undefined) return (<></>);
    const fromCenter = [
        fromEntity.rel_left + fromEntity.rel_w / 2,
        fromEntity.rel_top + fromEntity.rel_h / 2
    ];
    const toCenter = [
        toEntity.rel_left + toEntity.rel_w / 2,
        toEntity.rel_top + toEntity.rel_h / 2
    ];

    const [fromDegree, toDegree] = getDegree(fromCenter, toCenter);

    const fromStart = [fromEntity.rel_left, fromEntity.rel_top];
    const fromEnd = [fromEntity.rel_left + fromEntity.rel_w, fromEntity.rel_top + fromEntity.rel_h];
    const toStart = [toEntity.rel_left, toEntity.rel_top];
    const toEnd = [toEntity.rel_left + toEntity.rel_w, toEntity.rel_top + toEntity.rel_h];

    const fromStartPoint = adjust(getStartPoint(fromDegree,
                                fromCenter,
                                fromStart,
                                fromEnd),
                                fromDegree, order, size, fromEntity, (degree) => getStartPoint(degree, fromCenter, fromStart, fromEnd));

    const toStartPoint = adjust(getStartPoint(toDegree,
                                toCenter,
                                toStart,
                                toEnd),
                                toDegree, order, size, toEntity, (degree) => getStartPoint(degree, toCenter, toStart, toEnd));

    const length = Math.sqrt((fromStartPoint[0] - toStartPoint[0]) ** 2 + (fromStartPoint[1] - toStartPoint[1]) ** 2);

    const style = {
        position: 'absolute',
        left: fromStartPoint[0],
        top: fromStartPoint[1],
        width: length,
        height: relationship.height,
        transform: `rotate(${360 - fromDegree}deg)`,
        transformOrigin: '0% 0%'
    };

    const typeClass = (type === 'Realization' || type === 'Dependency')? 'dashed': 'general';
    return (
        <>
        {/* <div style={{
            position: 'absolute',
            left: fromStartPoint[0],
            top: fromStartPoint[1],
            width: 15,
            height: 15,
            backgroundColor: "black",
            borderRadius: 15
        }}></div>
        <div style={{
            position: 'absolute',
            left: toStartPoint[0],
            top: toStartPoint[1],
            width: 15,
            height: 15,
            backgroundColor: "red",
            borderRadius: 15
        }}></div> */}
        <div className='relationship-container' style={style}>
            <div className={`relationship ${typeClass}`}></div>
            <ClassDiagramRelationshipEdge type={type} point={toStartPoint} width={width} />
        </div>
        </>
    )
}

function getDegree(fromCenter, toCenter) {    
    let fromDegree, toDegree;
    const x = Math.abs(fromCenter[0] - toCenter[0]);
    const y = Math.abs(fromCenter[1] - toCenter[1]);
    const degree = toDegrees(Math.atan(y/x));
    if(fromCenter[0] > toCenter[0] && fromCenter[1] > toCenter[1]) {
        toDegree = 360 - degree;
        fromDegree = 180 - degree;
    }
    else if(fromCenter[0] > toCenter[0] && fromCenter[1] < toCenter[1]) {
        toDegree = degree;
        fromDegree = 180 + degree;
    }
    else if(fromCenter[0] < toCenter[0] && fromCenter[1] > toCenter[1]) {
        fromDegree = degree;
        toDegree = 180 + degree;
    }
    else if(fromCenter[0] < toCenter[0] && fromCenter[1] < toCenter[1]) {
        fromDegree = 360 - degree;
        toDegree = 180 - degree;
    }
    else if(fromCenter[0] === toCenter[0]) {
        if(fromCenter[1] > toCenter[1]) {
            fromDegree = 90;
            toDegree = 270;
        } else {
            toDegree = 90;
            fromDegree = 270;
        }
    }
    else if(fromCenter[1] === toCenter[1]) {
        if(fromCenter[0] > toCenter[0]) {
            fromDegree = 180;
            toDegree = 0;
        } else {
            toDegree = 0;
            fromDegree = 180;
        }
    }

    return [fromDegree, toDegree];
}


// degree, center, start, end of the entitiy.
function getStartPoint(degree, center, start, end) {
    const [left1, top1] = center;
    const [left2, top2] = start;
    const [left3, top3] = end;

    const height = top3 - top2;
    const width = left3 - left2;
    if(degree === 0)
        return [left3, top1];
    else if(degree === 90)
        return [left1, top2];
    else if(degree === 180)
        return [left2, top1];
    else if(degree === 270) 
        return [left1, top3];
    else if(degree < 90) {
        let top = (width / 2) * Math.tan(toRadians(degree));
        if(top > height / 2) top = height / 2;
        
        const left = top * Math.tan(toRadians(90 - degree)) + left1;
        top = top1 - top;
        return [left, top];
    }
    else if(degree < 180) {
        const nDegree = degree - 90;
        let left = (height / 2) * Math.tan(toRadians(nDegree));
        if(left > width / 2) left = width / 2;

        const top = top1 - left * Math.tan(toRadians(90 - nDegree));
        left = left1 - left;
        return [left, top];
    }
    else if(degree < 270) {
        const nDegree = degree - 180;
        let top = (width / 2) * Math.tan(toRadians(nDegree));
        if(top > height / 2) top = height / 2;

        const left = left1 - top * Math.tan(toRadians(90 - nDegree));
        top = top + top1;

        return [left, top];
    }
    else if(degree < 360) {
        const nDegree = degree - 270;
        let left = (height / 2) * Math.tan(toRadians(nDegree));
        if(left > width / 2) left = width / 2;

        const top = left * Math.tan(toRadians(90 - nDegree)) + top1;
        left = left + left1;
        return [left, top];
    }

    return [-1, -1];
}

function adjust(point, degree, order, size, entity, adjustPoint)  {
    if(size === 1) return point;
    else {
        const edgeDegree = toDegrees(Math.atan((entity.rel_h/2) / (entity.rel_w/2)));
        if(degree >= edgeDegree && degree < 180 - edgeDegree) {
            const newLeft = point[0] - order * 20;
            const boundary = entity.rel_left;
            if(newLeft < boundary)
                return adjust(adjustPoint(180 - edgeDegree + 1), 180 - edgeDegree + 1, order, size, entity);
            
            return [newLeft, point[1]];
        } 
        else if(degree >= 180 - edgeDegree && degree < 180 + edgeDegree) {
            const newTop = point[1] + order * 20;
            const boundary = entity.rel_top + entity.rel_h;
            if(newTop > boundary)
                return adjust(adjustPoint(edgeDegree + 180 + 1), edgeDegree + 180 + 1, order, size, entity);
            return [point[0], newTop];
        }
        else if(degree >= 180 + edgeDegree && degree < 360 - edgeDegree) {
            const newLeft = point[0] - order * 20;
            const boundary = entity.rel_left + entity.rel_w;
            if(newLeft > boundary)
                return adjust(adjustPoint(360 - edgeDegree + 1), 360 - edgeDegree + 1, order, size, entity);
            return [newLeft, point[1]];
        }
        else {
            const newTop = point[1] + order * 20;
            const boundary = entity.rel_top;
            if(newTop < boundary)
                return adjust(adjustPoint(edgeDegree + 1), edgeDegree + 1, order, size, entity);
            return [point[0], point[1] + order * 20];
        }
    }
}

function getDir(degree, edgeDegree) {
    if(degree >= edgeDegree && degree < 180 - edgeDegree)
        return 0; // top
    if(degree >= 180 - edgeDegree && degree < 180 + edgeDegree)
        return 1; // right
    if(degree >= 180 + edgeDegree && degree < 360 - edgeDegree)
        return 2; // bottom
    else return 3; // left
}