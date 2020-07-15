import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Sale} from '../model';
import {ActivatedRoute, Router} from '@angular/router';
import {SalesService} from '../../core/controllers';
import {AuthenticationService} from '../../core/authentication';
import {SnackBarService} from '../../core/utilities';
import {QueryableDatasource, SaleHelperService} from '../../core/helpers';
import {PolicyService} from '../../core/policy';
import {first, takeUntil} from 'rxjs/operators';
import {SearchComponent} from '../search-component';


@Component({
    templateUrl: './sales.component.html',
    styleUrls: ['../../styles/search-list.css', '../../styles/root.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SalesComponent extends SearchComponent<Sale> implements OnInit {

    private SIMPLE_NO_CARD_MESSAGE = 'Nessuna vendita disponibile';

    query: any;
    noCardMessage: string;

    id: number;

    canPay: boolean;
    canDelete: boolean;
    canSell: boolean;

    mixed: boolean;
    filters = [
        {name: 'Da Pagare', value: false},
        {name: 'Pagati', value: true},
        {name: 'Entrambi', value: undefined}];
    filterName = 'payed';
    selected = undefined;
    private root: string;

    constructor(private helper: SaleHelperService,
                private service: SalesService,
                private auth: AuthenticationService,
                protected router: Router,
                protected route: ActivatedRoute,
                private policy: PolicyService,
                private snackbar: SnackBarService) {
        super(router, route);
        this.noCardMessage = this.SIMPLE_NO_CARD_MESSAGE;
        this.ds = new QueryableDatasource<Sale>(helper, this.query);
    }

    ngOnInit(): void {
        this.root = this.route.parent.parent.snapshot.routeConfig.path;
        this.getPolicy();

        this.route.params
            .pipe(first(),
                takeUntil(this.unsubscribe$))
            .subscribe(params => {
                this.id = +params['id'];
                this.initQueryParams(this.id);
            });
    }

    private getPolicy() {
        this.canDelete = this.policy.get('sale', 'canDelete');
        this.canPay = this.policy.get('sale', 'canPay');
        this.canSell = this.policy.get('sale', 'canSell') && !!this.id;
    }

    protected initQueryParams(id?) {
        this.route.queryParams
            .pipe(takeUntil(this.unsubscribe$))
            .subscribe(params => {
                this.queryParams = Object.assign({}, params);
                if (Object.keys(params).length > 0) {
                    if (!!this.queryParams.date) {
                        this.queryParams.date = new Date(this.queryParams.date);
                    }
                }

                if (!!id || !!this.id) {
                    this.queryParams.id = this.id || id;
                }
                this.mixed = this.canDelete && !this.id;
                this.search(this.queryParams);
            });
    }

    protected updateQueryParams(event?) {
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

    handleEvent($event) {
        switch ($event.type) {
            case 'delete':
                this.deleteSale($event.sale);
                break;
            case 'pay':
                this.paySale($event.sale, $event.amount);
                break;
            case 'info':
                this.goToDetails($event.sale);
                break;
        }
    }

    private deleteSale(sale: Sale) {
        const confirmed = confirm('Vuoi confermare l\'eliminazione della vendita per il cliente ' +
            sale.customer.firstName + ' ' + sale.customer.lastName + '?');
        if (confirmed) {
            this.helper.delete(sale.id)
                .subscribe( _ => {
                    this.snackbar.open('Vendita eliminata per il cliente ' + sale.customer.lastName + '!');
                    return this.search(this.queryParams);
                }, err => this.snackbar.open(err.error.message));
        }
    }

    private paySale(sale: Sale, amount: number) {
        this.service.pay(sale.id, amount)
            .subscribe(_ => this.search(this.queryParams));
    }

    private goToDetails(sale: Sale) {
        const roleName = this.auth.getUserRoleName();
        this.router.navigate([roleName, 'sales', sale.id]);
    }

    sell() {
        return this.router.navigate([this.root, 'sales', 'buy', this.id]);
    }

    trackBy(index, item) {
        return item ? item.id : index;
    }
}
