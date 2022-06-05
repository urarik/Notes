import { useEffect, useRef, useState } from "react";
import { useSelector } from "react-redux";
import ModuleSidebar from "../sidebar/ModuleSidebar";
import { post, get, api } from '../../../api/api';
import { useParams } from "react-router";
import { initClassFromSave, initClassFromScratch } from "./initClass";
import ClassDiagramContent from "./ClassDiagramContent";
import ClassDiagramEntity from "./ClassDiagramEntity";
import ClassDiagramRelationship from "./ClassDiagramRelationship";

export default function({__plane, __entities, __relationships, onlyView}) {
    const [depth, setDepth] = useState(1);
    const [plane, setPlane] = useState(__plane === undefined? null: __plane);
    const [entities, setEntities] = useState(__entities === undefined? []: __entities);
    const [relationships, setRelationships] = useState(__relationships === undefined? []: __relationships);
    const [name, setName] = useState("");
    const [saves, setSaves] = useState([]);
    const [saveId, setSaveId] = useState(-1);

    const classEntity = useSelector((state) => state.activeClass);
    const {id} = useParams();

    const ref = useRef(null);

    useEffect(async () => {
        const response = await get('/analyze/classdiagram/saves', {pid: id});
        setSaves(response.data);
        if(response.data.length !== 0)
            setSaveId(response.data[0].id);
    }, []);

    const onAnalysis = async () => {
        const response = await get('analyze/class', {
            pid: id,
            cid: classEntity.id,
            depth
        })
        if(ref.current) {
            const container = [ref.current.offsetWidth, ref.current.offsetHeight];
            const [_plane, _entities, _relationships] = initClassFromScratch(id, classEntity.id, response.data.entities, response.data.relationships, container);
            setPlane(_plane);
            setEntities(_entities);
            setRelationships(_relationships);
        }
    };

    const renderEntity = () => {
        const result = Object.keys(entities).map(key => {
            const setEntity = (entity) => {
                if(entity === null) {
                    const newEntities = {...entities};
                    delete newEntities[key];
                    const newRels = relationships.filter(rel => rel.fromId !== key && rel.toId !== key);
                    
                    plane.entities = newEntities;
                    setEntities(newEntities);
                    setRelationships(newRels);
                } else {
                    const newEntities = {...entities, [entity.id]: entity};
                    plane.entities = newEntities;
                    setPlane(plane);
                    setEntities(newEntities);
                }
            };
            return <ClassDiagramEntity entity={entities[key]} key={key} setEntity={setEntity} id={key} />
        });
        return result;
    }

    const renderRelationship = () => {
        return relationships.map(relationship => 
            <ClassDiagramRelationship entities={entities} key={relationship.id} relationship={relationship}/>)
    };

    const handleSave = async _ => {
        if(plane.name !== name) {
            plane.setName(name);
            plane.id = null;
        }
        setPlane(plane);
        const response = await post('/analyze/classdiagram/save', plane.toJson());
        
    }
    const handleLoad = async _ => {
        const response = await get('/analyze/classdiagram', {pid: id, planeId: saveId});
        const [_plane, _entities, _relationships] = initClassFromSave(response.data.plane, response.data.entities, response.data.relationships)
        setPlane(_plane);
        setEntities(_entities);
        setRelationships(_relationships);
        setName(_plane.name);
    }

    const renderSaves =  _ => {
        return saves.map(save => <option key={save.id} value={save.id}>{save.name}</option>);
    }
    const handleSaveChange = e => {
        setSaveId(e.target.value);
    }

    let style = {};
    if(onlyView) style['height'] = 400;
    return (
        <div className="classdiagram-container" style={style}>
            {
                onlyView === undefined? <ModuleSidebar></ModuleSidebar>: <></>
            }
            <div>
                {
                    onlyView === undefined?
                    <div className="header">
                        <div className="analysis-container">
                            <span>Class:&nbsp;&nbsp;&nbsp;</span>
                            <div className="class-input">{classEntity.name}</div>

                            <span>Depth:&nbsp;&nbsp;&nbsp;  </span>
                            <input type="number" 
                                className="depth-input form-control" 
                                value={depth} 
                                onChange={e => setDepth(e.target.value)}></input>

                            <button className="btn btn-primary analysis" onClick={_ => onAnalysis()}>Analysis!</button>
                        </div>
                        <div className="save-load-container">
                            <input type="text" className="name form-control" placeholder="Name!" value={name} onChange={e=>setName(e.target.value)}/>
                            <button className="btn btn-primary analysis" onClick={handleSave}>Save!</button>
                            <select className="name form-select" onChange={handleSaveChange}>
                                {renderSaves()}
                            </select>
                            <button className="btn btn-primary analysis" onClick={handleLoad}>Load!</button>
                        </div>
                    </div>: <></>
                }
                <div ref={ref} className={`classdiagram-content ${onlyView === true? 'only-view': ''}`} id="classdiagram-content">
                    <ClassDiagramContent plane={plane} setEntities={setEntities} entities={entities}>
                        {renderEntity()}
                        {renderRelationship()}
                    </ClassDiagramContent>
                </div>
            </div>
        </div>
    );
}