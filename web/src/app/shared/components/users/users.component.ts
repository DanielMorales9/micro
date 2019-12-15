import {Component, OnInit} from '@angular/core';
import {User} from '../../model';
import {QueryableDatasource, UserHelperService, UserService} from '../../services';
import {AuthService, GymService, SnackBarService} from '../../../services';
import {MatDialog} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {UserModalComponent} from './user-modal.component';
import {AuthenticationService} from '../../../core/authentication';

@Component({
    templateUrl: './users.component.html',
    styleUrls: ['../../../styles/search-list.css']
})
export class UsersComponent implements OnInit {

    SIMPLE_NO_CARD_MESSAGE = 'Nessun utente registrato';

    currentUserId: number;

    query: string;
    private pageSize = 10;
    private queryParams: any;
    ds: QueryableDatasource<User>;
    canAdd: boolean;
    canDelete: boolean;
    canPatch: boolean;
    type: string;

    constructor(private service: UserService,
                private helper: UserHelperService,
                private gymService: GymService,
                private router: Router,
                private activatedRoute: ActivatedRoute,
                private auth: AuthenticationService,
                private authService: AuthService,
                private snackbar: SnackBarService,
                private dialog: MatDialog) {
        this.currentUserId = this.auth.getUser().id;
        this.ds = new QueryableDatasource<User>(this.helper, this.pageSize, this.query);
    }


    ngOnInit(): void {
        this.type = this.activatedRoute.parent.parent.snapshot.routeConfig.path;
        switch (this.type) {
            case 'admin':
                this.canPatch = this.canDelete = this.canAdd = true;
                break;
            case 'trainer':
                this.canPatch = this.canDelete = this.canAdd = false;
                break;
        }
        this.initQueryParams();
    }

    private initQueryParams() {
        this.activatedRoute.queryParams.subscribe(params => {
            this.queryParams = Object.assign({}, params);
            if (Object.keys(params).length > 0) {
                this.query = this.queryParams.query || undefined;
            }
            this.search(this.queryParams);
        });
    }

    private updateQueryParams($event?) {
        this.queryParams = $event;
        this.router.navigate(
            [],
            {
                relativeTo: this.activatedRoute,
                queryParams: this.queryParams,
            });
    }

    openDialog(): void {
        const dialogRef = this.dialog.open(UserModalComponent, {
            data : {
                title: 'Registra Nuovo Utente',
                method: 'post'
            },
        });

        dialogRef.afterClosed().subscribe(user => {
            if (user) { this.createUser(user); }
        });
    }

    search($event?) {
        if ($event) {if (!$event.query) { $event = {}; }}
        if (this.type === 'trainer') { $event.type = this.type; }
        this.ds.setQuery($event);
        this.ds.fetchPage(0);
        this.updateQueryParams($event);
    }

    handleEvent($event: any) {
        switch ($event.type) {
            case 'delete':
                this.deleteUser($event.user);
                break;
            case 'patch':
                this.patchUser($event.user);
                break;
        }
    }

    private async deleteUser(user: User) {
        const confirmed = confirm(`Vuoi rimuovere l'utente ${user.firstName} ${user.lastName}?`);
        if (confirmed) {
            const [data, err] = await this.service.delete(user.id);
            if (err) {
                this.snackbar.open(err.error.message);
            } else {
                this.search();
            }
        }
    }

    private async patchUser(user: User) {
        const [data, error] = await this.service.patch(user);
        if (error) {
            this.snackbar.open(error.error.message);
        } else {
            this.snackbar.open(`L'utente ${user.lastName} è stato modificato`);
        }
        this.search();
    }

    private async createUser(user: User) {
        const [data, err] = await this.authService.registration(user);
        if (err) {
            if (err.status === 500) {
                this.snackbar.open(err.error.message);
            } else { throw err; }
        } else {
            const message = `L'utente ${user.lastName} è stato creato`;
            this.snackbar.open(message);
        }
        this.search();
    }

    itsMe(id: any) {
        return this.currentUserId !== id;
    }
}
