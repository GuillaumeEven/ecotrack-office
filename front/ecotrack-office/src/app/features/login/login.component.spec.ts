import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '@services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: any;
  let routerSpy: any;

  beforeEach(async () => {
    const authServiceSpyObj = {
      login: vi.fn(),
      isAuthenticated: vi.fn()
    };
    const routerSpyObj = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpyObj },
        { provide: Router, useValue: routerSpyObj }
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as any;
    routerSpy = TestBed.inject(Router) as any;

    // Por defecto, usuario no autenticado
    authServiceSpy.isAuthenticated.mockReturnValue(false);

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Test: LoginComponent se crea correctamente
   *
   * Dado: el componente se inicializa
   * Cuando: el ngOnInit se ejecuta
   * Entonces: el formulario existe y tiene los campos email y password
   */
  it('should create and initialize form with email and password fields', () => {
    expect(component).toBeTruthy();
    expect(component.form).toBeDefined();
    expect(component.form.get('email')).toBeDefined();
    expect(component.form.get('password')).toBeDefined();
    expect(component.isLoading).toBe(false);
    expect(component.errorMessage).toBeNull();
  });

  /**
   * Test: LoginComponent valida el formulario
   *
   * Dado: campos vacíos o inválidos
   * Cuando: se intenta enviar el formulario
   * Entonces: el formulario está inválido (invalid = true)
   */
  it('should mark form as invalid with empty fields', () => {
    const emailControl = component.form.get('email');
    const passwordControl = component.form.get('password');

    expect(component.form.valid).toBe(false);

    // Solo email
    emailControl?.setValue('admin@ecotrack.local');
    expect(component.form.valid).toBe(false);

    // Email + password
    passwordControl?.setValue('password');
    expect(component.form.valid).toBe(true);
  });

  /**
   * Test: LoginComponent.onSubmit() - Success path
   *
   * Dado: formulario válido con credenciales correctas
   * Cuando: se hace submit
   * Entonces: llama authService.login() y redirige a /home
   */
  it('should call authService.login and navigate on successful login', () => {
    authServiceSpy.login.mockReturnValue(of({
      token: 'jwt_token_123',
      userId: 1,
      role: 'ADMIN',
      email: 'admin@ecotrack.local'
    }));

    component.form.patchValue({
      email: 'admin@ecotrack.local',
      password: 'password'
    });

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      email: 'admin@ecotrack.local',
      password: 'password'
    });

    expect(component.isLoading).toBe(false);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/home']);
    expect(component.errorMessage).toBeNull();
  });

  /**
   * Test: LoginComponent.onSubmit() - Error handling
   *
   * Dado: credenciales inválidas o error en servidor
   * Cuando: se hace submit
   * Entonces: muestra mensaje de error y no redirige
   */
  it('should display error message on login failure', () => {
    authServiceSpy.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));

    component.form.patchValue({
      email: 'wrong@ecotrack.local',
      password: 'wrong'
    });

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalled();
    expect(component.isLoading).toBe(false);
    expect(component.errorMessage).toBe('Invalid email or password. Please try again.');
    expect(routerSpy.navigate).not.toHaveBeenCalledWith(['/home']);
  });

  /**
   * Test: LoginComponent redirect si ya está autenticado
   *
   * Dado: usuario ya autenticado (isAuthenticated = true)
   * Cuando: accede a la página de login
   * Entonces: redirige a /home automáticamente
   */
  it('should redirect to /home if already authenticated', () => {
    authServiceSpy.isAuthenticated.mockReturnValue(true);

    // Recrear el componente para simular el constructor
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/home']);
  });
});
