import { SET_CLOSE } from "../actions";

export default function(state = "", action) {
    switch(action.type) {
        case SET_CLOSE:
            return action.payload;
        default:
            return state;
    }
}