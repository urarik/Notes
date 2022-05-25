export const W = 400;
export const H = 300;
export const fontSize = 16;

export default class Plane {

    //left, top, w, h는 view 기준
    //container 안에 view가 있고 view가 화면에 보이는 지역
    constructor(pid, left, top, w, h, containerW, containerH, id = null) {
        this.pid = pid;
        this.left = left;
        this.top = top;
        this.w = w;
        this.h = h;
        this.containerW = containerW;
        this.containerH = containerH;

        this.ratioW = this.containerW / this.w;
        this.ratioH = this.containerH / this.h;

        this.id = id;
        this.fontSize = fontSize;
    }

    zoomIn(percent) {
        this.w = this.w - W * percent;
        this.h = this.h - H * percent;
        this.fontSize = this.fontSize + fontSize * percent;

        this.ratioW = this.containerW / this.w;
        this.ratioH = this.containerH / this.h;

    }
    zoomOut(percent) {
        this.w = this.w + W * percent;
        this.h = this.h + H * percent;
        this.fontSize = this.fontSize - fontSize * percent;

        this.ratioW = this.containerW / this.w;
        this.ratioH = this.containerH / this.h;
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
            containerW: this.containerW,
            containerH: this.containerH,
            name: this.name
        };
        if(this.id !== null) ret.id = this.id;

        return ret;
    }
}