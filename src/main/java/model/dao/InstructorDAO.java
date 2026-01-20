package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.dto.InstructorDTO;

public class InstructorDAO {

    private static final InstructorDAO instance = new InstructorDAO();

    private InstructorDAO() {}

    public static InstructorDAO getInstance() {
        return instance;
    }

    // 강사 정보 조회
    public InstructorDTO selectInstructorInfo(Connection conn, long userId)
            throws SQLException {

        String sql = """
            SELECT
                user_id,
                instructor_no,
                department,
                office_room,
                office_phone,
                hire_date,
                created_at,
                updated_at
            FROM instructor
            WHERE user_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                InstructorDTO instructor = new InstructorDTO();

                instructor.setUserId(rs.getLong("user_id"));
                instructor.setInstructorNo(rs.getString("instructor_no"));
                instructor.setDepartment(rs.getString("department"));
                instructor.setOfficeRoom(rs.getString("office_room"));
                instructor.setOfficePhone(rs.getString("office_phone"));
                instructor.setHireDate(
                    rs.getDate("hire_date") != null
                        ? rs.getDate("hire_date").toLocalDate()
                        : null
                );
                instructor.setCreatedAt(
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                instructor.setUpdatedAt(
                    rs.getTimestamp("updated_at").toLocalDateTime()
                );

                return instructor;
            }
        }
    }
}