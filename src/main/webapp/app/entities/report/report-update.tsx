import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Label, Card, CardHeader, CardBody } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntities as getChemins } from 'app/entities/chemin/chemin.reducer';
import { getEntity, updateEntity, createEntity, reset } from './report.reducer';

export interface IReportUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ReportUpdate = (props: IReportUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { reportEntity, chemins, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/report' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getChemins();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...reportEntity,
        ...values,
        chemin: chemins.find(it => it.id.toString() === values.cheminId.toString()),
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
        <CardHeader tag="h2" id="restreamApp.report.home.createOrEditLabel" data-cy="ReportCreateUpdateHeading">
          {' '}
          Create or edit a Report
        </CardHeader>
        <CardBody>
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : reportEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="report-id">ID</Label>
                  <AvInput id="report-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="messageLabel" for="report-message">
                  Message
                </Label>
                <AvField
                  id="report-message"
                  data-cy="message"
                  type="text"
                  name="message"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="dateCreationLabel" for="report-dateCreation">
                  Date Creation
                </Label>
                <AvField id="report-dateCreation" data-cy="dateCreation" type="date" className="form-control" name="dateCreation" />
              </AvGroup>
              <AvGroup>
                <Label for="report-chemin">Chemin</Label>
                <AvInput id="report-chemin" data-cy="chemin" type="select" className="form-control" name="cheminId">
                  <option value="" key="0" />
                  {chemins
                    ? chemins.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/report" replace color="info">
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
  chemins: storeState.chemin.entities,
  reportEntity: storeState.report.entity,
  loading: storeState.report.loading,
  updating: storeState.report.updating,
  updateSuccess: storeState.report.updateSuccess,
});

const mapDispatchToProps = {
  getChemins,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ReportUpdate);
