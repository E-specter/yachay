import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  signal,
} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import {
  AdmissionRequest,
  NivelAcademico,
  PostulanteRequest,
} from '../../../../core/models/admission.models';

type NivelControl = NivelAcademico | '';

type PostulanteForm = FormGroup<{
  apellidoPaterno: FormControl<string>;
  apellidoMaterno: FormControl<string>;
  nombres: FormControl<string>;
  genero: FormControl<string>;
  documentoTipo: FormControl<string>;
  documentoNumero: FormControl<string>;
  fechaNacimiento: FormControl<string>;
  viveCon: FormControl<string>;
  colegioProcedencia: FormControl<string>;
  lugarColegioProcedencia: FormControl<string>;
  referenciaZonaColegio: FormControl<string>;
  nivel: FormControl<NivelControl>;
  grado: FormControl<string>;
}>;

type ApoderadoForm = FormGroup<{
  apellidoPaterno: FormControl<string>;
  apellidoMaterno: FormControl<string>;
  nombres: FormControl<string>;
  genero: FormControl<string>;
  documentoTipo: FormControl<string>;
  documentoNumero: FormControl<string>;
  parentesco: FormControl<string>;
  telefono: FormControl<string>;
  celular: FormControl<string>;
  correo: FormControl<string>;
  profesion: FormControl<string>;
  centroTrabajo: FormControl<string>;
  recibeNotificaciones: FormControl<boolean>;
}>;

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Register {
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly postulantesLength = signal(1);

  readonly submitted = signal(false);
  readonly loading = signal(false);
  readonly successMessage = signal('');
  readonly errorMessage = signal('');
  readonly totalPostulantes = computed(() => this.postulantesLength());

  readonly generos = ['Masculino', 'Femenino'] as const;
  readonly viveCon = ['Padre y madre', 'Madre', 'Padre', 'Apoderado legal', 'Otro'] as const;
  readonly parentescos = ['Madre', 'Padre', 'Abuelo(a)', 'Tío(a)', 'Tutor legal', 'Otro'] as const;
  readonly niveles: NivelAcademico[] = ['Inicial', 'Primaria', 'Secundaria'];
  readonly gradosPorNivel: Record<NivelAcademico, readonly string[]> = {
    Inicial: ['3 años', '4 años', '5 años'],
    Primaria: [
      '1° Primaria',
      '2° Primaria',
      '3° Primaria',
      '4° Primaria',
      '5° Primaria',
      '6° Primaria',
    ],
    Secundaria: [
      '1° Secundaria',
      '2° Secundaria',
      '3° Secundaria',
      '4° Secundaria',
      '5° Secundaria',
    ],
  };

  readonly form = this.fb.group({
    postulantes: this.fb.array<PostulanteForm>([this.crearPostulante()]),
    apoderado: this.crearApoderado(),
  });

  get postulantes(): FormArray<PostulanteForm> {
    return this.form.controls.postulantes;
  }

  agregarPostulante(): void {
    this.postulantes.push(this.crearPostulante());
    this.sincronizarTotal();
  }

  eliminarPostulante(index: number): void {
    if (this.postulantes.length === 1) return;

    this.postulantes.removeAt(index);
    this.sincronizarTotal();
  }

  gradosDisponibles(index: number): readonly string[] {
    const nivel = this.postulantes.at(index).controls.nivel.value;

    if (!this.esNivelAcademico(nivel)) {
      return [];
    }

    return this.gradosPorNivel[nivel];
  }

  cambiarNivel(index: number): void {
    this.postulantes.at(index).controls.grado.setValue('');
  }

  enviarPostulacion(): void {
    this.submitted.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');
    this.form.markAllAsTouched();

    if (this.form.invalid) {
      this.errorMessage.set('Completa los campos obligatorios antes de enviar.');
      return;
    }

    const payload = this.crearPayload();

    this.loading.set(true);
    console.info('Postulación pendiente para revisión:', payload);
    this.loading.set(false);
    this.submitted.set(false);
    this.successMessage.set(
      'Solicitud registrada correctamente. El administrador revisará la postulación.',
    );
    this.reiniciarFormulario();
  }

  campoInvalido(path: string): boolean {
    const control = this.form.get(path);
    return !!control && control.invalid && (control.touched || this.submitted());
  }

  postulanteId(index: number, control: string): string {
    return `postulante-${index}-${control}`;
  }

  postulanteErrorId(index: number, control: string): string {
    return `${this.postulanteId(index, control)}-error`;
  }

  apoderadoId(control: string): string {
    return `apoderado-${control}`;
  }

  apoderadoErrorId(control: string): string {
    return `${this.apoderadoId(control)}-error`;
  }

  private crearPostulante(): PostulanteForm {
    return this.fb.group({
      apellidoPaterno: ['', Validators.required],
      apellidoMaterno: ['', Validators.required],
      nombres: ['', Validators.required],
      genero: ['', Validators.required],
      documentoTipo: ['DNI', Validators.required],
      documentoNumero: ['', [Validators.required, Validators.minLength(8)]],
      fechaNacimiento: ['', Validators.required],
      viveCon: ['', Validators.required],
      colegioProcedencia: ['', Validators.required],
      lugarColegioProcedencia: ['', Validators.required],
      referenciaZonaColegio: [''],
      nivel: this.fb.control<NivelControl>('', Validators.required),
      grado: ['', Validators.required],
    });
  }

  private crearApoderado(): ApoderadoForm {
    return this.fb.group({
      apellidoPaterno: ['', Validators.required],
      apellidoMaterno: ['', Validators.required],
      nombres: ['', Validators.required],
      genero: ['', Validators.required],
      documentoTipo: ['DNI', Validators.required],
      documentoNumero: ['', [Validators.required, Validators.minLength(8)]],
      parentesco: ['', Validators.required],
      telefono: [''],
      celular: ['', [Validators.required, Validators.minLength(9)]],
      correo: ['', [Validators.required, Validators.email]],
      profesion: [''],
      centroTrabajo: [''],
      recibeNotificaciones: [true],
    });
  }

  private crearPayload(): AdmissionRequest {
    const raw = this.form.getRawValue();
    const postulantes: PostulanteRequest[] = raw.postulantes.map((postulante) => ({
      ...postulante,
      nivel: this.normalizarNivel(postulante.nivel),
    }));

    return {
      postulantes,
      apoderado: raw.apoderado,
    };
  }

  private normalizarNivel(nivel: NivelControl): NivelAcademico {
    if (!this.esNivelAcademico(nivel)) {
      throw new Error('Nivel académico inválido.');
    }

    return nivel;
  }

  private reiniciarFormulario(): void {
    this.form.reset();
    this.postulantes.clear();
    this.postulantes.push(this.crearPostulante());
    this.sincronizarTotal();
  }

  private sincronizarTotal(): void {
    this.postulantesLength.set(this.postulantes.length);
  }

  private esNivelAcademico(value: string): value is NivelAcademico {
    return this.niveles.includes(value as NivelAcademico);
  }
}
