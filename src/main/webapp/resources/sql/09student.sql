CREATE TABLE IF NOT EXISTS student (
    student_id BIGINT AUTO_INCREMENT PRIMARY KEY,  

    department_id BIGINT NOT NULL,                  
    user_id BIGINT NOT NULL,                        

    student_number INT NOT NULL UNIQUE,             
    student_grade INT,                              

    status ENUM('UNDERGRADUATE','GRADUATE') NOT NULL,           
    student_status ENUM('ENROLLED','LEAVE','GRADUATED','BREAK') 
                   NOT NULL DEFAULT 'ENROLLED',       

    enroll_date DATETIME,                           
    end_date DATETIME,                               
    tuition_account VARCHAR(255),          

    CONSTRAINT fk_student_user
        FOREIGN KEY (user_id) 
        REFERENCES `user`(user_id),

    CONSTRAINT fk_student_department
        FOREIGN KEY (department_id) 
        REFERENCES department(department_id)
);

INSERT INTO student
(
  department_id,
  user_id,
  student_number,
  student_grade,
  status,
  student_status,
  enroll_date,
  end_date,
  tuition_account
)
SELECT
  d.department_id,
  u.user_id,
  20260000 + u.user_id              AS student_number,
  1 + ((u.user_id - 1) % 4)         AS student_grade,
  'UNDERGRADUATE'                            AS status,
  CASE
    WHEN u.user_id % 10 = 0 THEN 'LEAVE'
    ELSE 'ENROLLED'
  END                               AS student_status,
  DATE_ADD('2022-03-01', INTERVAL (u.user_id % 4) YEAR) AS enroll_date,
  NULL                              AS end_date,
  CONCAT('110-', LPAD(u.user_id, 3, '0'), '-567890') AS tuition_account
FROM
  (SELECT user_id FROM `user` WHERE user_id BETWEEN 1 AND 70 ORDER BY user_id) u
JOIN
  (
    SELECT
      department_id,
      ROW_NUMBER() OVER (ORDER BY department_id) AS rn
    FROM department
  ) d
  ON d.rn = ((u.user_id - 1) % (SELECT COUNT(*) FROM department)) + 1;