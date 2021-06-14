import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Chaine from './chaine';
import ChaineDetail from './chaine-detail';
import ChaineUpdate from './chaine-update';
import ChaineDeleteDialog from './chaine-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ChaineUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ChaineUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ChaineDetail} />
      <ErrorBoundaryRoute path={match.url} component={Chaine} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ChaineDeleteDialog} />
  </>
);

export default Routes;
