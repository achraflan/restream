import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Card, CardImg, CardText, CardBody, CardSubtitle, Button, CardHeader } from 'reactstrap';

import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './categorie.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ICategorieDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CategorieDetail = (props: ICategorieDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { categorieEntity } = props;
  return (
    <Card className="jh-entity-details">
      <CardHeader tag="h5" data-cy="categorieDetailsHeading">
        {' '}
        Categorie
      </CardHeader>
      <CardImg top width="18rem" src={categorieEntity.categorieImage} alt="Card image cap" />
      <CardBody>
        <CardSubtitle tag="h6" className="mb-2 text-muted">
          Acive: {categorieEntity.categorieActive ? 'vrai' : 'faux'}
        </CardSubtitle>
        <CardText id="categorieNom">Nom : {categorieEntity.categorieNom}.</CardText>
        <Button tag={Link} to="/categorie" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/categorie/${categorieEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </CardBody>
    </Card>
  );
};

const mapStateToProps = ({ categorie }: IRootState) => ({
  categorieEntity: categorie.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CategorieDetail);
