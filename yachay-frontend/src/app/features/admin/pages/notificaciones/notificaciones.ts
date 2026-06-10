import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import {
  NotificationService,
  UserNotification,
} from '../../../../core/services/notification';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

@Component({
  selector: 'app-admin-notificaciones',
  imports: [ReactiveFormsModule, AppIcon],
  templateUrl: './notificaciones.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminNotificaciones implements OnInit {
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly notificationService = inject(NotificationService);

  readonly notifications = signal<UserNotification[]>([]);
  readonly loading = signal(false);
  readonly listErrorMessage = signal('');
  readonly listSuccessMessage = signal('');

  readonly emailLoading = signal(false);
  readonly whatsappLoading = signal(false);
  readonly emailMessage = signal('');
  readonly whatsappMessage = signal('');
  readonly emailSuccess = signal<boolean | null>(null);
  readonly whatsappSuccess = signal<boolean | null>(null);

  readonly emailForm = this.fb.group({
    to: ['', [Validators.required, Validators.email]],
    subject: ['Prueba Yachay', [Validators.required]],
    message: ['Este es un correo de prueba del campus virtual.', [Validators.required]],
  });

  readonly whatsappForm = this.fb.group({
    to: ['+51999999999', [Validators.required]],
    message: ['Mensaje de prueba Yachay', [Validators.required]],
  });

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading.set(true);
    this.listErrorMessage.set('');

    this.notificationService.list('ADMINISTRADOR').subscribe({
      next: (notifications) => {
        this.notifications.set(notifications);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error cargando notificaciones administrador', error);
        this.listErrorMessage.set('No se pudieron cargar las notificaciones administrativas.');
        this.loading.set(false);
      },
    });
  }

  markAsRead(notification: UserNotification): void {
    if (notification.read) return;

    this.notificationService.markAsRead('ADMINISTRADOR', notification.id).subscribe({
      next: (updated) => {
        this.notifications.update((items) =>
          items.map((item) => (item.id === updated.id ? updated : item)),
        );
      },
      error: (error) => {
        console.error('Error marcando notificacion administrador', error);
        this.listErrorMessage.set('No se pudo actualizar la notificación.');
      },
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead('ADMINISTRADOR').subscribe({
      next: (notifications) => {
        this.notifications.set(notifications);
        this.listSuccessMessage.set('Notificaciones marcadas como leídas.');
      },
      error: (error) => {
        console.error('Error marcando todas las notificaciones administrador', error);
        this.listErrorMessage.set('No se pudieron actualizar las notificaciones.');
      },
    });
  }

  sendEmail(): void {
    this.emailMessage.set('');
    this.emailSuccess.set(null);

    if (this.emailForm.invalid) {
      this.emailForm.markAllAsTouched();
      this.emailMessage.set('Completa un correo valido, asunto y mensaje.');
      this.emailSuccess.set(false);
      return;
    }

    this.emailLoading.set(true);
    this.notificationService.sendTestEmail(this.emailForm.getRawValue()).subscribe({
      next: (response) => {
        this.emailMessage.set(response.message);
        this.emailSuccess.set(response.success);
        this.emailLoading.set(false);
      },
      error: () => {
        this.emailMessage.set('No se pudo probar el correo. Verifica que el backend este activo.');
        this.emailSuccess.set(false);
        this.emailLoading.set(false);
      },
    });
  }

  sendWhatsapp(): void {
    this.whatsappMessage.set('');
    this.whatsappSuccess.set(null);

    if (this.whatsappForm.invalid) {
      this.whatsappForm.markAllAsTouched();
      this.whatsappMessage.set('Completa el numero y el mensaje.');
      this.whatsappSuccess.set(false);
      return;
    }

    this.whatsappLoading.set(true);
    this.notificationService.sendTestWhatsapp(this.whatsappForm.getRawValue()).subscribe({
      next: (response) => {
        this.whatsappMessage.set(response.message);
        this.whatsappSuccess.set(response.success);
        this.whatsappLoading.set(false);
      },
      error: () => {
        this.whatsappMessage.set('No se pudo probar WhatsApp. Verifica que el backend este activo.');
        this.whatsappSuccess.set(false);
        this.whatsappLoading.set(false);
      },
    });
  }

  resultClass(success: boolean | null): string {
    if (success === true) return 'border-green-200 bg-green-50 text-green-800';
    if (success === false) return 'border-red-200 bg-red-50 text-red-800';
    return 'border-yachay-300 bg-white text-yachay-muted';
  }

  formattedDate(value: string): string {
    return new Intl.DateTimeFormat('es-PE', {
      dateStyle: 'medium',
      timeStyle: 'short',
    }).format(new Date(value));
  }
}
