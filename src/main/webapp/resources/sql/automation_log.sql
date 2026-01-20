CREATE TABLE IF NOT EXISTS automation_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  job_code VARCHAR(50) NOT NULL,
  run_date DATE NOT NULL,
  status ENUM('SUCCESS','FAIL') NOT NULL,
  message TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_job_date (job_code, run_date),
  INDEX idx_run_date (run_date)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci
COMMENT='자동화 작업 실행 이력 및 중복 실행 방지';
