import { useEffect, useState } from "react";
import { useParams, useRoutes } from "react-router";
import { useDispatch } from "react-redux";
import { useLocation } from "react-router-dom";
import SideBar from "../components/side_bar";
import ProjectNotes from "./NoteList";
import Note from "./Note";
import ClassDiagram from "../components/analysis_modules/classdiagram/ClassDiagram";
import { Route, Routes} from 'react-router-dom';
import ProjectSetting from "../components/ProjectSetting";
import { fetchProject } from "../actions";
import NoteNew from '../components/NoteNew';

export default function(props) {
    const { id } = useParams()
    const [closeMain, setCloseMain] = useState("");
    const location = useLocation();
    const dispatch = useDispatch();

    useEffect(() => {
        if(location.state != null) {
            dispatch(fetchProject(location.state.project));
        }
    }, []);

    return (
        <div className="site-content">
            <div>
                <div className='nopadding max-height tran-5'>
                    <SideBar id={id} setCloseMain={setCloseMain}/>
                </div>
                
                <div className={`home ${closeMain}`}>
                    <Routes>
                        <Route path={`main`} element={<Note />} />
                        <Route path={`notes`} element={<ProjectNotes />} />
                        <Route path={`classdiagram`} element={<ClassDiagram />}/>
                        <Route path={`setting`} element={<ProjectSetting />} />
                        <Route path={`note/new`} element={<NoteNew />}/>
                    </Routes>
                </div>
            </div>
        </div>
    );
}