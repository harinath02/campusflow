import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { RequestDetailComponent } from './request-detail.component';
import { RequestDetailFacadeService } from './request-detail.service';

describe('RequestDetailComponent', () => {
  let component: RequestDetailComponent;
  let fixture: ComponentFixture<RequestDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestDetailComponent],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
        {
          provide: RequestDetailFacadeService,
          useValue: {
            getRequestDetail: () => of({
              request: { id: 1, requestNumber: 'REQ-1', requestType: 'General', requesterName: 'User', title: 'Test', status: 'PENDING' },
              approvals: []
            })
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RequestDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
