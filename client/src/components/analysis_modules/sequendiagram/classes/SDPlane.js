import Plane from "../../Plane";
import { height, msgOffset } from "../initSequence";

export default class SDPlane extends Plane {

    //left, top, w, h는 view 기준
    //container 안에 view가 있고 view가 화면에 보이는 지역
    constructor(pid, left, top, w, h, containerW, containerH, msgOffset, id = null, fontSize = null) {
        super(pid, left, top, w, h, containerW, containerH, id);
        this.msgOffset = msgOffset;
        this.lifeLines = {};
        this.fragments = {};
        if(fontSize !== null) this.fontSize = fontSize;
    }
    static getInstanceFromSave({id, pid, viewLeft, viewTop, viewW, viewH, containerW, containerH, name, lineLength, msgOffset, fontSize}) {
        const plane = new SDPlane(pid, viewLeft, viewTop, viewW, viewH, containerW, containerH, msgOffset, id, fontSize);
        plane.setName(name);
        plane.setLength(lineLength)
        return plane;
    }
    static getInstanceFromAnother(plane) {
        const nPlane = new SDPlane(plane.pid, plane.left, plane.top, plane.w, plane.h, plane.containerW, plane.containerH, plane.msgOffset, plane.id, plane.fontSize);
        nPlane.setName(plane.name);

        for(const key in plane.lifeLines) {
            plane.lifeLines[key].plane = nPlane;
            for(const mKey in plane.lifeLines[key].messages) {
                plane.lifeLines[key].messages[mKey].plane = nPlane;
            }
        }
        for(const key in plane.fragments) {
            // console.log(plane.fragments[key].absW);
            plane.fragments[key].plane = nPlane;
        }
        nPlane.subscribeLifes(plane.lifeLines);
        nPlane.subscribeFrags(plane.fragments);
        nPlane.setLength(plane.absLineLength);

        return nPlane;
    }

    setLength(lineLength) {
        this.absLineLength = lineLength;
        this.relLineLength = (this.absLineLength) * this.ratioH;
    }

    subscribeLifes(lifeLines) {
        this.lifeLines = lifeLines;
    }
    subscribeFrags(fragments) {
        this.fragments = fragments;
    }

    zoomIn(percent) {
        super.zoomIn(percent);
        for(const [key, lifeLine] of Object.entries(this.lifeLines))
            lifeLine.adjust(percent);
        for(const [key, fragment] of Object.entries(this.fragments))
            fragment.adjust(percent);
        this.lineLength += height * percent;
        this.msgOffset += msgOffset * percent;
    }
    zoomOut(percent) {
        super.zoomOut(percent);
        for(const [key, lifeLine] of Object.entries(this.lifeLines))
            lifeLine.adjust(-percent);
        for(const [key, fragment] of Object.entries(this.fragments))
            fragment.adjust(-percent);
        this.lineLength -= height * percent;
        this.msgOffset -= msgOffset * percent;
    }

    move(left, top) {
        this.left = left;
        this.top = top;
        this.relLineLength = (this.absLineLength) * this.ratioH;
        for(const key in this.lifeLines) {
            this.lifeLines[key].adjust();
        }
        for(const key in this.fragments) {
            this.fragments[key].adjust();
        }
    }

    toJson() {
        const ret = super.toJson();
        ret['msgOffset'] = msgOffset;
        ret['lineLength'] = this.absLineLength;

        const lifeLineSet = [];
        for(const key in this.lifeLines) {
            const lifeLine = this.lifeLines[key];
            const messageSet = [];
            for(const key in lifeLine.messages) {
                const message = lifeLine.messages[key];
                messageSet.push({
                    id: message.id,
                    fromCid: message.fromCid,
                    toCid: message.toCid,
                    methodName: message.methodName,
                    type: message.type,
                    absHeight: message.absHeight,
                    msg: message.msg
                });
            }

            lifeLineSet.push({
                id: lifeLine.id,
                absLeft: lifeLine.absLeft,
                absTop: lifeLine.absTop,
                absW: lifeLine.absW,
                absH: lifeLine.absH,
                name: lifeLine.name,
                url: lifeLine.url,
                messageSet: messageSet
            });
        }
        ret['lifeLineSet'] = lifeLineSet;

        const fragmentSet = [];
        for(const id in this.fragments) {
            const fragment = this.fragments[id];
            // id, absTop, absLeft, absW, absH, type, content, plane, fromId, toId
            fragmentSet.push({
                id: fragment.id,
                fromId: fragment.fromId,
                toId: fragment.toId,
                absTop: fragment.absTop,
                absLeft: fragment.absLeft,
                absW: fragment.absW,
                absH: fragment.absH,
                type: fragment.type,
                content: fragment.content
            });
        }
        ret['fragmentSet'] = fragmentSet;
        
        return ret;
    }
}