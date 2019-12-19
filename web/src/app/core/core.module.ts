import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';
import {ApiPrefixInterceptor, CacheInterceptor, HttpCacheService, HttpService, LoaderInterceptor, XhrInterceptor} from './http';
import {LoaderComponent, LoaderService} from './loader';
import {MatProgressBarModule} from '@angular/material';
import {AuthGuardService, RoleGuardService} from './guards';
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
import {BundleHelperService, BundlePayHelperService, SaleHelperService, UserHelperService} from './helpers';


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
        HttpCacheService,
        ApiPrefixInterceptor,
        CacheInterceptor,
        AuthService,
        BundleService,
        LoaderInterceptor,
        LoaderService,
        AuthGuardService,
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
        BundleHelperService,
        BundlePayHelperService,

    ]
})
export class CoreModule {

}
