import { ACTIVE_NOTE_BLOCK } from "../actions";
import _ from 'lodash';

export default function(state = -1, action) {
    switch(action.type) {
        case ACTIVE_NOTE_BLOCK:
            return action.payload;
        default:
            return state;
    }
}