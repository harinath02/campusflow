import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { DepartmentService } from '../../core/services/department.service';
import { ApprovalsComponent } from './approvals.component';
import { ApprovalsFacadeService } from './approvals.service';

describe('ApprovalsComponent', () => {
  let component: ApprovalsComponent;
  let fixture: ComponentFixture<ApprovalsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApprovalsComponent],
      providers: [
        provideRouter([]),
        { provide: ApprovalsFacadeService, useValue: { getPendingRequests: () => of([]), decide: () => of('') } },
        { provide: DepartmentService, useValue: { getDepartments: () => of([]) } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ApprovalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
