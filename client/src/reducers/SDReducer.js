import { ACTIVE_SD_RESIZING, ACTIVE_SD_MOVING, ACTIVE_SD_POINT } from "../actions";

export default function(state = {}, action) {
    switch(action.type) {
        case ACTIVE_SD_RESIZING:
            return {...state, resizingId: action.payload};
        case ACTIVE_SD_MOVING:
            return {...state, movingId: action.payload};
        case ACTIVE_SD_POINT:
            return {...state, point: action.payload};
        default:
            return state;
    }
}