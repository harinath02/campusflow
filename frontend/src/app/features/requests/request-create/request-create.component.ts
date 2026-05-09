import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Department } from '../../../core/models/department.model';
import { RequestType } from '../../../core/models/request.model';
import { AuthService } from '../../../core/services/auth.service';
import { DepartmentService } from '../../../core/services/department.service';
import { RequestService } from '../../../core/services/request.service';
import { RequestTypeService } from '../../../core/services/request-type.service';
import { ToastService } from '../../../shared/toast/toast.service';

@Component({
  selector: 'app-request-create',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './request-create.component.html',
  styleUrl: './request-create.component.scss'
})
export class RequestCreateComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly requestService = inject(RequestService);
  private readonly requestTypeService = inject(RequestTypeService);
  private readonly departmentService = inject(DepartmentService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);

  protected readonly currentUser = this.auth.getCurrentUser();
  protected saving = false;
  protected submitted = false;
  protected requestTypes: RequestType[] = [];
  protected departments: Department[] = [];
  protected readonly form = this.fb.nonNullable.group({
    requestTypeId: [0, [Validators.required, Validators.min(1)]],
    title: ['', Validators.required],
    description: [''],
    priority: ['MEDIUM', Validators.required]
  });

  protected get noDuesType(): RequestType | undefined {
    return this.requestTypes.find((type) => /NO[-_ ]?DUES|NODUES/i.test(`${type.code} ${type.name}`));
  }

  ngOnInit(): void {
    this.requestTypeService.getRequestTypes().subscribe({
      next: (items) => {
        this.requestTypes = (items ?? []).filter((type) => type.active);
        if (this.requestTypes.length && !this.form.controls.requestTypeId.value) {
          this.form.patchValue({ requestTypeId: this.requestTypes[0].id });
        }
      },
      error: () => this.toast.error('Request types failed to load', 'Please try again.')
    });
    this.departmentService.getDepartments().subscribe({
      next: (items) => this.departments = items ?? [],
      error: () => this.departments = []
    });
  }

  startNoDues(): void {
    const type = this.noDuesType;
    if (!type) return;
    this.form.patchValue({
      requestTypeId: type.id,
      title: 'No dues clearance request',
      description: 'Please initiate my department-wise no-dues clearance.',
      priority: 'MEDIUM'
    });
  }

  fieldError(name: 'requestTypeId' | 'title' | 'priority'): string {
    const control = this.form.controls[name];
    return this.submitted && control.invalid ? 'This field is required.' : '';
  }

  submit(): void {
    this.submitted = true;
    if (!this.currentUser) {
      this.toast.error('Session missing', 'Please login again.');
      return;
    }
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    const raw = this.form.getRawValue();
    this.requestService.createRequest({
      requestTypeId: Number(raw.requestTypeId),
      requesterId: this.currentUser.id,
      title: raw.title,
      description: raw.description,
      priority: raw.priority
    }).subscribe({
      next: (request) => {
        this.saving = false;
        this.toast.success('Request submitted', `${request.requestNumber} is now in department review.`);
        this.router.navigate(['/student/requests', request.id]);
      },
      error: (error) => {
        this.saving = false;
        this.toast.error('Could not create request', error?.error?.message || 'Please try again.');
      }
    });
  }
}
