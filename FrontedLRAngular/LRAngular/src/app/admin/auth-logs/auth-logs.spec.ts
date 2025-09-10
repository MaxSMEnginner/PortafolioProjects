import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthLogs } from './auth-logs';

describe('AuthLogs', () => {
  let component: AuthLogs;
  let fixture: ComponentFixture<AuthLogs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthLogs]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AuthLogs);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
