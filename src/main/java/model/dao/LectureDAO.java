package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.dto.LectureDTO;
import java.util.ArrayList;

import database.DBConnection;
import model.dto.LectureDTO;
import model.dto.LectureScheduleDTO;

public class LectureDAO {
	public static final LectureDAO instance = new LectureDAO();
	
	public static LectureDAO getInstance() {
		return instance;
	}
	
	public int getLectureCount() { // 개설된 강의 개수 (이름 중첩x)
		//TODO LectureDB에서 이름 중첩 안 되게 count(*) 가져와서 리턴해주기
		return 0;
	}
	
	public int getTotalLectureCount() { // 모든 강의 개수 (이름 중첩o)
		//TODO LectureDB에서 모든 강의 count(*) 가져와서 리턴해주기
		return 0;
	}
	
	public int getLectureFillRate() { // 정원/인원
		//TODO (LectureDB에 있는 모든 정원수) 나누기 (EnrollmentDB에서 status가 Enrolled상태인 모든 인원) 리턴
		return 0;
	}
	
	public int getLowFillRateLecture() { // 정원/인원이 50% 미만인 모든 강의 수
		//TODO (LectureDB에 있는 정원 수)나누기(해당 Lecture의 Enrollment 수)<50의 수 모두 가져와서 리턴해주기
		return 0;
	}
	
	public int getTotalLectureCapacity() { // 모든 정원 수
		//TODO LectureDB에 있는 모든 정원 수 더해서 리턴하기
		return 0;
	}
	
	public int getTotalEnrollment() { // 모든 수강 인원 수
		//TODO usersDB 에서 role이 student 인 모든 인원 수 중 status가 active인 모든 인원 수 리턴
		return 0;
	}
	
	public int getLectureRequestCount() { // 강의 개설 요청 총 수
		//TODO 강의 개설 요청상태를 알리는 컬럼부터 고민
		return 0;
	}
	
	
	// 교수 담당 강의 목록 조회
    public List<LectureDTO> selectLecturesByProfessor(
            Connection conn, int professorId) throws SQLException {

        String sql = """
            SELECT
                lecture_id,
                lecture_title,
                lecture_round,
                start_date,
                end_date,
                room,
                capacity,
                created_at,
                updated_at
            FROM lectures
            WHERE professor_id = ?
            ORDER BY start_date DESC
        """;

        List<LectureDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, professorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LectureDTO lecture = new LectureDTO();
                    lecture.setLectureId(rs.getInt("lecture_id"));
                    lecture.setLectureTitle(rs.getString("lecture_title"));
                    lecture.setLectureRound(rs.getInt("lecture_round"));
                    lecture.setStartDate(rs.getDate("start_date").toLocalDate());
                    lecture.setEndDate(rs.getDate("end_date").toLocalDate());
                    lecture.setRoom(rs.getString("room"));
                    lecture.setCapacity(rs.getInt("capacity"));
                    lecture.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    lecture.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                    list.add(lecture);
                }
            }
        }
        return list;
    }
	public void setLectureValidation(String validation, Long lectureId) { // 강의 개설 상태 업데이트
		
		String sql = "UPDATE lecture SET validation = ? WHERE lecture_id = ?;";
		
		try (	Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);	){
			
			pstmt.setString(1, validation);
			pstmt.setLong(2, lectureId);
			
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("setLectureValidation() 예외 발생");
			e.printStackTrace();
		}
	}
		
	
	
	
	
	
	
	
}
