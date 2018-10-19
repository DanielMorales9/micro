import {Component, OnInit} from '@angular/core';
import { AppService } from './services/app.service';
import { HttpClient } from '@angular/common/http';
import {ActivatedRoute, NavigationEnd, NavigationStart, Router} from '@angular/router';
import 'rxjs/add/operator/finally';
import {MessageService} from "./services/message.service";
import {ChangeViewService} from "./services/change-view.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

    current_role_view: number;
    user: any;
    roles: any[];

    constructor(private app: AppService,
                private http: HttpClient,
                private router: Router,
                private changeViewService: ChangeViewService,
                private messageService: MessageService) {

    }

    ngOnInit(): void {

        this.router.events.subscribe(event => {
            if(event instanceof NavigationStart && event.url == "/") {
                this.app.authenticate(undefined, (isAuthenticated) => {
                    if (isAuthenticated) {
                        this.current_role_view = this.app.current_role_view;
                        this.roles = this.app.roles;
                    }
                }, undefined);
            }
        });

        this.handleMessage();
        this.changeViewService.getView().subscribe(value => this.current_role_view = value)
    }

    handleMessage() {
        this.messageService.getMessage().subscribe((mess) => {
            let node = document.createElement("div");
            node.className = "alert " + mess.class;
            node.innerText = mess.text;
            let delay = mess.delay || 10000;
            node.addEventListener('click', function() {
                node.remove()
            }, false);
            document.getElementById('notifications').appendChild(node);
            setTimeout(function() {
                node.remove()
            }, delay);
        });
    }

    logout() {
        this.http.get('/logout').finally(() => {
            this.app.authenticated = false;
            this.current_role_view = undefined;
            this.roles = undefined;
            this.router.navigateByUrl("/login")
        }).subscribe(res => {
            this.app.logout();
        }, error1 => {console.log(error1)});
    }

    authenticated() { return this.app.authenticated; }


    switchView(role) {
        this.app.changeView(role);
    }

    hasMoreThanOneRole() {
        return this.roles && this.roles.length > 1;
    }
}