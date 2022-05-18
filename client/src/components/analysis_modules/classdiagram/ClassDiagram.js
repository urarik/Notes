import { useEffect, useRef, useState } from "react";
import { useSelector } from "react-redux";
import ModuleSidebar from "../sidebar/ModuleSidebar";
import { post, get } from '../../../api/api';
import { useParams } from "react-router";
import initClass from "./initClass";
import ClassDiagramContent from "./ClassDiagramContent";
import ClassDiagramEntity from "./ClassDiagramEntity";
import ClassDiagramRelationship from "./ClassDiagramRelationship";

export default function(props) {
    const [depth, setDepth] = useState(1);
    const [plane, setPlane] = useState(null);
    const [entities, setEntities] = useState({});
    const [relationships, setRelationships] = useState([]);
    const [name, setName] = useState("");

    const classEntity = useSelector((state) => state.activeClass);
    const {id} = useParams();

    const ref = useRef(null);

    const onAnalysis = async () => {
        const response = await get('analyze/class', {
            pid: id,
            cid: classEntity.id,
            depth
        })
        if(ref.current) {
            const container = [ref.current.offsetWidth, ref.current.offsetHeight];
            const [_plane, _entities, _relationships] = initClass(id, classEntity.id, response.data.entities, response.data.relationships, container);
            _plane.subscribe(_entities);
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
                    console.log(relationships);
                    const newRels = relationships.filter(rel => rel.fromId !== key && rel.toId !== key);
                    
                    plane.entities = newEntities;
                    setEntities(newEntities);
                    setRelationships(newRels);
                } else {
                    const newEntities = {...entities, [entity.id]: entity};
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
        plane.setName(name);
        setPlane(plane);
        console.log(plane.toJson());
        const response = await post('/analyze/classdiagram/save', plane.toJson());
        
    }

    return (
        <div className="classdiagram-container">
            <ModuleSidebar></ModuleSidebar>
            <div>
                <div className="header">
                    <div className="analysis-container">
                        <span>Class:&nbsp;&nbsp;&nbsp;</span>
                        <div className="class-input">{classEntity.name}</div>

                        <span>Depth:&nbsp;&nbsp;&nbsp;  </span>
                        <input type="number" 
                            className="depth-input" 
                            value={depth} 
                            onChange={e => setDepth(e.target.value)}></input>

                        <button className="btn btn-primary analysis" onClick={_ => onAnalysis()}>Analysis!</button>
                    </div>
                    <div className="save-load-container">
                        <input type="text" className="name-input" placeholder="Name!" value={name} onChange={e=>setName(e.target.value)}/>
                        <button className="btn btn-primary analysis" onClick={handleSave}>Save!</button>
                        <button className="btn btn-primary analysis">Load!</button>
                    </div>
                </div>
                <div ref={ref} className="classdiagram-content" id="classdiagram-content">
                    <ClassDiagramContent plane={plane} setEntities={setEntities} entities={entities}>
                        {renderEntity()}
                        {renderRelationship()}
                    </ClassDiagramContent>
                </div>
            </div>
        </div>
    );
}