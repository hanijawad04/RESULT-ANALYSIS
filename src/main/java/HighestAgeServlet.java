import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/HighestAgeServlet")
public class HighestAgeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private String jdbcURL = "jdbc:mysql://localhost:3306/yourdatabase"; // Change to your DB name
    private String jdbcUsername = "root"; // Change to your DB username
    private String jdbcPassword = "root"; // Change to your DB password

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC Driver

            // Query to find the highest age in the table
            String sql = "SELECT MAX(Age) AS maxAge FROM yourTableName"; // Replace 'yourTableName' with your table

            try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int highestAge = resultSet.getInt("maxAge");
                    out.println("<h3>The highest age in the table is: " + highestAge + "</h3>");
                } else {
                    out.println("<h3>No data found in the table.</h3>");
                }
            } catch (SQLException e) {
                out.println("<h3>Error while retrieving the highest age: " + e.getMessage() + "</h3>");
                e.printStackTrace(out);
            }
        } catch (ClassNotFoundException e) {
            out.println("<h3>MySQL JDBC Driver not found!</h3>");
            e.printStackTrace(out);
        }
    }
}
