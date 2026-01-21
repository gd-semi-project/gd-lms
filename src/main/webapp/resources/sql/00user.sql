CREATE TABLE IF NOT EXISTS user (
    user_id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 식별자',

    login_id        VARCHAR(50) NOT NULL COMMENT '로그인 아이디',
    password_hash   VARCHAR(255) NOT NULL COMMENT '비밀번호 해시',

    name            VARCHAR(50) NOT NULL COMMENT '이름',
    birth_date      DATE COMMENT '생년월일',

    email           VARCHAR(100) NOT NULL COMMENT '이메일',
    phone           VARCHAR(20) COMMENT '전화번호',

    role            ENUM('ADMIN', 'INSTRUCTOR', 'STUDENT')
                    NOT NULL COMMENT '역할',

    status          ENUM('ACTIVE', 'INACTIVE')
                    NOT NULL DEFAULT 'ACTIVE'
                    COMMENT '상태',

    gender          ENUM('M', 'F')
                    COMMENT '성별',

    address         VARCHAR(255) COMMENT '주소',

    must_change_pw  BOOLEAN NOT NULL DEFAULT FALSE
                    COMMENT '비밀번호 변경 필수 여부',

    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    COMMENT '생성일시',

    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP
                    COMMENT '수정일시',

    CONSTRAINT uk_user_login_id UNIQUE (login_id),
    CONSTRAINT uk_user_email UNIQUE (email)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='사용자 테이블';


INSERT INTO `user`
(login_id, password_hash, name, birth_date, email, phone, role, status, gender, address, must_change_pw)
VALUES

('s0001','{bcrypt}$2a$10$dummyhash','김민준','2002-03-14','s0001@student.test','010-4821-1932','ADMIN','ACTIVE','M','서울시 마포구 연남동',FALSE),
('s0002','{bcrypt}$2a$10$dummyhash','박서연','2001-11-02','s0002@student.test','010-7712-5521','STUDENT','ACTIVE','F','서울시 성북구 길음동',FALSE),
('s0003','{bcrypt}$2a$10$dummyhash','이지훈','2000-06-21','s0003@student.test','010-6349-8841','STUDENT','ACTIVE','M','경기도 성남시 분당구',TRUE),
('s0004','{bcrypt}$2a$10$dummyhash','최은지','2003-01-09','s0004@student.test','010-2284-9903','STUDENT','ACTIVE','F','경기도 수원시 영통구',FALSE),
('s0005','{bcrypt}$2a$10$dummyhash','장현우','2002-09-30','s0005@student.test','010-5091-7712','STUDENT','ACTIVE','M','인천시 남동구 구월동',FALSE),
('s0006','{bcrypt}$2a$10$dummyhash','윤수빈','2001-04-18','s0006@student.test','010-8129-3344','STUDENT','ACTIVE','F','대전시 서구 둔산동',TRUE),
('s0007','{bcrypt}$2a$10$dummyhash','강태현','1999-12-07','s0007@student.test','010-9102-6653','STUDENT','ACTIVE','M','부산시 해운대구 우동',FALSE),
('s0008','{bcrypt}$2a$10$dummyhash','한지민','2000-08-26','s0008@student.test','010-4773-1098','STUDENT','ACTIVE','F','광주시 북구 용봉동',FALSE),
('s0009','{bcrypt}$2a$10$dummyhash','정도윤','2003-05-11','s0009@student.test','010-3650-2418','STUDENT','ACTIVE','M','대구시 수성구 범어동',FALSE),
('s0010','{bcrypt}$2a$10$dummyhash','임나연','2002-02-03','s0010@student.test','010-7441-6802','STUDENT','ACTIVE','F','울산시 남구 삼산동',TRUE),

('s0011','{bcrypt}$2a$10$dummyhash','조현서','2001-07-19','s0011@student.test','010-1289-4570','STUDENT','ACTIVE','M','서울시 관악구 봉천동',FALSE),
('s0012','{bcrypt}$2a$10$dummyhash','오하린','2004-03-27','s0012@student.test','010-9014-3327','STUDENT','ACTIVE','F','경기도 고양시 일산동구',FALSE),
('s0013','{bcrypt}$2a$10$dummyhash','김도현','2000-10-15','s0013@student.test','010-5603-1194','STUDENT','ACTIVE','M','서울시 영등포구 당산동',TRUE),
('s0014','{bcrypt}$2a$10$dummyhash','이서현','2002-06-08','s0014@student.test','010-4302-9088','STUDENT','ACTIVE','F','서울시 송파구 잠실동',FALSE),
('s0015','{bcrypt}$2a$10$dummyhash','박준우','2001-01-25','s0015@student.test','010-6672-2401','STUDENT','ACTIVE','M','경기도 용인시 수지구',FALSE),
('s0016','{bcrypt}$2a$10$dummyhash','최지아','2003-12-02','s0016@student.test','010-2207-6839','STUDENT','ACTIVE','F','인천시 연수구 송도동',FALSE),
('s0017','{bcrypt}$2a$10$dummyhash','정지훈','1999-09-09','s0017@student.test','010-7880-3905','STUDENT','ACTIVE','M','부산시 남구 대연동',TRUE),
('s0018','{bcrypt}$2a$10$dummyhash','강예린','2002-11-21','s0018@student.test','010-1594-7710','STUDENT','ACTIVE','F','경기도 부천시 상동',FALSE),
('s0019','{bcrypt}$2a$10$dummyhash','조승현','2000-04-04','s0019@student.test','010-2751-9026','STUDENT','ACTIVE','M','대전시 유성구 봉명동',FALSE),
('s0020','{bcrypt}$2a$10$dummyhash','윤아린','2001-05-30','s0020@student.test','010-6123-0449','STUDENT','ACTIVE','F','광주시 서구 치평동',FALSE),

('s0021','{bcrypt}$2a$10$dummyhash','장우진','2003-08-13','s0021@student.test','010-3056-2179','STUDENT','ACTIVE','M','서울시 강서구 화곡동',TRUE),
('s0022','{bcrypt}$2a$10$dummyhash','임서영','2002-01-17','s0022@student.test','010-9801-5532','STUDENT','ACTIVE','F','서울시 중랑구 면목동',FALSE),
('s0023','{bcrypt}$2a$10$dummyhash','한성민','2000-02-28','s0023@student.test','010-4478-3316','STUDENT','ACTIVE','M','경기도 안양시 동안구',FALSE),
('s0024','{bcrypt}$2a$10$dummyhash','오유진','2004-09-06','s0024@student.test','010-7209-1061','STUDENT','ACTIVE','F','경기도 화성시 동탄동',TRUE),
('s0025','{bcrypt}$2a$10$dummyhash','김태호','2001-12-18','s0025@student.test','010-1162-7844','STUDENT','ACTIVE','M','인천시 부평구 부평동',FALSE),
('s0026','{bcrypt}$2a$10$dummyhash','이채원','2002-07-07','s0026@student.test','010-5382-6103','STUDENT','ACTIVE','F','서울시 강북구 수유동',FALSE),
('s0027','{bcrypt}$2a$10$dummyhash','박재현','1999-03-23','s0027@student.test','010-8902-4471','STUDENT','ACTIVE','M','대구시 달서구 상인동',FALSE),
('s0028','{bcrypt}$2a$10$dummyhash','최수아','2003-04-16','s0028@student.test','010-6310-2094','STUDENT','ACTIVE','F','부산시 수영구 광안동',TRUE),
('s0029','{bcrypt}$2a$10$dummyhash','정현수','2000-11-10','s0029@student.test','010-2088-7756','STUDENT','ACTIVE','M','경기도 김포시 장기동',FALSE),
('s0030','{bcrypt}$2a$10$dummyhash','강하은','2001-06-01','s0030@student.test','010-7719-3802','STUDENT','ACTIVE','F','대전시 중구 대흥동',FALSE),

('s0031','{bcrypt}$2a$10$dummyhash','조민성','2002-05-05','s0031@student.test','010-3321-9017','STUDENT','ACTIVE','M','서울시 동대문구 장안동',FALSE),
('s0032','{bcrypt}$2a$10$dummyhash','윤예원','2003-10-28','s0032@student.test','010-6408-1250','STUDENT','ACTIVE','F','경기도 의정부시 민락동',TRUE),
('s0033','{bcrypt}$2a$10$dummyhash','장동우','1999-08-19','s0033@student.test','010-9173-6641','STUDENT','ACTIVE','M','인천시 서구 청라동',FALSE),
('s0034','{bcrypt}$2a$10$dummyhash','임가은','2004-02-12','s0034@student.test','010-2055-3389','STUDENT','ACTIVE','F','서울시 광진구 화양동',FALSE),
('s0035','{bcrypt}$2a$10$dummyhash','한준혁','2001-09-24','s0035@student.test','010-7770-9142','STUDENT','ACTIVE','M','경기도 남양주시 다산동',TRUE),
('s0036','{bcrypt}$2a$10$dummyhash','오서윤','2002-12-29','s0036@student.test','010-4480-0029','STUDENT','ACTIVE','F','광주시 광산구 수완동',FALSE),
('s0037','{bcrypt}$2a$10$dummyhash','김건우','2000-01-06','s0037@student.test','010-6892-7713','STUDENT','ACTIVE','M','울산시 북구 매곡동',FALSE),
('s0038','{bcrypt}$2a$10$dummyhash','이하늘','2003-07-20','s0038@student.test','010-1306-5820','STUDENT','ACTIVE','F','서울시 노원구 공릉동',FALSE),
('s0039','{bcrypt}$2a$10$dummyhash','박시우','2002-04-09','s0039@student.test','010-9421-4015','STUDENT','ACTIVE','M','부산시 사하구 하단동',TRUE),
('s0040','{bcrypt}$2a$10$dummyhash','최다은','2001-02-14','s0040@student.test','010-5107-8340','STUDENT','ACTIVE','F','경기도 파주시 야당동',FALSE),

('s0041','{bcrypt}$2a$10$dummyhash','정우성','1999-11-03','s0041@student.test','010-2209-7138','STUDENT','ACTIVE','M','서울시 금천구 가산동',FALSE),
('s0042','{bcrypt}$2a$10$dummyhash','강민지','2004-06-23','s0042@student.test','010-8764-1190','STUDENT','ACTIVE','F','경기도 광명시 철산동',TRUE),
('s0043','{bcrypt}$2a$10$dummyhash','조하준','2002-08-08','s0043@student.test','010-6001-2549','STUDENT','ACTIVE','M','대전시 동구 가오동',FALSE),
('s0044','{bcrypt}$2a$10$dummyhash','윤지수','2001-10-31','s0044@student.test','010-4920-9031','STUDENT','ACTIVE','F','대구시 동구 신암동',FALSE),
('s0045','{bcrypt}$2a$10$dummyhash','장민혁','2000-05-17','s0045@student.test','010-3188-7704','STUDENT','ACTIVE','M','부산시 동래구 온천동',FALSE),
('s0046','{bcrypt}$2a$10$dummyhash','임소현','2003-09-12','s0046@student.test','010-7302-1187','STUDENT','ACTIVE','F','인천시 미추홀구 주안동',TRUE),
('s0047','{bcrypt}$2a$10$dummyhash','한도현','2002-01-27','s0047@student.test','010-2451-6699','STUDENT','ACTIVE','M','서울시 중구 신당동',FALSE),
('s0048','{bcrypt}$2a$10$dummyhash','오지윤','2001-07-04','s0048@student.test','010-9661-0422','STUDENT','ACTIVE','F','경기도 하남시 미사동',FALSE),
('s0049','{bcrypt}$2a$10$dummyhash','김승민','1999-04-30','s0049@student.test','010-1048-5528','STUDENT','ACTIVE','M','광주시 남구 봉선동',FALSE),
('s0050','{bcrypt}$2a$10$dummyhash','이예나','2004-01-15','s0050@student.test','010-8870-3312','STUDENT','ACTIVE','F','서울시 강동구 천호동',TRUE),

('s0051','{bcrypt}$2a$10$dummyhash','박현준','2000-09-01','s0051@student.test','010-5531-9097','STUDENT','ACTIVE','M','경기도 안산시 상록구',FALSE),
('s0052','{bcrypt}$2a$10$dummyhash','최보민','2002-12-06','s0052@student.test','010-6099-1183','STUDENT','ACTIVE','F','서울시 서대문구 홍제동',FALSE),
('s0053','{bcrypt}$2a$10$dummyhash','정민재','2003-06-10','s0053@student.test','010-3718-4602','STUDENT','ACTIVE','M','인천시 계양구 계산동',TRUE),
('s0054','{bcrypt}$2a$10$dummyhash','강유나','2001-03-26','s0054@student.test','010-7408-2216','STUDENT','ACTIVE','F','대전시 서구 월평동',FALSE),
('s0055','{bcrypt}$2a$10$dummyhash','조정우','2002-05-29','s0055@student.test','010-2981-7005','STUDENT','ACTIVE','M','대구시 북구 침산동',FALSE),
('s0056','{bcrypt}$2a$10$dummyhash','윤서진','2000-02-10','s0056@student.test','010-6620-8890','STUDENT','ACTIVE','F','부산시 부산진구 전포동',FALSE),
('s0057','{bcrypt}$2a$10$dummyhash','장세훈','2004-07-18','s0057@student.test','010-1419-3360','STUDENT','ACTIVE','M','경기도 평택시 비전동',TRUE),
('s0058','{bcrypt}$2a$10$dummyhash','임다희','2003-03-08','s0058@student.test','010-8510-6071','STUDENT','ACTIVE','F','서울시 강남구 역삼동',FALSE),
('s0059','{bcrypt}$2a$10$dummyhash','한상윤','2001-11-23','s0059@student.test','010-3905-7750','STUDENT','ACTIVE','M','경기도 구리시 인창동',FALSE),
('s0060','{bcrypt}$2a$10$dummyhash','오수정','2002-10-05','s0060@student.test','010-5098-1207','STUDENT','ACTIVE','F','인천시 중구 운서동',FALSE),

('s0061','{bcrypt}$2a$10$dummyhash','김준서','2000-04-25','s0061@student.test','010-7334-6008','STUDENT','ACTIVE','M','서울시 강서구 마곡동',TRUE),
('s0062','{bcrypt}$2a$10$dummyhash','이서아','2004-05-02','s0062@student.test','010-2184-7720','STUDENT','ACTIVE','F','경기도 시흥시 배곧동',FALSE),
('s0063','{bcrypt}$2a$10$dummyhash','박민규','2001-01-12','s0063@student.test','010-9062-1438','STUDENT','ACTIVE','M','대전시 유성구 노은동',FALSE),
('s0064','{bcrypt}$2a$10$dummyhash','최지유','2002-07-27','s0064@student.test','010-4409-3306','STUDENT','ACTIVE','F','대구시 달성군 다사읍',TRUE),
('s0065','{bcrypt}$2a$10$dummyhash','정시온','2003-02-22','s0065@student.test','010-6118-2045','STUDENT','ACTIVE','M','부산시 강서구 명지동',FALSE),
('s0066','{bcrypt}$2a$10$dummyhash','강채린','2000-12-14','s0066@student.test','010-2899-7119','STUDENT','ACTIVE','F','서울시 동작구 상도동',FALSE),
('s0067','{bcrypt}$2a$10$dummyhash','조연우','2002-09-09','s0067@student.test','010-8731-4516','STUDENT','ACTIVE','M','경기도 군포시 산본동',FALSE),
('s0068','{bcrypt}$2a$10$dummyhash','윤하진','2001-06-16','s0068@student.test','010-3276-8803','STUDENT','ACTIVE','F','인천시 남동구 논현동',TRUE),
('s0069','{bcrypt}$2a$10$dummyhash','장윤호','1999-10-20','s0069@student.test','010-7902-1401','STUDENT','ACTIVE','M','광주시 동구 충장로',FALSE),
('s0070','{bcrypt}$2a$10$dummyhash','임지안','2004-08-11','s0070@student.test','010-5560-9921','STUDENT','ACTIVE','F','서울시 용산구 이태원동',FALSE),

('i0001','{bcrypt}$2a$10$dummyhash','이재훈','1982-05-11','i0001@inst.test','010-3390-7721','INSTRUCTOR','ACTIVE','M','서울시 서초구 반포동',FALSE),
('i0002','{bcrypt}$2a$10$dummyhash','정영선','1979-10-03','i0002@inst.test','010-8821-4410','INSTRUCTOR','ACTIVE','F','경기도 고양시 일산서구',FALSE),
('i0003','{bcrypt}$2a$10$dummyhash','조동현','1985-02-17','i0003@inst.test','010-6643-2289','INSTRUCTOR','ACTIVE','M','대구시 수성구 만촌동',TRUE),
('i0004','{bcrypt}$2a$10$dummyhash','임민우','1981-07-29','i0004@inst.test','010-5912-8033','INSTRUCTOR','ACTIVE','M','경기도 용인시 기흥구',FALSE),
('i0005','{bcrypt}$2a$10$dummyhash','김소연','1986-12-08','i0005@inst.test','010-2077-1199','INSTRUCTOR','ACTIVE','F','서울시 종로구 혜화동',FALSE),

('i0006','{bcrypt}$2a$10$dummyhash','박상현','1980-04-22','i0006@inst.test','010-7740-2208','INSTRUCTOR','ACTIVE','M','인천시 연수구 연수동',TRUE),
('i0007','{bcrypt}$2a$10$dummyhash','최은정','1983-09-14','i0007@inst.test','010-4881-7703','INSTRUCTOR','ACTIVE','F','대전시 서구 갈마동',FALSE),
('i0008','{bcrypt}$2a$10$dummyhash','강동훈','1978-01-19','i0008@inst.test','010-9150-3402','INSTRUCTOR','ACTIVE','M','부산시 동래구 사직동',FALSE),
('i0009','{bcrypt}$2a$10$dummyhash','윤지현','1987-03-03','i0009@inst.test','010-3601-8822','INSTRUCTOR','ACTIVE','F','광주시 서구 금호동',FALSE),
('i0010','{bcrypt}$2a$10$dummyhash','장성호','1984-06-27','i0010@inst.test','010-7012-6091','INSTRUCTOR','ACTIVE','M','서울시 성동구 성수동',TRUE),

('i0011','{bcrypt}$2a$10$dummyhash','한미경','1982-11-05','i0011@inst.test','010-2301-7744','INSTRUCTOR','ACTIVE','F','경기도 수원시 팔달구',FALSE),
('i0012','{bcrypt}$2a$10$dummyhash','오준혁','1988-08-18','i0012@inst.test','010-8451-2009','INSTRUCTOR','ACTIVE','M','서울시 강남구 대치동',FALSE),
('i0013','{bcrypt}$2a$10$dummyhash','정하나','1981-02-10','i0013@inst.test','010-6190-3341','INSTRUCTOR','ACTIVE','F','인천시 부평구 산곡동',TRUE),
('i0014','{bcrypt}$2a$10$dummyhash','김태영','1979-12-24','i0014@inst.test','010-5520-9811','INSTRUCTOR','ACTIVE','M','대구시 달서구 월성동',FALSE),
('i0015','{bcrypt}$2a$10$dummyhash','박유진','1986-04-06','i0015@inst.test','010-1910-6652','INSTRUCTOR','ACTIVE','F','부산시 해운대구 중동',FALSE),

('i0016','{bcrypt}$2a$10$dummyhash','최진호','1980-09-09','i0016@inst.test','010-8033-1740','INSTRUCTOR','ACTIVE','M','경기도 성남시 수정구',FALSE),
('i0017','{bcrypt}$2a$10$dummyhash','강혜진','1987-10-13','i0017@inst.test','010-4007-2199','INSTRUCTOR','ACTIVE','F','서울시 동대문구 휘경동',TRUE),
('i0018','{bcrypt}$2a$10$dummyhash','조성민','1983-01-28','i0018@inst.test','010-6722-5038','INSTRUCTOR','ACTIVE','M','대전시 유성구 관평동',FALSE),
('i0019','{bcrypt}$2a$10$dummyhash','윤세진','1978-07-07','i0019@inst.test','010-9158-7711','INSTRUCTOR','ACTIVE','F','광주시 북구 문흥동',FALSE),
('i0020','{bcrypt}$2a$10$dummyhash','장원석','1985-05-20','i0020@inst.test','010-2460-9288','INSTRUCTOR','ACTIVE','M','서울시 영등포구 여의도동',FALSE),

('i0021','{bcrypt}$2a$10$dummyhash','임지훈','1982-03-12','i0021@inst.test','010-3310-4407','INSTRUCTOR','ACTIVE','M','경기도 고양시 덕양구',TRUE),
('i0022','{bcrypt}$2a$10$dummyhash','한수연','1984-11-29','i0022@inst.test','010-7701-1054','INSTRUCTOR','ACTIVE','F','인천시 서구 가정동',FALSE),
('i0023','{bcrypt}$2a$10$dummyhash','오상민','1981-06-15','i0023@inst.test','010-6029-1133','INSTRUCTOR','ACTIVE','M','대구시 중구 동성로',FALSE),
('i0024','{bcrypt}$2a$10$dummyhash','정다영','1988-02-02','i0024@inst.test','010-9901-6640','INSTRUCTOR','ACTIVE','F','부산시 연제구 연산동',TRUE),
('i0025','{bcrypt}$2a$10$dummyhash','김도윤','1979-08-26','i0025@inst.test','010-1188-7720','INSTRUCTOR','ACTIVE','M','서울시 마포구 상암동',FALSE),

('i0026','{bcrypt}$2a$10$dummyhash','박지은','1986-09-03','i0026@inst.test','010-4412-3800','INSTRUCTOR','ACTIVE','F','경기도 부천시 중동',FALSE),
('i0027','{bcrypt}$2a$10$dummyhash','최경수','1980-12-17','i0027@inst.test','010-5077-9201','INSTRUCTOR','ACTIVE','M','대전시 중구 은행동',FALSE),
('i0028','{bcrypt}$2a$10$dummyhash','강민아','1987-06-08','i0028@inst.test','010-2601-4492','INSTRUCTOR','ACTIVE','F','서울시 노원구 상계동',TRUE),
('i0029','{bcrypt}$2a$10$dummyhash','조현수','1983-04-25','i0029@inst.test','010-7130-6602','INSTRUCTOR','ACTIVE','M','경기도 하남시 덕풍동',FALSE),
('i0030','{bcrypt}$2a$10$dummyhash','윤하늘','1985-10-10','i0030@inst.test','010-8802-3107','INSTRUCTOR','ACTIVE','F','인천시 중구 신흥동',FALSE);


