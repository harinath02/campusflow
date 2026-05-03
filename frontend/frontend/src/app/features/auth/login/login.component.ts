import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../shared/toast/toast.service';

type Persona = 'student' | 'department' | 'admin';
const ROLE_MAP: Record<Persona, string> = { student: 'STUDENT', department: 'OFFICER', admin: 'ADMIN' };

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly toast = inject(ToastService);

  protected readonly personas: { key: Persona; label: string; helper: string }[] = [
    { key: 'student', label: 'Student', helper: 'Track your own paperwork.' },
    { key: 'department', label: 'Department', helper: 'Decide assigned requests.' },
    { key: 'admin', label: 'Admin', helper: 'Control the workspace.' }
  ];
  protected selected: Persona = this.normalizePersona(this.route.snapshot.queryParamMap.get('type'));
  protected loading = false;
  protected submitted = false;
  protected errorMessage = '';
  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(4)]]
  });

  selectPersona(persona: Persona): void { this.selected = persona; this.errorMessage = ''; }

  fieldError(name: 'email' | 'password'): string {
    const control = this.form.controls[name];
    if (!this.submitted || control.valid) return '';
    if (control.hasError('required')) return `${name === 'email' ? 'Email' : 'Password'} is required.`;
    if (control.hasError('email')) return 'Enter a valid email address.';
    if (control.hasError('minlength')) return 'Password must be at least 4 characters.';
    return 'Please check this field.';
  }

  submit(): void {
    this.submitted = true;
    this.errorMessage = '';
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.auth.login(this.form.getRawValue()).subscribe({
      next: (user) => {
        this.loading = false;
        if (user.role !== ROLE_MAP[this.selected]) {
          this.auth.clearSession();
          this.errorMessage = `This account is ${user.role}. Please choose the correct portal.`;
          this.toast.warning('Wrong portal selected', this.errorMessage);
          return;
        }
        this.toast.success('Signed in successfully', `Welcome ${user.name}`);
        this.router.navigate([this.auth.getHomeRoute(user.role)]);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error?.error?.message || error?.message || 'Unable to sign in. Check email and password.';
        this.toast.error('Login failed', this.errorMessage);
      }
    });
  }

  private normalizePersona(value: string | null): Persona {
    return value === 'department' || value === 'admin' || value === 'student' ? value : 'student';
  }
}
