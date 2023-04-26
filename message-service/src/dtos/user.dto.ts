export type UserDTO = {
  id: string;
  firstName?: string;
  lastName?: string;
  email: string;
  phoneNumber?: string;
  birthday?: string;
  gender?: boolean;
  active?: boolean;
  lastActive?: string;
  avatar?: string;
  enabled?: boolean;
  createdDate?: string;
  updatedDate?: string;
  connections?: string[];
  devices: DeviceDTO[];
};

export type DeviceDTO = {
  deviceID: string;
  pushProvider?: string;
};
