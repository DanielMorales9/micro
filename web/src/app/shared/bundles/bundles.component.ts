import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Bundle} from '../model';
import {BundleService} from '../../core/controllers';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackBarService} from '../../core/utilities';
import {BundleHelperService, QueryableDatasource} from '../../core/helpers';
import {MatDialog} from '@angular/material/dialog';
import {PolicyService} from '../../core/policy';
import {first, takeUntil} from 'rxjs/operators';
import {SearchComponent} from '../search-component';

@Component({
    templateUrl: './bundles.component.html',
    styleUrls: ['../../styles/search-list.css', '../../styles/root.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BundlesComponent extends SearchComponent<Bundle> implements OnInit {

    SIMPLE_NO_CARD_MESSAGE = 'Nessun pacchetto acquistato';

    id: number;

    canDelete: boolean;
    canEdit: boolean;

    filters = [
        {name: 'Attivi', value: false},
        {name: 'Terminati', value: true},
        {name: 'Entrambi', value: undefined}];
    filterName = 'expired';
    selected = undefined;

    constructor(private service: BundleService,
                private helper: BundleHelperService,
                protected route: ActivatedRoute,
                protected router: Router,
                private dialog: MatDialog,
                private policy: PolicyService,
                private snackbar: SnackBarService) {
        super(router, route);
        this.ds = new QueryableDatasource<Bundle>(helper, this.query);
    }

    ngOnInit(): void {
        this.getPolicies();
        this.initQueryParams(this.id);
    }

    protected initQueryParams(id?) {
        this.route.queryParams
            .pipe(
                first(),
                takeUntil(this.unsubscribe$))
            .subscribe(params => {
                this.queryParams = Object.assign({}, params);
                if (Object.keys(params).length > 0) {
                    if (!!this.queryParams.time) {
                        this.queryParams.time = new Date(this.queryParams.time);
                    }
                }
                this.search(this.queryParams);
            });
    }

    protected getDefaultQueryParams($event?): any {
        if (this.id) { $event.id = this.id; }
        return $event;
    }

    private getPolicies() {
        this.canDelete = this.policy.get('bundle', 'canDelete');
        this.canEdit = this.policy.get('bundle', 'canEdit');
    }

    handleEvent($event) {
        if ($event.type === 'info') {
            this.goToDetails($event.bundle);
        } else if ($event.type === 'edit') {
            this.service.patchBundle($event.bundle)
                .pipe(takeUntil(this.unsubscribe$))
                .subscribe({
                    error: err => this.snackbar.open(err.error.message)
                });
        }
    }

    private goToDetails(bundle: any) {
        this.router.navigate(['bundles', bundle.id], {
            relativeTo: this.route.parent
        });
    }

    trackBy(index, item) {
        return item ? item.id : index;
    }
}
