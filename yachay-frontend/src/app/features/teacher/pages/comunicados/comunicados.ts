import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { TeacherPortalService } from '../../../../core/services/teacher-portal';

interface TeacherAnnouncement { id: number; cursoId: number | null; titulo: string; contenido: string; curso: string; nivel: string; grado: string; seccion: string; fechaPublicacion: string; estado: 'BORRADOR' | 'PUBLICADO' | 'ARCHIVADO'; }
interface Course { id: number; nombre: string; }
type StatusFilter = TeacherAnnouncement['estado'] | 'TODOS';

@Component({ selector: 'app-teacher-comunicados', imports: [ReactiveFormsModule], templateUrl: './comunicados.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class TeacherComunicados implements OnInit {
  private readonly portal = inject(TeacherPortalService); private readonly fb = inject(FormBuilder);
  readonly search = signal(''); readonly statusFilter = signal<StatusFilter>('TODOS'); readonly announcements = signal<TeacherAnnouncement[]>([]); readonly courses = signal<Course[]>([]);
  readonly modalOpen = signal(false); readonly editingId = signal<number | null>(null); readonly viewing = signal<TeacherAnnouncement | null>(null); readonly errorMessage = signal(''); readonly successMessage = signal(''); readonly saving = signal(false);
  readonly form = this.fb.group({ cursoId: this.fb.control<number | null>(null), titulo: this.fb.nonNullable.control('', [Validators.required, Validators.maxLength(180)]), contenido: this.fb.nonNullable.control('', [Validators.required, Validators.maxLength(1500)]), estado: this.fb.nonNullable.control('BORRADOR') });
  readonly filteredAnnouncements = computed(() => { const query = this.search().trim().toLowerCase(); const status = this.statusFilter(); return this.announcements().filter((item) => (status === 'TODOS' || item.estado === status) && `${item.titulo} ${item.contenido} ${item.curso} ${item.nivel} ${item.grado} ${item.seccion}`.toLowerCase().includes(query)); });
  ngOnInit(): void { this.load(); }
  load(): void { forkJoin({ announcements: this.portal.getAnnouncements<TeacherAnnouncement[]>(), courses: this.portal.getCourses<Course[]>() }).subscribe({ next: ({ announcements, courses }) => { this.announcements.set(announcements); this.courses.set(courses); }, error: () => this.errorMessage.set('No se pudieron cargar los comunicados.') }); }
  openNew(): void { this.editingId.set(null); this.form.reset({ cursoId: null, titulo: '', contenido: '', estado: 'BORRADOR' }); this.modalOpen.set(true); }
  openEdit(item: TeacherAnnouncement): void { this.editingId.set(item.id); this.form.reset({ cursoId: item.cursoId, titulo: item.titulo, contenido: item.contenido, estado: item.estado }); this.modalOpen.set(true); }
  closeModal(): void { this.modalOpen.set(false); }
  viewAnnouncement(item: TeacherAnnouncement): void { this.viewing.set(item); }
  save(): void { if (this.form.invalid) { this.form.markAllAsTouched(); return; } this.saving.set(true); const payload = { ...this.form.getRawValue(), destinatario: 'ALUMNOS', fechaPublicacion: new Date().toISOString(), fijado: false }; const request = this.editingId() ? this.portal.updateAnnouncement<TeacherAnnouncement>(this.editingId()!, payload) : this.portal.createAnnouncement<TeacherAnnouncement>(payload); request.subscribe({ next: () => { this.saving.set(false); this.modalOpen.set(false); this.successMessage.set(this.editingId() ? 'Comunicado actualizado.' : 'Comunicado creado.'); this.load(); }, error: () => { this.saving.set(false); this.errorMessage.set('No se pudo guardar el comunicado.'); } }); }
  archive(item: TeacherAnnouncement): void { if (item.estado === 'ARCHIVADO') return; this.portal.updateAnnouncementStatus<TeacherAnnouncement>(item.id, 'ARCHIVADO').subscribe({ next: () => { this.successMessage.set('Comunicado archivado.'); this.load(); }, error: () => this.errorMessage.set('No se pudo archivar el comunicado.') }); }
  updateSearch(event: Event): void { this.search.set((event.target as HTMLInputElement).value); }
  updateStatusFilter(event: Event): void { this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter); }
}
