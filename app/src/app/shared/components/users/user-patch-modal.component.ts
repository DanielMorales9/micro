import {Component, Inject, OnInit} from "@angular/core";
import {User} from "../../model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../services";
import {AppService, NotificationService} from "../../../services";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";


@Component({
    selector: 'user-patch-modal',
    templateUrl: './user-patch-modal.component.html',
    styleUrls: ['../../../root.css']
})
export class UserPatchModalComponent implements OnInit {

    user: User;

    form: FormGroup;

    constructor(private builder: FormBuilder,
                private service: UserService,
                private appService: AppService,
                private messageService: NotificationService,
                public dialogRef: MatDialogRef<UserPatchModalComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any) {
        this.user = this.data.user
    }

    ngOnInit(): void {
        if (!this.user)
            this.user = new User();
        console.log(this.user);
        this.buildForm();
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

    buildForm() {
        let config = {};

        config['firstName'] = [this.user.firstName, [Validators.required]];
        config['lastName'] = [this.user.lastName, [Validators.required]];
        config['email'] = [this.user.email, [Validators.required, Validators.email]];
        if (this.user.type == 'C') {
            config['height'] = [this.user.height, [
                Validators.required,
                Validators.max(300),
                Validators.min(100)]];
            config['weight'] = [this.user.height, [
                Validators.required,
                Validators.max(1000),
                Validators.min(20)]];
        }

        this.form = this.builder.group(config);
    }

    get firstName() {
        return this.form.get("firstName")
    }

    get lastName() {
        return this.form.get("lastName")
    }

    get height() {
        return this.form.get("height")
    }

    get weight() {
        return this.form.get("weight")
    }

    get email() {
        return this.form.get("email")
    }

    submit() {

        delete this.user.roles;
        this.user.firstName = this.firstName.value;
        this.user.lastName = this.lastName.value;
        this.user.email = this.email.value;

        if (this.height && this.weight) {
            this.user.height = this.height.value;
            this.user.weight = this.weight.value;
        }

        this.service.patch(this.user).subscribe( _ => {
            this.messageService.sendMessage({
                text: `L'utente ${this.lastName.value} è stato modificato`,
                class: "alert-success"
            });
        }, err => {
            this.messageService.sendMessage({
                text: err.message,
                class: "alert-danger"
            });
        }, () => {
            this.onNoClick()
        })
    }

}
