import { DatePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { EmptyStateComponent } from '../../../shared/empty-state/empty-state.component';
import { RequestTimelineComponent } from '../../../shared/request-timeline/request-timeline.component';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { RequestDetailViewModel } from './request-detail.model';
import { RequestDetailFacadeService } from './request-detail.service';

@Component({
  selector: 'app-request-detail',
  imports: [DatePipe, RouterLink, EmptyStateComponent, RequestTimelineComponent, StatusBadgeComponent],
  templateUrl: './request-detail.component.html',
  styleUrl: './request-detail.component.scss'
})
export class RequestDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly auth = inject(AuthService);
  private readonly detailFacade = inject(RequestDetailFacadeService);

  protected readonly currentUser = this.auth.getCurrentUser();
  protected viewModel: RequestDetailViewModel | null = null;
  protected errorMessage = '';

  protected get backLink(): string {
    if (this.currentUser?.role === 'OFFICER') return '/department/queue';
    if (this.currentUser?.role === 'ADMIN') return '/requests';
    return '/student/requests';
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.detailFacade.getRequestDetail(id).subscribe({
      next: (viewModel) => this.viewModel = viewModel,
      error: () => this.errorMessage = 'Request details could not be loaded.'
    });
  }
}
