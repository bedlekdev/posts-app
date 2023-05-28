import { DialogService } from './../../service/dialog.service';
import { Observable } from 'rxjs';
import { Component, OnInit } from '@angular/core';
import { PostService } from '../../service/post.service';
import { Post } from '../../interface/post';
import { PostDetailComponent } from '../post-detail/post-detail.component';
import { PostAction } from 'src/app/enum/post-action.enum';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.scss'],
})
export class PostsComponent implements OnInit {
  posts$: Observable<Array<Post>>;

  constructor(
    private dialogService: DialogService,
    private postService: PostService
  ) {
    this.posts$ = this.postService.posts$;
  }

  ngOnInit(): void {}

  public onUpdate(post: Post): void {
    const dialogRef =
      this.dialogService.open<PostDetailComponent>(PostDetailComponent);
    dialogRef.componentInstance.action = PostAction.UPDATE;
    dialogRef.componentInstance.post = post;
  }

  public onDelete(post: Post): void {
    if (post.id !== null && post.id !== undefined) {
      this.postService.delete(post.id);
    }
  }
}
