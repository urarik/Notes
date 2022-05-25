import SpaceObject from "../../SpaceObject";

const lineWidth = 8;

export default class LifeLine extends SpaceObject {
    constructor(name, absLeft, absTop, absW, absH, eid, plane) {
        super(absLeft, absTop, absW, absH, plane);
        
        this.name = name;
        this.id = eid;
        this.lineWidth = 8;

        this.messages = [];
        this.fragments = [];
    }

    addMsg(message) {
        this.messages.push(message);
    }

    addFragment(fragment) {
        this.fragments.push(fragment);
    }
    move(left, top) {
        super.move(left,top);
        for(const key in this.plane.fragments) {
            this.plane.fragments[key].adjust();
        }
        for(const key in this.messages) {
            this.messages[key].adjust();
        }
    }
    moveEnd(left, top) {
        super.moveEnd(left, top);
        for(const key in this.plane.fragments) {
            this.plane.fragments[key].moveEnd();
        }
        for(const key in this.messages) {
            this.messages[key].moveEnd();
        }
    }
    
    adjust(percentage) {
        super.adjust();
        if(percentage !== undefined) {
            this.lineWidth += lineWidth * percentage;
            // this.fontSize += fontSize * percentage;
            for(const key in this.messages) {
                this.messages[key].adjust(percentage);
            }
        } else {
            for(const key in this.messages) {
                this.messages[key].adjust();
            }
        }
    }
} 