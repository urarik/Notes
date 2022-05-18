import { ACTIVE_RESIZING, ACTIVE_MOVING, ACTIVE_POINT } from "../actions";

export default function(state = {}, action) {
    switch(action.type) {
        case ACTIVE_RESIZING:
            return {...state, resizingId: action.payload};
        case ACTIVE_MOVING:
            return {...state, movingId: action.payload};
        case ACTIVE_POINT:
            return {...state, point: action.payload};
        default:
            return state;
    }
}