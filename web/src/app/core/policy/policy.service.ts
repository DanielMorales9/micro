import { Injectable, OnDestroy, OnInit, Directive } from '@angular/core';
import {AuthenticationService} from '../authentication';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Directive()
@Injectable()
export class PolicyService implements OnInit, OnDestroy {

    constructor(private auth: AuthenticationService) {}

    unsubscribe$ = new Subject<void>();
    currentRoleId = 1;

    ADMIN_POLICY = {
        gym: {
            canEdit: true
        },
        events: {
            canShowCourse: true,
            canShowPersonal: true,
            canShowTimeOff: true,
            canShowHoliday: true
        },
        bundleSpec: {
            canDelete: true,
            canEdit: true,
            canDisable: true,
            canCreate: true,
            canBookExternal: true
        },
        option: {
            canCreate: true,
            canDelete: true,
        },
        sale: {
            canDelete: true,
            canPay: true,
            canSell: true
        },
        user: {
            canDelete: true,
            canCreate: true,
            canEdit: true
        },
        admin: {
            canEdit: true,
            canDelete: true,
            canCreate: true,
            canSell: false,
            canSendToken: true,
            canMakeAppointments: false,
            canShow: {
                bundles: false,
                sales: false
            }
        },
        customer: {
            canEdit: true,
            canDelete: true,
            canCreate: true,
            canSell: true,
            canSendToken: true,
            canMakeAppointments: true,
            canShow: {
                bundles: true,
                sales: true,
                stats: true,
                sessions: true
            }
        },
        trainer: {
            canEdit: true,
            canDelete: true,
            canCreate: true,
            canSell: false,
            canSendToken: true,
            canMakeAppointments: false,
            canShow: {
                bundles: false,
                sales: false
            }
        },
        bundle: {
            canDelete: false,
            canEdit: true
        },
        payment: {
            canDelete: true
        },
        reservation: {
            canConfirm: true,
            canDelete: true
        },
        course: {
            canDelete: true,
            canConfirm: false,
            canBookAll: true,
        },
        personal: {
            canDelete: true,
            canComplete: true,
            canConfirm: true,
            canAssignWorkout: true,
        },
        timeOff: {
            canDelete: true
        },
        holiday: {
            canDelete: true
        },
        workout: {
            canCreate: true,
            canDelete: true,
            canEdit: true,
            canShow: true,
        }

    };

    TRAINER_POLICY = {
        events: {
            canShowCourse: true,
            canShowPersonal: true,
            canShowTimeOff: true,
            canShowHoliday: true
        },
        bundleSpec: {
            canDelete: false,
            canEdit: false,
            canDisable: false,
            canCreate: false,
            canBookExternal: true
        },
        sale: {
            canDelete: false,
            canPay: false
        },
        user: {
            canDelete: false,
            canCreate: false,
            canEdit: false
        },
        admin: {
            canEdit: false,
            canDelete: false,
            canCreate: false,
            canSell: false,
            canSendToken: false,
            canMakeAppointments: false,
            canShow: {
                bundles: false,
                sales: false
            }
        },
        customer: {
            canEdit: false,
            canDelete: false,
            canCreate: false,
            canSell: false,
            canSendToken: false,
            canMakeAppointments: true,
            canShow: {
                bundles: true,
                sales: false,
                stats: true,
                sessions: true
            }
        },
        trainer: {
            canEdit: false,
            canDelete: false,
            canCreate: false,
            canSell: false,
            canSendToken: false,
            canMakeAppointments: false,
            canShow: {
                bundles: false,
                sales: false
            }
        },
        bundle: {
            canDelete: false,
            canEdit: false
        },
        reservation: {
            canConfirm: true,
            canDelete: true
        },
        course: {
            canDelete: false,
            canBookAll: true,
        },
        personal: {
            canDelete: true,
            canComplete: true,
            canConfirm: true,
            canAssignWorkout: true,
        },
        timeOff: {
            canDelete: true
        },
        holiday: {
            canDelete: false
        },
        workout: {
            canCreate: true,
            canDelete: true,
            canEdit: true,
            canShow: true,

        }
    };

    CUSTOMER_POLICY = {
        events: {
            canShowCourse: true,
            canShowPersonal: true,
            canShowTimeOff: false,
            canShowHoliday: true
        },
        bundleSpec: {
            canDelete: false,
            canCreate: false,
            canEdit: false,
            canDisable: false,
            canBookExternal: false
        },
        sale: {
            canDelete: false,
            canPay: false
        },
        user: {
            canDelete: false,
            canCreate: false,
            canEdit: true
        },
        admin: {
            canEdit: false,
            canDelete: false,
            canCreate: false,
            canSell: false,
            canSendToken: false,
            canMakeAppointments: false,
            canShow: {
                bundles: false,
                sales: false
            }
        },
        customer: {
            canEdit: false,
            canDelete: false,
            canCreate: false,
            canSell: false,
            canSendToken: false,
            canMakeAppointments: false,
            canShow: {
                bundles: true,
                sales: false,
                stats: true,
                sessions: true
            }
        },
        trainer: {
            canEdit: false,
            canDelete: false,
            canCreate: false,
            canSell: false,
            canSendToken: false,
            canMakeAppointments: false,
            canShow: {
                bundles: false,
                sales: false
            }
        },
        bundle: {
            canDelete: false,
            canEdit: false
        },
        personal: {},
        course: {
            canBook: true,
        }
    };

    POLICIES = [this.ADMIN_POLICY, this.TRAINER_POLICY, this.CUSTOMER_POLICY];

    ngOnInit() {
        this.auth.getObservableCurrentUserRoleId()
            .pipe(takeUntil(this.unsubscribe$))
            .subscribe(v => {
                this.currentRoleId = v;
            });
    }

    ngOnDestroy(): void {
        this.unsubscribe$.next();
        this.unsubscribe$.complete();
    }


        get(entity: string, ...action) {
        const myPolicy = this.POLICIES[this.currentRoleId - 1];
        if (entity in myPolicy) {
            let policy = myPolicy[entity];
            let a, index;
            for (index = 0; index < action.length; ++index) {
                a = action[index];
                if (a in policy) {
                    policy = policy[a];
                }
                else {
                    return false;
                }
            }
            return policy;
        }
        return false;
    }
}
