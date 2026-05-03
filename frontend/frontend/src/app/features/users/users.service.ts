import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateUserPayload, User } from '../../core/models/user.model';
import { UserService } from '../../core/services/user.service';

@Injectable({ providedIn: 'root' })
export class UsersFacadeService {
  private readonly userService = inject(UserService);

  getUsers(): Observable<User[]> {
    return this.userService.getUsers();
  }

  createUser(payload: CreateUserPayload): Observable<User> {
    return this.userService.createUser(payload);
  }
}
