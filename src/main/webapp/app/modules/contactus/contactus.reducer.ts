import axios from 'axios';

import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

export const ACTION_TYPES = {
  SEND_MAIL: 'account/SEND_MAIL',
  RESET: 'account/RESET',
};

const initialState = {
  sendSuccess: false,
  sendFailure: false,
};

export type MailState = Readonly<typeof initialState>;

// Reducer
export default (state: MailState = initialState, action): MailState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEND_MAIL):
      return {
        ...state,
      };
    case FAILURE(ACTION_TYPES.SEND_MAIL):
      return {
        ...state,
        sendFailure: true,
      };
    case SUCCESS(ACTION_TYPES.SEND_MAIL):
      return {
        ...state,
        sendSuccess: true,
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

// Actions
export const sendMailAction = (mail : string, username : string, content: string) => ({
  type: ACTION_TYPES.SEND_MAIL,
  payload: axios.post('api/send-mail', { mail, username, content }),
  meta: {
    successMessage: '<strong>Your mail has been sent.</strong>',
  },
});


export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
