package service;

import java.util.List;

import model.dao.DepartmentDAO;
import model.dto.DepartmentDTO;

public class DepartmentService {

	private DepartmentDAO departmentDAO = DepartmentDAO.getInstance();

    public List<DepartmentDTO> getAllDepartments() {
        return departmentDAO.getDepartmentList();
    }
}
