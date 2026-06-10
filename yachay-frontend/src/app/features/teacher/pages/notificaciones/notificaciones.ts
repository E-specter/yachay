import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';

import {
  NotificationService,
  UserNotification,
} from '../../../../core/services/notification';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

@Component({
  selector: 'app-teacher-notificaciones',
  imports: [AppIcon],
  templateUrl: './notificaciones.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeacherNotificaciones implements OnInit {
  private readonly notificationService = inject(NotificationService);

  readonly notifications = signal<UserNotification[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.notificationService.list('DOCENTE').subscribe({
      next: (notifications) => {
        this.notifications.set(notifications);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error cargando notificaciones docente', error);
        this.errorMessage.set('No se pudieron cargar tus notificaciones.');
        this.loading.set(false);
      },
    });
  }

  markAsRead(notification: UserNotification): void {
    if (notification.read) return;

    this.notificationService.markAsRead('DOCENTE', notification.id).subscribe({
      next: (updated) => {
        this.notifications.update((items) =>
          items.map((item) => (item.id === updated.id ? updated : item)),
        );
      },
      error: (error) => {
        console.error('Error marcando notificacion docente', error);
        this.errorMessage.set('No se pudo actualizar la notificación.');
      },
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead('DOCENTE').subscribe({
      next: (notifications) => {
        this.notifications.set(notifications);
        this.successMessage.set('Notificaciones marcadas como leídas.');
      },
      error: (error) => {
        console.error('Error marcando todas las notificaciones docente', error);
        this.errorMessage.set('No se pudieron actualizar las notificaciones.');
      },
    });
  }

  formattedDate(value: string): string {
    return new Intl.DateTimeFormat('es-PE', {
      dateStyle: 'medium',
      timeStyle: 'short',
    }).format(new Date(value));
  }
}
