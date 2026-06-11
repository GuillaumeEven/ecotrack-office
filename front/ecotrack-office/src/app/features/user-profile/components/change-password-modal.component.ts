import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-change-password-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-password-modal.component.html',
})
export class ChangePasswordModalComponent {
  @Output() closed = new EventEmitter<void>();

  form: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
  ) {
    this.form = this.fb.group(
      {
        currentPassword: ['', [Validators.required]],
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordsMatch },
    );
  }

  // Validador personalizado: newPassword y confirmPassword deben coincidir
  private passwordsMatch(group: FormGroup): { mismatch: true } | null {
    const newPassword = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return newPassword === confirmPassword ? null : { mismatch: true };
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.isLoading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.userService
      .changePassword({
        currentPassword: this.form.value.currentPassword,
        newPassword: this.form.value.newPassword,
      })
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.successMessage = 'Password changed successfully.';
          // Cerramos el modal tras 1.5 segundos para que el usuario vea el mensaje
          setTimeout(() => this.closed.emit(), 1500);
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage =
            err.status === 400
              ? 'Current password is incorrect.'
              : 'Could not change password. Please try again.';
        },
      });
  }

  onClose(): void {
    this.closed.emit();
  }
}
