import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Label, Card, CardHeader, CardBody } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ICategorie } from 'app/shared/model/categorie.model';
import { getEntities as getCategories } from 'app/entities/categorie/categorie.reducer';
import { ITag } from 'app/shared/model/tag.model';
import { getEntities as getTags } from 'app/entities/tag/tag.reducer';
import { getEntity, updateEntity, createEntity, reset } from './chaine.reducer';
import { IChaine } from 'app/shared/model/chaine.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IChaineUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ChaineUpdate = (props: IChaineUpdateProps) => {
  const [idstags, setIdstags] = useState([]);
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { chaineEntity, categories, tags, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/chaine');
  };

  useEffect(() => {
    if (!isNew) {
      props.getEntity(props.match.params.id);
    }

    props.getCategories();
    props.getTags();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...chaineEntity,
        ...values,
        tags: mapIdList(values.tags),
        categorie: categories.find(it => it.id.toString() === values.categorieId.toString()),
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
        <CardHeader tag="h2" id="restreamApp.chaine.home.createOrEditLabel" data-cy="ChaineCreateUpdateHeading">
          {' '}
          Create or edit a Chaine
        </CardHeader>
        <CardBody>
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : chaineEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="chaine-id">ID</Label>
                  <AvInput id="chaine-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="chaineNomLabel" for="chaine-chaineNom">
                  Chaine Nom
                </Label>
                <AvField id="chaine-chaineNom" data-cy="chaineNom" type="text" name="chaineNom" />
              </AvGroup>
              <AvGroup>
                <Label id="chaineImageLabel" for="chaine-chaineImage">
                  Chaine Image
                </Label>
                <AvField id="chaine-chaineImage" data-cy="chaineImage" type="text" name="chaineImage" />
              </AvGroup>
              <AvGroup check>
                <Label id="chaineActiveLabel">
                  <AvInput
                    id="chaine-chaineActive"
                    data-cy="chaineActive"
                    type="checkbox"
                    className="form-check-input"
                    name="chaineActive"
                  />
                  Chaine Active
                </Label>
              </AvGroup>
              <AvGroup>
                <Label for="chaine-categorie">Categorie</Label>
                <AvInput id="chaine-categorie" data-cy="categorie" type="select" className="form-control" name="categorieId">
                  <option value="" key="0" />
                  {categories
                    ? categories.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.categorieNom}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="chaine-tags">Tags</Label>
                <AvInput
                  id="chaine-tags"
                  data-cy="tags"
                  type="select"
                  multiple
                  className="form-control"
                  name="tags"
                  value={!isNew && chaineEntity.tags && chaineEntity.tags.map(e => e.id)}
                >
                  <option value="" key="0" />
                  {tags
                    ? tags.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.tagNom}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/chaine" replace color="info">
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
  categories: storeState.categorie.entities,
  tags: storeState.tag.entities,
  chaineEntity: storeState.chaine.entity,
  loading: storeState.chaine.loading,
  updating: storeState.chaine.updating,
  updateSuccess: storeState.chaine.updateSuccess,
});

const mapDispatchToProps = {
  getCategories,
  getTags,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ChaineUpdate);
