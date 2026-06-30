import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador personalizado para comprobar que la contraseña y su confirmación coinciden.
 */
export const matchPasswordValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const password = control.get('passwordHash');
  const confirmPassword = control.get('confirmPassword');

  if (!password || !confirmPassword) {
    return null;
  }

  if (confirmPassword.errors && !confirmPassword.errors['passwordsNotMatch']) {
    return null;
  }

  if (password.value !== confirmPassword.value) {
    const error = { passwordsNotMatch: true };
    confirmPassword.setErrors({ ...confirmPassword.errors, ...error });
    return error;
  } else {
    if (confirmPassword.errors) {
      delete confirmPassword.errors['passwordsNotMatch'];
      if (Object.keys(confirmPassword.errors).length === 0) {
        confirmPassword.setErrors(null);
      }
    }
    return null;
  }
};