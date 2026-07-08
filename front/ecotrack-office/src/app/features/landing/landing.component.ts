import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { RouterLink, Router } from "@angular/router";
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { matchPasswordValidator } from '@validators/match-password.validator';

import { UserService,
   OrganizationService,
   ThemeService,
   NotificationService } from '@services/index.service';
import { CreateUserRequest,
  CreateOrganizationRequest,
  OrganizationResponse } from '@models/index.model';
import { switchMap, catchError, throwError } from 'rxjs';

// Definimos los pasos posibles para controlar el flujo visual
type RegistroPaso = 'USUARIO' | 'EMPRESA_NUEVA' | 'EMPRESA_EXISTENTE';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css'],
})
export class LandingComponent implements OnInit {

  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private organizationService = inject(OrganizationService);
  private cdr = inject(ChangeDetectorRef);
  private notificationService = inject(NotificationService);
  private router = inject(Router);
  public themeService = inject(ThemeService);

  registerForm!: FormGroup;

  /**
   * Observable del tema actual
   */
  currentTheme$ = this.themeService.getTheme$();

  // Control de estado del flujo secuencial
  pasoActual: RegistroPaso = 'USUARIO';

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      // Datos del usuario (Paso 1)
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      passwordHash: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],

      // Datos si crea empresa nueva (Paso 2)
      companyName: [''],
      companyCif: [''],
      companyAddress: [''],
      companyEmail: [''],


      // Datos si se asocia a empresa existente (Paso 3)
      companyCifAsociate: ['']
    }, {
      validators: matchPasswordValidator
    });
  }

  /**
   * Alterna el tema entre light y dark
   */
  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  // Navegación entre pasos actualizando dinámicamente los validadores requeridos
  irAPaso(paso: RegistroPaso): void {
    this.pasoActual = paso;

    const companyNameCtrl = this.registerForm.get('companyName');
    const companyCifCtrl = this.registerForm.get('companyCif');
    const companyAddressCtrl = this.registerForm.get('companyAddress');
    const companyEmailCtrl = this.registerForm.get('companyEmail');
    const companyCifAsociateCtrl = this.registerForm.get('companyCifAsociate');

    // Limpiamos validadores previos para evitar conflictos
    companyNameCtrl?.clearValidators();
    companyCifCtrl?.clearValidators();
    companyAddressCtrl?.clearValidators();
    companyEmailCtrl?.clearValidators();
    companyCifAsociateCtrl?.clearValidators();

    // Añadimos dinámicamente según el flujo elegido
    if (paso === 'EMPRESA_NUEVA') {
      companyNameCtrl?.setValidators([Validators.required]);
      companyCifCtrl?.setValidators([Validators.required]);
      companyAddressCtrl?.setValidators([Validators.required]);
      companyEmailCtrl?.setValidators([Validators.required, Validators.email])
    } else if (paso === 'EMPRESA_EXISTENTE') {
      companyCifAsociateCtrl?.setValidators([Validators.required]);
    }

    // Forzamos la actualización del estado de validez
    companyNameCtrl?.updateValueAndValidity();
    companyCifCtrl?.updateValueAndValidity();
    companyAddressCtrl?.updateValueAndValidity();
    companyEmailCtrl?.updateValueAndValidity();
    companyCifAsociateCtrl?.updateValueAndValidity();
  }

  // MÉTODO DE ENVÍO A LA API
  submitRegistro(): void {

    if (this.registerForm.invalid) {

      this.registerForm.markAllAsTouched();
      return;
    }

    const datosForm = this.registerForm.value;

    if (this.pasoActual === 'EMPRESA_NUEVA') {
      console.log('1. Enviando a API para CREAR empresa.');

      // Aquí iría el subscribe de tu servicio de empresa, y en el éxito ejecutas el del usuario:
      console.log('2. Empresa creada con éxito. Creando usuario administrador.');

      const payloadOrg: CreateOrganizationRequest = {

        name: datosForm.companyName,
        cif: datosForm.companyCif,
        address: datosForm.companyAddress,
        email: datosForm.companyEmail
      };
      this.organizationService.createOrganization(payloadOrg).pipe(

        catchError((err) => {

          err.origenError = 'EMPRESA';
          return throwError(() => err);
        }),
        switchMap((empresaCreada: OrganizationResponse) => {

          console.log('Empresa creada con éxito.');
          const payloadUsr: CreateUserRequest = {

            firstName: datosForm.firstName,
            lastName: datosForm.lastName,
            email: datosForm.email,
            password: datosForm.passwordHash,
            role: 'ADMIN',
            cif: empresaCreada.cif
          };
          return this.userService.registerAndAssociate(payloadUsr).pipe(

            catchError((err) => {

              err.origenError = 'USUARIO';
              return throwError(() => err);
            })
          );
        })
      ).subscribe({

        next: (usuarioCreado) => {
          this.notificationService.success('¡Registro exitoso!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          // Error capturado y mostrado por el interceptor
          console.error('Error completo capturado:', err);
        }
      });
    } else if (this.pasoActual === 'EMPRESA_EXISTENTE') {

      const payload: CreateUserRequest = {
        firstName: datosForm.firstName,
        lastName: datosForm.lastName,
        email: datosForm.email,
        password: datosForm.passwordHash,
        role: 'EMPLOYEE',
        cif: datosForm.companyCifAsociate
      };
      this.userService.registerAndAssociate(payload).subscribe({
        next: (response) => {
          this.notificationService.success('¡Registro exitoso!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          // Error capturado y mostrado por el interceptor
          console.error('Error capturado desde el servidor:', err);
        }
      });
    }
  }
}
