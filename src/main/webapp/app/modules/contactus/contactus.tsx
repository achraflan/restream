import './contactus.scss';
import React from 'react';
import { Alert } from 'reactstrap';
import { Col, Row, Button, FormGroup } from 'reactstrap';
import { AvForm, AvField } from 'availity-reactstrap-validation';

const ContactUs = () => {
  const handleValidSubmit = (event, values) => {
    //
  };

  return (
    <Alert color="light">
      <h3 className="text-center">يسعدنا دائمًا استقبال استفساراتكم</h3>
      <AvForm className="section-form" id="contact-form" onValidSubmit={handleValidSubmit}>
        <Row form>
          <Col md={6}>
            <AvField
              className="input-form"
              name="username"
              label="الإسم"
              placeholder={'الإسم'}
              type="text"
              validate={{
                required: { value: true, errorMessage: 'يرجى كتابة اسمك' },
              }}
              data-cy="username"
            />
          </Col>
          <Col md={6}>
            <AvField
              errorMessage="صيغة الايميل غير صحيحه"
              className="input-form"
              name="email"
              label="البريد الإلكتروني"
              placeholder={'البريد الإلكتروني'}
              type="email"
              validate={{
                required: { value: true, errorMessage: 'يرجى كتابة الايميل الخاص بك' },
              }}
              data-cy="email"
            />
          </Col>
        </Row>
        <AvField
          className="input-form"
          name="message"
          label="نص الرسالة"
          placeholder={'نص الرسالة'}
          type="textarea"
          validate={{
            required: { value: true, errorMessage: 'يرجى كتابة الرسالة' },
          }}
          data-cy="message"
        />
        <FormGroup className="text-center">
          <Button size="lg" type="submit" data-cy="submit">
            إرســـــال
          </Button>
        </FormGroup>
      </AvForm>
    </Alert>
  );
};
export default ContactUs;
