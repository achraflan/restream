import './home.scss';

import React, { useEffect, useState } from 'react';
import classnames from 'classnames';

import { connect } from 'react-redux';
import { Row, Col } from 'reactstrap';
import { ListGroup, ListGroupItem } from 'reactstrap';
import { Nav, NavItem, NavLink, Card, CardHeader, CardBody } from 'reactstrap';
import { Button, ButtonGroup, ButtonToolbar } from 'reactstrap';

import VideoPlayer from '../../shared/util/videoPlayer';
import Report from '../report/report';

import { getEntities as getCategories } from '../../entities/categorie/categorie.reducer';
import { getEntities as getChanels } from '../../entities/chaine/chaine.reducer';
import { createEntity as createReport } from '../../entities/report/report.reducer';
import { convertDateTimeToServer } from 'app/shared/util/date-utils';

export interface IHomeProp extends StateProps, DispatchProps {}

export const Home = (props: IHomeProp) => {
  const { categorieList, loadingCategorieList, account, chainesList, loadingChainesList } = props;

  const [activeChaine, setActiveChaine] = useState(null);
  const [activeLink, setActiveLink] = useState(null);
  const [Links, setLinks] = useState(null);
  const [timeoutID, setTimeoutID] = useState(null);
  const [modal, setModal] = useState(false);
  const [activeCategorie, setaAtiveCategorie] = useState(null);

  const getChaines = categorie => {
    setaAtiveCategorie(categorie);
    /* eslint-disable no-console */
    // console.log('we will use this to get channels.');
    setActiveChaine(null);
    setLinks(null);
    props.getChanels();
  };

  const handleChaine = link => {
    if (activeLink !== link) {
      setActiveLink(null);
      clearTimeout(timeoutID);
      setTimeoutID(
        setTimeout(() => {
          setActiveLink(link);
        }, 500)
      );
    }
  };

  const handleProblem = link => {
    setModal(true);
  };

  const closeModal = () => setModal(!modal);
  const confirmReport = (event, values) => {
    const entity = {
      ...values,
      cheminId: activeLink.id,
      dateCreation: convertDateTimeToServer(new Date()),
    };
    props.createReport(entity);
    setModal(!modal);
  };

  const toggle = chaine => {
    if (activeChaine !== chaine) {
      setActiveLink(null);
      setActiveChaine(null);
      clearTimeout(timeoutID);
      setTimeoutID(
        setTimeout(() => {
          setActiveChaine(chaine);
          if (chaine.chemins.length > 0) {
            setLinks(chaine.chemins);
            setActiveLink(chaine.chemins[0]);
          }
        }, 500)
      );
    }
  };

  const getAllEntities = () => {
    props.getCategories(0, 20, '');
  };

  useEffect(() => {
    getAllEntities();
  }, []);

  useEffect(() => {
    return () => window.clearTimeout(timeoutID);
  }, []);

  return (
    <>
      <Row>
        <Col md="3">
          {categorieList && categorieList.length > 0 ? (
            <ListGroup>
              <ListGroupItem disabled className="justify-content-between" style={{ color: '#fff', background: '#28a0d6' }}>
                Categorie
              </ListGroupItem>
              {categorieList.map(categorie => (
                <ListGroupItem key={categorie.id} className="justify-content-between" onClick={() => getChaines(categorie)}>
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
              {chainesList.map(
                chaine =>
                  chaine.chaineActive &&
                  activeCategorie &&
                  chaine.categorieId === activeCategorie.id && (
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
                  )
              )}
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
                {Links && Links.length > 0 && (
                  <ButtonGroup>
                    {Links.map(
                      (link, i) =>
                        link.cheminValide &&
                        link.cheminMarche && (
                          <Button key={link.id} onClick={() => handleChaine(link)}>
                            {link.cheminDescription}
                          </Button>
                        )
                    )}
                  </ButtonGroup>
                )}
                <div className="empty" />
                {activeLink && (
                  <>
                    {activeLink.type === 'Embed' && activeLink.cheminMarche && (
                      <div dangerouslySetInnerHTML={{ __html: activeLink.cheminNon }} />
                    )}
                    {activeLink.type !== 'Embed' && activeLink.cheminValide && <VideoPlayer src={activeLink.cheminNon} />}
                  </>
                )}
                <div className="empty" />
                {activeLink && (
                  <Button color="link" onClick={() => handleProblem(activeLink)}>
                    تبليغ
                  </Button>
                )}
              </CardBody>
            </Card>
          )}
        </Col>
      </Row>
      <Report modal={modal} handleClose={closeModal} confirmReport={confirmReport} />
    </>
  );
};

const mapStateToProps = storeState => ({
  categorieList: storeState.categorie.entities,
  loadingCategorieList: storeState.categorie.loading,
  chainesList: storeState.chaine.entities,
  loadingChainesList: storeState.chaine.loading,
  account: storeState.authentication.account,
  loadingReport: storeState.report.loading,
  isAuthenticated: storeState.authentication.isAuthenticated,
});

const mapDispatchToProps = {
  getCategories,
  getChanels,
  createReport,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Home);
