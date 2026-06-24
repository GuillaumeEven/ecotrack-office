import { ResourceResponse } from "./resource.model";

export interface ReservationResponse {

    id: number;
    date: string;
    status: string;
    createdAt: string;
    userId: number;
    resourceName: string;
    resourceEquipmentList: string;
}