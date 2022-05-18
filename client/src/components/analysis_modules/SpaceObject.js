export default class SpaceObject {
    constructor(abs_left, abs_top, abs_w, abs_h, plane, font_size) {
        this.abs_left = abs_left;
        this.abs_top = abs_top;
        this.abs_w = abs_w;
        this.abs_h = abs_h;
        this.plane = plane;
        this.font_size = font_size;
        
        this.adjust();
    }

    moveEnd(left, top) {
        this.move(left, top);
        this.abs_left = (this.rel_left / this.plane.ratio_w) + this.plane.left;
        this.abs_top = (this.rel_top / this.plane.ratio_h) + this.plane.top;
    }

    move(left, top) {
        this.rel_left = left;
        this.rel_top = top;
    }

    resize(nW, nH, dir) {

        if(dir === 0b00) { // Left Top
            this.rel_left += this.rel_w - nW;
            this.rel_top += this.rel_h - nH;
        } else if(dir === 0b01) { // Left Bottom
            this.rel_left += this.rel_w - nW;
        } else if(dir === 0b10) { // Right Top
            this.rel_top += this.rel_h - nH;
        } else if(dir === 0b11) { // Right Bottom
            //do nothing
        }

        this.rel_w = nW;
        this.rel_h = nH;
    }

    resizeEnd(nW, nH, dir) {
        this.resize(nW, nH, dir);

        this.abs_left = (this.rel_left / this.plane.ratio_w) + this.plane.left;
        this.abs_top = (this.rel_top / this.plane.ratio_h) + this.plane.top;

        this.abs_w = this.rel_w / this.plane.ratio_w;
        this.abs_h = this.rel_h / this.plane.ratio_h;
    }

    adjust() {
        const oRel_h = this.rel_h;
        this.rel_left = (this.abs_left - this.plane.left) * this.plane.ratio_w;
        this.rel_top = (this.abs_top - this.plane.top) * this.plane.ratio_h;
        this.rel_w = (this.abs_w) * this.plane.ratio_w;
        this.rel_h = (this.abs_h) * this.plane.ratio_h;

        const ratio = this.rel_h / oRel_h;
        if(!isNaN(ratio)) {
            this.font_size *= ratio;
        }
    }
}