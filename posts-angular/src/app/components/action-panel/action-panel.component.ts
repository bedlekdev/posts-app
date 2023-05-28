import { PostAction } from './../../enum/post-action.enum';
import { Component, OnInit } from '@angular/core';
import { PostOption } from '../../enum/post-option.enum';
import { DialogService } from './../../service/dialog.service';
import { PostService } from './../../service/post.service';
import { PostDetailComponent } from '../post-detail/post-detail.component';

interface SelectOption {
  value: PostOption;
  label: string;
}

@Component({
  selector: 'app-action-panel',
  templateUrl: './action-panel.component.html',
  styleUrls: ['./action-panel.component.scss'],
})
export class ActionPanelComponent implements OnInit {
  options: SelectOption[] = [
    { value: PostOption.ALL, label: 'All' },
    { value: PostOption.POST_ID, label: 'Post ID' },
    { value: PostOption.USER_ID, label: 'User ID' },
  ];

  searchOption = this.options[0].value;
  searchValue = '';

  constructor(
    private dialogService: DialogService,
    private postService: PostService
  ) {}

  ngOnInit(): void {
    this.onSearch();
  }

  public onSearch(): void {
    this.postService.getPosts(this.searchOption, this.searchValue);
  }

  public onAddPost(): void {
    const dialogRef =
      this.dialogService.open<PostDetailComponent>(PostDetailComponent);
    dialogRef.componentInstance.action = PostAction.ADD;
  }
}
