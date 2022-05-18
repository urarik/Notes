import SpaceObject from '../../SpaceObject';

export default class Entity extends SpaceObject {
    constructor(abs_left, abs_top, abs_w, abs_h, plane, font_size, entity, methods, members) {
        super(abs_left, abs_top, abs_w, abs_h, plane, font_size);

        this.isStatic = entity.properties.isStatic;
        this.isAbstract = entity.properties.isAbstract;
        this.url = entity.properties.url;
        this.name = entity.properties.name;
        this.id = entity.id;
        this.type = entity.label[0];

        this.methods = methods;
        this.members = members;
    }

    adjust() {
        super.adjust();
    }
}