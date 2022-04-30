import { useEffect, useState } from "react";
import Note from "./Note";
import {Link, useParams} from 'react-router-dom';
import { get } from "../api/api";
import { __SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED } from "react-dom";
import { useDispatch } from "react-redux";
import { doActiveNoteBlock } from "../actions";

// https://dribbble.com/shots/14378025/attachments/6048127?mode=media
export default function(props) {
    const [noteList, setNoteList] = useState([]);
    const [selectedNote, setSelectedNote] = useState({});
    const [selected, setSelected] = useState(false);
    const dispatch = useDispatch();
    const { id } = useParams();

    useEffect(() => {
        const fetchNotes = async () => {
            const response = await get('/notes', {pid: id});
            if(response.status === 200) {
                setNoteList(response.data.notes);
            } else console.log(response);
        };

        fetchNotes();
    }, [id]);

    const renderNotes = () => {
        return noteList.map(note => {
            const renderAdmins = () => {
                return note.admins.map(admin => admin.username).join(" · ");
            };
            const onClick = (note) => {
                setSelected(true);
                setSelectedNote(note);
                dispatch(doActiveNoteBlock(-1));
            };

            return (
                <li className={`item ${selectedNote.nid === note.nid? "clicked": ""}`} 
                    key={note.title}
                    onClick={() => onClick(note)}>
                    <div className="title">{note.title}</div>
                    <div className="admins">{renderAdmins()}</div>
                </li>
            )
        });
    }

    return (
        <div className="note-list-container">
            <header>
                <h1>Notes</h1>
                <Link className='btn btn-primary mb-4' to={`/project/${id}/note/new`}>
                    새로운 노트
                </Link>
            </header>
            <div className="note-list-body max-height">
                <ul className="note-list">
                    {renderNotes()}
                </ul>
                {
                    selected && (
                        <div className="note">
                            <Note selectedNote={selectedNote}></Note>
                        </div>
                    )
                }
            </div>
        </div>
    );
}