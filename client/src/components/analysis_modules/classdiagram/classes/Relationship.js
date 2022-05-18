export default class Relationship {
    constructor(id, fromId, toId, order, size, type) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.order = order;
        this.size = size;
        this.type = type;

        this.height = 2;
        this.edgeWidth = 25;
    }

    adjust(percentage) {
        if(percentage !== undefined)
            this.edgeWidth += this.edgeWidth * percentage;   
    }

    getProperties() {
        return [this.id, this.fromId, this.toId, this.order, this.size, this.type, this.edgeWidth];
    }
}