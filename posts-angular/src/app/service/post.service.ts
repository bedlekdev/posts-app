import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { PostOption } from '../enum/post-option.enum';
import { Post } from '../interface/post';
import { Response } from '../interface/response';
import { environment } from './../../environments/environment';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private readonly apiUrl = environment.apiUrl + '/posts';
  private lastFilterOption?: PostOption;
  private lastFilterValue?: string;
  private posts = new BehaviorSubject<Array<Post>>([]);
  posts$ = this.posts.asObservable();

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) {}

  public getPosts(filterOption: PostOption, filterValue: string): void {
    this.lastFilterOption = filterOption;
    this.lastFilterValue = filterValue;
    if (PostOption.ALL === filterOption) {
      this.getAllPosts();
    } else if (PostOption.POST_ID === filterOption) {
      this.getPostById(filterValue);
    } else if (PostOption.USER_ID === filterOption) {
      this.getAllPosts({ 'user-id': filterValue });
    }
  }

  private getAllPosts(params?: {
    [param: string]:
      | string
      | number
      | boolean
      | ReadonlyArray<string | number | boolean>;
  }): void {
    this.http
      .get<Response<Post>>(this.apiUrl, { params })
      .pipe(map((res) => res.content))
      .subscribe({
        next: (posts) => this.posts.next(posts),
        error: this.handleError.bind(this),
      });
  }

  private getPostById(postId: string): void {
    this.http.get<Post>(`${this.apiUrl}/${postId}`).subscribe({
      next: (post) => this.posts.next([post]),
      error: this.handleError.bind(this),
    });
  }

  public createPost(post: Post): Observable<boolean> {
    const response = new Subject<boolean>();
    this.http.post(this.apiUrl, post).subscribe({
      next: () =>
        this.handleCreateOrUpdateSuccess(response, 'Post was created'),
      error: (error) => this.handleCreateOrUpdateError(response, error),
    });
    return response;
  }

  public updatePost(post: Post): Observable<boolean> {
    const response = new Subject<boolean>();
    this.http.put(`${this.apiUrl}/${post.id}`, post).subscribe({
      next: () =>
        this.handleCreateOrUpdateSuccess(response, 'Post was updated'),
      error: (error) => this.handleCreateOrUpdateError(response, error),
    });
    return response;
  }

  public delete(postId: number): void {
    this.http.delete(`${this.apiUrl}/${postId}`).subscribe({
      next: () => this.getAllPosts(),
      error: this.handleError.bind(this),
    });
  }

  private handleCreateOrUpdateSuccess(
    response: Subject<boolean>,
    message: string
  ): void {
    this.notificationService.success(message);
    response.next(true);
    if (this.lastFilterOption) {
      this.getPosts(this.lastFilterOption, this.lastFilterValue ?? '');
    }
  }

  private handleCreateOrUpdateError(
    response: Subject<boolean>,
    error: HttpErrorResponse
  ): void {
    this.notifyError(error);
    response.next(false);
  }

  private handleError(error: HttpErrorResponse): void {
    this.notifyError(error);
    this.posts.next([]);
  }

  private notifyError(error: HttpErrorResponse): void {
    console.log(error);
    if (error.error && error.error.message) {
      this.notificationService.error(error.error.message);
    } else {
      this.notificationService.error(
        `An error occured - Error code: ${error.status}`
      );
    }
  }
}
