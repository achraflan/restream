import React from 'react';
import './iframePlayer.scss';

interface IframePlayerProps {
  src: string;
}

export default function IframePlayer({ src }: IframePlayerProps) {
  return (
    <div className="container-iframe">
      <iframe
        className="responsive-iframe"
        src={src}
        title="Restream Player"
        frameBorder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; fullscreen"
        allowFullScreen
      ></iframe>
    </div>
  );
}
