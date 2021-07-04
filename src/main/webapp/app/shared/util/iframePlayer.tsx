import React from 'react';
import './iframePlayer.scss';

interface IframePlayerProps {
  src: string;
}

export default function IframePlayer({ src }: IframePlayerProps) {
  return (
    <div className="container-iframe">
      <iframe
        height="100%"
        className="responsive-iframe"
        src={src}
        title="Restream Player"
        frameBorder="0"
        allowFullScreen
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; fullscreen *"
      ></iframe>
    </div>
  );
}
