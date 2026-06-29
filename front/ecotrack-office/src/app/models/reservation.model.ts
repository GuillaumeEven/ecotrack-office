export interface ReservationResponseModel {

    id: number;
    date: string;
    status: string;
    createdAt: string;
    userId: number;
    resourceType?: string;
    resourceName: string;
    resourceEquipmentList: string;
}

export interface ReservationResponseWithNameModel {

    id: number;
    date: string;
    status: string;
    createdAt: string;
    userId: number;
    resourceType?: string;
    userFullName: string;
    resourceName: string;
    resourceEquipmentList: string;
}

export type ReservationModel = {
  id: number;
  deskId: number;
  roomId: number;
  floorId: number;
  userId: number;
  startDate: string; // ISO date format: "2026-06-09"
  endDate: string;
  status: 'CONFIRMED' | 'PENDING' | 'CANCELLED';
};