import axios from 'axios';
import {
  parseHeaderForLinks,
  loadMoreDataWhenScrolled,
  ICrudGetAction,
  ICrudGetAllAction,
  ICrudPutAction,
  ICrudDeleteAction,
} from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IChemin, defaultValue } from 'app/shared/model/chemin.model';

export const ACTION_TYPES = {
  FETCH_CHEMIN_LIST: 'chemin/FETCH_CHEMIN_LIST',
  FETCH_CHEMIN: 'chemin/FETCH_CHEMIN',
  CREATE_CHEMIN: 'chemin/CREATE_CHEMIN',
  UPDATE_CHEMIN: 'chemin/UPDATE_CHEMIN',
  PARTIAL_UPDATE_CHEMIN: 'chemin/PARTIAL_UPDATE_CHEMIN',
  DELETE_CHEMIN: 'chemin/DELETE_CHEMIN',
  RESET: 'chemin/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IChemin>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type CheminState = Readonly<typeof initialState>;

// Reducer

export default (state: CheminState = initialState, action): CheminState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_CHEMIN_LIST):
    case REQUEST(ACTION_TYPES.FETCH_CHEMIN):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_CHEMIN):
    case REQUEST(ACTION_TYPES.UPDATE_CHEMIN):
    case REQUEST(ACTION_TYPES.DELETE_CHEMIN):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_CHEMIN):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_CHEMIN_LIST):
    case FAILURE(ACTION_TYPES.FETCH_CHEMIN):
    case FAILURE(ACTION_TYPES.CREATE_CHEMIN):
    case FAILURE(ACTION_TYPES.UPDATE_CHEMIN):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_CHEMIN):
    case FAILURE(ACTION_TYPES.DELETE_CHEMIN):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_CHEMIN_LIST): {
      const links = parseHeaderForLinks(action.payload.headers.link);

      return {
        ...state,
        loading: false,
        links,
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links),
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    }
    case SUCCESS(ACTION_TYPES.FETCH_CHEMIN):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_CHEMIN):
    case SUCCESS(ACTION_TYPES.UPDATE_CHEMIN):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_CHEMIN):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_CHEMIN):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/chemins';

// Actions

export const getEntities: ICrudGetAllAction<IChemin> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_CHEMIN_LIST,
    payload: axios.get<IChemin>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IChemin> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_CHEMIN,
    payload: axios.get<IChemin>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IChemin> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_CHEMIN,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const updateEntity: ICrudPutAction<IChemin> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_CHEMIN,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IChemin> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_CHEMIN,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IChemin> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_CHEMIN,
    payload: axios.delete(requestUrl),
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
