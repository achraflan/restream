import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Label, Card, CardHeader, CardBody } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntities as getChaines } from 'app/entities/chaine/chaine.reducer';
import { getEntity, updateEntity, createEntity, reset } from './chemin.reducer';

export interface ICheminUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CheminUpdate = (props: ICheminUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { cheminEntity, chaines, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/chemin');
  };

  useEffect(() => {
    if (!isNew) {
      props.getEntity(props.match.params.id);
    }

    props.getChaines();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...cheminEntity,
        ...values,
        chaine: chaines.find(it => it.id.toString() === values.chaineId.toString()),
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
        <CardHeader tag="h2" id="restreamApp.chemin.home.createOrEditLabel" data-cy="CheminCreateUpdateHeading">
          {' '}
          Create or edit a Chemin
        </CardHeader>
        <CardBody>
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : cheminEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="chemin-id">ID</Label>
                  <AvInput id="chemin-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="cheminNonLabel" for="chemin-cheminNon">
                  Chemin Nom
                </Label>
                <AvField id="chemin-cheminNon" data-cy="cheminNon" type="text" name="cheminNon" />
              </AvGroup>
              <AvGroup>
                <Label id="cheminDescriptionLabel" for="chemin-cheminDescription">
                  Chemin Description
                </Label>
                <AvField id="chemin-cheminDescription" data-cy="cheminDescription" type="text" name="cheminDescription" />
              </AvGroup>
              <AvGroup>
                <Label id="typeLabel" for="chemin-type">
                  Type
                </Label>
                <AvInput
                  id="chemin-type"
                  data-cy="type"
                  type="select"
                  className="form-control"
                  name="type"
                  value={(!isNew && cheminEntity.type) || 'Direct'}
                >
                  <option value="Direct">Direct</option>
                  <option value="Embed">Embed</option>
                </AvInput>
              </AvGroup>
              <AvGroup check>
                <Label id="cheminValideLabel">
                  <AvInput
                    id="chemin-cheminValide"
                    data-cy="cheminValide"
                    type="checkbox"
                    className="form-check-input"
                    name="cheminValide"
                  />
                  Chemin Valide
                </Label>
              </AvGroup>
              <AvGroup check>
                <Label id="cheminMarcheLabel">
                  <AvInput
                    id="chemin-cheminMarche"
                    data-cy="cheminMarche"
                    type="checkbox"
                    className="form-check-input"
                    name="cheminMarche"
                  />
                  Chemin Marche
                </Label>
              </AvGroup>
              <AvGroup>
                <Label for="chemin-chaine">Chaine</Label>
                <AvInput id="chemin-chaine" data-cy="chaine" type="select" className="form-control" name="chaineId">
                  <option value="" key="0" />
                  {chaines
                    ? chaines.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.chaineNom}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/chemin" replace color="info">
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
  chaines: storeState.chaine.entities,
  cheminEntity: storeState.chemin.entity,
  loading: storeState.chemin.loading,
  updating: storeState.chemin.updating,
  updateSuccess: storeState.chemin.updateSuccess,
});

const mapDispatchToProps = {
  getChaines,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CheminUpdate);
