import { ChangeDetectionStrategy, Component, signal } from '@angular/core';

@Component({
  selector: 'app-admin-configuracion',
  imports: [],
  templateUrl: './configuracion.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminConfiguracion {
  readonly savedMessage = signal('');

  saveLocalSettings(): void {
    this.savedMessage.set('Configuración guardada localmente.');
  }

  restoreDefaults(): void {
    this.savedMessage.set('Configuración restaurada localmente.');
  }
}
