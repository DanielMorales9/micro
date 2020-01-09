import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {CustomerCalendarComponent} from './calendar';
import {SaleDetailsComponent, SalesComponent} from '../shared/components/sales';
import {BundleSpecDetailsComponent} from '../shared/components/bundle-specs';
import {BundleDetailsComponent} from '../shared/components/bundles';

const routes = [
    { path: '', children : [
            { path: '', redirectTo: 'calendar', pathMatch: 'full'},
            { path: 'calendar', component: CustomerCalendarComponent },
            { path: 'sales', component: SalesComponent },
            { path: 'sales/:id', component: SaleDetailsComponent },
            { path: 'bundleSpecs/:id', component: BundleSpecDetailsComponent },
            { path: 'bundle/:id', component: BundleDetailsComponent },
            { path: '**', redirectTo: 'bundleSpecs' }
        ]},
];

@NgModule({
    imports: [
        RouterModule.forChild(routes)
    ],
    exports: [
        RouterModule
    ]
})
export class CustomerRouting { }
