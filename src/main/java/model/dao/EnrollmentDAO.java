package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.dto.LectureDTO;
import model.dto.LectureRequestDTO;
import model.dto.LectureScheduleDTO;
import model.enumtype.LectureValidation;
import model.enumtype.Week;

public class EnrollmentDAO {
	
	// 싱글톤 패턴
	public static final EnrollmentDAO instance = new EnrollmentDAO();
	public static EnrollmentDAO getInstance() {
		return instance;
	}
	
	
	// 강의 리스트 메소드
	public ArrayList<LectureRequestDTO> getLectureList(String validation){
		
		if (validation==null||validation.isEmpty()) validation = "CONFIRMED";
			
		
		String sql =  "		SELECT									"
					+ "			l.lecture_id    AS lectureId,		"
					+ "			l.lecture_title AS lectureTitle,	"
					+ "			l.section		AS section,			"
					+ "			l.capacity      AS capacity,		"
					+ "			l.validation    AS validation,		"
					+ "			l.created_at    AS createdAt,		"
					+ "			u.name		    AS instructorName,	"
					+ "  	GROUP_CONCAT(							"
					+ "		    CONCAT(								"
					+ "				s.week_day,						"
					+ "				' ',							"
					+ "				TIME_FORMAT(s.start_time, '%H:%i'),									"
					+ "				'~',																"
					+ "				TIME_FORMAT(s.end_time, '%H:%i')									"
					+ "			)																		"
					+ "			ORDER BY FIELD(s.week_day,'MON','TUE','WED','THU','FRI','SAT','SUN')	"
					+ "		    SEPARATOR ' <br> '															"
					+ "		    ) 				AS schedule			"
					+ "		FROM 									"
					+ "			lecture l							"
					+ "		JOIN lecture_schedule s					"
					+ "    		ON l.lecture_id = s.lecture_id		"
					+ "		JOIN user u								"
					+ "    		ON u.user_id = l.user_id			"
					+ "		WHERE l.validation = ?					"
					+ "		GROUP BY l.lecture_id, l.lecture_title, l.section, l.capacity, l.validation, l.created_at, u.name"
					+ "		ORDER BY								"
					+ "    		l.lecture_id,						"
					+ "    		l.section;							";
		
		ArrayList<LectureRequestDTO> list = new ArrayList<LectureRequestDTO>();
		
		//TODO 뭔가 정리 기준을 추가할 수 있는 기능 sql+="ORDER BY =?" 수정 및 추가
		
		
		try (	Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);	){
			
				pstmt.setString(1, validation);
			
			try(	ResultSet rs = pstmt.executeQuery()		){
				while (rs.next()) {
					LectureRequestDTO dto = new LectureRequestDTO();
					dto.setLectureId(rs.getLong("lectureId"));
					dto.setLectureTitle(rs.getString("lectureTitle"));
					dto.setSection(rs.getString("section"));
					dto.setSchedule(rs.getString("schedule"));
					dto.setCapacity(rs.getInt("capacity"));
					dto.setValidation(LectureValidation.valueOf(rs.getString("validation")));
					dto.setCreatedAt((rs.getTimestamp("createdAt").toLocalDateTime()));
					dto.setInstructorName(rs.getString("instructorName"));
					list.add(dto);
				}
			}
		} catch (Exception e) {
			System.out.println("getLectureList() 예외 발생");
			e.printStackTrace();
		}
				
		return list;
	}
	
	// 학생ID로 특정강의ID 이수 확인 메소드
	public boolean isStudentEnrolled(Connection conn, long userId, long lectureId) throws SQLException {
        String sql = 
            "SELECT COUNT(*) AS cnt " +
            "FROM enrollments " +
            "WHERE user_id = ? AND lecture_id = ? AND status = 'ACTIVE'";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, lectureId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt("cnt") > 0;
            }
        }
    }
	
	
	// 특정 학생이 듣는 모든 강의 리스트 메소드
	public List<Long> findEnrolledLectureIds(Connection conn, long userId) throws SQLException {
        String sql = 
            "SELECT lecture_id " +
            "FROM enrollments " +
            "WHERE user_id = ? AND status = 'ACTIVE'";
        
        List<Long> lectureIds = new ArrayList<>();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lectureIds.add(rs.getLong("lecture_id"));
                }
            }
        }
        
        return lectureIds;
    }
	
	// 특정 강의를 수강 중인 학생 수
	public int countStudentsByLecture(Connection conn, long lectureId) throws SQLException {
	       String sql = 
	           "SELECT COUNT(*) AS cnt " +
	           "FROM enrollments " +
	           "WHERE lecture_id = ? AND status = 'ACTIVE'";
	        
	       try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	           pstmt.setLong(1, lectureId);
	            
	           try (ResultSet rs = pstmt.executeQuery()) {
	               return rs.next() ? rs.getInt("cnt") : 0;
	           }
	       }
	   }
	

}
