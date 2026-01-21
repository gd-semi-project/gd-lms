package model.enumtype;

public enum ScheduleCode {
	LECTURE_OPEN_REQUEST,              // 강의개설 신청(강사/학과) -써야 됨 INSTRUCTOR
	LECTURE_OPEN_REVIEW_DEPT,        	// 강의개설 검토(학과)	-무시
	LECTURE_OPEN_REVISION_WINDOW,      // 강의개설 변경/보완 요청	-무시
	LECTURE_OPEN_APPROVAL_ADMIN,       // 강의개설 승인·반려(교무) -써야 됨 ADMIN
	INSTRUCTOR_ASSIGN_CONFIRM ,        // 강사 배정 확정	-무시
	SECTION_QUOTA_CONFIRM     ,        // 분반/정원 확정	-무시
	TIMETABLE_BUILD_DEPT      ,        // 시간표 편성		-무시
	CLASSROOM_ASSIGN_ANNOUNCE    ,     // 강의실 배정 공지	-무시
	CLASSROOM_ASSIGN_FINAL      ,      // 강의실 최종 배정	-무시
	SYLLABUS_FINAL_INPUT           ,    // 강의계획서 최종 입력	-무시
	LMS_COURSE_SPACE_AUTO_CREATE  ,     // LMS 강의공간 자동 생성  -써야 됨 INSTRUCTOR(강의자료,과제등록제출공간, 출결관리영역, 성적입력UI 등)
	COURSE_REG_FRESHMAN  ,           	// 신입생 수강신청  -써야 됨	STUDENT
	COURSE_REG_ENROLLED     ,          // 재학생 수강신청 	-써야 됨	STUDENT
	COURSE_ADD_DROP         ,          // 수강신청 정정기간	-써야 됨	STUDENT
	SEMESTER_START           ,         // 개강		-써야 됨 ADMIN
	CLASS_DAYS      		,      	 	// 수업		-무시
	SEMESTER_END             ,         // 종강		 -써야 됨	ADMIN
	MIDTERM_EXAM            ,          // 중간고사 기간	 -무시
	FINAL_EXAM               ,         // 기말고사 기간 -무시
	MIDTERM_GRADE_APPEAL    ,          // 중간고사 성적 열람/이의신청	 -써야 됨	STUDENT
	FINAL_GRADE_APPEAL       ,         // 성적 열람/이의신청		 -써야 됨		STUDENT
	GRADE_INPUT_INSTRUCTOR            // 성적 입력기간(교강사)	 -써야 됨  INSTRUCTOR
}
