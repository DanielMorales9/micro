import {Component, OnInit} from '@angular/core';
import {BundleSpecsService} from '../../core/controllers';
import {BundleSpecification, BundleType, CourseBundleSpecification, PersonalBundleSpecification} from '../model';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material';
import {BundleSpecModalComponent} from './bundle-spec-modal.component';
import {PolicyService} from '../../core/policy';
import {OptionModalComponent} from './option-modal.component';

@Component({
    selector: 'bundle-spec-details',
    templateUrl: './bundle-spec-details.component.html',
    styleUrls: ['../../styles/details.css', '../../styles/root.css', '../../styles/card.css'],
})
export class BundleSpecDetailsComponent implements OnInit {
    PERSONAL = BundleType.PERSONAL;
    COURSE   = BundleType.COURSE;

    bundleSpec: CourseBundleSpecification|PersonalBundleSpecification;

    canDelete: boolean;
    canDisable: boolean;
    displayedPaymentsColumns = ['index', 'name', 'number', 'price', 'date'];
    canMakeOption: boolean;
    canEdit: boolean;

    constructor(private service: BundleSpecsService,
                private dialog: MatDialog,
                private router: Router,
                private policy: PolicyService,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.params.subscribe(async params => {
            await this.getBundleSpec(+params['id']);
            this.getPolicies();
        });
    }

    private getPolicies() {
        this.canDelete = this.policy.get('bundleSpec', 'canDelete');
        this.canDisable = this.policy.get('bundleSpec', 'canDisable');
        this.canEdit = this.policy.get('bundleSpec', 'canEdit');
        this.canMakeOption = this.policy.get('bundleSpec', 'canMakeOption');
    }

    editBundleSpec(): void {
        const title = 'Modifica Pacchetto';

        const dialogRef = this.dialog.open(BundleSpecModalComponent, {
            data: {
                title: title,
                bundle: this.bundleSpec
            }
        });

        dialogRef.afterClosed().subscribe(res => {
            if (res) {
                this.service.patch(res).subscribe((v: CourseBundleSpecification|PersonalBundleSpecification) => this.bundleSpec = v);
            }
        });
    }

    deleteBundle() {
        const confirmed = confirm(`Vuoi eliminare il pacchetto ${this.bundleSpec.name}?`);
        if (confirmed) {
            this.service.delete(this.bundleSpec.id).subscribe(_ => this.router.navigateByUrl('/'));
        }
    }

    toggleDisabled() {
        this.bundleSpec.disabled = !this.bundleSpec.disabled;
        this.service.patch(this.bundleSpec);
    }

    private async getBundleSpec(id: number) {
        const [data, error] = await this.service.findById(id);
        if (error) { throw error; }
        this.bundleSpec = data;
    }

    getBundleType() {
        let name;
        if (!this.bundleSpec) { return name; }
        switch (this.bundleSpec.type) {
            case this.PERSONAL:
                name = 'Allenamento Personale';
                break;
            case this.COURSE:
                name = 'Corso';
                break;
            default:
                name = 'Allenamento Personale';
                break;
        }
        return name;
    }

    createOption() {
        const title = 'Crea Opzione';

        const dialogRef = this.dialog.open(OptionModalComponent, {
            data: {
                title: title,
                bundle: this.bundleSpec
            }
        });

        dialogRef.afterClosed().subscribe(async res => {
            if (res) {
                const [data, error] = await this.service.createOption(this.bundleSpec.id, res);
                if (error) { throw error; }
                this.bundleSpec = data;
            }
        });
    }
}
