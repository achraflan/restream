import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Card, CardText, CardBody, CardSubtitle, Button, CardHeader } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './report.reducer';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IReportDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ReportDetail = (props: IReportDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { reportEntity } = props;
  return (
    <Card className="jh-entity-details">
      <CardHeader tag="h5" data-cy="reportDetailsHeading">
        {' '}
        Report
      </CardHeader>
      <CardBody>
        <CardSubtitle tag="h6" id="dateCreation" className="mb-2 text-muted">
          Date Creation:{' '}
          {reportEntity.dateCreation ? <TextFormat value={reportEntity.dateCreation} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
        </CardSubtitle>
        <CardText id="message">Message : {reportEntity.message}.</CardText>
        <CardText id="chemin">
          {' '}
          Chemin :{' '}
          {reportEntity.chemin && (
            <Button tag={Link} to={`/chemin/${reportEntity.chemin.id}`} color="link" data-cy="entityCheminButton">
              {' '}
              {reportEntity.chemin.id}{' '}
            </Button>
          )}
        </CardText>
        <Button tag={Link} to="/report" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/report/${reportEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </CardBody>
    </Card>
  );
};

const mapStateToProps = ({ report }: IRootState) => ({
  reportEntity: report.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ReportDetail);
