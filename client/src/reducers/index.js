import { combineReducers } from 'redux';
import DoActiveNoteBlockReducer from './reducer_active_note_block';
import NoteReducer from './reducer_note';
import ProjectReducer from './ProjectReducer';
import UserReducer from './UserReducer.js';
import BlockReducer from './BlockReducer'

import storage from 'redux-persist/lib/storage';
import persistReducer from 'redux-persist/es/persistReducer';

const persistConfig = {
    key: "root",
    storage,
    whitelist: ["username", "project"]
};

const rootReducer = combineReducers({
    username: UserReducer,
    project: ProjectReducer,
    note: NoteReducer,
    activeNoteBlock: DoActiveNoteBlockReducer,
    blockList: BlockReducer
});

export default persistReducer(persistConfig, rootReducer);
