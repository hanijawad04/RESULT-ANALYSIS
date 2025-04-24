<!DOCTYPE html>
<html>
<head>
    <title>Reset Password</title>
</head>
<body>
    <h2>Reset Password</h2>
    <form action="ResetPasswordServlet" method="post">
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required><br>

        <label for="newpassword">New Password:</label>
        <input type="password" id="newpassword" name="newpassword" required><br>

        <input type="submit" value="Reset Password">
    </form>
</body>
</html>
