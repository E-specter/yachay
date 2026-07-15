import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { StudentPortalService } from '../../../../core/services/student-portal';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

type CourseDetail = { id: number; nombre: string; docente: string; aula: string; progreso: string; proximaActividad: string; descripcion: string; horario: string[]; tareas: string[]; notas: string[]; comunicados: string[]; };
const EMPTY: CourseDetail = { id: 0, nombre: '', docente: '', aula: '', progreso: '0%', proximaActividad: '', descripcion: '', horario: [], tareas: [], notas: [], comunicados: [] };

@Component({ selector: 'app-curso-detalle', imports: [RouterLink, AppIcon], templateUrl: './curso-detalle.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class CursoDetalle implements OnInit {
  private readonly route = inject(ActivatedRoute); private readonly portal = inject(StudentPortalService); private readonly id = Number(this.route.snapshot.paramMap.get('id'));
  readonly course = signal<CourseDetail>(EMPTY); readonly loading = signal(false); readonly errorMessage = signal('');
  ngOnInit(): void { this.loading.set(true); this.portal.getCourse<CourseDetail>(this.id).subscribe({ next: (item) => { this.course.set(item); this.loading.set(false); }, error: () => { this.errorMessage.set('No se pudo cargar el detalle del curso.'); this.loading.set(false); } }); }
}
