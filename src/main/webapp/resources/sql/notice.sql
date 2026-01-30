CREATE TABLE IF NOT EXISTS notice (
    notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lecture_id BIGINT NULL,
    author_id BIGINT NOT NULL,
    notice_type ENUM('ANNOUNCEMENT', 'LECTURE') NOT NULL, 
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    view_count INT DEFAULT 0,
    pinned CHAR(1) DEFAULT 'N',
    is_deleted CHAR(1) DEFAULT 'N',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (lecture_id) REFERENCES lecture(lecture_id),
    FOREIGN KEY (author_id) REFERENCES `user`(user_id)
);

INSERT INTO notice
(lecture_id, author_id, notice_type, title, content)
VALUES
(NULL, 1, 'ANNOUNCEMENT', '시스템 점검 안내',
 '이번 주 일요일 02:00~04:00 서버 점검이 예정되어 있습니다.'),
(NULL, 1, 'ANNOUNCEMENT', '학사 일정 공지',
 '2026학년도 1학기 수강신청 일정은 다음과 같습니다.'),
(NULL, 1, 'ANNOUNCEMENT', '휴강 안내',
 '기상 악화로 인해 금일 전체 휴강입니다.'),
(NULL, 1, 'ANNOUNCEMENT', 'LMS 이용 안내',
 '강의 자료 및 과제 제출은 LMS를 통해 진행됩니다.'),
(NULL, 1, 'ANNOUNCEMENT', '비밀번호 변경 권장',
 '개인정보 보호를 위해 비밀번호를 주기적으로 변경해 주세요.'),
(NULL, 1, 'ANNOUNCEMENT', '모바일 접속 안내',
 '모바일 환경에서도 LMS 이용이 가능합니다.'),
(NULL, 1, 'ANNOUNCEMENT', '이메일 인증 안내',
 '원활한 서비스 이용을 위해 이메일 인증을 완료해 주세요.'),
(NULL, 1, 'ANNOUNCEMENT', '강의 평가 일정',
 '강의 평가는 학기 말에 진행됩니다.'),
(NULL, 1, 'ANNOUNCEMENT', '성적 공개 일정',
 '기말고사 이후 성적이 순차적으로 공개됩니다.'),
(NULL, 1, 'ANNOUNCEMENT', '서버 증설 안내',
 '서비스 안정화를 위해 서버 증설 작업을 완료했습니다.'),
(NULL, 1, 'ANNOUNCEMENT', '공지사항 이용 안내',
 '중요 공지는 상단에 노출됩니다.'),
(NULL, 1, 'ANNOUNCEMENT', '계정 도용 주의',
 '의심스러운 로그인 기록이 있으면 즉시 비밀번호를 변경하세요.'),
(NULL, 1, 'ANNOUNCEMENT', '브라우저 권장 환경',
 'Chrome 최신 버전 사용을 권장합니다.'),
(NULL, 1, 'ANNOUNCEMENT', '과제 제출 유의사항',
 '마감 시간 이후 제출된 과제는 인정되지 않습니다.'),
(NULL, 1, 'ANNOUNCEMENT', '출석 처리 기준',
 '출석은 강의 시작 시간 기준으로 처리됩니다.'),
(NULL, 1, 'ANNOUNCEMENT', 'FAQ 확인 안내',
 '자주 묻는 질문은 FAQ 메뉴를 참고해 주세요.'),
(NULL, 1, 'ANNOUNCEMENT', '공지사항 검색 기능 안내',
 '상단 검색창을 통해 공지사항을 검색할 수 있습니다.'),
(NULL, 1, 'ANNOUNCEMENT', '파일 업로드 제한 안내',
 '업로드 가능한 파일 크기는 최대 20MB입니다.'),
(NULL, 1, 'ANNOUNCEMENT', '개인정보 처리방침 변경',
 '개인정보 처리방침이 일부 변경되었습니다.'),
(NULL, 1, 'ANNOUNCEMENT', '학기 종료 안내',
 '이번 학기 LMS 서비스는 종강 이후에도 일정 기간 유지됩니다.');
 
 INSERT INTO notice
(lecture_id, author_id, notice_type, title, content)
VALUES
(158, 2, 'LECTURE', '1주차 강의 안내',
 '1주차 강의는 오리엔테이션으로 진행됩니다.'),

(159, 2, 'LECTURE', '과제 제출 공지',
 '1차 과제는 다음 주 금요일까지 제출하세요.'),

(160, 2, 'LECTURE', '중간고사 범위 안내',
 '중간고사는 1~6주차 내용이 범위입니다.'),

(161, 2, 'LECTURE', '보강 수업 안내',
 '휴강된 수업은 다음 주 토요일에 보강합니다.'),

(162, 2, 'LECTURE', '팀 프로젝트 안내',
 '팀 프로젝트 주제 및 제출 방식은 강의자료를 참고하세요.'),

(163, 2, 'LECTURE', '출석 관련 공지',
 '출석은 LMS 로그인 기준으로 자동 체크됩니다.'),

(164, 2, 'LECTURE', '과제 피드백 공개',
 '1차 과제 피드백이 업로드되었습니다.'),

(165, 2, 'LECTURE', '기말고사 일정',
 '기말고사는 6월 마지막 주에 진행됩니다.'),

(166, 2, 'LECTURE', '강의자료 업로드',
 '오늘 수업 자료를 업로드했습니다.'),

(167, 2, 'LECTURE', '추가 자료 안내',
 '심화 학습용 자료를 참고 바랍니다.'),

(168, 2, 'LECTURE', '실습 환경 안내',
 '실습에 필요한 개발 환경 설정 방법을 공지합니다.'),

(169, 2, 'LECTURE', '발표 일정 공지',
 '팀별 발표 일정은 다음 공지를 확인하세요.'),

(170, 2, 'LECTURE', '수업 종료 안내',
 '이번 주 수업을 마지막으로 강의가 종료됩니다.');