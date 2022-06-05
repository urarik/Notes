import { useEffect, useState, useCallback } from "react";
import ReactMarkdown from "react-markdown";
import { useDispatch, useSelector } from "react-redux";
import { doActiveNoteBlock, setNoteBlockContent } from "../actions";
import Switch from "./util/Switch";
import { post } from "../api/api";

export default function ({content, order, id, setBlock, blocks, setBlocks, nid}) {
    const dispatch = useDispatch();
    const activeNoteBlock = useSelector((state) => state.activeNoteBlock);
    const [active, setActive] = useState('');
    const [visibility, setvisibility] = useState('hidden');
    const [revise, setRevise] = useState(false);
    const exitStr = '/exit';
    const [exitState, setExitState] = useState(0);
    const [reviseContent, setReviseContent] = useState(content);
    const [exitStart, setExitStart] = useState(-1);

    const onClick = () => {
        if(activeNoteBlock != order)
            dispatch(doActiveNoteBlock(order));
    }

    const setReviseProxy = async () => {
        if(revise == true && reviseContent != content) {
            const block = {
                id: id,
                type: 'Text',
                content: reviseContent
            }
            const response = await post('/note/block/update', block);
            if(response.status == 200) {
                setBlock(block);
            }
        } 
        setRevise(!revise);
        if(revise == false) {
            setTimeout(() => {
                const element = document.getElementById('ta');
                element.style.height = `${element.scrollHeight}px`;
            }, 0);
        }
    };

    useEffect(() => {
        if(activeNoteBlock === order) 
            setActive('note-block-content-active');
        else if(active === 'note-block-content-active')
            setActive('');
    }, [activeNoteBlock])

    const optionShowEvent = useCallback(() => {
        if(visibility !== 'visible')
            setvisibility('visible');
    }, [visibility]);
    const optionHidevent = useCallback(() => {
        if(visibility !== 'hidden') {
            setvisibility('hidden');
        }
    }, [visibility]);

    const onInput = (e) => {
        e.target.style.height = '5px'; //height 리셋. 문장을 지울때 다시 줄어들게 해줌.
        e.target.style.height = `${e.target.scrollHeight}px`;
        // 최대값 설정시
        //e.target.style.height = `${Math.min(e.target.scrollHeight, limit)}px`

        setReviseContent(e.target.value);
        if(e.nativeEvent.data === exitStr.charAt(exitState)) {
            if(exitState === 0)
                setExitStart(e.target.selectionStart - 1);
            setExitState(exitState + 1);
        }
        else if(exitState !== 0) setExitState(0);

        if(exitState === 4) {
            //console.log(reviseContent.substring(exitStart+4, reviseContent.length));
            let str = reviseContent.substring(0, exitStart) + reviseContent.substring(exitStart + 4, reviseContent.length);
            
            //네트워크 요청으로 수정한 후 revise를 false로 바꾸자
            dispatch(setNoteBlockContent(str, order));

            setReviseContent(str);
            setRevise(false);
        }
    }

    const onDeleteConfirm = async () => {
        const response = await post('/note/block/delete', {bid: id});
        if(response.status == 200) {
            blocks.splice(order, 1);
            setBlocks([...blocks]);
        }
    }

    const handleUp = async () => {
        if(order === 0) return;

        const response = await post('note/block/up', {bid: id, nid})
        if(response.status === 200) {
            [blocks[order], blocks[order-1]] = [blocks[order-1], blocks[order]];
            setBlocks([...blocks]);
        }
    }

    const handleDown = async () => {
        if(order === blocks.length - 1) return;

        const response = await post('note/block/down', {bid: id, nid})
        if(response.status === 200) {
            [blocks[order], blocks[order+1]] = [blocks[order+1], blocks[order]];
            setBlocks([...blocks]);
        }
    }

    return (
    <div className='note-block' 
         onMouseOver={optionShowEvent} 
         onMouseLeave={optionHidevent} 
         onClick={optionShowEvent}>
        {/* <div className="option" style={{visibility}}>
            <FiSettings id="setting"/>
        </div> */}
        <div onClick={onClick} 
             className={`note-block-content ${active}`} >
                 <div className="option-container" style={{visibility}}>
                    <Switch 
                        isOn={revise}
                        handleToggle={setReviseProxy}
                        id={order}
                        color="#BEC9E5"
                    />
                    <span className="note-button" 
                        onClick={() => {if (window.confirm('Are you sure you wish to delete this note?')) onDeleteConfirm(); }}>X</span>
                    <i className='bx bx-up-arrow-alt note-button' onClick={() => handleUp()}></i>
                    <i className='bx bx-down-arrow-alt note-button' onClick={() => handleDown()}></i>
                 </div>
                 {  !revise && 
                    <ReactMarkdown children={reviseContent} />
                 }
                 {
                     revise &&
                     <textarea 
                        id="ta"
                        value={reviseContent}
                        className='note-block-revise'
                        onInput={onInput} />
                 }
        </div>
    </div>
    )
}