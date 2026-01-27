package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.dto.StudentInfoUpdateRequestDTO;

public class StudentInfoUpdateRequestDAO {
	private static final StudentInfoUpdateRequestDAO instance =
            new StudentInfoUpdateRequestDAO();

    public static StudentInfoUpdateRequestDAO getInstance() {
        return instance;
    }

    private StudentInfoUpdateRequestDAO() {}
    
    
    private static final String INSERT_SQL =
            "INSERT INTO student_info_update_request ("
          + " student_id, new_name, new_gender, new_account_no, "
          + " new_department_id, new_academic_status, reason "
          + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        public Long insert(StudentInfoUpdateRequestDTO dto) {

            try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                    conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)
            ) {
                ps.setLong(1, dto.getStudentId());
                ps.setString(2, dto.getNewName());
                ps.setString(3,
                    dto.getNewGender() != null ? dto.getNewGender().name() : null);
                ps.setString(4, dto.getNewAccountNo());

                if (dto.getNewDepartmentId() != null) {
                    ps.setLong(5, dto.getNewDepartmentId());
                } else {
                    ps.setNull(5, java.sql.Types.BIGINT);
                }

                ps.setString(6,
                    dto.getNewAcademicStatus() != null
                        ? dto.getNewAcademicStatus().name()
                        : null);

                ps.setString(7, dto.getReason());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1); // request_id
                    }
                }

                throw new RuntimeException("request_id 생성 실패");

            } catch (Exception e) {
                throw new RuntimeException("학생 정보 변경 요청 저장 실패", e);
            }
        }
    
    public int countPending() {
        String sql = """
                SELECT COUNT(*)
                FROM student_info_update_request
                WHERE status = 'PENDING'
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    	
    }

    public List<StudentInfoUpdateRequestDTO> selectPendingList() {
        String sql = """
            SELECT
                request_id,
                student_id,
                new_name,
                new_gender,
                new_account_no,
                new_department_id,
                new_academic_status,
                reason,
                status,
                created_at
            FROM student_info_update_request
            WHERE status = 'PENDING'
            ORDER BY request_id DESC
        """;

        List<StudentInfoUpdateRequestDTO> list = new ArrayList<StudentInfoUpdateRequestDTO>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StudentInfoUpdateRequestDTO dto = new StudentInfoUpdateRequestDTO();
                dto.setRequestId(rs.getLong("request_id"));
                dto.setStudentId(rs.getLong("student_id"));

                dto.setNewName(rs.getString("new_name"));

                String g = rs.getString("new_gender");
                if (g != null && !g.isBlank()) {
                    try { dto.setNewGender(model.enumtype.Gender.valueOf(g)); }
                    catch (Exception ignore) { dto.setNewGender(null); }
                }

                dto.setNewAccountNo(rs.getString("new_account_no"));

                long dep = rs.getLong("new_department_id");
                if (!rs.wasNull()) dto.setNewDepartmentId(dep);

                String st = rs.getString("new_academic_status");
                if (st != null && !st.isBlank()) {
                    try { dto.setNewAcademicStatus(model.enumtype.StudentStatus.valueOf(st)); }
                    catch (Exception ignore) { dto.setNewAcademicStatus(null); }
                }

                dto.setReason(rs.getString("reason"));
                dto.setCreatedAt(rs.getString("created_at"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public StudentInfoUpdateRequestDTO selectById(Long requestId) {
        String sql = """
            SELECT
                request_id,
                student_id,
                new_name,
                new_gender,
                new_account_no,
                new_department_id,
                new_academic_status,
                reason,
                created_at
            FROM student_info_update_request
            WHERE request_id = ?
            LIMIT 1
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StudentInfoUpdateRequestDTO dto = new StudentInfoUpdateRequestDTO();
                    dto.setRequestId(rs.getLong("request_id"));
                    dto.setStudentId(rs.getLong("student_id"));
                    dto.setNewName(rs.getString("new_name"));

                    String g = rs.getString("new_gender");
                    if (g != null && !g.isBlank()) {
                        try { dto.setNewGender(model.enumtype.Gender.valueOf(g)); } catch (Exception ignore) {}
                    }

                    dto.setNewAccountNo(rs.getString("new_account_no"));

                    long dep = rs.getLong("new_department_id");
                    if (!rs.wasNull()) dto.setNewDepartmentId(dep);

                    String st = rs.getString("new_academic_status");
                    if (st != null && !st.isBlank()) {
                        try { dto.setNewAcademicStatus(model.enumtype.StudentStatus.valueOf(st)); } catch (Exception ignore) {}
                    }

                    dto.setReason(rs.getString("reason"));
                    dto.setCreatedAt(rs.getString("created_at"));
                    return dto;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	public void markCompleted(Connection conn, Long requestId) {
		 String sql = """
			        UPDATE student_info_update_request
			           SET status = 'COMPLETED'
			         WHERE request_id = ?
			    """;
			    try (PreparedStatement ps = conn.prepareStatement(sql)) {
			        ps.setLong(1, requestId);
			        ps.executeUpdate();
			    } catch (Exception e ) {
			    	e.printStackTrace();
			    	System.out.println("markCompleted(): 실패");
			    }
	}

}
	