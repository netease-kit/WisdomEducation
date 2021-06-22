import { Message } from './chatroomHelper';

export interface InitialState {
  messages: Message[];
}

export const initialState: InitialState = { messages: [] };

export const reducer = (
  state: InitialState,
  action: { type: string; payload: any },
): InitialState => {
  switch (action.type) {
    case 'updateMessages':
      return { messages: [...action.payload] };
    case 'addMessage':
      return { messages: state.messages.concat(action.payload) };
    case 'updateMessage':
      return {
        messages: state.messages.map((item) =>
          item.idClient === action.payload.idClient
            ? {
              ...item,
              ...action.payload,
            }
            : item,
        ),
      };
    case 'removeMessage':
      return {
        messages: state.messages.filter(
          (item) => item.idClient !== action.payload.idClient,
        ),
      };
    default:
      return state;
  }
};
