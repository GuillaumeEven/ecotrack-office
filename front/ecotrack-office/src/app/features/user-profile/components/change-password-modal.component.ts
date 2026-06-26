import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '@core/services/notification.service';

@Component({
  selector: 'app-change-password-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-password-modal.component.html',
  styleUrl: './change-password-modal.component.css',
})
export class ChangePasswordModalComponent {
  @Output() closed = new EventEmitter<void>();

  form: FormGroup;
  isLoading = false;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private notificationService: NotificationService
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
    this.successMessage = null;
    console.log('contraseña actual:',this.form.value.currentPassword,'contraseña nueva:',this.form.value.newPassword);

    this.userService
      .changePassword({
        currentPassword: this.form.value.currentPassword,
        newPassword: this.form.value.newPassword,
      })
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.successMessage = 'Contraseña cambiada correctamente.';
          this.notificationService.success(this.successMessage);
          // Cerramos el modal tras 1.5 segundos para que el usuario vea el mensaje
          setTimeout(() => this.closed.emit(), 1500);
        },
        error: (err) => {
          this.isLoading = false;
        },
      });
  }

  onClose(): void {
    this.closed.emit();
  }
}
