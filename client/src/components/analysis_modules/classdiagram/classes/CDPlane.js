import Plane from "../../Plane";

export default class CDPlane extends Plane {

    //left, top, w, h는 view 기준
    //container 안에 view가 있고 view가 화면에 보이는 지역
    constructor(pid, left, top, w, h, containerW, containerH, id = null) {
        super(pid, left, top, w, h, containerW, containerH, id);
        this.entities = {};
        this.rels = {};
    }
    static getInstanceFromSave({id, pid, viewLeft, viewTop, viewW, viewH, containerW, containerH, name, fontSize}) {
        const plane = new CDPlane(pid, viewLeft, viewTop, viewW, viewH, containerW, containerH, id);
        plane.setName(name);
        plane.fontSize = fontSize;
        return plane;
    }

    subscribe(entities) {
        this.entities = entities;
    }
    subscribeRel(relationship) {
        this.rels[relationship.id] = relationship;
    }

    zoomIn(percent) {
        super.zoomIn(percent);
        for(const [key, entity] of Object.entries(this.entities))
            entity.adjust(percent);
        for(const [key, rel] of Object.entries(this.rels))
            rel.adjust(percent);

    }
    zoomOut(percent) {
        super.zoomOut(percent);
        for(const [key, entity] of Object.entries(this.entities))
            entity.adjust(percent);
        for(const [key, rel] of Object.entries(this.rels))
            rel.adjust(-percent);            
    }

    move(left, top) {
        this.left = left;
        this.top = top;
        for(const key in this.entities) {
            this.entities[key].adjust();
        }
    }

    toJson() {
        const ret = super.toJson();

        const entitySet = [];
        for(const id in this.entities) {
            const entity = this.entities[id];
            entitySet.push({
                id: entity.id,
                absLeft: entity.absLeft,
                absTop: entity.absTop,
                absW: entity.absW,
                absH: entity.absH
            });
        }
        ret['entitySet'] = entitySet;

        const relationshipSet = [];
        for(const id in this.rels) {
            const rel = this.rels[id];
            relationshipSet.push({
                id: rel.id,
                fromId: rel.fromId,
                toId: rel.toId,
                theOrder: rel.order,
                size: rel.size,
                type: rel.type,
                height: rel.height,
                edgeWidth: rel.edgeWidth
            });
        }
        ret['relationshipSet'] = relationshipSet;
        
        return ret;
    }
}