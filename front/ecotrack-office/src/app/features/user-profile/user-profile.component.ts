import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

export type ProfileSection = 'personal' | 'security' | 'notifications';

export interface UserProfile {
  fullName: string;
  email: string;
  department: string;
  workLocation: string;
}

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-profile.component.html',
})
export class UserProfileComponent implements OnInit {
  activeSection: ProfileSection = 'personal';

  profileForm!: FormGroup;

  departments = ['Facilities Management', 'Administration', 'Human Resources', 'IT Support'];

  // TODO: reemplazar con datos del AuthService / UserService del backend
  private initialProfile: UserProfile = {
    fullName: 'Alex Thompson',
    email: 'alex.thompson@ecotrackoffice.com',
    department: 'Facilities Management',
    workLocation: 'Main Wing, Floor 4',
  };

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      fullName: [this.initialProfile.fullName, [Validators.required]],
      email: [this.initialProfile.email, [Validators.required, Validators.email]],
      department: [this.initialProfile.department, [Validators.required]],
      workLocation: [this.initialProfile.workLocation, [Validators.required]],
    });
  }

  setActiveSection(section: ProfileSection): void {
    this.activeSection = section;
  }

  onSaveChanges(): void {
    if (this.profileForm.invalid) return;
    const updated: UserProfile = this.profileForm.value;
    console.log('Saving profile:', updated);
    // TODO: llamar al UserService para PATCH /api/users/me
  }

  onDiscardChanges(): void {
    this.profileForm.patchValue(this.initialProfile);
  }

  onChangePassword(): void {
    console.log('Open change password dialog');
    // TODO: abrir modal o navegar a /settings/change-password
  }

  onSignOutAll(): void {
    console.log('Sign out from all devices');
    // TODO: llamar al AuthService para revocar todas las sesiones
  }

  onChangePhoto(): void {
    console.log('Open photo upload');
    // TODO: abrir file picker y llamar al endpoint de upload
  }
}
