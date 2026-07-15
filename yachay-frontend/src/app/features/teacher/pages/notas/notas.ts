import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { TeacherPortalService } from '../../../../core/services/teacher-portal';

interface TeacherGrade { id: number; alumnoId: number; cursoId: number; alumno: string; curso: string; bimestre: 'I' | 'II' | 'III' | 'IV'; nota: number; observacion: string; fechaRegistro: string; estado: 'REGISTRADA' | 'PUBLICADA' | 'ANULADA'; }
interface Course { id: number; nombre: string; } interface Student { id: number; nombres: string; apellidos: string; curso: string; }
type StatusFilter = TeacherGrade['estado'] | 'TODOS';

@Component({ selector: 'app-teacher-notas', imports: [ReactiveFormsModule], templateUrl: './notas.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class TeacherNotas implements OnInit {
  private readonly portal = inject(TeacherPortalService); private readonly fb = inject(FormBuilder);
  readonly search = signal(''); readonly statusFilter = signal<StatusFilter>('TODOS'); readonly grades = signal<TeacherGrade[]>([]); readonly courses = signal<Course[]>([]); readonly students = signal<Student[]>([]);
  readonly modalOpen = signal(false); readonly editingId = signal<number | null>(null); readonly errorMessage = signal(''); readonly successMessage = signal(''); readonly saving = signal(false);
  readonly form = this.fb.nonNullable.group({ cursoId: [0, Validators.min(1)], alumnoId: [0, Validators.min(1)], bimestre: ['I', Validators.required], nota: [0, [Validators.required, Validators.min(0), Validators.max(20)]], observacion: ['', Validators.maxLength(500)], estado: ['REGISTRADA'] });
  readonly filteredGrades = computed(() => { const query = this.search().trim().toLowerCase(); const status = this.statusFilter(); return this.grades().filter((item) => (status === 'TODOS' || item.estado === status) && `${item.alumno} ${item.curso} ${item.bimestre} ${item.observacion}`.toLowerCase().includes(query)); });
  readonly availableStudents = computed(() => { const course = this.courses().find((item) => item.id === this.form.controls.cursoId.value); return course ? this.students().filter((student) => student.curso === course.nombre) : this.students(); });
  ngOnInit(): void { this.load(); }
  load(): void { forkJoin({ grades: this.portal.getGrades<TeacherGrade[]>(), courses: this.portal.getCourses<Course[]>(), students: this.portal.getStudents<Student[]>() }).subscribe({ next: ({ grades, courses, students }) => { this.grades.set(grades); this.courses.set(courses); this.students.set(students); }, error: () => this.errorMessage.set('No se pudieron cargar las notas.') }); }
  openNew(): void { this.editingId.set(null); const course = this.courses()[0]; const student = course ? this.students().find((item) => item.curso === course.nombre) : this.students()[0]; this.form.reset({ cursoId: course?.id ?? 0, alumnoId: student?.id ?? 0, bimestre: 'I', nota: 0, observacion: '', estado: 'REGISTRADA' }); this.modalOpen.set(true); }
  openEdit(item: TeacherGrade): void { this.editingId.set(item.id); this.form.reset({ cursoId: item.cursoId, alumnoId: item.alumnoId, bimestre: item.bimestre, nota: item.nota, observacion: item.observacion, estado: item.estado }); this.modalOpen.set(true); }
  closeModal(): void { this.modalOpen.set(false); }
  save(): void { if (this.form.invalid) { this.form.markAllAsTouched(); return; } this.saving.set(true); const value = this.form.getRawValue(); const payload = { ...value, tipoEvaluacion: 'BIMESTRAL' }; const request = this.editingId() ? this.portal.updateGrade<TeacherGrade>(this.editingId()!, payload) : this.portal.createGrade<TeacherGrade>(payload); request.subscribe({ next: () => { this.saving.set(false); this.modalOpen.set(false); this.successMessage.set(this.editingId() ? 'Nota actualizada.' : 'Nota registrada.'); this.load(); }, error: () => { this.saving.set(false); this.errorMessage.set('No se pudo guardar la nota. Comprueba que no esté duplicada.'); } }); }
  annul(item: TeacherGrade): void { if (item.estado === 'ANULADA') return; this.portal.updateGradeStatus<TeacherGrade>(item.id, 'ANULADA').subscribe({ next: () => { this.successMessage.set('Nota anulada.'); this.load(); }, error: () => this.errorMessage.set('No se pudo anular la nota.') }); }
  updateSearch(event: Event): void { this.search.set((event.target as HTMLInputElement).value); }
  updateStatusFilter(event: Event): void { this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter); }
}
