import SpaceObject from "../../SpaceObject";

export default class Fragment extends SpaceObject{
    constructor(id, absTop, absLeft, absW, absH, type, content, plane, fromId, toId) {
        super(absLeft, absTop, absW, absH, plane, null);

        this.id = id;
        this.type = type;
        this.content = content;

        this.fromId = fromId;
        this.toId = toId;
    }
    
    static getInstanceFromSave(id, {absTop, absW, type, content, fromId, toId, absLeft, absH}, plane) {
        return new Fragment(id, absTop, absLeft, absW, absH, type, content, plane, fromId, toId);
    }

    setAbsW(absW) {
        this.absW = absW;
    }
    setAbsH(absH) {
        this.absH = absH;
    }

    adjust() {
        if(this.fromId !== undefined) { // constructor때는 실행 X
            const fromLifeLine = this.plane.lifeLines[this.fromId];
            const toLifeLine = this.plane.lifeLines[this.toId];

            this.relLeft = fromLifeLine.relLeft;
            this.relW = (toLifeLine.relLeft - fromLifeLine.relLeft) + toLifeLine.relW;
        } else {
            this.relLeft = (this.absLeft - this.plane.left) * this.plane.ratioW;
            this.relW = (this.absW) * this.plane.ratioW;
        }

        const oRelH = this.relH;
        // console.log(`${this.id}: ${this.absTop}, ${this.plane.top}, ${this.plane.ratioH}`);
        this.relTop = (this.absTop - this.plane.top) * this.plane.ratioH;
        this.relH = (this.absH) * this.plane.ratioH;

        //TODO: 최소 폰트 사이즈는 10px로 이 방법으론 이 이하로 줄어들지가 않음.
        // const ratio = this.relH / oRelH;
        // if(!isNaN(ratio)) {
        //     this.fontSize *= ratio;
        // }
    }

    moveEnd() {
        this.absLeft = (this.relLeft / this.plane.ratioW) + this.plane.left;
        this.absTop = (this.relTop / this.plane.ratioH) + this.plane.top;
    }
}