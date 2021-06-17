import './report.scss';
import React from 'react';
import { Modal, ModalBody, Button, FormGroup } from 'reactstrap';
import { AvForm, AvField } from 'availity-reactstrap-validation';

const Report = (props: any) => {
  const { modal, handleClose, confirmReport } = props;

  return (
    <Modal isOpen={modal} toggle={handleClose}>
      <ModalBody id="report.question">
        <AvForm className="section-form" id="contact-form" onValidSubmit={confirmReport}>
          <h5 className="text-align">تبليغ </h5>
          <AvField
            className="input-form"
            name="message"
            placeholder={'نص التبليغ'}
            type="textarea"
            rows="5"
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
      </ModalBody>
    </Modal>
  );
};
export default Report;
