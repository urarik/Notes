import React, { useEffect, useState, useMemo } from 'react';
import sample_image from '../../assets/sample.jpg';
import {Link} from 'react-router-dom';
import {Card} from 'react-bootstrap'
import {api} from '../../api/api';
import jsCookies from 'js-cookies';


export default function() {
    const [projects, setProjects] = useState([]);
    
    useEffect(() => {
        const config = {headers: {Authorization: `Bearer ${jsCookies.getItem("token")}`}}
        api.get(`/projects`, config)
            .then((res) => {
                setProjects(res.data.projects);
            })
            .catch((err) => console.log(err));
    }, []);

    const render_project = useMemo(() => {
        return projects.map((project) => {
            return (
                <Link
                    className='col-3'
                    to={`/project/${project.pid}/notes`}
                    state={{project}}
                    key={project.pid}>
                <Card className='project_card margin-16'>
                    <Card.Img variant='top' src={sample_image} />
                    <Card.Body>
                        <Card.Title>{project.title}</Card.Title>
                    </Card.Body>
                </Card>
                </Link>
            )
        }
        );
    }, [projects]);

    return (
        <div className='container'>
            <div className='row'>
                {render_project}
            </div>
        </div>
    );
}