import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { Announcement, AnnouncementRecipient, AnnouncementStatus } from '../../../../core/models/announcement.models';
import { AnnouncementService } from '../../../../core/services/announcement';

type AnnouncementStatusFilter = AnnouncementStatus | 'TODOS';

@Component({
  selector: 'app-comunicados',
  imports: [ReactiveFormsModule],
  templateUrl: './comunicados.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Comunicados {
  private readonly fb = inject(FormBuilder);
  private readonly announcementService = inject(AnnouncementService);

  readonly search = signal('');
  readonly statusFilter = signal<AnnouncementStatusFilter>('TODOS');
  readonly announcements = signal<Announcement[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly modalOpen = signal(false);
  readonly editingId = signal<number | null>(null);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  readonly form = this.fb.nonNullable.group({
    titulo: ['', Validators.required],
    contenido: ['', Validators.required],
    destinatario: ['TODOS', Validators.required],
    fechaPublicacion: [this.localDateTimeNow(), Validators.required],
    fechaExpiracion: [''],
    fijado: [false],
  });

  readonly filteredAnnouncements = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.announcements().filter((announcement) => {
      const matchesStatus = status === 'TODOS' || announcement.estado === status;
      const searchable = `${announcement.titulo} ${announcement.contenido} ${announcement.destinatario} ${announcement.nivel ?? ''} ${announcement.grado ?? ''} ${announcement.seccion ?? ''}`.toLowerCase();
      return matchesStatus && searchable.includes(query);
    });
  });

  constructor() {
    this.loadAnnouncements();
  }

  openCreateModal(): void {
    this.editingId.set(null);
    this.form.reset({
      titulo: '',
      contenido: '',
      destinatario: 'TODOS',
      fechaPublicacion: this.localDateTimeNow(),
      fechaExpiracion: '',
      fijado: false,
    });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  editAnnouncement(announcement: Announcement): void {
    this.editingId.set(announcement.id);
    this.form.reset({ titulo: announcement.titulo, contenido: announcement.contenido, destinatario: announcement.destinatario, fechaPublicacion: announcement.fechaPublicacion.slice(0, 16), fechaExpiracion: announcement.fechaExpiracion?.slice(0, 16) ?? '', fijado: announcement.fijado ?? false });
    this.errorMessage.set(''); this.modalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.saving()) return;
    this.modalOpen.set(false);
  }

  createAnnouncement(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const payload = {
      titulo: raw.titulo,
      contenido: raw.contenido,
      destinatario: raw.destinatario as AnnouncementRecipient,
      publicoObjetivo: raw.destinatario as AnnouncementRecipient,
      fechaPublicacion: raw.fechaPublicacion,
      fechaExpiracion: raw.fechaExpiracion || undefined,
      fijado: raw.fijado,
      cursoId: this.editingId() ? this.announcements().find((item) => item.id === this.editingId())?.cursoId : undefined,
      estado: this.editingId() ? this.announcements().find((item) => item.id === this.editingId())?.estado ?? 'PUBLICADO' as const : 'PUBLICADO' as const,
    };
    const request = this.editingId() ? this.announcementService.updateAnnouncement(this.editingId()!, payload) : this.announcementService.createAnnouncement(payload);
    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.modalOpen.set(false);
        this.successMessage.set(this.editingId() ? 'Comunicado actualizado correctamente.' : 'Registro creado correctamente.');
        this.loadAnnouncements();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  updateStatus(announcement: Announcement, estado: AnnouncementStatus): void {
    this.announcementService.updateStatus(announcement.id, { estado }).subscribe({
      next: () => {
        this.successMessage.set('Estado actualizado correctamente.');
        this.loadAnnouncements();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as AnnouncementStatusFilter);
  }

  viewAnnouncement(announcement: Announcement): void {
    this.announcementService.getAnnouncement(announcement.id).subscribe({
      next: (item) => this.successMessage.set(`${item.titulo}: ${item.contenido}`),
      error: (error) => this.handleSaveError(error),
    });
  }

  statusClass(status: AnnouncementStatus): string {
    if (status === 'PUBLICADO') return 'border-green-200 bg-green-50 text-green-700';
    if (status === 'ARCHIVADO') return 'border-slate-200 bg-slate-50 text-slate-600';
    return 'border-yellow-200 bg-yellow-50 text-yellow-800';
  }

  private loadAnnouncements(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.announcementService.list().subscribe({
      next: (announcements) => {
        this.announcements.set(announcements);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        this.handleLoadError('Error cargando comunicados', error);
      },
    });
  }

  private localDateTimeNow(): string {
    return new Date().toISOString().slice(0, 16);
  }

  private handleSaveError(error: unknown): void {
    this.saving.set(false);
    this.errorMessage.set('No se pudo guardar. Verifica los datos o la conexión con el servidor.');
    this.logHttpError('Error guardando comunicado', error);
  }

  private handleLoadError(label: string, error: unknown): void {
    this.errorMessage.set('No se pudo conectar con el servidor. Verifica que el backend esté activo en http://localhost:8080/api y que MySQL esté iniciado.');
    this.logHttpError(label, error);
  }

  private logHttpError(label: string, error: unknown): void {
    if (error instanceof HttpErrorResponse) {
      console.error(label, {
        status: error.status,
        statusText: error.statusText,
        url: error.url,
        message: error.message,
        error: error.error,
      });
    }
  }
}
