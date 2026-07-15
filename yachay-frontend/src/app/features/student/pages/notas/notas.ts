import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { StudentPortalService } from '../../../../core/services/student-portal';

interface StudentGrade { id: number; curso: string; docente: string; bimestre: 'I' | 'II' | 'III' | 'IV'; nota: number; observacion: string; fechaRegistro: string; }

@Component({ selector: 'app-student-notas', imports: [], templateUrl: './notas.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class StudentNotas implements OnInit {
  private readonly portal = inject(StudentPortalService);
  readonly search = signal(''); readonly grades = signal<StudentGrade[]>([]); readonly loading = signal(false); readonly errorMessage = signal('');
  readonly filteredGrades = computed(() => { const query = this.search().trim().toLowerCase(); return this.grades().filter((grade) => `${grade.curso} ${grade.docente} ${grade.bimestre}`.toLowerCase().includes(query)); });
  ngOnInit(): void { this.load(); }
  load(): void { this.loading.set(true); this.errorMessage.set(''); this.portal.getGrades<StudentGrade[]>().subscribe({ next: (items) => { this.grades.set(items); this.loading.set(false); }, error: () => { this.errorMessage.set('No se pudieron cargar tus notas.'); this.loading.set(false); } }); }
  updateSearch(event: Event): void { this.search.set((event.target as HTMLInputElement).value); }
}
