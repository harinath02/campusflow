import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  template: `
    @if (open) {
      <div class="modal-backdrop" (click)="cancel.emit()"></div>
      <section class="dialog" role="dialog" aria-modal="true">
        <h3>{{ title }}</h3>
        <p>{{ message }}</p>
        <div class="panel-actions">
          <button class="btn ghost" type="button" (click)="cancel.emit()">Cancel</button>
          <button class="btn danger" type="button" (click)="confirm.emit()">Confirm</button>
        </div>
      </section>
    }
  `
})
export class ConfirmDialogComponent {
  @Input() open = false;
  @Input() title = 'Confirm action';
  @Input() message = 'This action cannot be undone.';
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
}
