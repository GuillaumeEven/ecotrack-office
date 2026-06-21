export interface CreateOrganizationRequest {

    name: string,
    cif: string,
    address: string,
    email: string
}

export interface OrganizationResponse {

    id: number,
    name: string,
    cif: string,
    address: string,
    email: string,
    endSubscription: string,
    isActive: boolean,
    createdAt: string
}