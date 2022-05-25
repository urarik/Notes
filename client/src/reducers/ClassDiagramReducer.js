import { ACTIVE_CD_RESIZING, ACTIVE_CD_MOVING, ACTIVE_CD_POINT } from "../actions";

export default function(state = {}, action) {
    switch(action.type) {
        case ACTIVE_CD_RESIZING:
            return {...state, resizingId: action.payload};
        case ACTIVE_CD_MOVING:
            return {...state, movingId: action.payload};
        case ACTIVE_CD_POINT:
            return {...state, point: action.payload};
        default:
            return state;
    }
}