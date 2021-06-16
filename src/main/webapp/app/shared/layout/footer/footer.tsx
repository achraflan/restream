import './footer.scss';

import React from 'react';
import { NavLink as Link } from 'react-router-dom';
import { Navbar, Nav, NavItem, NavLink, NavbarText } from 'reactstrap';

const Footer = props => (
  <div className="footer page-content">
    <Navbar data-cy="navbar" dark expand="sm" fixed="bottom" className="footer-nav">
      <Nav className="mr-auto" navbar>
        <NavItem>
          <NavLink className="footer-NavLink" tag={Link} to="/contact">
            اتصل بنا
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink className="footer-NavLink" tag={Link} to="/policy">
            سياسة الاستخدام
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink className="footer-NavLink" tag={Link} to="/copyright">
            Copyright
          </NavLink>
        </NavItem>
      </Nav>
      <NavbarText className="d-flex align-items-center">كل الحقوق محفوظة © 2021</NavbarText>
    </Navbar>
  </div>
);

export default Footer;
