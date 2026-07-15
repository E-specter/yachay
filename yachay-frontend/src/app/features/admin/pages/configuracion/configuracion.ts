import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { SystemConfiguration, SystemConfigurationService } from '../../../../core/services/system-configuration';

@Component({ selector: 'app-admin-configuracion', imports: [ReactiveFormsModule], templateUrl: './configuracion.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class AdminConfiguracion implements OnInit {
  private readonly fb = inject(FormBuilder); private readonly service = inject(SystemConfigurationService);
  readonly savedMessage = signal(''); readonly errorMessage = signal(''); readonly saving = signal(false); readonly integration = signal({ correo: false, whatsapp: false });
  readonly form = this.fb.nonNullable.group({ nombreInstitucion: ['', Validators.required], correoInstitucional: ['', Validators.email], telefono: [''], direccion: [''], logoUrl: [''], versionVisible: ['1.0', Validators.required] });
  ngOnInit(): void { this.load(); }
  load(): void { this.service.get().subscribe({ next: (data) => this.apply(data), error: () => this.errorMessage.set('No se pudo cargar la configuración.') }); }
  saveSettings(): void { if (this.form.invalid) { this.form.markAllAsTouched(); return; } this.saving.set(true); this.errorMessage.set(''); this.service.update(this.form.getRawValue()).subscribe({ next: (data) => { this.apply(data); this.savedMessage.set('Configuración guardada en MySQL.'); this.saving.set(false); }, error: () => { this.errorMessage.set('No se pudo guardar la configuración.'); this.saving.set(false); } }); }
  restoreDefaults(): void { this.saving.set(true); this.service.restore().subscribe({ next: (data) => { this.apply(data); this.savedMessage.set('Configuración restaurada y persistida.'); this.saving.set(false); }, error: () => { this.errorMessage.set('No se pudo restaurar la configuración.'); this.saving.set(false); } }); }
  private apply(data: SystemConfiguration): void { this.form.reset({ nombreInstitucion: data.nombreInstitucion, correoInstitucional: data.correoInstitucional ?? '', telefono: data.telefono ?? '', direccion: data.direccion ?? '', logoUrl: data.logoUrl ?? '', versionVisible: data.versionVisible }); this.integration.set({ correo: data.correoConfigurado, whatsapp: data.whatsappConfigurado }); }
}
