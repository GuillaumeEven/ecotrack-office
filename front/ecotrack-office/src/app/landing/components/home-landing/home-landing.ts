import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from "@angular/router";
import { UserModel } from '@models/user.model';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { matchPasswordValidator } from '@validators/match-password.validator';

@Component({
  selector: 'app-home-landing',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './home-landing.html',
  styleUrl: './home-landing.css',
})
export class HomeLanding implements OnInit {

  usuarioNuevo: UserModel = {} as UserModel;
  private fb = inject(FormBuilder);
  registerForm!: FormGroup;

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      // Hago las validaciones individuales por campos
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      passwordHash: ['', [Validators.required]],
      confirmPassword: ['', [Validators.required]]
    }, {
      // Aplico la validación cruzada de contraseña al grupo completo
      validators: matchPasswordValidator
    });
  }

  register(): void {
    // Si el usuario guardó campos invalidos o vacíos bloqueamos el envío
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    // Si todo está correcto continuamos con la lógia hacia el back y aquí es donde haría mi envio de datos
    console.log('Formulario válidos, con datos:', this.registerForm.value);
  }
}