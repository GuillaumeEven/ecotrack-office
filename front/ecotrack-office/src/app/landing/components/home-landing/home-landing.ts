import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from "@angular/router";
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { matchPasswordValidator } from '@validators/match-password.validator';

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
  registerForm!: FormGroup;
  
  // Control de estado del flujo secuencial
  pasoActual: RegistroPaso = 'USUARIO';

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

  // Método unificado de envío final
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
      
    } else if (this.pasoActual === 'EMPRESA_EXISTENTE') {
      console.log('Enviando a API para CREAR usuario asociado al CIF existente:', datosForm.companyCifAsociar, {
        firstName: datosForm.firstName,
        lastName: datosForm.lastName,
        email: datosForm.email,
        passwordHash: datosForm.passwordHash,
        rol: 'Employee'
      });
    }
  }
}