import _ from 'lodash';
import Entity from './classes/Entity.js';
import CDPlane from './classes/CDPlane';
import Relationship from './classes/Relationship.js';
import { H, W } from '../Plane.js';

const contentRatio = 8.5;
const headerRatio = 10.7;
const width = 80;
const horizontalOffset = width / 2;
const verticalOffset = 50;
const startTop = 10;
const startLeft = 20;

export function initClassFromScratch(pid, cid, es, rels, container) {
    console.log(es);
    console.log(rels);
    const entities = {};
    es.forEach(ary => entities[ary.Entity.id] = ary);

    const queue = [];
    const center = {[cid]: entities[cid], __maxHeight: getHeight(entities[cid])};
    delete entities[cid];

    const collectRelatedEntity = ({ Entity }, collector, isTop) => {
        let maxHeight = 0;
        
        const id = Entity.id
        for(const rel of rels) {
            if(rel.start === id && rel.end === id) continue;
            // Caution: the value of entities must not be undefined.
            if(rel.start === id || rel.end === id) {
                const targetId = (rel.start == id)? rel.end: rel.start;
                if(entities[targetId] !== undefined) {
                    if(isTop === null) {
                        if(rel.start === id) collector.push([entities[targetId], false]);
                        else collector.push([entities[targetId], true]);
                    } 
                    else collector.push([entities[targetId], isTop]);

                    maxHeight = Math.max(maxHeight, getHeight(entities[targetId]));
                    delete entities[targetId];
                }
            }
        }
    };

    // Collect direct children.
    collectRelatedEntity({Entity: {id: cid}}, queue, null);

    // Collect others.
    const top = [], bottom = [];
    while(queue.length != 0) {
        const n = queue.length;

        // Collect by level
        const nTop = {}, nBottom = {};
        let topMaxHeight = 0, bottomMaxHeight = 0;
        for(const i of Array(n).keys()) {
            const [entity, isTop] = queue.shift();

            if(isTop === true) {
                nTop[entity.Entity.id] = entity;
                topMaxHeight = Math.max(topMaxHeight, getHeight(entity));
                collectRelatedEntity(entity, queue, true);
            }
            else if(isTop === false) {
                nBottom[entity.Entity.id] = entity;
                bottomMaxHeight = Math.max(bottomMaxHeight, getHeight(entity));
                collectRelatedEntity(entity, queue, false);
            }
        }
        if(Object.keys(nTop).length !== 0) {
            nTop['__maxHeight'] = topMaxHeight;
            top.push(nTop);
        }
        if(Object.keys(nBottom).length !== 0) {
            nBottom['__maxHeight'] = bottomMaxHeight;
            bottom.push(nBottom);
        }
    }

    // Convert data into class diagram entity.
    let maxLength = 1;
    let totalHeight = 0;
    for(const entityObject of top) {
        maxLength = Math.max(maxLength, Object.keys(entityObject).length); // horizontal
        totalHeight += entityObject['__maxHeight']; //vertical
    }
    for(const entityObject of bottom) {
        maxLength = Math.max(maxLength, Object.keys(entityObject).length);
        totalHeight += entityObject['__maxHeight'];
    }
    totalHeight += getHeight(center[cid]);

    // const plane = new CDPlane(startLeft,
    //                         startTop,
    //                         maxLength * (width + horizontalOffset),
    //                         ((top.length + bottom.length + 1) * (verticalOffset + headerRatio)) + (totalHeight * contentRatio),
    //                         container[0],
    //                         container[1]);
    const plane = new CDPlane(pid,
        startLeft,
        startTop,
        W,
        H,
        container[0],
        container[1]);

    const retEntities = {};
    let accuTop = {accuTop: 20}; // default top offset
    convertToSpaceEntity(top.reverse(), retEntities, accuTop, plane, maxLength);
    convertToSpaceEntity([center], retEntities, accuTop, plane, maxLength);
    convertToSpaceEntity(bottom, retEntities, accuTop, plane, maxLength);
    plane.subscribe(retEntities);

    // Convert relationship into class diagram relationship.
    const relMapping = {};
    const relCnts = {};
    for(const rel of rels) {
        const [start, end, id, type] = [rel.start, rel.end, rel.id, rel.properties.type];
        if(relMapping[start] === undefined) relMapping[start] = [];
        if(relMapping[end] !== undefined && type === "Association") {
            const biIndex = relMapping[end].findIndex(val => val[1] === start && val[2] === "Association");
            if(biIndex !== -1) {
                relMapping[end][biIndex][2] = "Bi-Association";
                continue;
            }
        }
        relMapping[start].push([id, end, type]);
        if(relCnts[[start, end]] === undefined) 
            relCnts[[start, end]] = [0, 0];
        if(relCnts[[end, start]] === undefined) 
            relCnts[[end, start]] = [0, 0];
        relCnts[[start, end]][0] += 1;
        relCnts[[end, start]][0] += 1;
    }

    const retRelationships = [];
    for(const [start, ary] of Object.entries(relMapping)) {
        for(const [i, target] of ary.entries()) {
            const cnt = relCnts[[target[1], Number(start)]];
            const relationship = 
                new Relationship(target[0], Number(start), target[1], cnt[1], cnt[0], target[2]);
            relCnts[[target[1], Number(start)]][1] += 1;
            relCnts[[Number(start), target[1]]][1] += 1;
            
            retRelationships.push(relationship);
            plane.subscribeRel(relationship);
        }
    }

    return [plane, retEntities, retRelationships];
}

function getHeight(entity) {
    // default absolute height 2
    const len1 = entity['Methods'].length;
    const len2 = entity['Members'].length;
    return (Math.max(((len1 === 0)? 1: len1) + ((len2 === 0)? 1: len2), 2) * contentRatio) + headerRatio;
}

function convertToSpaceEntity(list, collector, accuTop, plane, maxLength) {
    const startRowLeft = (plane.w - (maxLength * width) + ((maxLength - 1) * horizontalOffset)) / 2;

    for(const entityObject of list) {
        const startCurRowLeft = startRowLeft + (maxLength - Object.keys(entityObject).length) * 3/4 * width;
        let left = startCurRowLeft;

        const maxHeight = entityObject['__maxHeight'];
        delete entityObject['__maxHeight'];
        for(const [key, entity] of Object.entries(entityObject)) {
            let height = getHeight(entity);
            if(entity.Entity.label[0] === 'Interface') height += contentRatio * 0.75;

            const spaceEntity = 
                new Entity(left, accuTop.accuTop, width, height, plane, entity.Entity, entity.Methods, entity.Members);
                collector[entity.Entity.id] = spaceEntity;
            
            left += width + horizontalOffset;
        }

        accuTop.accuTop += maxHeight + verticalOffset;
    }
}

export function initClassFromSave(_plane, _entities, _rels, container) {
    if(container !== undefined) {
        _plane.containerW = container[0];
        _plane.containerH = container[1];
    }
    const plane = CDPlane.getInstanceFromSave(_plane);
    const entities = Object.fromEntries(Object.entries(_entities).map(([id, entity]) => [id, Entity.getInstanceFromSave(id, entity, plane)]));
    const rels = _rels.map(_rel => {
        const rel = Relationship.getInstanceFromSave(_rel);
        plane.subscribeRel(rel);
        return rel;
    });

    plane.subscribe(entities);

    return [plane, entities, rels];
}