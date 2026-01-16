package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import database.DBConnection;
import model.dto.LectureDTO;
import model.dto.LectureRequestDTO;
import model.dto.LectureScheduleDTO;
import model.enumtype.LectureValidation;
import model.enumtype.Week;

public class EnrollmentDAO {
	public static final EnrollmentDAO instance = new EnrollmentDAO();
	
	public static EnrollmentDAO getInstance() {
		return instance;
	}
	
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

}
