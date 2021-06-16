import './footer.scss';

import React from 'react';

import { Navbar, Nav, NavItem, NavLink, NavbarText } from 'reactstrap';

const Footer = props => (
  <div className="footer page-content">
    <Navbar data-cy="navbar" dark expand="sm" fixed="bottom" className="footer-nav">
      <Nav className="mr-auto" navbar>
        <NavItem>
          <NavLink className="footer-NavLink" href="/">
            اتصل بنا
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink className="footer-NavLink" href="/">
            سياسة الاستخدام
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink className="footer-NavLink" href="/">
            Copyright
          </NavLink>
        </NavItem>
      </Nav>
      <NavbarText className="d-flex align-items-center">كل الحقوق محفوظة © 2021</NavbarText>
    </Navbar>
  </div>
);

export default Footer;
