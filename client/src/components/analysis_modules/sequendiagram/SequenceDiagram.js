import { useEffect, useRef, useState } from "react";
import { useLocation, useParams } from "react-router";
import { get } from "../../../api/api";
import { initSequenceFromScratch } from "./initSequence";
import SDContent from "./SDContent";
import SDFragment from "./SDFragment";
import SDLifeLine from "./SDLifeLine";
import SDPlane from "./classes/SDPlane";

export default function(props) {
    const { id, mid } = useParams();
    const [plane, setPlane] = useState();
    const [cid, setCid] = useState();
    const ref = useRef(null);

    useEffect(async () => {
        const response = await get('/analyze/sd', {pid: id, mid});
        if(ref.current) {
            const container = [ref.current.offsetWidth, ref.current.offsetHeight];
            const [_cid, _plane] = initSequenceFromScratch(id, response.data, container);
            setCid(_cid);
            setPlane(_plane);
        }
    }, [mid]);

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


    return (
        <div className="sd-container">
            <div ref={ref} className="sd-content" id="sd-content">
                <SDContent plane={plane} setPlane={plane => setPlane(plane)}>
                    {renderFragment()}
                    {renderLifeLine()}
                </SDContent>
            </div>
        </div>
    );
}
