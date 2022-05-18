export default class Plane {

    //left, top, w, h는 view 기준
    //container 안에 view가 있고 view가 화면에 보이는 지역
    constructor(pid, left, top, w, h, container_w, container_h) {
        this.pid = pid;
        this.left = left;
        this.top = top;
        this.w = w;
        this.h = h;
        this.container_w = container_w;
        this.container_h = container_h;
        this.entities = {};
        this.rels = {};

        this.ratio_w = this.container_w / this.w;
        this.ratio_h = this.container_h / this.h;
    }

    subscribe(entities) {
        this.entities = entities;
    }
    subscribeRel(relationship) {
        this.rels[relationship.id] = relationship;
    }

    zoomIn(percent) {
        this.w = this.w - this.w * percent;
        this.h = this.h - this.h * percent;

        this.ratio_w = this.container_w / this.w;
        this.ratio_h = this.container_h / this.h;
        for(const [key, entity] of Object.entries(this.entities))
            entity.adjust(percent);
        for(const [key, rel] of Object.entries(this.rels))
            rel.adjust(percent);

    }
    zoomOut(percent) {
        this.w = this.w + this.w * percent;
        this.h = this.h + this.h * percent;

        this.ratio_w = this.container_w / this.w;
        this.ratio_h = this.container_h / this.h;
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

    setName(name) {
        this.name = name;
    }

    toJson() {
        const ret = {
            pid: this.pid,
            viewLeft: this.left,
            viewTop: this.top,
            viewW: this.w,
            viewH: this.h,
            containerW: this.container_w,
            containerH: this.container_h,
            name: this.name
        };

        const entitySet = [];
        for(const id in this.entities) {
            const entity = this.entities[id];
            entitySet.push({
                id: entity.id,
                absLeft: entity.abs_left,
                absTop: entity.abs_top,
                absW: entity.abs_w,
                absH: entity.abs_h,
                fontSize: entity.font_size
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