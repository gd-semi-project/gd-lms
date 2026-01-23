package model.enumtype;

public enum ScheduleCode {
	LECTURE_OPEN_REQUEST("강의 개설 신청"),              // 강의개설 신청(강사/학과) -써야 됨 INSTRUCTOR
	LECTURE_OPEN_REVIEW_DEPT("강의 개설 학과 검토"),        	// 강의개설 검토(학과)	-무시
	LECTURE_OPEN_REVISION_WINDOW("강의계획서 수정 기간"),      // 강의개설 변경/보완 요청	-무시
	LECTURE_OPEN_APPROVAL_ADMIN("강의 개설 교무 승인"),       // 강의개설 승인·반려(교무) -써야 됨 ADMIN
	INSTRUCTOR_ASSIGN_CONFIRM("강사 배정 확정"),        // 강사 배정 확정	-무시
	SECTION_QUOTA_CONFIRM("분반 정원 확정"),        // 분반/정원 확정	-무시
	TIMETABLE_BUILD_DEPT("시간표 편성"),        // 시간표 편성		-무시
	CLASSROOM_ASSIGN_ANNOUNCE("강의실 배정 안내"),    // 강의실 배정 공지	-무시
	CLASSROOM_ASSIGN_FINAL("강의실 배정 확정"),      // 강의실 최종 배정	-무시
	SYLLABUS_FINAL_INPUT("강의계획서 최종 입력"),    // 강의계획서 최종 입력	-무시
	LMS_COURSE_SPACE_AUTO_CREATE("LMS 강의공간 생성"),     // LMS 강의공간 자동 생성  -써야 됨 INSTRUCTOR(강의자료,과제등록제출공간, 출결관리영역, 성적입력UI 등)
	COURSE_REG_FRESHMAN("수강신청 (신입생)"),           	// 신입생 수강신청  -써야 됨	STUDENT
	COURSE_REG_ENROLLED("수강신청 (재학생)"),         // 재학생 수강신청 	-써야 됨	STUDENT
	COURSE_ADD_DROP("수강신청 변경/취소"),          // 수강신청 정정기간	-써야 됨	STUDENT
	SEMESTER_START("학기 시작"),         // 개강		-써야 됨 ADMIN
	CLASS_DAYS("수업 기간"),      	 	// 수업		-무시
	SEMESTER_END("학기 종료"),         // 종강		 -써야 됨	ADMIN
	MIDTERM_EXAM("중간고사"),          // 중간고사 기간	 -무시
	FINAL_EXAM("기말고사"),        // 기말고사 기간 -무시
	MIDTERM_GRADE_APPEAL("중간성적 이의신청"),          // 중간고사 성적 열람/이의신청	 -써야 됨	STUDENT
	FINAL_GRADE_APPEAL("기말성적 이의신청"),      // 성적 열람/이의신청		 -써야 됨		STUDENT
	GRADE_INPUT_INSTRUCTOR("성적 입력 기간");        // 성적 입력기간(교강사)	 -써야 됨  INSTRUCTOR
	
	private final String label;
	
	private ScheduleCode(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	
	
}
