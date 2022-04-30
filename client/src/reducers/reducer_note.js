import { FETCH_NOTE, SET_NOTE_BLOCK_CONTENT } from "../actions";
import _ from 'lodash';

export default function(state = [], action) {
    switch(action.type) {
        case FETCH_NOTE:
            return _.map(action.payload, function(content, i) {
                return _.extend(content, {idx: i});
            });
        case SET_NOTE_BLOCK_CONTENT:
            return state.map((item) => {
                return item.idx == action.payload.order? {content: action.payload.content, idx: item.idx} :
                item;
            })
        default:
            return state;
    }
}