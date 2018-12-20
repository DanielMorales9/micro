import { NgModule } from '@angular/core';
import {RouterModule} from "@angular/router";
import {HomeComponent} from "./home.component";
import {UsersComponent} from "./users";
import {BundlesComponent} from "./bundles";
import {CalendarComponent} from "./calendar";
import {SalesComponent} from "../shared/components/sales";

const routes = [

    { path: '', component: HomeComponent, children : [
            { path: '', redirectTo: 'calendar', pathMatch: "full"},
            { path: 'users', component: UsersComponent },
            { path: 'sales', component: SalesComponent },
            { path: 'bundles', component: BundlesComponent },
            { path: 'calendar', component: CalendarComponent },
            { path: '**', redirectTo: 'calendar' }
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
export class HomeRouting { }
