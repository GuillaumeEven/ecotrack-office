import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, takeUntil, switchMap } from 'rxjs';
import { UserService, PageResponse, UserStats } from '../../../services/user.service';
import { UserService as UserSvc } from '../../../services/user.service';
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
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  loadStats(): void {
    if (!this.organizationId) return;
    this.userService.getUserStats(this.organizationId).subscribe({
      next: (stats) => {
        this.stats = stats;
        this.isLoadingStats = false;
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
  }

  onSaveEdit(): void {
    if (!this.selectedUser || this.editForm.invalid) return;
    this.userService.updateUser(this.selectedUser.id, this.editForm.value).subscribe({
      next: () => {
        this.showEditModal = false;
        this.loadUsers();
        this.loadStats();
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
      },
    });
  }

  openDeleteModal(user: UserResponse): void {
    this.selectedUser = user;
    this.showDeleteModal = true;
  }

  onConfirmDelete(): void {
    if (!this.selectedUser) return;
    this.userService.deleteUser(this.selectedUser.id).subscribe({
      next: () => {
        this.showDeleteModal = false;
        this.selectedUser = null;
        this.loadUsers();
        this.loadStats();
      },
    });
  }
}
