import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { UserResponse } from '../../models/user.model';
import { ChangePasswordModalComponent } from './components/change-password-modal.component';
import { NotificationService } from '../../services/notification.service';

export type ProfileSection = 'personal' | 'security' | 'notifications';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe, ChangePasswordModalComponent],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent implements OnInit {
  activeSection: ProfileSection = 'personal';
  profileForm!: FormGroup;
  user: UserResponse | null = null;
  isLoading = true;
  isSaving = false;
  successMessage: string | null = null;
  showPasswordModal = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
    });

    this.userService.getMe().subscribe({
      next: (user) => {
        this.user = user;
        this.profileForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
        });
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  setActiveSection(section: ProfileSection): void {
    this.activeSection = section;
  }

  onSaveChanges(): void {
    if (this.profileForm.invalid) return;
    this.isSaving = true;
    this.successMessage = null;

    this.userService.updateMe(this.profileForm.value).subscribe({
      next: (updated) => {
        this.user = updated;
        this.isSaving = false;
        this.successMessage = 'Perfil actualizado correctamente.';
        this.notificationService.success(this.successMessage);
      },
      error: () => {
        this.isSaving = false;
      },
    });
  }

  onDiscardChanges(): void {
    if (!this.user) return;
    this.profileForm.patchValue({
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      email: this.user.email,
    });
    this.successMessage = null;
  }

  onChangePassword(): void {
    this.showPasswordModal = true;
  }

  onSignOutAll(): void {
    // TODO: llamar al AuthService para revocar todas las sesiones
    console.log('Sign out all');
  }

  onChangePhoto(): void {
    // TODO: abrir file picker y llamar al endpoint de upload
    console.log('Change photo');
  }
}
