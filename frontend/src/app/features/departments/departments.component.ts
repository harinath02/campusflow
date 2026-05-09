import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Department } from '../../core/models/department.model';
import { DepartmentService } from '../../core/services/department.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';
import { ToastService } from '../../shared/toast/toast.service';

@Component({
  selector: 'app-departments',
  imports: [ReactiveFormsModule, ConfirmDialogComponent],
  templateUrl: './departments.component.html',
  styleUrl: './departments.component.scss'
})
export class DepartmentsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(DepartmentService);
  private readonly toast = inject(ToastService);

  protected departments: Department[] = [];
  protected loading = false;
  protected saving = false;
  protected showPanel = false;
  protected editing: Department | null = null;
  protected pendingDelete: Department | null = null;
  protected submitted = false;
  protected search = '';
  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    code: ['', Validators.required],
    description: ['']
  });

  protected get filtered(): Department[] {
    const q = this.search.toLowerCase().trim();
    return q ? this.departments.filter((department) => `${department.name} ${department.code} ${department.description}`.toLowerCase().includes(q)) : this.departments;
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.service.getDepartments().subscribe({
      next: (value) => {
        this.departments = value ?? [];
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.toast.error('Departments failed to load', error?.error?.message || 'Please try again.');
      }
    });
  }

  openCreate(): void {
    this.editing = null;
    this.submitted = false;
    this.form.reset({ name: '', code: '', description: '' });
    this.showPanel = true;
  }

  openEdit(department: Department): void {
    this.editing = department;
    this.submitted = false;
    this.form.patchValue({ name: department.name, code: department.code, description: department.description || '' });
    this.showPanel = true;
  }

  fieldError(name: 'name' | 'code'): string {
    const control = this.form.controls[name];
    return this.submitted && control.invalid ? 'This field is required.' : '';
  }

  save(): void {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toast.warning('Missing details', 'Department name and code are required.');
      return;
    }
    this.saving = true;
    const payload = this.form.getRawValue();
    const call = this.editing ? this.service.updateDepartment(this.editing.id, payload) : this.service.createDepartment(payload);
    call.subscribe({
      next: () => {
        this.saving = false;
        this.showPanel = false;
        this.toast.success(this.editing ? 'Department updated' : 'Department created');
        this.load();
      },
      error: (error) => {
        this.saving = false;
        this.toast.error('Save failed', error?.error?.message || 'Could not save department.');
      }
    });
  }

  remove(department: Department): void {
    this.pendingDelete = department;
  }

  confirmDelete(): void {
    if (!this.pendingDelete) return;
    const department = this.pendingDelete;
    this.service.deleteDepartment(department.id).subscribe({
      next: () => {
        this.toast.success('Department deleted');
        this.pendingDelete = null;
        this.load();
      },
      error: (error) => this.toast.error('Delete failed', error?.error?.message || 'Department may be linked with users or requests.')
    });
  }
}
