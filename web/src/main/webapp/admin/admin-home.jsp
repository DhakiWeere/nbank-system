<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<html>
<head>
  <title>Admin Home</title>
</head>
<body>
<h2>Welcome, Admin</h2>

<%
  String username = (String) session.getAttribute("username");
  if (username != null) {
%>
<p>Logged in as: <b><%= username %></b></p>
<%
  }
%>

<h3>System Overview</h3>
<p>Total Users: ${userCount}</p>
<p>Total Accounts: ${accountCount}</p>
<p>Pending Transfers: ${pendingTransfers}</p>

<a href="logout">Logout</a>
<script src="js/banking_system_script.js"></script>
</body>
</html>
