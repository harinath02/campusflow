import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RequestType } from '../../core/models/request.model';
import { RequestTypeService } from '../../core/services/request-type.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';
import { ToastService } from '../../shared/toast/toast.service';

@Component({
  selector: 'app-request-types',
  imports: [ReactiveFormsModule, ConfirmDialogComponent],
  templateUrl: './request-types.component.html',
  styleUrl: './request-types.component.scss'
})
export class RequestTypesComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly requestTypeService = inject(RequestTypeService);
  private readonly toast = inject(ToastService);

  protected items: RequestType[] = [];
  protected loading = false;
  protected saving = false;
  protected search = '';
  protected showPanel = false;
  protected editing: RequestType | null = null;
  protected pendingDeactivate: RequestType | null = null;
  protected submitted = false;
  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    code: ['', Validators.required],
    description: ['']
  });

  protected get filtered(): RequestType[] {
    const q = this.search.toLowerCase().trim();
    return this.items.filter((item) => !q || `${item.name} ${item.code} ${item.description || ''}`.toLowerCase().includes(q));
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.requestTypeService.getRequestTypes().subscribe({
      next: (items) => {
        this.items = items ?? [];
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.toast.error('Request types failed to load', error?.error?.message || 'Please try again.');
      }
    });
  }

  openCreate(): void {
    this.editing = null;
    this.submitted = false;
    this.form.reset({ name: '', code: '', description: '' });
    this.showPanel = true;
  }

  openEdit(item: RequestType): void {
    this.editing = item;
    this.submitted = false;
    this.form.patchValue({ name: item.name, code: item.code, description: item.description || '' });
    this.showPanel = true;
  }

  closePanel(): void { this.showPanel = false; }

  fieldError(name: 'name' | 'code'): string {
    const control = this.form.controls[name];
    return this.submitted && control.invalid ? 'This field is required.' : '';
  }

  save(): void {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    const payload = this.form.getRawValue();
    const call = this.editing
      ? this.requestTypeService.updateRequestType(this.editing.id, payload)
      : this.requestTypeService.createRequestType(payload);

    call.subscribe({
      next: () => {
        this.toast.success(this.editing ? 'Request type updated' : 'Request type created');
        this.saving = false;
        this.showPanel = false;
        this.load();
      },
      error: (error) => {
        this.saving = false;
        this.toast.error('Save failed', error?.error?.message || 'Could not save request type.');
      }
    });
  }

  deactivate(item: RequestType): void {
    this.pendingDeactivate = item;
  }

  confirmDeactivate(): void {
    if (!this.pendingDeactivate) return;
    const item = this.pendingDeactivate;
    this.requestTypeService.deactivateRequestType(item.id).subscribe({
      next: () => {
        this.toast.success('Request type deactivated');
        this.pendingDeactivate = null;
        this.load();
      },
      error: (error) => this.toast.error('Deactivate failed', error?.error?.message || 'Could not deactivate request type.')
    });
  }
}
