export default class SpaceObject {
    constructor(absLeft, absTop, absW, absH, plane) {
        this.absLeft = absLeft;
        this.absTop = absTop;
        this.absW = absW;
        this.absH = absH;
        this.plane = plane;
        
        this.adjust();
    }

    moveEnd(left, top) {
        this.move(left, top);
        this.absLeft = (this.relLeft / this.plane.ratioW) + this.plane.left;
        this.absTop = (this.relTop / this.plane.ratioH) + this.plane.top;
    }

    move(left, top) {
        this.relLeft = left;
        this.relTop = top;
    }

    resize(nW, nH, dir) {

        if(dir === 0b00) { // Left Top
            this.relLeft += this.relW - nW;
            this.relTop += this.relH - nH;
        } else if(dir === 0b01) { // Left Bottom
            this.relLeft += this.relW - nW;
        } else if(dir === 0b10) { // Right Top
            this.relTop += this.relH - nH;
        } else if(dir === 0b11) { // Right Bottom
            //do nothing
        }

        this.relW = nW;
        this.relH = nH;
    }

    resizeEnd(nW, nH, dir) {
        this.resize(nW, nH, dir);

        this.absLeft = (this.relLeft / this.plane.ratioW) + this.plane.left;
        this.absTop = (this.relTop / this.plane.ratioH) + this.plane.top;

        this.absW = this.relW / this.plane.ratioW;
        this.absH = this.relH / this.plane.ratioH;
    }

    adjust() {
        const oRelH = this.relH;
        this.relLeft = (this.absLeft - this.plane.left) * this.plane.ratioW;
        this.relTop = (this.absTop - this.plane.top) * this.plane.ratioH;
        this.relW = (this.absW) * this.plane.ratioW;
        this.relH = (this.absH) * this.plane.ratioH;

        //TODO: 최소 폰트 사이즈는 10px로 이 방법으론 이 이하로 줄어들지가 않음.
        // const ratio = this.relH / oRelH;
        // if(!isNaN(ratio)) {
        //     this.fontSize *= ratio;
        // }
    }

    static relToAbs(rel, plane, dir) {
        if(dir === "w")
            return rel / plane.ratioW;
        else return rel / plane.ratioH;
    }
}