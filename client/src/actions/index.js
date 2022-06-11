export const MANI_SIDEBAR = "mani_sidebar";
export const FETCH_NOTE = "fetch_note";
export const ACTIVE_NOTE_BLOCK = "active_note_block";
export const SET_NOTE_BLOCK_CONTENT = "set_note_block_content";

export const FETCH_PROJECT = "fetch_project";
export const FETCH_USER = "fetch_user";

export const FETCH_BLOCKS = "fetch_blocks";
export const DELETE_BLOCK = "delete_block";
export const ADD_BLOCK = "add_block";
export const UPDATE_BLOCK = "update_block";

export const ACTIVE_CLASS = "active_class";

export const ACTIVE_CD_RESIZING = "active_cd_resizing";
export const ACTIVE_CD_MOVING = "active_cd_moving";
export const ACTIVE_CD_POINT = "active_cd_point";

export const ACTIVE_SD_RESIZING = "active_sd_resizing";
export const ACTIVE_SD_MOVING = "active_sd_moving";
export const ACTIVE_SD_POINT = "active_sd_point";

export const SET_CLOSE = "set_close";

export function fetchNote(id) {
    const note = [
        {
            content: '# H1!'
        },
        {
            content: '## H2!'
        }
    ];

    return {
        type: FETCH_NOTE,
        payload: note
    };
}

export function fetchProject(project) {
    return {
        type: FETCH_PROJECT,
        payload: project
    };
}
export function fetchUser(username) {
    return {
        type: FETCH_USER,
        payload: username
    };
}

export function doActiveNoteBlock(id) {
    return {
        type: ACTIVE_NOTE_BLOCK,
        payload: id
    }
}

export function setNoteBlockContent(content, order) {
    return {
        type: SET_NOTE_BLOCK_CONTENT,
        payload: {
            content, order
        }
    };
}

export function fetchBlocks(blocks) {
    return {
        type: FETCH_BLOCKS,
        payload: blocks
    }
}

export function deleteBlock(bid) {
    return {
        type: DELETE_BLOCK,
        payload: bid
    }
}

export function addBlock(bid, content, type) {
    return {
        type: ADD_BLOCK,
        payload: {
            bid,
            content,
            type
        }
    }
}

export function updateBlock(block) {
    return {
        type: UPDATE_BLOCK,
        payload: block
    }
}

export function activeClass(classEntity) {
    return {
        type: ACTIVE_CLASS,
        payload: classEntity
    }
}

export function activeCdResizing(id) {
    return {
        type: ACTIVE_CD_RESIZING,
        payload: id
    };
}

export function activeCdMoving(id) {
    return {
        type: ACTIVE_CD_MOVING,
        payload: id
    };
}

export function activeCdPoint(point) {
    return {
        type: ACTIVE_CD_POINT,
        payload: point
    };
}

export function activeSdResizing(id) {
    return {
        type: ACTIVE_SD_RESIZING,
        payload: id
    };
}

export function activeSdMoving(id) {
    return {
        type: ACTIVE_SD_MOVING,
        payload: id
    };
}

export function activeSdPoint(point) {
    return {
        type: ACTIVE_SD_POINT,
        payload: point
    };
}

export function setGlobalClose(closed) {
    return {
        type: SET_CLOSE,
        package: closed
    }
}