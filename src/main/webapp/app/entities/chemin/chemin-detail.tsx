import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Card, CardText, CardBody, CardSubtitle, Button, CardHeader } from 'reactstrap';

import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './chemin.reducer';
import VideoPlayer from '../../shared/util/videoPlayer';

export interface ICheminDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CheminDetail = (props: ICheminDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { cheminEntity } = props;
  return (
    <Card className="jh-entity-details">
      <CardHeader tag="h5" data-cy="cheminDetailsHeading">
        {' '}
        Chemin
      </CardHeader>
      <CardBody>
        <CardSubtitle tag="h6" id="cheminValide" className="mb-2 text-muted">
          Valide: {cheminEntity.cheminValide ? 'true' : 'false'}
        </CardSubtitle>
        <CardSubtitle tag="h6" id="cheminMarche" className="mb-2 text-muted">
          Running: {cheminEntity.cheminMarche ? 'true' : 'false'}
        </CardSubtitle>
        <CardText id="cheminDescription">
          <span>Chemin Description : </span> {cheminEntity.cheminDescription}
        </CardText>
        <CardText id="chaineNom">Chaine : {cheminEntity.chaine ? cheminEntity.chaine.chaineNom : ''}.</CardText>
        {cheminEntity.type === 'Embed' && cheminEntity.cheminMarche && <div dangerouslySetInnerHTML={{ __html: cheminEntity.cheminNon }} />}
        {cheminEntity.type !== 'Embed' && cheminEntity.cheminValide && <VideoPlayer src={cheminEntity.cheminNon} />}
        <CardText></CardText>
        <Button tag={Link} to="/chemin" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/chemin/${cheminEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </CardBody>
    </Card>
  );
};

const mapStateToProps = ({ chemin }: IRootState) => ({
  cheminEntity: chemin.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CheminDetail);
