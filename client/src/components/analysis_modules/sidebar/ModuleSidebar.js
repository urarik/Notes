import { useEffect, useState } from "react";
import {Link, useParams} from 'react-router-dom';
import {get} from '../../../api/api'
import PackageEntity from "./PackageEntity";

export default function(props) {
    const [order, setOrder] = useState(0);
    const [packageList, setPackageList] = useState([]);
    const { id } = useParams();

    useEffect(async () => {
        const response = await get('analyze/package', {pid: id, order});
        if(response.status === 200) {
            setPackageList(response.data.packages)
        } else console.log(response);
    }, []);

    const renderPackageEntity = () => {
        return packageList.map(pac => {
            return <PackageEntity key = {pac.id} pacid={pac.id} path={pac.properties.path}></PackageEntity>
        })
    };

    return (
        <div className="module-sidebar">
            <div className="module-header">Classes!</div>
            {renderPackageEntity()}
        </div>
    );
}