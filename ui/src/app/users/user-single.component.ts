import {Component, OnInit} from "@angular/core";
import {AppService} from "../services/app.service";
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../services/users.service";
import {MessageService} from "../services/message.service";
import {ChangeViewService} from "../services/change-view.service";

@Component({
    templateUrl: './user-single.component.html',
    styleUrls: ['../app.component.css'],
})
export class SingleUserComponent implements OnInit {

    current_role_view: number;
    sub: any;
    id : number;
    roles: string[];
    email: string;

    constructor(private app: AppService,
                private userService: UserService,
                private changeViewService: ChangeViewService,
                private messageService: MessageService,
                private route: ActivatedRoute) {
        this.current_role_view = this.app.current_role_view;
        this.email = this.app.user.email;
        this.changeViewService.getView().subscribe(value => this.current_role_view = value)
    }

    _systemError() {
        return err => {
            console.log(err);
            let message ={
                text: err.error.message,
                class: "alert-danger"
            };
            this.messageService.sendMessage(message);
        }
    }

    private getRoles(user) {
        this.userService.getRoles(user, res => {
            this.roles = res['_embedded']['roles'];
        }, this._systemError())
    }

    ngOnInit(): void {
        this.sub = this.route.params.subscribe(params => {
            this.id = +params['id?'];
            console.log(params);
            if (this.id) {
                this.getRoles({ id: this.id });
            }
            else {
                this.app.getFullUser(res => {
                    this.id = res.id;
                    if (!res['roles']) {
                        this.getRoles(res);
                    }
                    else {
                        this.roles = res['roles']['_embedded']['roleResources']
                    }
                }, this.app._systemError())
            }
        });
    }

    ngOnDestroy() {
        this.sub.unsubscribe();
    }

}