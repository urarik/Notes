import { useState } from "react";
import { useParams } from "react-router";
import { get } from "../../../api/api";
import ClassEntity from "./ClassEntity";



export default function({pacid, path}) {
    const [packageId, setPackageId] = useState(pacid);
    const [folding, setFolding] = useState("");
    const [entityList, setEntityList] = useState([]);
    const {id} = useParams();

    const onClick = async () => {
        if(folding == "") {
            setFolding("unfolding");
            const response = await get('analyze/entities', {pid: id, packageId});
            setEntityList(response.data.entities);
        }
        else {
            setFolding("");
            setEntityList([]);
        }
    }

    const renderClasses = () => {
        return entityList.map(entity => {
            return <ClassEntity key={entity.id} classEntity={entity}></ClassEntity>
        });
    };

    return (
        <div>
            <div className="packageEntity" onClick={() => onClick()}>
                <i className={`bx bxs-right-arrow unfold ${folding}`}></i>
                <span className="path">{path === ""? "default": path}</span>
            </div>

            {renderClasses()}
        </div>
    );
}