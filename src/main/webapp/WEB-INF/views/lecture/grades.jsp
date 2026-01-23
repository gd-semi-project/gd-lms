<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="role" value="${sessionScope.AccessInfo.role}" />

<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<h3 class="mb-4">📝 성적</h3>

<!-- =========================
     👩‍🎓 학생 화면
========================= -->
<c:if test="${role eq 'STUDENT'}">

    <div class="card w-50">
        <div class="card-body">

            <table class="table table-bordered text-center">
                <tr>
                    <th>중간고사</th>
                    <td>85</td>
                </tr>
                <tr>
                    <th>기말고사</th>
                    <td>90</td>
                </tr>
                <tr>
                    <th>과제</th>
                    <td>95</td>
                </tr>
                <tr>
                    <th>출석</th>
                    <td>100</td>
                </tr>
                <tr class="table-light fw-bold">
                    <th>총점</th>
                    <td>92</td>
                </tr>
                <tr>
                    <th>등급</th>
                    <td>
                        <span class="badge bg-success fs-6">A</span>
                    </td>
                </tr>
            </table>

        </div>
    </div>

</c:if>

<!-- =========================
     👨‍🏫 교수 화면
========================= -->
<c:if test="${role eq 'INSTRUCTOR'}">

    <table class="table table-bordered text-center align-middle">
        <thead class="table-light">
            <tr>
                <th>학번</th>
                <th>이름</th>
                <th>중간</th>
                <th>기말</th>
                <th>과제</th>
                <th>출석</th>
                <th>총점</th>
                <th>등급</th>
                <th>비고</th>
            </tr>
        </thead>
        <tbody>

            <!-- 하드코딩 데이터 -->
            <tr>
                <td>20260001</td>
                <td>김철수</td>
                <td>80</td>
                <td>85</td>
                <td>90</td>
                <td>100</td>
                <td>88</td>
                <td><span class="badge bg-primary">B+</span></td>
                <td>-</td>
            </tr>

            <tr>
                <td>20260002</td>
                <td>이영희</td>
                <td>90</td>
                <td>95</td>
                <td>92</td>
                <td>100</td>
                <td>94</td>
                <td><span class="badge bg-success">A</span></td>
                <td>우수</td>
            </tr>

            <tr>
                <td>20260003</td>
                <td>이지훈</td>
                <td>70</td>
                <td>75</td>
                <td>80</td>
                <td>90</td>
                <td>79</td>
                <td><span class="badge bg-warning text-dark">C+</span></td>
                <td>-</td>
            </tr>

        </tbody>
    </table>

</c:if>