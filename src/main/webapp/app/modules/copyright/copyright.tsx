import './copyright.scss';
import React from 'react';
import { Alert } from 'reactstrap';

const Copyright = () => {
  return (
    <Alert color="light">
      <h5 className="text-center ">
        لا علاقة لنا بروابط النقل التي نقوم بأخذها من طرف ثالث ولا ننقل أي أحداث أو مباريات موقعنا عبارة عن وسيط فقط بين المشاهد و المحتوى و
        لسنا مسؤولين عن أي خرق للقوانين
      </h5>
    </Alert>
  );
};
export default Copyright;
