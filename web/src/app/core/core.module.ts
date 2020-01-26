import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';
import {ApiPrefixInterceptor, CacheInterceptor, HttpCacheService, HttpService, LoaderInterceptor, XhrInterceptor} from './http';
import {LoaderComponent, LoaderService} from './loader';
import {MatProgressBarModule} from '@angular/material';
import {AuthGuardService, NoAuthGuardService, RoleGuardService} from './guards';
import {
    AuthService,
    BundleService,
    BundlesNotDisabledService,
    BundleSpecsService,
    EventService,
    ReservationService,
    SalesService, UserService
} from './controllers';
import {DateService, GymService, ScreenService, SnackBarService} from './utilities';
import {
    BundleSpecHelperService,
    SaleHelperService,
    UserHelperService,
    BundleHelperService, BundleCustomerHelperService,
} from './helpers';
import {PolicyService} from './policy';
import {CalendarFacade} from './facades';


@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        MatProgressBarModule
    ],
    declarations: [LoaderComponent],
    exports: [LoaderComponent],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: XhrInterceptor,
            multi: true
        },
        {
            provide: HttpClient,
            useClass: HttpService
        },
        CalendarFacade,
        HttpCacheService,
        ApiPrefixInterceptor,
        CacheInterceptor,
        AuthService,
        BundleService,
        LoaderInterceptor,
        LoaderService,
        AuthGuardService,
        NoAuthGuardService,
        RoleGuardService,
        ScreenService,
        DateService,
        SnackBarService,
        BundleSpecsService,
        BundlesNotDisabledService,
        SalesService,
        EventService,
        ReservationService,
        UserService,
        GymService,
        SaleHelperService,
        UserHelperService,
        BundleSpecHelperService,
        BundleHelperService,
        BundleCustomerHelperService,
        PolicyService
    ]
})
export class CoreModule {

}
