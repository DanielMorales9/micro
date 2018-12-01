import {Component, OnInit} from '@angular/core';
import {NavigationStart, Router} from '@angular/router';
import 'rxjs/add/operator/finally';
import { ChangeViewService, NotificationService} from "./shared/services";
import {AppService} from "./app.service";
import {User} from "./shared/model";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

    current_role_view: number;
    user: User;
    authenticated: boolean;

    constructor(private app: AppService,
                private router: Router,
                private changeViewService: ChangeViewService,
                private messageService: NotificationService) {
    }

    ngOnInit(): void {
        this.user = new User();
        this.router.events.subscribe(event => {
            if(event instanceof NavigationStart) {
                this.app.authenticate(undefined, (isAuthenticated) => {
                    if (isAuthenticated) {
                        this.current_role_view = this.app.current_role_view;
                        this.user = this.app.user;
                    }
                    this.authenticated = isAuthenticated;
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
        this.app.logout( () => {
            this.current_role_view = undefined;
            this.user = undefined;
            this.router.navigateByUrl("/auth/login")
        })
    }

    switchView(role) {
        this.app.changeView(role);
    }

}