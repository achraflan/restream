import './footer.scss';

import React from 'react';

import { Navbar, Nav, NavItem, NavLink } from 'reactstrap';

const Footer = props => (
  <div className="footer page-content">
    <Navbar fixed="bottom">
      <Nav className="mr-auto" navbar>
        <NavItem>
          <NavLink href="/">Contacter nous</NavLink>
        </NavItem>
      </Nav>
    </Navbar>
  </div>
);

export default Footer;
