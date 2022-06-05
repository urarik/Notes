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
import SequenceDiagram from "../components/analysis_modules/sequendiagram/SequenceDiagram";

export default function(props) {
    const { id } = useParams()
    const [closeMain, setCloseMain] = useState("");
    const location = useLocation();
    const dispatch = useDispatch();

    useEffect(() => {
        if(location.state != null) {
            // TODO: fetchProject는 필요없는듯 나중에 다 지워보자
            // dispatch(fetchProject(location.state.project));
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
                        <Route path={`sequencediagram/:mid`} element={<SequenceDiagram />}/>
                        <Route path={`sequencediagram`} element={<SequenceDiagram />}/>
                        <Route path={`setting`} element={<ProjectSetting />} />
                        <Route path={`note/new`} element={<NoteNew />}/>
                    </Routes>
                </div>
            </div>
        </div>
    );
}