import Plane from "../../Plane";
import { height, msgOffset } from "../initSequence";

export default class SDPlane extends Plane {

    //left, top, w, h는 view 기준
    //container 안에 view가 있고 view가 화면에 보이는 지역
    constructor(pid, left, top, w, h, containerW, containerH, msgOffset, id = null) {
        super(pid, left, top, w, h, containerW, containerH, id);
        this.msgOffset = msgOffset;
        this.lifeLines = {};
        this.fragments = {};
    }
    static getInstanceFromSave({id, pid, viewLeft, viewTop, viewW, viewH, containerW, containerH, name}) {
        const plane = new SDPlane(pid, viewLeft, viewTop, viewW, viewH, containerW, containerH, id);
        plane.setName(name);
        return plane;
    }
    static getInstanceFromAnother(plane) {
        const nPlane = new SDPlane(plane.pid, plane.left, plane.top, plane.w, plane.h, plane.containerW, plane.containerH, plane.msgOffset, plane.id);

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

        // const entitySet = [];
        // for(const id in this.entities) {
        //     const entity = this.entities[id];
        //     entitySet.push({
        //         id: entity.id,
        //         absLeft: entity.absLeft,
        //         absTop: entity.absTop,
        //         absW: entity.absW,
        //         absH: entity.absH,
        //         fontSize: entity.fontSize
        //     });
        // }
        // ret['entitySet'] = entitySet;

        // const relationshipSet = [];
        // for(const id in this.rels) {
        //     const rel = this.rels[id];
        //     relationshipSet.push({
        //         id: rel.id,
        //         fromId: rel.fromId,
        //         toId: rel.toId,
        //         theOrder: rel.order,
        //         size: rel.size,
        //         type: rel.type,
        //         height: rel.height,
        //         edgeWidth: rel.edgeWidth
        //     });
        // }
        // ret['relationshipSet'] = relationshipSet;
        
        return ret;
    }
}