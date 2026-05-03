import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { DepartmentsComponent } from './departments.component';
import { DepartmentsFacadeService } from './departments.service';

describe('DepartmentsComponent', () => {
  let component: DepartmentsComponent;
  let fixture: ComponentFixture<DepartmentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepartmentsComponent],
      providers: [
        { provide: DepartmentsFacadeService, useValue: { getDepartments: () => of([]), saveDepartment: () => of({}), deleteDepartment: () => of('') } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DepartmentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
