import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-remark-dialog',
  standalone: true,
  imports: [FormsModule],
  template: `
    @if (open) {
      <button class="modal-backdrop" type="button" aria-label="Close remark dialog" (click)="dismiss.emit()"></button>
      <form class="dialog" (ngSubmit)="submit()" role="dialog" aria-modal="true">
        <p class="eyebrow">{{ actionLabel }}</p>
        <h3>{{ title }}</h3>
        <p>{{ description }}</p>
        <label>Remark
          <textarea rows="4" name="remarks" [(ngModel)]="remarks"></textarea>
        </label>
        <div class="panel-actions">
          <button class="btn ghost" type="button" (click)="dismiss.emit()">Cancel</button>
          <button class="btn primary" type="submit" [disabled]="saving || !remarks.trim()">{{ saving ? 'Saving...' : 'Save decision' }}</button>
        </div>
      </form>
    }
  `
})
export class RemarkDialogComponent {
  @Input() open = false;
  @Input() saving = false;
  @Input() title = '';
  @Input() description = '';
  @Input() actionLabel = '';
  @Input() remarks = '';
  @Output() save = new EventEmitter<string>();
  @Output() dismiss = new EventEmitter<void>();

  submit(): void {
    const value = this.remarks.trim();
    if (value) this.save.emit(value);
  }
}
