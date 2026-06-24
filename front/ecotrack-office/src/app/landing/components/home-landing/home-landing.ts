import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { RouterLink } from "@angular/router";
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { matchPasswordValidator } from '@validators/match-password.validator';
import { UserService } from '../../../services/user.service';
import { OrganizationService } from '../../../services/organization.service';
import { CreateUserRequest } from '@models/user.model';
import { CreateOrganizationRequest, OrganizationResponse } from '@models/organization.model';
import { switchMap, catchError, throwError } from 'rxjs';

// Definimos los pasos posibles para controlar el flujo visual
type RegistroPaso = 'USUARIO' | 'EMPRESA_NUEVA' | 'EMPRESA_EXISTENTE';

@Component({
  selector: 'app-home-landing',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './home-landing.html',
  styleUrl: './home-landing.css',
})
export class HomeLanding implements OnInit {

  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private organizationService = inject(OrganizationService);
  private cdr = inject(ChangeDetectorRef);

  registerForm!: FormGroup;
  
  // Control de estado del flujo secuencial
  pasoActual: RegistroPaso = 'USUARIO';

  // ESTADO LOCAL PARA CONTROLAR EL POPUP DE ÉXITO O ERROR EN LA PETICIÓN
  notificacion = {
    visible: false,
    tipo: 'success' as 'success' | 'error',
    mensaje: ''
  };

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      // Datos del usuario (Paso 1)
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      passwordHash: ['', [Validators.required]],
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
      console.log('1. Enviando a API para CREAR empresa:', {
        name: datosForm.companyName,
        cif: datosForm.companyCif,
        address: datosForm.companyAddress,
        email: datosForm.companyEmail
      });
      
      // Aquí iría el subscribe de tu servicio de empresa, y en el éxito ejecutas el del usuario:
      console.log('2. Empresa creada con éxito. Creando usuario administrador:', {
        firstName: datosForm.firstName,
        lastName: datosForm.lastName,
        email: datosForm.email,
        passwordHash: datosForm.passwordHash,
        rol: 'Admin'
      });

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

          console.log('Empresa creada con éxito:', empresaCreada);
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
          this.mostrarNotificacion('success', 'Empresa y Usuario registrados con exito.');
          this.cdr.detectChanges();
          setTimeout(() => {
            window.location.reload();
          }, 3500);
        },
        error: (err) => {

          console.error('Error completo capturado:', err);
          let mensajePersonalizado = 'Hubo un falle en el proceso de registro.';
          if(err.origenError === 'EMPRESA') {

            mensajePersonalizado = 'No se pudo crear la empresa. Revisa el CIF o el correo electrónico.';
          } else if (err.origenError === 'USUARIO') {

            mensajePersonalizado = 'La empresa se creó con éxito, pero falló el registro de tus datos de usuario. Intente ahora registrarse asociandose a su empresa.';
          }
          this.mostrarNotificacion('error', mensajePersonalizado);
          this.cdr.detectChanges();
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

          //SI SE HA CREADO CORRECTAMENTE MOSTRAMOS EL POPUP DE ÉXITO
          this.mostrarNotificacion('success', 'Usuario registrado con éxito.');
          //USO EL CD PARA QUE PINGE EL POPUP
          this.cdr.detectChanges();
          //RECARGO LA PÁGINA
          setTimeout(() => {
            window.location.reload();
          }, 3500);
        },
        error: (err) => {

          //SI LA LLAMADA HA DADO ERROR MOSTRAMOS POPUP EN ROJO
          console.error('Error capturado desde el servidor:', err);
          // 1. Intentamos leer el mensaje específico que envíe el backend
          let mensajeError = 'Hubo un fallo al intentar registrar el usuario.';
          if (err?.error?.message) {

            mensajeError = err.error.message;
          } else if (err.status === 403 || err.status === 401) {

            // 2. Si el backend sigue devolviendo 403 por el CIF, lo interceptamos manualmente aquí:
            mensajeError = 'El CIF introducido no coincide con ninguna empresa registrada.';
          }
          // 3. Forzamos la activación del pop-up con el mensaje correspondiente
          this.mostrarNotificacion('error', mensajeError);
          this.cdr.detectChanges();
        }
      });
    }
  }

  //MÉTODO PARA ACTIVAR EL POPUP Y AUTODESTRUIRSE A LOS 5 SEGUNDOS
  private mostrarNotificacion(tipo: 'success' | 'error', mensaje: string) {

    this.notificacion = { visible: true, tipo, mensaje};
    setTimeout(() => {
      this.notificacion.visible = false;
    }, 5000);
  }
}