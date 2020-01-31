import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {
    SalesComponent,
    UserDetailsComponent,
    UserItemComponent,
    UserModalComponent,
    UsersComponent,
    InfoCourseEventComponent,
    DeletePersonalEventComponent,
    GymClosedComponent,
    NoItemComponent,
    InfoPersonalEventComponent,
    SearchDateToolbar,
    SearchMixedToolbar,
    SimpleSearchToolbar,
    DeleteTimeOffEventComponent,
    ReservationsComponent,
    DeleteHolidayEventComponent,
    ReserveCourseEventComponent,
    DeleteReservationCourseEventComponent,
    CustomerInfoModalComponent,
    BundleSpecModalComponent,
    BundleSpecDetailsComponent,
    BundleModalComponent,
    OptionModalComponent,
    FilterSearchToolbar,
    BundleItemComponent,
    BundlesCustomerComponent,
    BundleSpecItemComponent,
    BundleSpecsComponent,
    FilterComponent,
    ProfileComponent,
    ChangePasswordModalComponent
} from './index';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatDialogModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatNativeDateModule,
    MatOptionModule,
    MatSelectModule,
    MatToolbarModule
} from '@angular/material';
import {
    CalendarButtonToolbar,
    CalendarHeaderToolbar,
    CustomerDeleteModalComponent,
    CustomerHourModalComponent
} from './calendar';
import {CalendarModule, DateAdapter} from 'angular-calendar';
import {adapterFactory} from 'angular-calendar/date-adapters/date-fns';
import {PaySaleModalComponent, SaleDetailsComponent, SaleItemComponent} from './sales';
import {RouterModule} from '@angular/router';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {BundleDetailsComponent} from './bundles';
import {MAT_DATE_LOCALE} from '@angular/material/core';
import {MatMenuModule} from '@angular/material/menu';
import {MatTreeModule} from '@angular/material/tree';
import {MatBadgeModule} from '@angular/material/badge';
import {MatTableModule} from '@angular/material/table';
import {GymModalComponent, GymSettingsComponent} from './settings';
import {GymService} from '../core/utilities';

@NgModule({
    imports: [
        RouterModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        CalendarModule.forRoot({
            provide: DateAdapter,
            useFactory: adapterFactory
        }),
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatDialogModule,
        MatInputModule,
        MatButtonModule,
        MatListModule,
        MatOptionModule,
        MatSelectModule,
        MatToolbarModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatExpansionModule,
        MatCheckboxModule,
        ScrollingModule,
        MatMenuModule,
        MatTreeModule,
        MatBadgeModule,
        MatTableModule,
    ],

    entryComponents: [
        UserModalComponent,
        PaySaleModalComponent,
        BundleSpecModalComponent,
        CustomerHourModalComponent,
        CustomerInfoModalComponent,
        CustomerDeleteModalComponent,
        BundleModalComponent,
        OptionModalComponent,
        ChangePasswordModalComponent,
        GymModalComponent
    ],
    declarations: [
        NoItemComponent,
        GymClosedComponent,
        InfoCourseEventComponent,
        ReserveCourseEventComponent,
        InfoPersonalEventComponent,
        DeletePersonalEventComponent,
        DeleteTimeOffEventComponent,
        DeleteHolidayEventComponent,
        ReservationsComponent,
        DeleteReservationCourseEventComponent,
        SearchDateToolbar,
        SimpleSearchToolbar,
        FilterSearchToolbar,
        SearchMixedToolbar,
        CalendarHeaderToolbar,
        CalendarButtonToolbar,
        UserModalComponent,
        SaleItemComponent,
        PaySaleModalComponent,
        SaleDetailsComponent,
        BundleSpecModalComponent,
        BundleSpecDetailsComponent,
        BundleDetailsComponent,
        SalesComponent,
        UsersComponent,
        UserItemComponent,
        UserDetailsComponent,
        CustomerHourModalComponent,
        CustomerInfoModalComponent,
        CustomerDeleteModalComponent,
        BundleModalComponent,
        OptionModalComponent,
        BundlesCustomerComponent,
        BundleItemComponent,
        BundleSpecItemComponent,
        BundleSpecsComponent,
        FilterComponent,
        ProfileComponent,
        GymSettingsComponent,
        GymModalComponent,
        ChangePasswordModalComponent
    ],
    exports: [
        BundleModalComponent,
        NoItemComponent,
        FilterComponent,
        GymClosedComponent,
        InfoPersonalEventComponent,
        DeletePersonalEventComponent,
        DeleteTimeOffEventComponent,
        DeleteHolidayEventComponent,
        ReserveCourseEventComponent,
        InfoCourseEventComponent,
        ReservationsComponent,
        DeleteReservationCourseEventComponent,
        CalendarHeaderToolbar,
        CalendarButtonToolbar,
        SimpleSearchToolbar,
        SearchMixedToolbar,
        SearchDateToolbar,
        FilterSearchToolbar,
        SaleItemComponent,
        PaySaleModalComponent,
        SaleDetailsComponent,
        BundleSpecModalComponent,
        BundleSpecDetailsComponent,
        BundleDetailsComponent,
        BundlesCustomerComponent,
        BundleItemComponent,
        SalesComponent,
        CustomerHourModalComponent,
        CustomerInfoModalComponent,
        CustomerDeleteModalComponent,
        OptionModalComponent,
        BundleSpecItemComponent,
        BundleSpecsComponent,
        ProfileComponent
    ],
    providers: [
        {provide: MAT_DATE_LOCALE, useValue: 'it-IT'}
    ]

})
export class SharedModule {

}
