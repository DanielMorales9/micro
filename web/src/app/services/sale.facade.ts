import {Injectable} from '@angular/core';
import {AuthenticationService} from '../core/authentication';

@Injectable()
export class SaleFacade {

    constructor(private auth: AuthenticationService) {
    }

    canPay() {
        return this.auth.getCurrentRoleView() === 1;
    }

    canDelete() {
        return this.auth.getCurrentRoleView() === 1;
    }
}
