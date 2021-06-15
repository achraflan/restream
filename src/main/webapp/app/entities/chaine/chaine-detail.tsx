import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Card, CardImg, CardText, CardBody, CardSubtitle, Button, CardHeader } from 'reactstrap';

import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './chaine.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IChaineDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ChaineDetail = (props: IChaineDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
    // props.getEntityChemin(props.match.params.id);
  }, []);

  const { chaineEntity } = props;
  return (
    <Card className="jh-entity-details">
      <CardHeader tag="h5" data-cy="chaineDetailsHeading">
        {' '}
        Chaine
      </CardHeader>
      <CardImg top style={{ width: 'fit-content' }} id="chaineImage" src={chaineEntity.chaineImage} alt="Card image cap" />
      <CardBody>
        <CardSubtitle tag="h6" id="chaineActive" className="mb-2 text-muted">
          Acive: {chaineEntity.chaineActive ? 'vrai' : 'faux'}
        </CardSubtitle>
        <CardText id="chaineNom">Nom : {chaineEntity.chaineNom}.</CardText>
        <CardText>Categorie : {chaineEntity.categorie ? chaineEntity.categorie.categorieNom : ''}.</CardText>
        <CardText>
          Tags :{' '}
          {chaineEntity.tags
            ? chaineEntity.tags.map((val, i) => (
                <span key={val.id}>
                  <a>{val.tagNom}</a>
                  {chaineEntity.tags && i === chaineEntity.tags.length - 1 ? '' : ', '}
                </span>
              ))
            : null}
          .
        </CardText>
        <CardText>
          Liste des chemins :{' '}
          {chaineEntity.chemins
            ? chaineEntity.chemins.map(val => (
                <Button
                  style={{ margin: '5px' }}
                  key={val.id}
                  tag={Link}
                  to={`/chemin/${val.id}`}
                  active={val.cheminMarche && val.cheminValide}
                  color={val.cheminMarche && val.cheminValide ? 'success' : 'danger'}
                >
                  {val.cheminNon.length > 20 ? val.cheminNon.substring(1, 20) + ' ...' : val.cheminNon}
                </Button>
              ))
            : null}
        </CardText>
        <Button tag={Link} to="/chaine" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/chaine/${chaineEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </CardBody>
    </Card>
  );
};

const mapStateToProps = ({ chaine }: IRootState) => ({
  chaineEntity: chaine.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ChaineDetail);
