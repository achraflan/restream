import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Label, Card, CardHeader, CardBody } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './categorie.reducer';
import { ICategorie } from 'app/shared/model/categorie.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ICategorieUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CategorieUpdate = (props: ICategorieUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { categorieEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/categorie' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...categorieEntity,
        ...values,
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Card>
        <CardHeader tag="h2" id="restreamApp.categorie.home.createOrEditLabel" data-cy="CategorieCreateUpdateHeading">
          {' '}
          Create or edit a Categorie
        </CardHeader>
        <CardBody>
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : categorieEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="categorie-id">ID</Label>
                  <AvInput id="categorie-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="categorieNomLabel" for="categorie-categorieNom">
                  Categorie Nom
                </Label>
                <AvField id="categorie-categorieNom" data-cy="categorieNom" type="text" name="categorieNom" />
              </AvGroup>
              <AvGroup>
                <Label id="categorieImageLabel" for="categorie-categorieImage">
                  Categorie Image
                </Label>
                <AvField id="categorie-categorieImage" data-cy="categorieImage" type="text" name="categorieImage" />
              </AvGroup>
              <AvGroup check>
                <Label id="categorieActiveLabel">
                  <AvInput
                    id="categorie-categorieActive"
                    data-cy="categorieActive"
                    type="checkbox"
                    className="form-check-input"
                    name="categorieActive"
                  />
                  Categorie Active
                </Label>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/categorie" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </AvForm>
          )}
        </CardBody>
      </Card>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  categorieEntity: storeState.categorie.entity,
  loading: storeState.categorie.loading,
  updating: storeState.categorie.updating,
  updateSuccess: storeState.categorie.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CategorieUpdate);
