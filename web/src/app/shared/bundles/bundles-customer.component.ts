import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Bundle} from '../model';
import {BundleService} from '../../core/controllers';
import {ActivatedRoute, Router} from '@angular/router';
import {SnackBarService} from '../../core/utilities';
import {BundleCustomerHelperService, QueryableDatasource} from '../../core/helpers';
import {MatDialog} from '@angular/material/dialog';
import {PolicyService} from '../../core/policy';
import {first, takeUntil} from 'rxjs/operators';
import {BaseComponent} from '../base-component';

@Component({
    templateUrl: './bundles-customer.component.html',
    styleUrls: ['../../styles/search-list.css', '../../styles/root.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BundlesCustomerComponent extends BaseComponent implements OnInit {

    SIMPLE_NO_CARD_MESSAGE = 'Nessun pacchetto acquistato';

    query: any;
    id: number;

    ds: QueryableDatasource<Bundle>;
    canDelete: boolean;
    canEdit: boolean;

    private queryParams: any;
    private pageSize = 10;
    filters = [
        {name: 'Attivi', value: false},
        {name: 'Terminati', value: true},
        {name: 'Entrambi', value: undefined}];
    filterName = 'expired';
    selected = undefined;

    constructor(private service: BundleService,
                private helper: BundleCustomerHelperService,
                private route: ActivatedRoute,
                private router: Router,
                private dialog: MatDialog,
                private policy: PolicyService,
                private snackbar: SnackBarService) {
        super();
        this.ds = new QueryableDatasource<Bundle>(helper, this.pageSize, this.query);
    }

    ngOnInit(): void {
        this.getPolicies();
        this.route.params.pipe(
            first(),
            takeUntil(this.unsubscribe$))
        .subscribe(param => {
            this.id = +param['id'];
            this.initQueryParams(this.id);
        });
    }

    private initQueryParams(id?) {
        this.route.queryParams.pipe(first()).subscribe(params => {
            this.queryParams = Object.assign({}, params);
            if (Object.keys(params).length > 0) {
                if (!!this.queryParams.date) {
                    this.queryParams.date = new Date(this.queryParams.date);
                }
            }
            if (!!id) {
                this.queryParams.id = id;
            }
            this.search(this.queryParams);
        });
    }

    private updateQueryParams(event?) {
        if (!event) { event = {}; }
        if (this.id) { event.id = this.id; }

        this.queryParams = this.query = event;
        this.router.navigate(
            [],
            {
                replaceUrl: true,
                relativeTo: this.route,
                queryParams: this.queryParams,
            });
    }

    search($event?) {
        this.ds.setQuery($event);
        this.ds.fetchPage(0);
        this.updateQueryParams($event);
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
                .subscribe( {
                    error: err => this.snackbar.open(err.error.message)
                });
        }
    }

    private goToDetails(bundle: any) {
        this.router.navigate(['bundles', bundle.id], {
            relativeTo: this.route.parent
        });
    }
}
