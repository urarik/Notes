import { useEffect, useState, useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import { post } from "../api/api";

export default function ({id, order, children, blocks, setBlocks, nid}) {
    const dispatch = useDispatch();
    const [active, setActive] = useState('');
    const [visibility, setvisibility] = useState('hidden');

    const optionShowEvent = useCallback(() => {
        if(visibility !== 'visible')
            setvisibility('visible');
    }, [visibility]);
    const optionHidevent = useCallback(() => {
        if(visibility !== 'hidden') {
            setvisibility('hidden');
        }
    }, [visibility]);

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
        <div className={`note-block-content ${active}`} >
                 <div className="option-container" style={{visibility}}>
                    <span className="note-button" 
                        onClick={() => {if (window.confirm('Are you sure you wish to delete this note?')) onDeleteConfirm(); }}>X</span>
                    <i className='bx bx-up-arrow-alt note-button' onClick={() => handleUp()}></i>
                    <i className='bx bx-down-arrow-alt note-button' onClick={() => handleDown()}></i>
                 </div>
            {children}
        </div>
    </div>
    )
}