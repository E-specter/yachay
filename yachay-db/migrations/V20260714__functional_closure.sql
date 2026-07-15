-- Cierre funcional Yachay. Ejecutar una sola vez antes de iniciar con perfil prod.
ALTER TABLE profiles ADD COLUMN document_type VARCHAR(20) NULL;
ALTER TABLE profiles ADD COLUMN document_number VARCHAR(30) NULL;
ALTER TABLE schools ADD COLUMN email VARCHAR(255) NULL;
ALTER TABLE student_profiles ADD COLUMN education_level VARCHAR(30) NULL;
ALTER TABLE student_profiles ADD COLUMN guardian_name VARCHAR(255) NULL;
ALTER TABLE student_profiles ADD COLUMN guardian_email VARCHAR(255) NULL;
ALTER TABLE student_profiles ADD COLUMN guardian_phone VARCHAR(20) NULL;
ALTER TABLE yachay_courses ADD COLUMN education_level VARCHAR(30) NULL;
ALTER TABLE yachay_calendar_events ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVO';
ALTER TABLE yachay_admission_applications ADD COLUMN student_profile_id INT NULL;
ALTER TABLE yachay_admission_applications ADD COLUMN decided_at DATETIME(6) NULL;
ALTER TABLE yachay_admission_applications
  ADD CONSTRAINT uk_admission_student_profile UNIQUE (student_profile_id),
  ADD CONSTRAINT fk_admission_student_profile FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id);

CREATE TABLE yachay_homework_submissions (
  id INT NOT NULL AUTO_INCREMENT,
  task_id INT NOT NULL,
  student_profile_id INT NOT NULL,
  content VARCHAR(2000) NOT NULL,
  attachment_url VARCHAR(500) NULL,
  status VARCHAR(30) NOT NULL,
  score DECIMAL(5,2) NULL,
  feedback VARCHAR(1000) NULL,
  submitted_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_homework_submission UNIQUE (task_id, student_profile_id),
  CONSTRAINT fk_submission_task FOREIGN KEY (task_id) REFERENCES yachay_academic_tasks(id),
  CONSTRAINT fk_submission_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id)
);

CREATE TABLE yachay_announcement_reads (
  id INT NOT NULL AUTO_INCREMENT,
  announcement_id INT NOT NULL,
  student_profile_id INT NOT NULL,
  read_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_announcement_read UNIQUE (announcement_id, student_profile_id),
  CONSTRAINT fk_read_announcement FOREIGN KEY (announcement_id) REFERENCES yachay_announcements(id),
  CONSTRAINT fk_read_student FOREIGN KEY (student_profile_id) REFERENCES student_profiles(id)
);

CREATE TABLE yachay_system_settings (
  setting_key VARCHAR(100) NOT NULL,
  setting_value VARCHAR(1000) NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (setting_key)
);

CREATE TABLE yachay_password_reset_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token_hash VARCHAR(64) NOT NULL,
  expires_at DATETIME(6) NOT NULL,
  used_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_password_reset_token_hash UNIQUE (token_hash),
  CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES auth_users(id)
);

CREATE INDEX idx_submission_student ON yachay_homework_submissions(student_profile_id);
CREATE INDEX idx_announcement_read_student ON yachay_announcement_reads(student_profile_id);
CREATE INDEX idx_password_reset_user ON yachay_password_reset_tokens(user_id);
