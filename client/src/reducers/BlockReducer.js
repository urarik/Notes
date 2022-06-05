import { FETCH_BLOCKS, DELETE_BLOCK, ADD_BLOCK, UPDATE_BLOCK } from "../actions";

export default function(state = [], action) {
    switch(action.type) {
        case FETCH_BLOCKS:
            return action.payload;
        case DELETE_BLOCK:
            return state.filter(block => block.id != action.payload);
        case ADD_BLOCK:
            if(action.payload.type === 'CD' ||
               action.payload.type === 'SD') {

               }
            return [...state, action.payload];
        case UPDATE_BLOCK:
            return state.map(block => {
                if(block.id == action.payload.id) {
                    block.content = action.payload.content;
                    block.type = action.payload.type;
                } 
                return block;
            })
        default:
            return state;
    }
}