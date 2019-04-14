import {AfterContentChecked, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Bundle} from '../../../shared/model';


@Component({
    selector: 'bundle-select-item',
    templateUrl: './bundle-select-item.component.html',
    styleUrls: ['../../../styles/search-list.css', '../../../styles/root.css', '../../../styles/search-card-list.css']
})
export class BundleSelectItemComponent {

    @Input() bundle: Bundle;
    @Output() done = new EventEmitter();

    @Input() selected: boolean;

    constructor() {}

    selectBundle() {
        this.selected = !this.selected;
        this.done.emit();
    }


}
