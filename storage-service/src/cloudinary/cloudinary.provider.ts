import { v2 } from 'cloudinary';
import { CLOUDINARY } from 'src/constants';

export const CloudinaryProvider = {
  provide: CLOUDINARY,
  useFactory: () => {
    return v2.config({
      cloud_name: 'df7jgzg96',
      api_key: '616294569617223',
      api_secret: 'kNqZ1Xni9dId441OaMzDZm9H_zQ',
    });
  },
};
