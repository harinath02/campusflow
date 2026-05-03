import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { UserService } from '../../../core/services/user.service';
import { RequestListComponent } from './request-list.component';
import { RequestListFacadeService } from './request-list.service';

describe('RequestListComponent', () => {
  let component: RequestListComponent;
  let fixture: ComponentFixture<RequestListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestListComponent],
      providers: [
        provideRouter([]),
        { provide: RequestListFacadeService, useValue: { getRequests: () => of([]), getRequestTypes: () => of([]), createRequest: () => of({}) } },
        { provide: UserService, useValue: { getUsers: () => of([]) } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RequestListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
