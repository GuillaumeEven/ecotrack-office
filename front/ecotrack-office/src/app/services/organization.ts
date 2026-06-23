import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateOrganizationRequest, OrganizationResponse } from '@models/organization.model';

@Injectable({
  providedIn: 'root',
})
export class Organization {

  private readonly BASE_URL = `${environment.apiUrl}/organizations`;

  constructor(private http: HttpClient) {}

  createOrganization(data: CreateOrganizationRequest): Observable <OrganizationResponse> {

    return this.http.post<OrganizationResponse>(`${this.BASE_URL}/public/create`, data);
  }
}
