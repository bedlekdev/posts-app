import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {}

  public clear(): void {
    this.snackBar.dismiss();
  }

  public error(message: string): void {
    this.showSnackbar(message, ['snackbar-error']);
  }

  public success(message: string): void {
    this.showSnackbar(message, ['snackbar-success']);
  }

  private showSnackbar(message: string, panelClass: Array<string>): void {
    this.snackBar.open(message, undefined, { panelClass });
  }
}
