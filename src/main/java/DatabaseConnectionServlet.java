import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/DatabaseConnectionServlet")
public class DatabaseConnectionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private String jdbcURL = "jdbc:mysql://localhost:3306/yourdatabase"; // Change to your DB name
    private String jdbcUsername = "root"; // Change to your DB username
    private String jdbcPassword = "root"; // Change to your DB password

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection connection = null;

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

            if (connection != null) {
                out.println("<h3>Database Connection Successful!</h3>");
            } else {
                out.println("<h3>Database Connection Failed!</h3>");
            }
        } catch (ClassNotFoundException e) {
            out.println("<h3>MySQL JDBC Driver not found!</h3>");
            e.printStackTrace(out);
        } catch (SQLException e) {
            out.println("<h3>Error while connecting to the database: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(out);
            }
        }
    }
}
