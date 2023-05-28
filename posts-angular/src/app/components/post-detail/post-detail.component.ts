import { PostService } from './../../service/post.service';
import { Component, Input, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { PostAction } from './../../enum/post-action.enum';
import { Post } from './../../interface/post';

@Component({
  selector: 'app-post-detail',
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.scss'],
})
export class PostDetailComponent implements OnInit {
  @Input() action!: PostAction;
  @Input() post?: Post;

  title!: string;
  form!: FormGroup;

  constructor(
    private dialogRef: MatDialogRef<PostDetailComponent>,
    private formBuilder: FormBuilder,
    private postService: PostService
  ) {}

  ngOnInit(): void {
    this.title = this.action === PostAction.ADD ? 'Add Post' : 'Update Post';
    this.initForm();
  }

  private initForm(): void {
    this.form = this.formBuilder.group({
      id: [{ value: this.post?.id ?? null, disabled: true }],
      userId: [
        {
          value: this.post?.userId ?? null,
          disabled: this.action === PostAction.UPDATE,
        },
        Validators.required,
      ],
      title: [this.post?.title ?? null, Validators.required],
      body: [this.post?.body ?? null, Validators.required],
    });
  }

  public onSubmit(): void {
    if (this.action === PostAction.ADD) {
      this.addPost();
    } else {
      this.updatePost();
    }
  }

  private addPost(): void {
    this.postService
      .createPost(this.form.getRawValue() as Post)
      .subscribe((success) => this.processSubmitResponse(success));
  }

  private updatePost(): void {
    this.postService
      .updatePost(this.form.getRawValue() as Post)
      .subscribe((success) => this.processSubmitResponse(success));
  }

  private processSubmitResponse(success: boolean) {
    if (success) {
      this.dialogRef.close();
    }
  }

  public getError(control: AbstractControl): string {
    return control && control.hasError('required')
      ? 'You must enter a value'
      : '';
  }
}
