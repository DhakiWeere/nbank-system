<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login User</title>
</head>
<body>
<h2>Login</h2>
<form action="j_security_check" method="post">
    <label for="username">Username:</label>
    <input type="text" name="j_username" id="username" required><br><br>

    <label for="password">Password:</label>
    <input type="password" name="j_password" id="password" required><br><br>

    <input type="submit" value="Login">
</form>
<script src="js/banking_system_script.js"></script>
</body>
</html>
