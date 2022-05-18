import { ACTIVE_CLASS } from "../actions";

export default function(state = {}, action) {
    switch(action.type) {
        case ACTIVE_CLASS:
            //id, name
            return action.payload;
        default:
            return state;
    }
}