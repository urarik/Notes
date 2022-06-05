import SpaceObject from "../../SpaceObject";
import Message from "./Message";

const lineWidth = 8;

export default class LifeLine extends SpaceObject {
    constructor(name, absLeft, absTop, absW, absH, eid, plane) {
        super(absLeft, absTop, absW, absH, plane);
        
        this.name = name;
        this.id = eid;
        this.lineWidth = 8;

        this.messages = [];
    }

    static getInstanceFromSave(id, {absTop, absW, name, absLeft, absH, messageSet}, plane) {
        const newLifLine = new LifeLine(name, absLeft, absTop, absW, absH, id, plane);
        for(const idx in messageSet) {
            newLifLine.addMsg(Message.getInstanceFromSave(messageSet[idx], plane));
        }
        return newLifLine;
    }

    addMsg(message) {
        this.messages.push(message);
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