
export default class Message {
    constructor(absHeight, type, methodName, msg, id, fromCid, toCid, plane) {
        this.absHeight = absHeight;
        this.type = type;
        this.methodName = methodName;
        this.msg = msg;
        this.id = id;
        this.fromCid = fromCid;
        this.toCid = toCid;
        this.plane = plane;

        this.relHeight = (this.absHeight - this.plane.top) * this.plane.ratioH;
    }
    static getInstanceFromSave({absHeight, type, methodName, msg, id, fromCid, toCid}, plane) {
        return new Message(absHeight, type, methodName, msg, id, fromCid, toCid, plane);
    }


    moveEnd() {
        this.absHeight = (this.relHeight / this.plane.ratioH) + this.plane.top;
    }

    adjust(percentage) {
        // console.log(((this.absHeight - this.plane.top) * this.plane.ratioH));
        if(percentage !== undefined)
            this.relHeight += ((this.absHeight - this.plane.top) * this.plane.ratioH) * percentage;
        else this.relHeight = (this.absHeight - this.plane.top) * this.plane.ratioH;
    }
}