import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {
    BundleItemComponent,
    BundleModalComponent,
    BundlesCustomerComponent,
    BundleSpecDetailsComponent,
    BundleSpecItemComponent,
    BundleSpecModalComponent,
    BundleSpecsComponent,
    CalendarControlsComponent,
    CalendarCustomerControlsComponent,
    ChangePasswordModalComponent,
    CustomerInfoModalComponent,
    DeleteHolidayEventComponent,
    DeletePersonalEventComponent,
    DeleteReservationCourseEventComponent,
    DeleteTimeOffEventComponent, EventDetailsComponent,
    FilterComponent,
    FilterSearchToolbar,
    GymClosedComponent,
    InfoCourseEventComponent,
    InfoPersonalEventComponent,
    NoItemComponent,
    OptionModalComponent,
    ProfileComponent, ReservationModalComponent,
    ReservationsComponent,
    ReserveCourseEventComponent,
    SalesComponent,
    SearchDateToolbar,
    SearchMixedToolbar,
    SimpleSearchToolbar,
    UserControlsComponent,
    UserDetailsComponent,
    UserItemComponent,
    UserModalComponent,
    UsersComponent
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
import {CalendarButtonToolbar, CalendarHeaderToolbar, CustomerDeleteModalComponent, CustomerHourModalComponent} from './calendar';
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
import {NgxMaterialTimepickerModule} from 'ngx-material-timepicker';

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
        NgxMaterialTimepickerModule.setLocale('it-IT')

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
        GymModalComponent,
        CalendarControlsComponent,
        CalendarCustomerControlsComponent,
        UserControlsComponent,
        ReservationModalComponent
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
        ChangePasswordModalComponent,
        CalendarControlsComponent,
        CalendarCustomerControlsComponent,
        UserControlsComponent,
        EventDetailsComponent,
        ReservationModalComponent,
    ],
    exports: [
        CalendarControlsComponent,
        CalendarCustomerControlsComponent,
        UserControlsComponent,
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
        ProfileComponent,
        EventDetailsComponent
    ],
    providers: [
        {provide: MAT_DATE_LOCALE, useValue: 'it-IT'}
    ]

})
export class SharedModule {

}
