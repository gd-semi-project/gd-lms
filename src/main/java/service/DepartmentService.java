package service;

import java.sql.Connection;
import java.util.List;

import database.DBConnection;
import model.dao.DepartmentDAO;
import model.dao.EnrollmentDAO;
import model.dao.LectureDAO;
import model.dto.DepartmentDTO;
import model.dto.EnrollmentDTO;
import model.dto.LectureForEnrollDTO;

public class DepartmentService {

	private DepartmentDAO departmentDAO = DepartmentDAO.getInstance();

    public List<DepartmentDTO> getAllDepartments() {
        return departmentDAO.getDepartmentList();
    }
}
