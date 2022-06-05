import { useEffect, useRef, useState } from "react";
import { useLocation, useParams } from "react-router";
import { get, post } from "../../../api/api";
import { initSequenceFromSave, initSequenceFromScratch } from "./initSequence";
import SDContent from "./SDContent";
import SDFragment from "./SDFragment";
import SDLifeLine from "./SDLifeLine";
import SDPlane from "./classes/SDPlane";

export default function({__plane, __cid, onlyView}) {
    const { id, mid } = useParams();
    const [plane, setPlane] = useState(__plane === undefined? undefined: __plane);
    const [cid, setCid] = useState(__cid === undefined? 0: __cid);
    const [name, setName] = useState("");
    const [saves, setSaves] = useState([]);
    const [saveId, setSaveId] = useState(-1);
    const ref = useRef(null);

    useEffect(async () => {
        if(mid !== undefined) {
            const response = await get('/analyze/sd/invokes', {pid: id, mid});
            if(ref.current) {
                const container = [ref.current.offsetWidth, ref.current.offsetHeight];
                const [_cid, _plane] = initSequenceFromScratch(id, response.data, container);
                setCid(_cid);
                setPlane(_plane);
            }
        }
    }, [mid]);

    useEffect(async () => {
        const response = await get('/analyze/sd/saves', {pid: id});
        setSaves(response.data);
        if(response.data.length !== 0)
            setSaveId(response.data[0].id);
    }, []);

    const handleSave = async _ => {
        console.log(plane.name);
        console.log(name)
        if(plane.name !== name) {
            plane.setName(name);
            plane.id = null;
        }
        setPlane(plane);
        console.log(plane.toJson());
        const response = await post('/analyze/sd/save', plane.toJson());
        console.log(response);
        
    }
    const handleLoad = async _ => {
        const response = await get('/analyze/sd', {pid: id, planeId: saveId});
        const [_plane] = initSequenceFromSave(response.data)
        setPlane(_plane);
        setCid(response.data.cid);       
        setName(_plane.name);
    }

    const renderSaves =  _ => {
        return saves.map(save => <option key={save.id} value={save.id}>{save.name}</option>);
    }
    const handleSaveChange = e => {
        setSaveId(e.target.value);
    }


    const renderLifeLine = _ => {
        if(plane === undefined) return (<div></div>);

        return Object.keys(plane.lifeLines).map(key => {
            const lifeLine = plane.lifeLines[key];
            const setLifeLine = (lifeLine) => {
                plane.lifeLines[key] = lifeLine;
                setPlane(SDPlane.getInstanceFromAnother(plane));
            }
            return <SDLifeLine key={lifeLine.id} 
                               id={lifeLine.id}
                               lifeLine={lifeLine} 
                               setLifeLine={lifeLine => setLifeLine(lifeLine)}
                               lifeLines={plane.lifeLines}
                               cid={cid}>
                    </SDLifeLine>;
        })
    };

    const renderFragment = _ => {
        if(plane === undefined) return <></>;
        
        return Object.keys(plane.fragments).map(key => {
            const fragment = plane.fragments[key];
            const setFragment = (fragment) => {
                plane.fragments[key] = fragment;
                setPlane(SDPlane.getInstanceFromAnother(plane));
            }

            return <SDFragment 
                        key={fragment.id}
                        id={fragment.id}
                        fragment={fragment}
                        setFragment={fragment => setFragment(fragment)}
                        lifeLines={plane.lifeLines}
                    />
        });
    }

    let style = {};
    if(onlyView) style['height'] = 400;
    return (
        <div className="sd-container" style={style}>
            {onlyView !== true? 
                <div className="header">
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

            <div ref={ref} className="sd-content" id="sd-content">
                <SDContent plane={plane} setPlane={plane => setPlane(plane)}>
                    {renderFragment()}
                    {renderLifeLine()}
                </SDContent>
            </div>
        </div>
    );
}
