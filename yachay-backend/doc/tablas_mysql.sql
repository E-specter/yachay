-- ============================================================
-- VIRTUAL CAMPUS - MySQL
-- Compatible con Spring Boot + MySQL
-- ============================================================

CREATE DATABASE IF NOT EXISTS yachay
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE yachay;

-- ============================================================
-- 1. USUARIOS DE AUTENTICACIÓN
-- ============================================================

CREATE TABLE IF NOT EXISTS auth_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    display_name VARCHAR(255),
    encrypted_password VARCHAR(255),
    email_confirmed_at DATETIME,
    phone_confirmed_at DATETIME,
    last_sign_in_at DATETIME,
    raw_app_meta_data JSON DEFAULT (JSON_OBJECT()),
    raw_user_meta_data JSON DEFAULT (JSON_OBJECT()),
    providers JSON,
    provider_type VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 2. ROLES
-- ============================================================

CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB;

-- ============================================================
-- 3. PERFIL UNIFICADO
-- ============================================================

CREATE TABLE IF NOT EXISTS profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    avatar_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES auth_users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 4. USUARIO - ROLES
-- ============================================================

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES auth_users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 5. COLEGIOS Y PERFILES ESPECÍFICOS
-- ============================================================

CREATE TABLE IF NOT EXISTS schools (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE,
    address VARCHAR(255),
    phone VARCHAR(20),
    logo_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS student_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    profile_id INT NOT NULL UNIQUE,
    school_id INT NOT NULL,
    student_code VARCHAR(50) UNIQUE,
    grade_level SMALLINT NOT NULL,
    section VARCHAR(20),
    enrollment_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_student_grade_level
        CHECK (grade_level BETWEEN 1 AND 12),

    CONSTRAINT fk_student_profiles_profile
        FOREIGN KEY (profile_id)
        REFERENCES profiles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_student_profiles_school
        FOREIGN KEY (school_id)
        REFERENCES schools(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS teacher_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    profile_id INT NOT NULL UNIQUE,
    school_id INT NOT NULL,
    employee_code VARCHAR(50) UNIQUE,
    subject_area VARCHAR(100),
    hire_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_teacher_profiles_profile
        FOREIGN KEY (profile_id)
        REFERENCES profiles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_teacher_profiles_school
        FOREIGN KEY (school_id)
        REFERENCES schools(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS guardian_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    profile_id INT NOT NULL UNIQUE,
    relationship VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_guardian_profiles_profile
        FOREIGN KEY (profile_id)
        REFERENCES profiles(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS student_guardians (
    student_profile_id INT NOT NULL,
    guardian_profile_id INT NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (student_profile_id, guardian_profile_id),

    CONSTRAINT fk_student_guardians_student
        FOREIGN KEY (student_profile_id)
        REFERENCES student_profiles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_student_guardians_guardian
        FOREIGN KEY (guardian_profile_id)
        REFERENCES guardian_profiles(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 6. TÉRMINOS ACADÉMICOS, MATERIAS Y CURSOS
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_terms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    school_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    term_type ENUM('year', 'semester', 'trimester', 'quarter') NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_academic_terms_school
        FOREIGN KEY (school_id)
        REFERENCES schools(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_id INT NOT NULL,
    teacher_id INT NOT NULL,
    academic_term_id INT NOT NULL,
    grade_level SMALLINT NOT NULL,
    section VARCHAR(20),
    room VARCHAR(50),
    max_students INT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_courses_grade_level
        CHECK (grade_level BETWEEN 1 AND 12),

    CONSTRAINT fk_courses_subject
        FOREIGN KEY (subject_id)
        REFERENCES subjects(id),

    CONSTRAINT fk_courses_teacher
        FOREIGN KEY (teacher_id)
        REFERENCES teacher_profiles(id),

    CONSTRAINT fk_courses_term
        FOREIGN KEY (academic_term_id)
        REFERENCES academic_terms(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_enrollments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    student_profile_id INT NOT NULL,
    enrollment_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    status ENUM('active', 'completed', 'dropped') NOT NULL DEFAULT 'active',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (course_id, student_profile_id),

    CONSTRAINT fk_course_enrollments_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_course_enrollments_student
        FOREIGN KEY (student_profile_id)
        REFERENCES student_profiles(id)
) ENGINE=InnoDB;

-- ============================================================
-- 7. MÓDULOS Y LECCIONES
-- ============================================================

CREATE TABLE IF NOT EXISTS modules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    order_index INT NOT NULL DEFAULT 0,
    release_date DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_modules_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS lessons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    module_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    lesson_type ENUM('video', 'reading', 'quiz', 'assignment', 'discussion', 'external_link') NOT NULL DEFAULT 'reading',
    resource_url VARCHAR(500),
    duration_minutes INT,
    order_index INT NOT NULL DEFAULT 0,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_lessons_module
        FOREIGN KEY (module_id)
        REFERENCES modules(id)
) ENGINE=InnoDB;

-- ============================================================
-- 8. TAREAS Y ENTREGAS
-- ============================================================

CREATE TABLE IF NOT EXISTS assignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lesson_id INT,
    course_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATETIME,
    max_score DECIMAL(5, 2) NOT NULL DEFAULT 100.00,
    assignment_type ENUM('homework', 'quiz', 'project', 'exam', 'participation') NOT NULL DEFAULT 'homework',
    allow_late_submission BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_assignments_lesson
        FOREIGN KEY (lesson_id)
        REFERENCES lessons(id),

    CONSTRAINT fk_assignments_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS submissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    assignment_id INT NOT NULL,
    student_profile_id INT NOT NULL,
    submission_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content TEXT,
    file_url VARCHAR(500),
    score DECIMAL(5, 2),
    graded_by INT,
    graded_at DATETIME,
    status ENUM('submitted', 'graded', 'returned', 'revision') NOT NULL DEFAULT 'submitted',
    teacher_comments TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE (assignment_id, student_profile_id),

    CONSTRAINT fk_submissions_assignment
        FOREIGN KEY (assignment_id)
        REFERENCES assignments(id),

    CONSTRAINT fk_submissions_student
        FOREIGN KEY (student_profile_id)
        REFERENCES student_profiles(id),

    CONSTRAINT fk_submissions_teacher
        FOREIGN KEY (graded_by)
        REFERENCES teacher_profiles(id)
) ENGINE=InnoDB;

-- ============================================================
-- 9. CALIFICACIONES Y ASISTENCIA
-- ============================================================

CREATE TABLE IF NOT EXISTS course_grades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    student_profile_id INT NOT NULL,
    academic_term_id INT NOT NULL,
    final_score DECIMAL(5, 2),
    letter_grade VARCHAR(5),
    comments TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE (course_id, student_profile_id, academic_term_id),

    CONSTRAINT fk_course_grades_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_course_grades_student
        FOREIGN KEY (student_profile_id)
        REFERENCES student_profiles(id),

    CONSTRAINT fk_course_grades_term
        FOREIGN KEY (academic_term_id)
        REFERENCES academic_terms(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS attendance_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    student_profile_id INT NOT NULL,
    session_date DATE NOT NULL,
    status ENUM('present', 'absent', 'late', 'excused') NOT NULL,
    remarks VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE (course_id, student_profile_id, session_date),

    CONSTRAINT fk_attendance_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_attendance_student
        FOREIGN KEY (student_profile_id)
        REFERENCES student_profiles(id)
) ENGINE=InnoDB;

-- ============================================================
-- 10. COMUNICACIÓN Y CALENDARIO
-- ============================================================

CREATE TABLE IF NOT EXISTS calendar_events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT,
    school_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    event_type ENUM('class', 'exam', 'holiday', 'meeting', 'extracurricular', 'other') NOT NULL DEFAULT 'other',
    created_by INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_calendar_events_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_calendar_events_school
        FOREIGN KEY (school_id)
        REFERENCES schools(id),

    CONSTRAINT fk_calendar_events_created_by
        FOREIGN KEY (created_by)
        REFERENCES profiles(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS announcements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT,
    author_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    target_roles JSON,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    published_at DATETIME,
    expires_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_announcements_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_announcements_author
        FOREIGN KEY (author_id)
        REFERENCES profiles(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    subject VARCHAR(255),
    body TEXT NOT NULL,
    parent_message_id INT,
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_messages_sender
        FOREIGN KEY (sender_id)
        REFERENCES profiles(id),

    CONSTRAINT fk_messages_parent
        FOREIGN KEY (parent_message_id)
        REFERENCES messages(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS message_recipients (
    message_id INT NOT NULL,
    recipient_id INT NOT NULL,
    read_at DATETIME,

    PRIMARY KEY (message_id, recipient_id),

    CONSTRAINT fk_message_recipients_message
        FOREIGN KEY (message_id)
        REFERENCES messages(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_message_recipients_recipient
        FOREIGN KEY (recipient_id)
        REFERENCES profiles(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 11. FOROS
-- ============================================================

CREATE TABLE IF NOT EXISTS discussion_forums (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_by INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_discussion_forums_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_discussion_forums_created_by
        FOREIGN KEY (created_by)
        REFERENCES profiles(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS forum_topics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    forum_id INT NOT NULL,
    author_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_forum_topics_forum
        FOREIGN KEY (forum_id)
        REFERENCES discussion_forums(id),

    CONSTRAINT fk_forum_topics_author
        FOREIGN KEY (author_id)
        REFERENCES profiles(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS forum_posts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    topic_id INT NOT NULL,
    author_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_forum_posts_topic
        FOREIGN KEY (topic_id)
        REFERENCES forum_topics(id),

    CONSTRAINT fk_forum_posts_author
        FOREIGN KEY (author_id)
        REFERENCES profiles(id)
) ENGINE=InnoDB;

-- ============================================================
-- 12. RECURSOS Y NOTIFICACIONES
-- ============================================================

CREATE TABLE IF NOT EXISTS resources (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT,
    uploaded_by INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    file_url VARCHAR(500) NOT NULL,
    resource_type ENUM('document', 'video', 'audio', 'link', 'image', 'other') NOT NULL DEFAULT 'document',
    file_size_bytes BIGINT,
    upload_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_resources_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_resources_uploaded_by
        FOREIGN KEY (uploaded_by)
        REFERENCES profiles(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recipient_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT,
    notification_type VARCHAR(50) NOT NULL,
    link_url VARCHAR(500),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_recipient
        FOREIGN KEY (recipient_id)
        REFERENCES profiles(id)
) ENGINE=InnoDB;

-- ============================================================
-- 13. ÍNDICES RECOMENDADOS
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