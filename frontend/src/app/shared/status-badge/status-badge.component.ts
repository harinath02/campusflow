import { Component, Input } from '@angular/core';
import { getStatusBadgeClass } from '../../core/constants/request-status.constants';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  template: `<span class="badge {{ tone }}">{{ label }}</span>`
})
export class StatusBadgeComponent {
  @Input({ required: true }) status = '';

  get label(): string {
    return (this.status || 'UNKNOWN').replace('_', ' ');
  }

  get tone(): string {
    return getStatusBadgeClass(this.status);
  }
}
