
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

    moveEnd() {
        this.absHeight = (this.relHeight / this.plane.ratioH) + this.plane.top;
    }

    adjust(percentage) {
        // console.log(((this.absHeight - this.plane.top) * this.plane.ratioH));
        if(percentage !== undefined)
            this.relHeight += ((this.absHeight - this.plane.top) * this.plane.ratioH) * percentage;
        else this.relHeight = (this.absHeight - this.plane.top) * this.plane.ratioH;

        if(this.id< 0) 
            console.log(this.relHeight);
        if(this.id == 3066) {
            // console.log(this.absHeight);
            // console.log(this.plane.top);
            // console.log(this.plane.ratioH);
            // console.log(this.relTop);
        }
    }
}