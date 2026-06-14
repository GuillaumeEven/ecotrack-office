import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador personalizado para comprobar que la contraseña y su confirmación coinciden.
 * Se debe aplicar a nivel de FormGroup.
 */
export const matchPasswordValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const password = control.get('passwordHash');
  const confirmPassword = control.get('confirmPassword');

  // Si los controles no existen todavía, no validamos
  if (!password || !confirmPassword) {
    return null;
  }

  // Si el campo de confirmar contraseña tiene un error propio (como 'required'),
  // esperamos a que se solucione antes de aplicar la validación cruzada
  if (confirmPassword.errors && !confirmPassword.errors['passwordsNotMatch']) {
    return null;
  }

  // Si coinciden, devolvemos null (válido). Si no, inyectamos el error 'passwordsNotMatch'
  if (password.value !== confirmPassword.value) {
    const error = { passwordsNotMatch: true };
    confirmPassword.setErrors({ ...confirmPassword.errors, ...error });
    return error;
  } else {
    // Si coinciden eliminamos el error específico de la confirmación
    if (confirmPassword.errors) {
      delete confirmPassword.errors['passwordsNotMatch'];
      if (Object.keys(confirmPassword.errors).length === 0) {
        confirmPassword.setErrors(null);
      }
    }
    return null;
  }
};