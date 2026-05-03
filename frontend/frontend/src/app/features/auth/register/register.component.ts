import { Component, computed, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Department } from '../../../core/models/department.model';
import { Role } from '../../../core/models/role.model';
import { AuthService } from '../../../core/services/auth.service';
import { DepartmentService } from '../../../core/services/department.service';
import { RoleService } from '../../../core/services/role.service';
import { ToastService } from '../../../shared/toast/toast.service';

type Persona = 'student' | 'department' | 'admin';
const ROLE_BY_PERSONA: Record<Persona, string> = { student: 'STUDENT', department: 'OFFICER', admin: 'ADMIN' };

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly roleService = inject(RoleService);
  private readonly departmentService = inject(DepartmentService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly toast = inject(ToastService);

  protected selected: Persona = this.normalizePersona(this.route.snapshot.queryParamMap.get('type'));
  protected roles: Role[] = [];
  protected departments: Department[] = [];
  protected loading = false;
  protected loadingLookups = false;
  protected submitted = false;
  protected errorMessage = '';
  protected readonly currentYear = new Date().getFullYear();
  protected readonly branches = ['Computer Science', 'Information Technology', 'Electronics', 'Mechanical', 'Civil', 'Electrical', 'MBA', 'MCA'];
  protected readonly form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(4)]],
    departmentId: this.fb.control<number | null>(null),
    branch: [''],
    admissionYear: this.fb.control<number | null>(null),
    rollNumber: ['']
  });

  protected readonly isStudent = computed(() => this.selected === 'student');
  protected readonly isDepartment = computed(() => this.selected === 'department');

  ngOnInit(): void { this.loadLookups(); }

  selectPersona(persona: Persona): void {
    this.selected = persona;
    this.submitted = false;
    this.errorMessage = '';
    this.form.patchValue({ departmentId: null, branch: '', admissionYear: null, rollNumber: '' });
  }

  fieldError(name: 'name' | 'email' | 'password' | 'departmentId' | 'branch' | 'admissionYear' | 'rollNumber'): string {
    const c = this.form.controls[name];
    if (!this.submitted || c.valid) return '';
    if (c.hasError('required')) return 'This field is mandatory.';
    if (c.hasError('email')) return 'Enter a valid email address.';
    if (c.hasError('minlength')) return 'Minimum 4 characters required.';
    if (c.hasError('min')) return 'Please select a valid value.';
    return 'Please check this field.';
  }

  loadLookups(): void {
    this.loadingLookups = true;
    let done = 0; const finish = () => this.loadingLookups = ++done < 2;
    this.roleService.getRoles().subscribe({ next: (v) => { this.roles = v ?? []; finish(); }, error: () => { this.toast.error('Roles failed to load', 'Check backend /api/roles'); finish(); } });
    this.departmentService.getDepartments().subscribe({ next: (v) => { this.departments = v ?? []; finish(); }, error: () => { this.toast.error('Departments failed to load', 'Check backend /api/departments'); finish(); } });
  }

  submit(): void {
    this.submitted = true; this.errorMessage = ''; this.applyDynamicValidators();
    if (this.form.invalid) { this.form.markAllAsTouched(); this.toast.warning('Missing required fields', 'Please fill highlighted fields.'); return; }
    const role = this.roles.find((item) => item.name === ROLE_BY_PERSONA[this.selected]);
    if (!role) { this.toast.error('Role not available', 'Seed roles in backend first.'); return; }
    const raw = this.form.getRawValue();
    this.loading = true;
    this.auth.register({
      name: raw.name!, email: raw.email!, password: raw.password!, roleId: role.id,
      departmentId: this.selected === 'admin' ? null : raw.departmentId,
      branch: this.selected === 'student' ? raw.branch : null,
      admissionYear: this.selected === 'student' ? raw.admissionYear : null,
      rollNumber: this.selected === 'student' ? raw.rollNumber : null
    }).subscribe({
      next: () => { this.loading = false; this.toast.success('Account created', 'Please sign in with your new account.'); this.router.navigate(['/login'], { queryParams: { type: this.selected } }); },
      error: (error) => { this.loading = false; this.errorMessage = error?.error?.message || error?.message || 'Account could not be created.'; this.toast.error('Signup failed', this.errorMessage); }
    });
  }

  private applyDynamicValidators(): void {
    const dept = this.form.controls.departmentId; const branch = this.form.controls.branch; const year = this.form.controls.admissionYear; const roll = this.form.controls.rollNumber;
    dept.clearValidators(); branch.clearValidators(); year.clearValidators(); roll.clearValidators();
    if (this.selected === 'student') { dept.addValidators([Validators.required]); branch.addValidators([Validators.required]); year.addValidators([Validators.required, Validators.min(1990)]); roll.addValidators([Validators.required]); }
    if (this.selected === 'department') { dept.addValidators([Validators.required]); }
    dept.updateValueAndValidity(); branch.updateValueAndValidity(); year.updateValueAndValidity(); roll.updateValueAndValidity();
  }

  private normalizePersona(value: string | null): Persona { return value === 'department' || value === 'admin' || value === 'student' ? value : 'student'; }
}
