import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, Form } from '@angular/forms';
import { OrganizationService } from '../services/organization.service';
import { OrganizationResponse } from '@models/organization.model';


@Component({
  selector: 'app-organization',
  imports: [CommonModule, ReactiveFormsModule, DatePipe],
  templateUrl: './organization.html',
  styleUrl: './organization.css',
})
export class Organization implements OnInit{

  profileForm!: FormGroup;
  organization: OrganizationResponse | null = null;
  isLoading = true;
  isSaving = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor (private fb: FormBuilder, private organizationService: OrganizationService) {}

  ngOnInit(): void {
    
    this.profileForm = this.fb.group({
      name: ['', [Validators.required]],
      cif: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      address: ['', [Validators.required]]
    });

    this.organizationService.getOrganization().subscribe({

      next: (org) => {

        this.organization = org;
        this.profileForm.patchValue({

          name: org.name,
          cif: org.cif,
          email: org.email,
          address: org.address
        });
        this.isLoading = false
      },
      error: () => {

        this.errorMessage = 'No se pudo cargar el perfil de emrpesa.';
        this.isLoading = false;
      } 
    });
  }

  onSaveChanges(): void {

    if(this.profileForm.invalid) return;
    this.isSaving = true;
    this.successMessage = null;
    this.errorMessage = null;

    this.organizationService.updateOrganization(this.profileForm.value).subscribe({

      next: (updated) => {

        this.organization = updated;
        this.isSaving = false;
        this.successMessage = 'Perfil actualizado correctamente.';
      }
    });
  }

  onDiscardChanges(): void {

    if(!this.organization) return;
    this.profileForm.patchValue({
      name: this.organization.name,
      cif: this.organization.cif,
      email: this.organization.email,
      address: this.organization.address
    });
    this.successMessage = null;
    this.errorMessage = null;
  }

  onChangePhoto(): void {

    console.log('Cambio de foto en un futuro!');
  }
}
