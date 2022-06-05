import { useEffect, useMemo, useState } from "react"
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router";
import { doActiveNoteBlock, fetchBlocks, addBlock } from "../actions";
import {get, post} from "../api/api";
import ClassDiagram from "../components/analysis_modules/classdiagram/ClassDiagram";
import { initClassFromSave } from "../components/analysis_modules/classdiagram/initClass";
import { initSequenceFromSave } from "../components/analysis_modules/sequendiagram/initSequence";
import SequenceDiagram from "../components/analysis_modules/sequendiagram/SequenceDiagram";
import NoteBlock from "../components/NoteBlock";
import NoteBlockForDiagram from "../components/NoteBlockForDiagram";

export default function({ selectedNote }) {
    const { nid, title } = selectedNote;
    // const blocks = useSelector((state) => state.blockList);
    const { id } = useParams();
    const dispatch = useDispatch();
    const [blocks, setBlocks] = useState([])

    const [hidingMenu, setHidingMenu] = useState('hiding');
    const [hidingPlane, setHidingPlane] = useState('hiding');
    const [planeType, setPlaneType] = useState('CD');
    const [saves, setSaves] = useState([]);

    const getCD = async (content) => {
        const response = await get('/analyze/classdiagram', {pid: id, planeId: content});
        const [plane, entities, relationships] = initClassFromSave(
            response.data.plane,
            response.data.entities,
            response.data.relationships,
            [816, 400]);
        plane.fontSize = (plane.fontSize / (917 / 400));

        return [plane, entities, relationships]
    }

    const getSD = async (content) => {
        const response = await get('/analyze/sd', {pid: id, planeId: content});
        const [plane] = initSequenceFromSave(response.data, [816, 400])
        plane.fontSize = (plane.fontSize / (917 / 400));

        return [plane, response.data.cid];
    };

    //componentDidMount
    useEffect(async () => {
        if(id == undefined || nid == undefined) return;

        const noteResponse = await get('/note', {pid: id, nid})
        if(noteResponse.status == 200) {
            const newBlocks = await Promise.all(noteResponse.data.contents.map(async block => {
                if(block.type === 'Text') return block;
                if(block.type === 'CD') {
                    const [plane, entities, relationships] = await getCD(block.content);
                    return {
                        id: block.id,
                        type: block.type,
                        content: {plane, entities, relationships}
                    }
                }
                if(block.type === 'SD') {
                    const [plane, cid] = await getSD(block.content);
                    return {
                        id: block.id,
                        type: block.type,
                        content: {plane, cid}
                    };
                }
            }))
            setBlocks(newBlocks);
        }
        
    }, [id, nid]);

    useEffect(async () => {
        if(planeType === 'CD') {
            const response = await get('/analyze/classdiagram/saves', {pid: id});
            setSaves(response.data);
        } else if(planeType === 'SD') {
            const response = await get('/analyze/sd/saves', {pid: id});
            setSaves(response.data);
        }
    }, [planeType])
    
    const renderBlocks = () => {
        if(blocks !== null)
            return blocks.map(({content, type, id}, idx) => {
                console.log(content)
                const setBlock = (newBlock) => {
                    const newBlocks = blocks.map(block => {
                        if(block.id == newBlock.id) {
                            block.content = newBlock.content;
                            block.type = newBlock.type;
                        } 
                        return block;
                    })
                    setBlocks(newBlocks);
                };

                if(type == "Text")
                    return <NoteBlock key={id} content={content} order={idx} id={id} setBlock={setBlock} blocks={blocks} setBlocks={setBlocks} nid={nid}/>;
                else if(type === "CD") {
                    const {plane, entities, relationships} = content;
                    return (
                        <NoteBlockForDiagram key={id} order={idx} id={id} blocks={blocks} setBlocks={setBlocks} nid={nid}>
                            <ClassDiagram __plane={plane} __entities={entities} __relationships={relationships} onlyView={true}/>
                        </NoteBlockForDiagram>
                    );
                } 
                else if(type === "SD") {
                    const {plane, cid} = content;
                    return (
                        <NoteBlockForDiagram key={id} order={idx} id={id} blocks={blocks} setBlocks={setBlocks} nid={nid}>
                            <SequenceDiagram __plane={plane} __cid={cid} onlyView={true}/>
                        </NoteBlockForDiagram>
                    )
                } 
            });
    };

    const handleAddText = async () => {
        setHidingPlane('');
        const response = await post('/note/block/add', {id: nid, content: "", type: "Text"});
        console.log(response.data)
        if(response.status == 200) {
            setBlocks([...blocks, {id: response.data, content: '', type: 'Text'}]);
        }
    }

    const handleNewBlock = () => {
        if(hidingMenu === "") {
            setHidingMenu("hiding");
            setHidingPlane("hiding");
        }
        else setHidingMenu("");
    }

    const handleAddCD = () => {
        setHidingPlane("");
        setPlaneType('CD');
    }

    const handleAddSD = () => {
        setHidingPlane("");
        setPlaneType('SD');
    }
    const renderPlanes = () => {
        return saves.map(save => {
            const handleClick = async () => {
                const response = await post('/note/block/add', {id: nid, content: save.id, type: planeType});
                if(response.status == 200) {
                    if(planeType === 'CD') {
                        const [plane, entities, relationships] = await getCD(save.id);
                        setBlocks([...blocks, {
                            id: response.data, 
                            content: {plane, entities, relationships}, 
                            type: planeType
                        }]);
                    }
                    else if(planeType === 'SD') {
                        const [plane, cid] = await getSD(save.id);
                        setBlocks([...blocks, {
                            id: response.data, 
                            content: {plane, cid}, 
                            type: planeType
                        }]);
                    }
                }
            }

            return <li key={save.id} value={save.id} onClick={handleClick}>{save.name}</li>
        });
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
            <span className="add-block-button" onClick={() => handleNewBlock()}>+</span>
            <ul className={`block-menu ${hidingMenu}`}>
                <li onClick={() => handleAddText()}>Text</li>
                <li onClick={() => handleAddCD()}>ClassDiagram</li>
                <li onClick={() => handleAddSD()}>SequenceDiagram</li>
            </ul>
            <ul className={`plane-menu ${hidingPlane}`}>
                {renderPlanes()}
            </ul>
        </div>
        <div className="note-block-list">
                {renderBlocks()}
        </div>
    </div>
    );
}
                
