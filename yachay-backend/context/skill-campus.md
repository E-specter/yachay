 # Habilidades de IA – Campus Virtual de Educación Secundaria
**Enfoque modular · Escala 0–20 · Roles: Administrador, Docente, Estudiante, Apoderado**

Cada habilidad es un microservicio independiente que puede activarse por curso y por rol. La calificación refleja su importancia combinada (impacto pedagógico + complejidad técnica) dentro del campus virtual.

---

## 1. Motor de Personalización del Aprendizaje (19/20)
- **¿Qué hace?** Recomienda automáticamente materiales, tareas y rutas de estudio adaptadas al perfil, ritmo y estilo de aprendizaje de cada estudiante.
- **Roles:** Estudiante, Docente.

## 2. Analítica de Perfil del Estudiante (18/20)
- **¿Qué hace?** Construye y actualiza el perfil académico y socioemocional del alumno a partir de interacciones, calificaciones y autoevaluaciones.
- **Roles:** Estudiante, Docente, Apoderado.

## 3. Motor de Habilidades Blandas (17/20)
- **¿Qué hace?** Evalúa competencias como trabajo en equipo, comunicación y liderazgo mediante análisis de texto (NLP) y sentimiento en foros y entregas colaborativas.
- **Roles:** Estudiante, Docente.

## 4. Sistema Tutor Inteligente – ITS (16/20)
- **¿Qué hace?** Agente conversacional que resuelve dudas y guía al estudiante en problemas STEM, ofreciendo pistas adaptativas en tiempo real.
- **Roles:** Estudiante.

## 5. Simulaciones Interactivas STEM (16/20)
- **¿Qué hace?** Genera laboratorios virtuales y simulaciones con evaluación automática; los escenarios se ajustan al nivel del curso y a las dificultades del estudiante.
- **Roles:** Estudiante, Docente.

## 6. Calificación Automática de Tareas (18/20)
- **¿Qué hace?** Corrige ejercicios numéricos, de opción múltiple y preguntas abiertas cortas, entregando retroalimentación inmediata al estudiante.
- **Roles:** Docente, Estudiante.

## 7. Detección de Similitud y Plagio (14/20)
- **¿Qué hace?** Analiza las entregas contra fuentes internas y externas, mostrando el porcentaje de similitud y las fuentes detectadas.
- **Roles:** Docente.

## 8. Gestión Inteligente de Cursos para el Estudiante (17/20)
- **¿Qué hace?** Dashboard que organiza semanas, sesiones, tareas y materiales, priorizando lo pendiente y prediciendo la carga de trabajo para mejorar la autorregulación.
- **Roles:** Estudiante.

## 9. Planificador Docente Inteligente (18/20)
- **¿Qué hace?** Asistente que sugiere la estructura de semanas/sesiones, materiales y tareas alineados al currículo y al perfil del grupo, basándose en el avance real.
- **Roles:** Docente.

## 10. Sistema de Alertas Tempranas (19/20)
- **¿Qué hace?** Predice riesgo de abandono o bajo rendimiento analizando asistencia, entregas, calificaciones y participación. Emite alertas automáticas a los responsables.
- **Roles:** Docente, Administrador, Apoderado.

## 11. Notificaciones y Correos Inteligentes a Apoderados (18/20)
- **¿Qué hace?** Genera y envía resúmenes periódicos personalizados con el avance del estudiante (asistencia, notas, habilidades blandas, sesiones completadas).
- **Roles:** Apoderado, Administrador.

## 12. Registro Automatizado de Asistencia y Sesiones (15/20)
- **¿Qué hace?** Marca la asistencia mediante reconocimiento facial o análisis de interacciones en la sesión virtual. Almacena automáticamente el contenido cubierto en cada sesión.
- **Roles:** Administrador, Docente.

## 13. Analítica del Perfil Docente (14/20)
- **¿Qué hace?** Construye un perfil con las fortalezas, áreas de mejora y preferencias metodológicas del docente, y recomienda formación continua.
- **Roles:** Administrador, Docente.

## 14. Consola de Administración Inteligente (16/20)
- **¿Qué hace?** Panel unificado con métricas en tiempo real (rendimiento global, uso de módulos, alertas) y asistentes para configuración y autoescalado.
- **Roles:** Administrador.

## 15. Motor de Roles y Permisos Modulares (17/20)
- **¿Qué hace?** Sistema dinámico que permite habilitar o deshabilitar cada habilidad de IA según el rol (Apoderado, Docente, Estudiante, Admin) y por curso. Es la base de la modularidad.
- **Roles:** Administrador.

## 16. Generación de Materiales de Estudio Personalizados (17/20)
- **¿Qué hace?** Crea resúmenes, cuestionarios y guías adaptadas al nivel de cada estudiante o grupo, usando modelos generativos supervisados.
- **Roles:** Docente, Estudiante.

## 17. Chatbot de Soporte Multicanal (13/20)
- **¿Qué hace?** Atiende consultas frecuentes sobre el uso del campus, fechas y procedimientos; deriva casos complejos al personal humano.
- **Roles:** Todos.

---

## Principios de modularidad y cobertura obligatoria
- **Modularidad real:** Cada habilidad es un microservicio independiente. El **Motor de Roles (M15)** controla qué módulo ve cada usuario.
- **Registros que exige el problema:** notas (M06 + M02), asistencia (M12 + M10), avance de sesiones (M12 + M09), gestión de cursos para estudiante (M08) y docente (M09), notificaciones a apoderados (M11).
- **Enfoque STEM:** impulsado por el tutor inteligente (M04), las simulaciones interactivas (M05), la calificación automática (M06) y la generación de materiales (M16).
- **Desarrollo de habilidades blandas:** cubierto por el perfil socioemocional (M02) y el motor de habilidades blandas (M03).