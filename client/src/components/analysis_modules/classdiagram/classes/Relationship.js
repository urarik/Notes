export default class Relationship {
    constructor(id, fromId, toId, order, size, type, height = 2, edgeWidth = 25) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.order = order;
        this.size = size;
        this.type = type;

        this.height = height;
        this.edgeWidth = edgeWidth;
    }

    static getInstanceFromSave({id, fromId, toId, theOrder, size, type, height, edgeWidth}) {
        return new Relationship(id, fromId, toId, theOrder, size, type, height, edgeWidth);
    }

    adjust(percentage) {
        if(percentage !== undefined)
            this.edgeWidth += this.edgeWidth * percentage;   
    }

    getProperties() {
        return [this.id, this.fromId, this.toId, this.order, this.size, this.type, this.edgeWidth];
    }
}