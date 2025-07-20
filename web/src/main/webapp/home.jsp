<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Home - User Dashboard</title>
</head>
<body>
<h2>Welcome to Your Dashboard</h2>

<p>User: <b>${user.name}</b> (NIC: <b>${user.nic}</b>)</p>

<h3>Your Accounts</h3>
<c:choose>
  <c:when test="${not empty accounts}">
    <ul>
      <c:forEach var="acc" items="${accounts}">
        <li>
          Account No: ${acc.accNo}, Type: ${acc.type}, Balance: ${acc.balance}
        </li>
      </c:forEach>
    </ul>
  </c:when>
  <c:otherwise>
    <p>No accounts found.</p>
  </c:otherwise>
</c:choose>

<a href="logout">Logout</a>

<script src="js/banking_system_script.js"></script>
</body>
</html>
