import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateOrganizationRequest, OrganizationResponse, UpdateOrganizationRequest } from '@models/organization.model';

@Injectable({
  providedIn: 'root',
})
export class OrganizationService {

  private readonly BASE_URL = `${environment.apiUrl}/organizations`;

  constructor(private http: HttpClient) {}

  createOrganization(data: CreateOrganizationRequest): Observable <OrganizationResponse> {

    return this.http.post<OrganizationResponse>(`${this.BASE_URL}/public/create`, data);
  }

  getOrganization(): Observable <OrganizationResponse> {

    return this.http.get<OrganizationResponse>(`${this.BASE_URL}`);
  }

  updateOrganization(data: UpdateOrganizationRequest): Observable <OrganizationResponse> {

    return this.http.put<OrganizationResponse>(`${this.BASE_URL}/update`, data)
  }
}
