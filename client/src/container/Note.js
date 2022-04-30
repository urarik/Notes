import { useEffect, useMemo, useState } from "react"
import { useDispatch, useSelector } from "react-redux";
import { doActiveNoteBlock, fetchBlocks, addBlock } from "../actions";
import {get, post} from "../api/api";
import NoteBlock from "../components/NoteBlock";

export default function({ selectedNote }) {
    const { nid, title } = selectedNote;
    const dispatch = useDispatch();
    const blocks = useSelector((state) => state.blockList);
    const { pid } = useSelector(state => state.project);

    //componentDidMount
    useEffect(async () => {
        if(pid == undefined || nid == undefined) return;

        const response = await get('/note', {pid, nid})
        if(response.status == 200) {
            dispatch(fetchBlocks(response.data.contents));
        }
        
    }, [pid, nid]);
    
    const renderBlocks = () => {
        if(blocks != null)
            return blocks.map(({content, type, id}, idx) => {
                if(type == "Text")
                    return <NoteBlock key={idx} content={content} order={idx} bid={id} />;
            });
    };

    const onAddblock = async () => {
        const response = await post('/note/block/add', {id: nid, content: "", type: "Text"});
        if(response.status == 200) {
            dispatch(addBlock(response.data));
        }
    }

    if(!blocks) return <div>"Loading..."</div>;

    const onClick = (event) => {
        if(event.target.id == 'note-margin' ||
           event.target.id == 'note-border')
           dispatch(doActiveNoteBlock(-1));
    }
    //id는 클릭시 이벤트 식별에 사용함. 바로 위 onClick
    return (
    <div className="note-block-container" onClick={onClick}>
        <div className="header">
            <span className="title">{title}</span>
            <span className="add-block-button" onClick={() => onAddblock()}>+</span>
        </div>
        <div className="note-block-list">
                {renderBlocks()}
        </div>
    </div>
    );
}
                
