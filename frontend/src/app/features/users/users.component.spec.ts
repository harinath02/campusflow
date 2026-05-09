import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { DepartmentService } from '../../core/services/department.service';
import { RoleService } from '../../core/services/role.service';
import { UsersComponent } from './users.component';
import { UsersFacadeService } from './users.service';

describe('UsersComponent', () => {
  let component: UsersComponent;
  let fixture: ComponentFixture<UsersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsersComponent],
      providers: [
        { provide: UsersFacadeService, useValue: { getUsers: () => of([]), createUser: () => of({}) } },
        { provide: RoleService, useValue: { getRoles: () => of([]) } },
        { provide: DepartmentService, useValue: { getDepartments: () => of([]) } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
