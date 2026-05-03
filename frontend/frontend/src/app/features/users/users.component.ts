import { Component, inject, OnInit } from '@angular/core';
import { User } from '../../core/models/user.model';
import { UserService } from '../../core/services/user.service';
import { ToastService } from '../../shared/toast/toast.service';

@Component({ selector: 'app-users', imports: [], templateUrl: './users.component.html', styleUrl: './users.component.scss' })
export class UsersComponent implements OnInit {
  private readonly userService=inject(UserService); private readonly toast=inject(ToastService);
  protected users: User[]=[]; protected loading=false; protected search=''; protected roleFilter='ALL';
  protected get filtered(){ const q=this.search.toLowerCase().trim(); return this.users.filter(u=>(this.roleFilter==='ALL'||u.role===this.roleFilter)&&(!q||`${u.name} ${u.email} ${u.role} ${u.department||''} ${u.rollNumber||''}`.toLowerCase().includes(q))); }
  ngOnInit(): void { this.load(); }
  load(): void { this.loading=true; this.userService.getUsers().subscribe({next:v=>{this.users=v??[];this.loading=false;},error:()=>{this.loading=false;this.toast.error('Users failed to load');}}); }
}
