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
      <h5 className="text-center ">
        Disclaimer: When content is provided and hosted by Another Website We are not responsible for and cannot be accountable for any of
        the content hosted on the third-party site For any legal complaints please contact the video/audio hosting provider
      </h5>
    </Alert>
  );
};
export default Copyright;
