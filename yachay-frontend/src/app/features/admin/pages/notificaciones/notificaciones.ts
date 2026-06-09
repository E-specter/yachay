import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { NotificationService } from '../../../../core/services/notification';

@Component({
  selector: 'app-admin-notificaciones',
  imports: [ReactiveFormsModule],
  templateUrl: './notificaciones.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminNotificaciones {
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly notificationService = inject(NotificationService);

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
}
