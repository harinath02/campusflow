import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-portal',
  imports: [RouterLink],
  templateUrl: './portal.component.html',
  styleUrl: './portal.component.scss'
})
export class PortalComponent {
  protected readonly personas = [
    { key: 'student', title: 'Student portal', subtitle: 'Create requests, upload details, and track status.', icon: 'ST' },
    { key: 'department', title: 'Department desk', subtitle: 'Review department queue and record decisions.', icon: 'DP' },
    { key: 'admin', title: 'Admin console', subtitle: 'Manage departments, users, request types, and workflow health.', icon: 'AD' }
  ];
}
