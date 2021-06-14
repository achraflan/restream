import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Chemin from './chemin';
import CheminDetail from './chemin-detail';
import CheminUpdate from './chemin-update';
import CheminDeleteDialog from './chemin-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CheminUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CheminUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CheminDetail} />
      <ErrorBoundaryRoute path={match.url} component={Chemin} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CheminDeleteDialog} />
  </>
);

export default Routes;
