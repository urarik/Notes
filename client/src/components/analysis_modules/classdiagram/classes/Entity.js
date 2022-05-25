import SpaceObject from '../../SpaceObject';

export default class Entity extends SpaceObject {
    constructor(absLeft, absTop, absW, absH, plane, entity, methods, members) {
        super(absLeft, absTop, absW, absH, plane);

        this.isStatic = entity.properties.isStatic;
        this.isAbstract = entity.properties.isAbstract;
        this.url = entity.properties.url;
        this.name = entity.properties.name;
        this.id = entity.id;
        this.type = entity.label[0];

        this.methods = methods;
        this.members = members;
    }

    static getInstanceFromSave(id, {isStatic, absTop, absW, name, absLeft, absH, type, isAbstract, Members, Methods, url}, plane) {
        return new Entity(absLeft, absTop, absW, absH, plane, {
            id: Number(id),
            label: [type],
            properties: {
                isStatic,
                isAbstract,
                url,
                name
            }
        }, Methods, Members);
    }

    adjust() {
        super.adjust();
    }
}