<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<div class="container my-4">

  <form method="post" action="${pageContext.request.contextPath}/keron/time/apply">
    <div class="card shadow-sm">
      <div class="card-body">

        <!-- 이동할 시간 선택 -->
        <div class="fw-semibold mb-2">시간 이동</div>

        <div class="row g-3">
          <!-- 년도 -->
          <div class="col-12 col-md-4">
            <label class="form-label" for="keronYear">년도</label>
            <select class="form-select" id="keronYear" name="year" required>
              <option value="" selected disabled>선택</option>
              <option value="2024">2024</option>
              <option value="2025">2025</option>
              <option value="2026">2026</option>
              <option value="2027">2027</option>
            </select>
          </div>

          <!-- 월 -->
          <div class="col-6 col-md-2">
            <label class="form-label" for="keronMonth">월</label>
            <select class="form-select" id="keronMonth" name="month" required>
              <option value="" selected disabled>선택</option>
              <option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option>
              <option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="12">12</option>
            </select>
          </div>

          <!-- 일 -->
          <div class="col-6 col-md-2">
            <label class="form-label" for="keronDay">일</label>
            <select class="form-select" id="keronDay" name="day" required>
              <option value="" selected disabled>선택</option>
              <option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option><option value="7">7</option>
              <option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="12">12</option><option value="13">13</option><option value="14">14</option>
              <option value="15">15</option><option value="16">16</option><option value="17">17</option><option value="18">18</option><option value="19">19</option><option value="20">20</option><option value="21">21</option>
              <option value="22">22</option><option value="23">23</option><option value="24">24</option><option value="25">25</option><option value="26">26</option><option value="27">27</option><option value="28">28</option>
              <option value="29">29</option><option value="30">30</option><option value="31">31</option>
            </select>
          </div>

          <!-- 시간(24h) -->
          <div class="col-6 col-md-2">
            <label class="form-label" for="keronHour">시간(24)</label>
            <select class="form-select" id="keronHour" name="hour" required>
              <option value="" selected disabled>선택</option>
              <option value="0">0</option><option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5">5</option>
              <option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option>
              <option value="12">12</option><option value="13">13</option><option value="14">14</option><option value="15">15</option><option value="16">16</option><option value="17">17</option>
              <option value="18">18</option><option value="19">19</option><option value="20">20</option><option value="21">21</option><option value="22">22</option><option value="23">23</option>
            </select>
          </div>

          <!-- 분 -->
          <div class="col-6 col-md-2">
            <label class="form-label" for="keronMinute">분</label>
            <select class="form-select" id="keronMinute" name="minute" required>
              <option value="" selected disabled>선택</option>
              <%-- 0~59 --%>
              <%
                for (int i = 0; i <= 59; i++) {
              %>
                <option value="<%=i%>"><%=i%></option>
              <%
                }
              %>
            </select>
          </div>
        </div>

        <div class="d-flex gap-2 mt-4">
          <button type="submit" class="btn btn-primary">적용</button>
          <button type="button" class="btn btn-outline-secondary" disabled>복원</button>
        </div>

      </div>
    </div>
  </form>

</div>

