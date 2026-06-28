import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResourceResponse } from '@models/index.model';

@Injectable({
  providedIn: 'root',
})
export class ResourceService {

  private readonly BASE_URL = `${environment.apiUrl}/resources`;

  constructor(private http: HttpClient) {}

  getResourceById(resourceId: number): Observable<ResourceResponse> {
    return this.http.get<ResourceResponse>(`${this.BASE_URL}/${resourceId}`);
  }
}
