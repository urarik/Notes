import _ from 'lodash';
import SDPlane from './classes/SDPlane';
import Fragment from './classes/Fragment';
import Message from './classes/Message';
import LifeLine from './classes/LifeLine';
import SpaceObject from '../SpaceObject';
import { H, W } from '../Plane';

const horizontalOffset = 10;
export const msgOffset = 15;
export let height;
const lifeLineHeight = 15;
const fontRatio = 3.4;

export function initSequenceFromScratch(pid, ary, container) {
    const nAry = _.sortBy(ary, a => a.invokes.order);
    console.log(nAry);

    const mid = nAry[0].entity.id;

    const plane = new SDPlane(pid, 0, 0, W, H,
        container[0],
        container[1],
        msgOffset);

    const classes = {};
    let startLeft = 10, lastCid;
    for(const element of nAry) {
        const {name, id, url} = element.entity;
         if(!(id in classes)) {
            const absW = name.length * fontRatio + 8;
            classes[id] = new LifeLine(name, startLeft, 10, absW, lifeLineHeight, id, url, plane);

            lastCid = id;
            startLeft += absW + horizontalOffset;
        }
    }
    
    const stateStack = [];
    const fragments = {};

    height = msgOffset + 10 + lifeLineHeight;
    for(const element of nAry) {
        const {returnType} = element.method;
        const methodName = element.method.name;
        const args = element.invokes.arguments;
        const argsStr = (args === undefined)? "": args.join(", ");
        const {id, states, order} = element.invokes;
        const toCid = element.entity.id;
        const cName = element.entity.name;

        for(const state in states) {
            if(state.maxOrder < order) {
                state.maxOrder = order;
                state.maxCid = toCid;
            }
        }

        if(states !== null && states !== undefined) {
            for(const state of states) {
                const [cmd, id, parentId, content, type] = state.split("|");

                if(cmd === "PUSH") {
                    const left = (stateStack.length <= 0)? classes[mid].absLeft:
                                          classes[mid].absLeft + horizontalOffset / 8;

                    const lifeline = classes[mid];
                    stateStack.push({
                        id,
                        parentId,
                        content, 
                        type,
                        absTop: height,
                        absLeft: left,
                        toCid: toCid,
                        maxOrder: -1,
                        maxCid: -1
                    });
                    
                    height += msgOffset;

                } else { // POP
                    const {id, parentId, content, type, absTop, absLeft, maxCid} = stateStack.pop();
                    let cid = maxCid;
                    if(cid === -1) cid = lastCid;

                    const toClass = classes[cid];
                    const fromClass = classes[mid];
                    const absW = (toClass.absLeft - absLeft) + toClass.absW;
                    const absH = height - absTop;

                    fragments[id] = new Fragment(id, absTop, absLeft, absW, absH, type, content, plane, mid, cid);
                }
            }
        }

        // order == 0는 클래스 본인의 정보를 얻기 위해 가져온 것이므로
        // 여기의 invokes는 제외
        if(returnType !== undefined && order !== 0) {
            if(toCid === mid) {
                const message = new Message(height, "Sync", methodName, argsStr, id, mid, toCid, plane);
                height += msgOffset * 2;
                classes[mid].messages.push(message);
            } else {
                const message = new Message(height, "Sync", methodName, argsStr, id, mid, toCid, plane);
                height += msgOffset/1.4;
                classes[mid].messages.push(message);

                const replyMessage = new Message(height, "Reply", "", returnType, -id, toCid, mid, plane);
                height += msgOffset;
                classes[toCid].messages.push(replyMessage);
            }
        } else if(order !== 0) {
            const message = new Message(height, "Async", methodName, argsStr, id, mid, toCid, plane);
            if(toCid === mid) height += msgOffset * 2;
            else height += msgOffset;
            classes[mid].messages.push(message);
        }
    }



    height += msgOffset;
    while(stateStack.length !== 0) {
        const {id, parentId, content, type, absTop, absLeft, maxCid} = stateStack.pop();
        let cid = maxCid;
        if(cid === -1) cid = lastCid;

        const toClass = classes[cid];
        const fromClass = classes[mid];
        
        const absW = (toClass.absLeft - absLeft) + toClass.absW;
        const absH = height - absTop;

        fragments[id] = new Fragment(id, absTop, absLeft, absW, absH, type, content, plane, mid, cid);
    }

    plane.setLength(height);
    plane.subscribeLifes(classes);
    plane.subscribeFrags(fragments);

    return [mid, plane];
}

export function initSequenceFromSave(_plane, container) {
    if(container !== undefined) {
        _plane.containerW = container[0];
        _plane.containerH = container[1];
    }

    const _lifeLines = _plane.lifeLineSet;
    const _fragments = _plane.fragmentSet;
    const plane = SDPlane.getInstanceFromSave(_plane);
    const lifeLines = Object.fromEntries(Object.entries(_lifeLines).map(
        ([idx, lifeLine]) => [lifeLine.id, LifeLine.getInstanceFromSave(lifeLine.id, lifeLine, plane)]));
    const fragments = Object.fromEntries(Object.entries(_fragments).map(
        ([idx, fragment]) => [fragment.id, Fragment.getInstanceFromSave(fragment.id, fragment, plane)]));

    plane.subscribeFrags(fragments);
    plane.subscribeLifes(lifeLines);

    return [plane];
}