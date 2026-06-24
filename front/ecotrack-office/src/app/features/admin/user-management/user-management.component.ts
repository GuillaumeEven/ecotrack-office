import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core'; // 🆕 Añadido ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import { UserService, UserStats } from '../../../services/user.service';
import { UserResponse, Role } from '../../../models/user.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-management.component.html',
})
export class UserManagementComponent implements OnInit, OnDestroy {
  // ─── Estado ───────────────────────────────────────────────────────────────
  organizationId: number | null = null;
  users: UserResponse[] = [];
  stats: UserStats = { totalUsers: 0, activeUsers: 0, newThisMonth: 0 };

  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  pageSize = 10;

  isLoading = true;
  isLoadingStats = true;

  // ─── Filtros ──────────────────────────────────────────────────────────────
  searchValue = '';
  selectedRole: string = '';
  selectedStatus: string = '';
  roles: Role[] = ['ADMIN', 'EMPLOYEE', 'TECHNICIAN'];

  // ─── Modales ──────────────────────────────────────────────────────────────
  showEditModal = false;
  showDeleteModal = false;
  selectedUser: UserResponse | null = null;
  editForm!: FormGroup;

  // ─── Search con debounce ──────────────────────────────────────────────────
  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef, // 🆕 Inyectado en el constructor
  ) {}

  ngOnInit(): void {
    this.editForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      role: ['', Validators.required],
    });

    // Primero obtenemos el organizationId del usuario logueado
    this.userService.getMe().subscribe({
      next: (me) => {
        this.organizationId = me.organizationId;
        this.loadStats();
        this.loadUsers();
        this.cdr.detectChanges(); // 🆕 Forzar actualización tras obtener perfil
      },
    });

    // Debounce en el search — espera 300ms tras dejar de escribir
    this.searchSubject
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.currentPage = 0;
        this.loadUsers();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ─── Carga de datos ───────────────────────────────────────────────────────

  loadUsers(): void {
    if (!this.organizationId) return;
    this.isLoading = true;
    this.cdr.detectChanges(); // 🆕 Actualiza la vista para mostrar el spinner inmediatamente

    this.userService
      .getUsers(this.organizationId, {
        search: this.searchValue || undefined,
        role: this.selectedRole || undefined,
        isActive: this.selectedStatus === '' ? undefined : this.selectedStatus === 'true',
        page: this.currentPage,
        size: this.pageSize,
      })
      .subscribe({
        next: (response) => {
          this.users = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.isLoading = false;
          this.cdr.detectChanges(); // 🆕 Fuerza el renderizado de la tabla con los usuarios nuevos
        },
        error: () => {
          this.isLoading = false;
          this.cdr.detectChanges(); // 🆕 Fuerza el renderizado si da error para quitar el spinner
        },
      });
  }

  loadStats(): void {
    if (!this.organizationId) return;
    this.userService.getUserStats(this.organizationId).subscribe({
      next: (stats) => {
        this.stats = stats;
        this.isLoadingStats = false;
        this.cdr.detectChanges(); // 🆕 Redibuja las tarjetas KPI con las nuevas estadísticas
      },
    });
  }

  // ─── Filtros ──────────────────────────────────────────────────────────────

  onSearchInput(event: Event): void {
    this.searchValue = (event.target as HTMLInputElement).value;
    this.searchSubject.next(this.searchValue);
  }

  onRoleChange(event: Event): void {
    this.selectedRole = (event.target as HTMLSelectElement).value;
    this.currentPage = 0;
    this.loadUsers();
  }

  onStatusChange(event: Event): void {
    this.selectedStatus = (event.target as HTMLSelectElement).value;
    this.currentPage = 0;
    this.loadUsers();
  }

  // ─── Paginación ───────────────────────────────────────────────────────────

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadUsers();
  }

  get pagesArray(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  get showingFrom(): number {
    return this.currentPage * this.pageSize + 1;
  }
  get showingTo(): number {
    return Math.min((this.currentPage + 1) * this.pageSize, this.totalElements);
  }

  // ─── Acciones de fila ─────────────────────────────────────────────────────

  openEditModal(user: UserResponse): void {
    this.selectedUser = user;
    this.editForm.patchValue({
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      role: user.role,
    });
    this.showEditModal = true;
    this.cdr.detectChanges(); // 🆕 Asegura que el modal de edición se pinte en pantalla
  }

  onSaveEdit(): void {
    if (!this.selectedUser || this.editForm.invalid) return;
    this.userService.updateUser(this.selectedUser.id, this.editForm.value).subscribe({
      next: () => {
        this.showEditModal = false;
        this.loadUsers();
        this.loadStats();
        this.cdr.detectChanges(); // 🆕 Cierra el modal y refresca los cambios visualmente
      },
    });
  }

  toggleUserStatus(user: UserResponse): void {
    const action$ = user.isActive
      ? this.userService.deactivateUser(user.id)
      : this.userService.reactivateUser(user.id);

    action$.subscribe({
      next: () => {
        this.loadUsers();
        this.loadStats();
        this.cdr.detectChanges(); // 🆕 Actualiza el switch/badge de Activo/Inactivo sin retraso
      },
    });
  }

  openDeleteModal(user: UserResponse): void {
    this.selectedUser = user;
    this.showDeleteModal = true;
    this.cdr.detectChanges(); // 🆕 Garantiza que aparezca el modal de confirmación de borrado
  }

  onConfirmDelete(): void {
    if (!this.selectedUser) return;
    this.userService.deleteUser(this.selectedUser.id).subscribe({
      next: () => {
        this.showDeleteModal = false;
        this.selectedUser = null;
        this.loadUsers();
        this.loadStats();
        this.cdr.detectChanges(); // 🆕 Cierra el modal de confirmación y purga al usuario de la tabla
      },
    });
  }
}
