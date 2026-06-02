-- ============================================================
-- VIRTUAL CAMPUS - PostgreSQL Estándar (Portable)
-- Compatible con Supabase, pero desplegable en cualquier PostgreSQL
-- ============================================================

-- 0. ESQUEMA AUTH (emula auth.users de Supabase) ---------------
CREATE SCHEMA IF NOT EXISTS auth;

-- Tabla auth.users (igual que Supabase, solo columnas necesarias)
CREATE TABLE auth.users
(
    id                 UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    email              VARCHAR(255) UNIQUE,
    phone              VARCHAR(20) UNIQUE,
    display_name       VARCHAR(255),
    encrypted_password VARCHAR(255),
    email_confirmed_at TIMESTAMPTZ,
    phone_confirmed_at TIMESTAMPTZ,
    last_sign_in_at    TIMESTAMPTZ,
    raw_app_meta_data  JSONB                DEFAULT '{}'::jsonb,
    raw_user_meta_data JSONB                DEFAULT '{}'::jsonb,
    providers          TEXT[],
    provider_type      VARCHAR(50),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 1. ENUM TYPES -----------------------------------------------
-- ============================================================
CREATE TYPE term_type_enum AS ENUM ('year', 'semester', 'trimester', 'quarter');
CREATE TYPE lesson_type_enum AS ENUM ('video', 'reading', 'quiz', 'assignment', 'discussion', 'external_link');
CREATE TYPE assignment_type_enum AS ENUM ('homework', 'quiz', 'project', 'exam', 'participation');
CREATE TYPE submission_status_enum AS ENUM ('submitted', 'graded', 'returned', 'revision');
CREATE TYPE attendance_status_enum AS ENUM ('present', 'absent', 'late', 'excused');
CREATE TYPE event_type_enum AS ENUM ('class', 'exam', 'holiday', 'meeting', 'extracurricular', 'other');
CREATE TYPE enrollment_status_enum AS ENUM ('active', 'completed', 'dropped');
CREATE TYPE resource_type_enum AS ENUM ('document', 'video', 'audio', 'link', 'image', 'other');

-- ============================================================
-- 2. ROLES (aplicación) ---------------------------------------
-- ============================================================
CREATE TABLE roles
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- ============================================================
-- 3. PERFIL UNIFICADO (extiende auth.users) --------------------
-- ============================================================
CREATE TABLE profiles
(
    id            SERIAL PRIMARY KEY,
    user_id       UUID         NOT NULL UNIQUE REFERENCES auth.users (id) ON DELETE CASCADE,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    avatar_url    VARCHAR(500),
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 4. RELACIÓN USUARIO - ROL(es) (muchos a muchos) --------------
CREATE TABLE user_roles
(
    user_id UUID NOT NULL REFERENCES auth.users (id) ON DELETE CASCADE,
    role_id INT  NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- ============================================================
-- 5. PERFILES ESPECÍFICOS ------------------------------------
-- ============================================================
CREATE TABLE schools
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    code       VARCHAR(50) UNIQUE,
    address    VARCHAR(255),
    phone      VARCHAR(20),
    logo_url   VARCHAR(500),
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE student_profiles
(
    id              SERIAL PRIMARY KEY,
    profile_id      INTEGER     NOT NULL UNIQUE REFERENCES profiles (id) ON DELETE CASCADE,
    school_id       INTEGER     NOT NULL REFERENCES schools (id),
    student_code    VARCHAR(50) UNIQUE,
    grade_level     SMALLINT    NOT NULL CHECK (grade_level BETWEEN 1 AND 12),
    section         VARCHAR(20),
    enrollment_date DATE        NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE teacher_profiles
(
    id            SERIAL PRIMARY KEY,
    profile_id    INTEGER     NOT NULL UNIQUE REFERENCES profiles (id) ON DELETE CASCADE,
    school_id     INTEGER     NOT NULL REFERENCES schools (id),
    employee_code VARCHAR(50) UNIQUE,
    subject_area  VARCHAR(100),
    hire_date     DATE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE guardian_profiles
(
    id           SERIAL PRIMARY KEY,
    profile_id   INTEGER     NOT NULL UNIQUE REFERENCES profiles (id) ON DELETE CASCADE,
    relationship VARCHAR(50),
    phone        VARCHAR(20),
    address      VARCHAR(255),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE student_guardians
(
    student_profile_id  INTEGER NOT NULL REFERENCES student_profiles (id) ON DELETE CASCADE,
    guardian_profile_id INTEGER NOT NULL REFERENCES guardian_profiles (id) ON DELETE CASCADE,
    is_primary          BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (student_profile_id, guardian_profile_id)
);

-- ============================================================
-- 6. TÉRMINOS ACADÉMICOS, MATERIAS, CURSOS --------------------
-- ============================================================
CREATE TABLE academic_terms
(
    id         SERIAL PRIMARY KEY,
    school_id  INTEGER        NOT NULL REFERENCES schools (id),
    name       VARCHAR(100)   NOT NULL,
    start_date DATE           NOT NULL,
    end_date   DATE           NOT NULL,
    term_type  term_type_enum NOT NULL,
    is_current BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE subjects
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    category    VARCHAR(100),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE courses
(
    id               SERIAL PRIMARY KEY,
    subject_id       INTEGER     NOT NULL REFERENCES subjects (id),
    teacher_id       INTEGER     NOT NULL REFERENCES teacher_profiles (id),
    academic_term_id INTEGER     NOT NULL REFERENCES academic_terms (id),
    grade_level      SMALLINT    NOT NULL CHECK (grade_level BETWEEN 1 AND 12),
    section          VARCHAR(20),
    room             VARCHAR(50),
    max_students     INTEGER,
    is_active        BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE course_enrollments
(
    id                 SERIAL PRIMARY KEY,
    course_id          INTEGER                NOT NULL REFERENCES courses (id),
    student_profile_id INTEGER                NOT NULL REFERENCES student_profiles (id),
    enrollment_date    DATE                   NOT NULL DEFAULT CURRENT_DATE,
    status             enrollment_status_enum NOT NULL DEFAULT 'active',
    created_at         TIMESTAMPTZ            NOT NULL DEFAULT NOW(),
    UNIQUE (course_id, student_profile_id)
);

-- ============================================================
-- 7. MÓDULOS Y LECCIONES --------------------------------------
-- ============================================================
CREATE TABLE modules
(
    id           SERIAL PRIMARY KEY,
    course_id    INTEGER      NOT NULL REFERENCES courses (id),
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    order_index  INTEGER      NOT NULL DEFAULT 0,
    release_date TIMESTAMPTZ,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE lessons
(
    id               SERIAL PRIMARY KEY,
    module_id        INTEGER          NOT NULL REFERENCES modules (id),
    title            VARCHAR(255)     NOT NULL,
    content          TEXT,
    lesson_type      lesson_type_enum NOT NULL DEFAULT 'reading',
    resource_url     VARCHAR(500),
    duration_minutes INTEGER,
    order_index      INTEGER          NOT NULL DEFAULT 0,
    is_published     BOOLEAN          NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 8. TAREAS Y ENTREGAS ----------------------------------------
-- ============================================================
CREATE TABLE assignments
(
    id                    SERIAL PRIMARY KEY,
    lesson_id             INTEGER REFERENCES lessons (id),
    course_id             INTEGER              NOT NULL REFERENCES courses (id),
    title                 VARCHAR(255)         NOT NULL,
    description           TEXT,
    due_date              TIMESTAMPTZ,
    max_score             NUMERIC(5, 2)        NOT NULL DEFAULT 100.00,
    assignment_type       assignment_type_enum NOT NULL DEFAULT 'homework',
    allow_late_submission BOOLEAN              NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMPTZ          NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ          NOT NULL DEFAULT NOW()
);

CREATE TABLE submissions
(
    id                 SERIAL PRIMARY KEY,
    assignment_id      INTEGER                NOT NULL REFERENCES assignments (id),
    student_profile_id INTEGER                NOT NULL REFERENCES student_profiles (id),
    submission_date    TIMESTAMPTZ            NOT NULL DEFAULT NOW(),
    content            TEXT,
    file_url           VARCHAR(500),
    score              NUMERIC(5, 2),
    graded_by          INTEGER REFERENCES teacher_profiles (id),
    graded_at          TIMESTAMPTZ,
    status             submission_status_enum NOT NULL DEFAULT 'submitted',
    teacher_comments   TEXT,
    created_at         TIMESTAMPTZ            NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ            NOT NULL DEFAULT NOW(),
    UNIQUE (assignment_id, student_profile_id)
);

-- ============================================================
-- 9. CALIFICACIONES FINALES Y ASISTENCIA ----------------------
-- ============================================================
CREATE TABLE course_grades
(
    id                 SERIAL PRIMARY KEY,
    course_id          INTEGER     NOT NULL REFERENCES courses (id),
    student_profile_id INTEGER     NOT NULL REFERENCES student_profiles (id),
    academic_term_id   INTEGER     NOT NULL REFERENCES academic_terms (id),
    final_score        NUMERIC(5, 2),
    letter_grade       VARCHAR(5),
    comments           TEXT,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (course_id, student_profile_id, academic_term_id)
);

CREATE TABLE attendance_records
(
    id                 SERIAL PRIMARY KEY,
    course_id          INTEGER                NOT NULL REFERENCES courses (id),
    student_profile_id INTEGER                NOT NULL REFERENCES student_profiles (id),
    session_date       DATE                   NOT NULL,
    status             attendance_status_enum NOT NULL,
    remarks            VARCHAR(255),
    created_at         TIMESTAMPTZ            NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ            NOT NULL DEFAULT NOW(),
    UNIQUE (course_id, student_profile_id, session_date)
);

-- ============================================================
-- 10. COMUNICACIÓN Y CALENDARIO --------------------------------
-- ============================================================
CREATE TABLE calendar_events
(
    id             SERIAL PRIMARY KEY,
    course_id      INTEGER REFERENCES courses (id),
    school_id      INTEGER         NOT NULL REFERENCES schools (id),
    title          VARCHAR(255)    NOT NULL,
    description    TEXT,
    start_datetime TIMESTAMPTZ     NOT NULL,
    end_datetime   TIMESTAMPTZ     NOT NULL,
    event_type     event_type_enum NOT NULL DEFAULT 'other',
    created_by     INTEGER         NOT NULL REFERENCES profiles (id),
    created_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE announcements
(
    id           SERIAL PRIMARY KEY,
    course_id    INTEGER REFERENCES courses (id),
    author_id    INTEGER      NOT NULL REFERENCES profiles (id),
    title        VARCHAR(255) NOT NULL,
    body         TEXT         NOT NULL,
    target_roles TEXT[],
    is_pinned    BOOLEAN      NOT NULL DEFAULT FALSE,
    published_at TIMESTAMPTZ,
    expires_at   TIMESTAMPTZ,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE messages
(
    id                SERIAL PRIMARY KEY,
    sender_id         INTEGER     NOT NULL REFERENCES profiles (id),
    subject           VARCHAR(255),
    body              TEXT        NOT NULL,
    parent_message_id INTEGER REFERENCES messages (id),
    sent_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE message_recipients
(
    message_id   INTEGER NOT NULL REFERENCES messages (id) ON DELETE CASCADE,
    recipient_id INTEGER NOT NULL REFERENCES profiles (id) ON DELETE CASCADE,
    read_at      TIMESTAMPTZ,
    PRIMARY KEY (message_id, recipient_id)
);

-- ============================================================
-- 11. FOROS DE DISCUSIÓN ---------------------------------------
-- ============================================================
CREATE TABLE discussion_forums
(
    id          SERIAL PRIMARY KEY,
    course_id   INTEGER      NOT NULL REFERENCES courses (id),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    created_by  INTEGER      NOT NULL REFERENCES profiles (id),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE forum_topics
(
    id         SERIAL PRIMARY KEY,
    forum_id   INTEGER      NOT NULL REFERENCES discussion_forums (id),
    author_id  INTEGER      NOT NULL REFERENCES profiles (id),
    title      VARCHAR(255) NOT NULL,
    is_pinned  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE forum_posts
(
    id         SERIAL PRIMARY KEY,
    topic_id   INTEGER     NOT NULL REFERENCES forum_topics (id),
    author_id  INTEGER     NOT NULL REFERENCES profiles (id),
    content    TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 12. RECURSOS Y NOTIFICACIONES --------------------------------
-- ============================================================
CREATE TABLE resources
(
    id              SERIAL PRIMARY KEY,
    course_id       INTEGER REFERENCES courses (id),
    uploaded_by     INTEGER            NOT NULL REFERENCES profiles (id),
    title           VARCHAR(255)       NOT NULL,
    description     TEXT,
    file_url        VARCHAR(500)       NOT NULL,
    resource_type   resource_type_enum NOT NULL DEFAULT 'document',
    file_size_bytes BIGINT,
    upload_date     TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ        NOT NULL DEFAULT NOW()
);

CREATE TABLE notifications
(
    id                SERIAL PRIMARY KEY,
    recipient_id      INTEGER      NOT NULL REFERENCES profiles (id),
    title             VARCHAR(255) NOT NULL,
    body              TEXT,
    notification_type VARCHAR(50)  NOT NULL,
    link_url          VARCHAR(500),
    is_read           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 13. TRIGGERS PARA updated_at AUTOMÁTICO ----------------------
-- ============================================================
CREATE
OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at
= NOW();
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- auth.users
CREATE TRIGGER trg_auth_users_updated_at
    BEFORE UPDATE
    ON auth.users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Todas las tablas con updated_at
CREATE TRIGGER trg_profiles_updated_at
    BEFORE UPDATE
    ON profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_schools_updated_at
    BEFORE UPDATE
    ON schools
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_student_profiles_updated_at
    BEFORE UPDATE
    ON student_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_teacher_profiles_updated_at
    BEFORE UPDATE
    ON teacher_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_guardian_profiles_updated_at
    BEFORE UPDATE
    ON guardian_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_courses_updated_at
    BEFORE UPDATE
    ON courses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_modules_updated_at
    BEFORE UPDATE
    ON modules
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_lessons_updated_at
    BEFORE UPDATE
    ON lessons
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_assignments_updated_at
    BEFORE UPDATE
    ON assignments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_submissions_updated_at
    BEFORE UPDATE
    ON submissions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_course_grades_updated_at
    BEFORE UPDATE
    ON course_grades
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_attendance_records_updated_at
    BEFORE UPDATE
    ON attendance_records
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_calendar_events_updated_at
    BEFORE UPDATE
    ON calendar_events
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_announcements_updated_at
    BEFORE UPDATE
    ON announcements
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_discussion_forums_updated_at
    BEFORE UPDATE
    ON discussion_forums
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_forum_topics_updated_at
    BEFORE UPDATE
    ON forum_topics
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_forum_posts_updated_at
    BEFORE UPDATE
    ON forum_posts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trg_resources_updated_at
    BEFORE UPDATE
    ON resources
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- 14. ÍNDICES RECOMENDADOS PARA PERFORMANCE -------------------
-- ============================================================
CREATE INDEX idx_profiles_user_id ON profiles (user_id);
CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX idx_student_profiles_school ON student_profiles (school_id);
CREATE INDEX idx_teacher_profiles_school ON teacher_profiles (school_id);
CREATE INDEX idx_courses_subject ON courses (subject_id);
CREATE INDEX idx_courses_teacher ON courses (teacher_id);
CREATE INDEX idx_courses_term ON courses (academic_term_id);
CREATE INDEX idx_course_enrollments_course ON course_enrollments (course_id);
CREATE INDEX idx_course_enrollments_student ON course_enrollments (student_profile_id);
CREATE INDEX idx_modules_course ON modules (course_id);
CREATE INDEX idx_lessons_module ON lessons (module_id);
CREATE INDEX idx_assignments_course ON assignments (course_id);
CREATE INDEX idx_submissions_assignment ON submissions (assignment_id);
CREATE INDEX idx_submissions_student ON submissions (student_profile_id);
CREATE INDEX idx_attendance_course_date ON attendance_records (course_id, session_date);
CREATE INDEX idx_messages_parent ON messages (parent_message_id);
CREATE INDEX idx_message_recipients_recipient ON message_recipients (recipient_id);
CREATE INDEX idx_forum_topics_forum ON forum_topics (forum_id);
CREATE INDEX idx_forum_posts_topic ON forum_posts (topic_id);
CREATE INDEX idx_notifications_recipient ON notifications (recipient_id, is_read);