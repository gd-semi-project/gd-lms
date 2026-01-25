package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import model.dto.LectureRequestDTO;
import model.dto.RoomDTO;
import model.enumtype.LectureStatus;
import model.enumtype.LectureValidation;

public class LectureRequestDAO {

    private static final LectureRequestDAO instance =
        new LectureRequestDAO();

    private LectureRequestDAO() {}

    public static LectureRequestDAO getInstance() {
        return instance;
    }

    /* ==================================================
     * 1. 강사별 강의 개설 신청 목록
     * ================================================== */
    public List<LectureRequestDTO> selectByInstructor(
            Connection conn,
            Long instructorId
    ) throws SQLException {

        String sql = """
            SELECT
                lecture_id,
                lecture_title,
                start_date,
                end_date,
                section,
                capacity,
                room,
                validation,
                status,
                created_at
            FROM lecture
            WHERE user_id = ?
              AND validation IN ('PENDING','CONFIRMED')
            ORDER BY created_at DESC
        """;

        List<LectureRequestDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, instructorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LectureRequestDTO dto = new LectureRequestDTO();
                    dto.setLectureId(rs.getLong("lecture_id"));
                    dto.setLectureTitle(rs.getString("lecture_title"));
                    dto.setStartDate(
                        rs.getDate("start_date").toLocalDate()
                    );
                    dto.setEndDate(
                        rs.getDate("end_date").toLocalDate()
                    );
                    dto.setSection(rs.getString("section"));
                    dto.setCapacity(rs.getInt("capacity"));
                    dto.setRoom(rs.getString("room"));
                    dto.setValidation(
                        LectureValidation.valueOf(
                            rs.getString("validation")
                        )
                    );
                    dto.setStatus(
                        LectureStatus.valueOf(
                            rs.getString("status")
                        )
                    );
                    dto.setCreatedAt(
                        rs.getTimestamp("created_at")
                          .toLocalDateTime()
                    );
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /* ==================================================
     * 2. 강의 개설 신청 단건 조회
     * ================================================== */
    public LectureRequestDTO selectByLectureId(
            Connection conn,
            Long lectureId
    ) throws SQLException {

        String sql = """
            SELECT
                lecture_id,
                lecture_title,
                lecture_round,
                start_date,
                end_date,
                room,
                capacity,
                section,
                validation
            FROM lecture
            WHERE lecture_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                LectureRequestDTO dto = new LectureRequestDTO();
                dto.setLectureId(rs.getLong("lecture_id"));
                dto.setLectureTitle(rs.getString("lecture_title"));
                dto.setLectureRound(rs.getInt("lecture_round"));
                dto.setStartDate(
                    rs.getDate("start_date").toLocalDate()
                );
                dto.setEndDate(
                    rs.getDate("end_date").toLocalDate()
                );
                dto.setRoom(rs.getString("room"));
                dto.setCapacity(rs.getInt("capacity"));
                dto.setSection(rs.getString("section"));
                dto.setValidation(
                    LectureValidation.valueOf(
                        rs.getString("validation")
                    )
                );
                return dto;
            }
        }
    }

    /* ==================================================
     * 3. 강의 개설 신청 insert (lecture)
     * ================================================== */
    public Long insertLecture(
            Connection conn,
            Long instructorId,
            HttpServletRequest request
    ) throws SQLException {

        Long departmentId =
            findDepartmentIdByInstructor(conn, instructorId);

        String sql = """
            INSERT INTO lecture (
                lecture_title,
                lecture_round,
                user_id,
                department_id,
                start_date,
                end_date,
                room,
                capacity,
                section,
                validation
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')
        """;

        try (PreparedStatement pstmt =
                 conn.prepareStatement(
                     sql,
                     PreparedStatement.RETURN_GENERATED_KEYS
                 )) {

            pstmt.setString(1,
                request.getParameter("lectureTitle"));
            pstmt.setInt(2,
                Integer.parseInt(
                    request.getParameter("lectureRound")
                ));
            pstmt.setLong(3, instructorId);
            pstmt.setLong(4, departmentId);
            pstmt.setDate(5,
                java.sql.Date.valueOf(
                    request.getParameter("startDate")
                ));
            pstmt.setDate(6,
                java.sql.Date.valueOf(
                    request.getParameter("endDate")
                ));
            pstmt.setString(7,
                request.getParameter("room"));
            pstmt.setInt(8,
                Integer.parseInt(
                    request.getParameter("capacity")
                ));
            pstmt.setString(9,
                request.getParameter("section"));

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("lecture_id 생성 실패");
    }

    /* ==================================================
     * 4. 강의 요일/시간 insert
     * ================================================== */
    public void insertSchedule(
            Connection conn,
            Long lectureId,
            HttpServletRequest request
    ) throws SQLException {

        String sql = """
            INSERT INTO lecture_schedule (
                lecture_id,
                week_day,
                start_time,
                end_time
            ) VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

            pstmt.setLong(1, lectureId);
            pstmt.setString(2,
                request.getParameter("weekDay"));
            pstmt.setTime(3,
                java.sql.Time.valueOf(
                    request.getParameter("startTime") + ":00"
                ));
            pstmt.setTime(4,
                java.sql.Time.valueOf(
                    request.getParameter("endTime") + ":00"
                ));
            pstmt.executeUpdate();
        }
    }

    /* ==================================================
     * 5. 강의 기본 정보 수정
     * ================================================== */
    public void updateLecture(
            Connection conn,
            Long lectureId,
            HttpServletRequest request
    ) throws SQLException {

        String sql = """
            UPDATE lecture
            SET
                lecture_title = ?,
                lecture_round = ?,
                start_date = ?,
                end_date = ?,
                room = ?,
                capacity = ?,
                section = ?
            WHERE lecture_id = ?
        """;

        try (PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

            pstmt.setString(1,
                request.getParameter("lectureTitle"));
            pstmt.setInt(2,
                Integer.parseInt(
                    request.getParameter("lectureRound")
                ));
            pstmt.setDate(3,
                java.sql.Date.valueOf(
                    request.getParameter("startDate")
                ));
            pstmt.setDate(4,
                java.sql.Date.valueOf(
                    request.getParameter("endDate")
                ));
            pstmt.setString(5,
                request.getParameter("room"));
            pstmt.setInt(6,
                Integer.parseInt(
                    request.getParameter("capacity")
                ));
            pstmt.setString(7,
                request.getParameter("section"));
            pstmt.setLong(8, lectureId);

            pstmt.executeUpdate();
        }
    }

    /* ==================================================
     * 6. 강의 상태(validation) 조회
     * ================================================== */
    public LectureValidation getValidation(
            Connection conn,
            Long lectureId
    ) throws SQLException {

        String sql =
            "SELECT validation FROM lecture WHERE lecture_id = ?";

        try (PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

            pstmt.setLong(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return LectureValidation.valueOf(
                        rs.getString("validation")
                    );
                }
            }
        }
        throw new SQLException("강의 상태 조회 실패");
    }

    /* ==================================================
     * 7. 강의 개설 신청 삭제
     * ================================================== */
    public void deleteLecture(
            Connection conn,
            Long lectureId
    ) throws SQLException {

        try (PreparedStatement pstmt1 =
                 conn.prepareStatement(
                     "DELETE FROM lecture_schedule WHERE lecture_id = ?"
                 )) {
            pstmt1.setLong(1, lectureId);
            pstmt1.executeUpdate();
        }

        try (PreparedStatement pstmt2 =
                 conn.prepareStatement(
                     "DELETE FROM lecture WHERE lecture_id = ?"
                 )) {
            pstmt2.setLong(1, lectureId);
            pstmt2.executeUpdate();
        }
    }

    /* ==================================================
     * 내부 유틸 – 강사 소속 학과 조회
     * ================================================== */
    private Long findDepartmentIdByInstructor(
            Connection conn,
            Long instructorId
    ) throws SQLException {

        String sql = """
            SELECT department_id
            FROM instructor
            WHERE user_id = ?
        """;

        try (PreparedStatement pstmt =
                 conn.prepareStatement(sql)) {

            pstmt.setLong(1, instructorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("department_id");
                }
            }
        }
        throw new SQLException(
            "강사의 department_id를 찾을 수 없습니다."
        );
    }
    
    
    public List<RoomDTO> selectAllRooms(Connection conn) throws Exception {

        String sql = """
            SELECT room_id, room_code, room_name, capacity
            FROM room
            WHERE room_type = 'LECTURE'
            ORDER BY room_code
        """;

        List<RoomDTO> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                RoomDTO dto = new RoomDTO();
                dto.setRoomId(rs.getLong("room_id"));
                dto.setRoomCode(rs.getString("room_code"));
                dto.setRoomName(rs.getString("room_name"));
                dto.setCapacity(rs.getInt("capacity"));
                list.add(dto);
            }
        }
        return list;
    }
}