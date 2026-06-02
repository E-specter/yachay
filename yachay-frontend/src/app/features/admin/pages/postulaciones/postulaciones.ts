import { ChangeDetectionStrategy, Component } from '@angular/core';

interface PostulacionMock {
  id: number;
  postulante: string;
  apoderado: string;
  nivel: string;
  grado: string;
  estado: 'Pendiente' | 'Aceptada' | 'Rechazada';
}

@Component({
  selector: 'app-postulaciones',
  imports: [],
  templateUrl: './postulaciones.html',
  styleUrl: './postulaciones.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Postulaciones {
  readonly postulaciones: readonly PostulacionMock[] = [
    {
      id: 1,
      postulante: 'María Fernanda Salazar Rojas',
      apoderado: 'Rosa Rojas Pérez',
      nivel: 'Primaria',
      grado: '3° Primaria',
      estado: 'Pendiente',
    },
    {
      id: 2,
      postulante: 'Luis Alberto Torres Quispe',
      apoderado: 'Carlos Torres Medina',
      nivel: 'Inicial',
      grado: '5 años',
      estado: 'Aceptada',
    },
    {
      id: 3,
      postulante: 'Ana Paula Huamán Soto',
      apoderado: 'Elena Soto Vargas',
      nivel: 'Secundaria',
      grado: '1° Secundaria',
      estado: 'Rechazada',
    },
  ];
}
