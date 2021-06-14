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

import { IChaine, defaultValue } from 'app/shared/model/chaine.model';
import { IChemin } from 'app/shared/model/chemin.model';

export const ACTION_TYPES = {
  FETCH_CHAINE_LIST: 'chaine/FETCH_CHAINE_LIST',
  FETCH_CHEMIN_LIST: 'chaine/FETCH_CHEMIN_LIST',
  FETCH_CHAINE: 'chaine/FETCH_CHAINE',
  CREATE_CHAINE: 'chaine/CREATE_CHAINE',
  UPDATE_CHAINE: 'chaine/UPDATE_CHAINE',
  PARTIAL_UPDATE_CHAINE: 'chaine/PARTIAL_UPDATE_CHAINE',
  DELETE_CHAINE: 'chaine/DELETE_CHAINE',
  RESET: 'chaine/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IChaine>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type ChaineState = Readonly<typeof initialState>;

// Reducer

export default (state: ChaineState = initialState, action): ChaineState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_CHAINE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_CHAINE):
    case REQUEST(ACTION_TYPES.FETCH_CHEMIN_LIST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_CHAINE):
    case REQUEST(ACTION_TYPES.UPDATE_CHAINE):
    case REQUEST(ACTION_TYPES.DELETE_CHAINE):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_CHAINE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_CHAINE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_CHAINE):
    case FAILURE(ACTION_TYPES.FETCH_CHEMIN_LIST):
    case FAILURE(ACTION_TYPES.CREATE_CHAINE):
    case FAILURE(ACTION_TYPES.UPDATE_CHAINE):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_CHAINE):
    case FAILURE(ACTION_TYPES.DELETE_CHAINE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_CHAINE_LIST): {
      const links = parseHeaderForLinks(action.payload.headers.link);

      return {
        ...state,
        loading: false,
        links,
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links),
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    }
    case SUCCESS(ACTION_TYPES.FETCH_CHEMIN_LIST): {
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    }
    case SUCCESS(ACTION_TYPES.FETCH_CHAINE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_CHAINE):
    case SUCCESS(ACTION_TYPES.UPDATE_CHAINE):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_CHAINE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_CHAINE):
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

const apiUrl = 'api/chaines';

// Actions

export const getEntities: ICrudGetAllAction<IChaine> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_CHAINE_LIST,
    payload: axios.get<IChaine>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntityChemin: ICrudGetAction<IChemin> = id => {
  const requestUrl = `${apiUrl}/${id}/chemins`;
  /* eslint no-console: off */
  console.log('requestUrl:', requestUrl);
  return {
    type: ACTION_TYPES.FETCH_CHEMIN_LIST,
    payload: axios.get<IChemin>(requestUrl),
  };
};

export const getEntity: ICrudGetAction<IChaine> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_CHAINE,
    payload: axios.get<IChaine>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IChaine> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_CHAINE,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const updateEntity: ICrudPutAction<IChaine> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_CHAINE,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IChaine> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_CHAINE,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IChaine> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_CHAINE,
    payload: axios.delete(requestUrl),
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
