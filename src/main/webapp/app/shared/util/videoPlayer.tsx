import React, { useEffect } from 'react';
import Hls from 'hls.js';

interface VideoPlayerProps {
  src: string;
}

export default function VideoPlayer({ src }: VideoPlayerProps) {
  let player = null;
  // Similaire à componentDidMount et componentDidUpdate :
  useEffect(() => {
    const video = player;
    if (Hls.isSupported() && player) {
      const hls = new Hls();
      hls.loadSource(src);
      hls.attachMedia(video);
      hls.on(Hls.Events.MANIFEST_PARSED, (event, data: any) => {
        if (data.fatal) {
          switch (data.type) {
            case Hls.ErrorTypes.NETWORK_ERROR:
              // try to recover network error
              hls.startLoad();
              break;
            case Hls.ErrorTypes.MEDIA_ERROR:
              hls.recoverMediaError();
              break;
            default:
              // cannot recover
              hls.destroy();
              break;
          }
        } else if (data.details === 'internalException' && data.type === 'otherError') {
          hls.startLoad();
        }
      });
      return () => {
        hls.destroy();
      };
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      video.src = src;
      video.addEventListener('loadedmetadata', function () {
        video.play();
      });
    }
  }, []);

  return <video controls autoPlay ref={playerVD => (player = playerVD)}></video>;
}
