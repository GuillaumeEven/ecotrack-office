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
      companyCifNueva: [''],
      
      // Datos si se asocia a empresa existente (Paso 3)
      companyCifAsociar: ['']
    }, {
      validators: matchPasswordValidator
    });
  }

  // Navegación entre pasos actualizando dinámicamente los validadores requeridos
  irAPaso(paso: RegistroPaso): void {
    this.pasoActual = paso;
    
    const companyNameCtrl = this.registerForm.get('companyName');
    const cifNuevaCtrl = this.registerForm.get('companyCifNueva');
    const cifAsociarCtrl = this.registerForm.get('companyCifAsociar');

    // Limpiamos validadores previos para evitar conflictos
    companyNameCtrl?.clearValidators();
    cifNuevaCtrl?.clearValidators();
    cifAsociarCtrl?.clearValidators();

    // Añadimos dinámicamente según el flujo elegido
    if (paso === 'EMPRESA_NUEVA') {
      companyNameCtrl?.setValidators([Validators.required]);
      cifNuevaCtrl?.setValidators([Validators.required]);
    } else if (paso === 'EMPRESA_EXISTENTE') {
      cifAsociarCtrl?.setValidators([Validators.required]);
    }

    // Forzamos la actualización del estado de validez
    companyNameCtrl?.updateValueAndValidity();
    cifNuevaCtrl?.updateValueAndValidity();
    cifAsociarCtrl?.updateValueAndValidity();
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
        cif: datosForm.companyCifNueva
      });
      
      // Aquí iría el subscribe de tu servicio de empresa, y en el éxito ejecutas el del usuario:
      console.log('2. Empresa creada con éxito. Creando usuario administrador...');
      
    } else if (this.pasoActual === 'EMPRESA_EXISTENTE') {
      console.log('Enviando a API para CREAR usuario asociado al CIF existente:', datosForm.companyCifAsociar);
    }
  }
}