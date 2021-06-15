import './home.scss';

import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import classnames from 'classnames';

import { connect } from 'react-redux';
import { Row, Col } from 'reactstrap';
import { ListGroup, ListGroupItem } from 'reactstrap';
import { Nav, NavItem, NavLink, Card, CardHeader, CardBody } from 'reactstrap';
import { Button, ButtonGroup, ButtonToolbar } from 'reactstrap';

import VideoPlayer from '../../shared/util/videoPlayer';

import { getEntities as getCategories } from '../../entities/categorie/categorie.reducer';
import { getEntities as getChanels } from '../../entities/chaine/chaine.reducer';

export interface IHomeProp extends StateProps, DispatchProps {}

export const Home = (props: IHomeProp) => {
  const [activeChaine, setActiveChaine] = useState(null);
  const [activeLink, setActiveLink] = useState(null);
  const [Links, setLinks] = useState(null);

  const getChaines = event => {
    /* eslint-disable no-console */
    console.log('we will use this to get channels.');
    setActiveChaine(null);
    setLinks(null);
    props.getChanels();
  };

  const toggle = chaine => {
    if (activeChaine !== chaine) {
      setActiveLink(null);
      setActiveChaine(chaine);
      if (chaine.chemins.length > 0) {
        setLinks(chaine.chemins);
        setActiveLink(chaine.chemins[0]);
      }
    }
  };

  const getAllEntities = () => {
    props.getCategories(0, 20, '');
  };

  useEffect(() => {
    getAllEntities();
  }, []);

  const { categorieList, loadingCategorieList, account, chainesList, loadingChainesList } = props;
  return (
    <Row>
      <Col md="3">
        {categorieList && categorieList.length > 0 ? (
          <ListGroup>
            <ListGroupItem disabled className="justify-content-between" style={{ color: '#fff', background: '#28a0d6' }}>
              Categorie
            </ListGroupItem>
            {categorieList.map(categorie => (
              <ListGroupItem key={categorie.id} className="justify-content-between" onClick={getChaines}>
                {categorie.categorieNom}{' '}
              </ListGroupItem>
            ))}
          </ListGroup>
        ) : (
          !loadingCategorieList && <div className="alert alert-warning">No Categories found</div>
        )}
      </Col>
      <Col md="9">
        {chainesList && chainesList.length > 0 ? (
          <Nav tabs>
            {chainesList.map(chaine => (
              <NavItem key={chaine.id}>
                <NavLink
                  className={classnames({ active: activeChaine?.id === chaine?.id })}
                  onClick={() => {
                    toggle(chaine);
                  }}
                >
                  {chaine.chaineNom}
                </NavLink>
              </NavItem>
            ))}
          </Nav>
        ) : (
          !loadingChainesList && <div className="alert alert-warning">No Channel found</div>
        )}
        {activeChaine && (
          <Card>
            <CardHeader tag="h5" data-cy="cheminDetailsHeading">
              {activeChaine.chaineNom}
            </CardHeader>
            <CardBody>
              {activeLink && (
                <>
                  {activeLink.type === 'Embed' && activeLink.cheminMarche && (
                    <div dangerouslySetInnerHTML={{ __html: activeLink.cheminNon }} />
                  )}
                  {activeLink.type !== 'Embed' && activeLink.cheminValide && <VideoPlayer src={activeLink.cheminNon} />}
                </>
              )}
              {Links && Links.length > 0 && (
                <ButtonGroup>
                  {Links.map((link, i) => (
                    <Button key={link.id}>{i + 1}</Button>
                  ))}
                </ButtonGroup>
              )}
            </CardBody>
          </Card>
        )}
      </Col>
    </Row>
  );
};

const mapStateToProps = storeState => ({
  categorieList: storeState.categorie.entities,
  loadingCategorieList: storeState.categorie.loading,
  chainesList: storeState.chaine.entities,
  loadingChainesList: storeState.chaine.loading,
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
});

const mapDispatchToProps = {
  getCategories,
  getChanels,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Home);
