<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container mt-4">
    <div class="row">
        <!-- 좌측 프로필 카드 -->
        <div class="col-md-4">
            <div class="card shadow-sm">
                <div class="card-body text-center">
                    <div class="mb-3">
                        <i class="bi bi-person-circle" style="font-size: 4rem;"></i>
                    </div>
                    <h5 class="card-title">${instructor.name}</h5>
                    <p class="text-muted mb-1">강사 ID</p>
                    <p class="fw-bold">${instructor.instructorNo}</p>
                </div>
            </div>
        </div>

        <!-- 우측 상세 정보 -->
        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-header bg-dark text-white">
                    <h6 class="mb-0">강사 정보</h6>
                </div>
                <div class="card-body">
                    <table class="table table-bordered align-middle mb-0">
                        <tbody>
                            <tr>
                                <th class="table-light" style="width: 30%;">이름</th>
                                <td>${instructor.name}</td>
                            </tr>
                            <tr>
                                <th class="table-light">이메일</th>
                                <td>${instructor.email}</td>
                            </tr>
                            <tr>
                                <th class="table-light">연락처</th>
                                <td>${instructor.phone}</td>
                            </tr>
                            <tr>
                                <th class="table-light">학과</th>
                                <td>${instructor.department}</td>
                            </tr>
                            <tr>
                                <th class="table-light">연구실</th>
                                <td>${instructor.officeRoom}</td>
                            </tr>
                            <tr>
                                <th class="table-light">연구실 전화</th>
                                <td>${instructor.officePhone}</td>
                            </tr>
                            <tr>
                                <tr>
								    <th class="table-light">임용일</th>
								    <td>${instructor.hireDate}</td>
								</tr>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <div class="card-footer text-end">
                    <a href="${pageContext.request.contextPath}/instructor/lectures"
                       class="btn btn-outline-primary btn-sm">
                        내 강의 보기
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>