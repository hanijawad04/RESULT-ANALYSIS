import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@MultipartConfig
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private String jdbcURL = "jdbc:mysql://localhost:3306/yourdatabase"; // Change to your DB name
    private String jdbcUsername = "root"; // Change to your DB username
    private String jdbcPassword = "root"; // Change to your DB password

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC Driver
            out.println("<h3>Driver Loaded!</h3>");

            // Retrieve the table name from the form input
            String tableName = request.getParameter("tableName").trim();
            // Sanitize the table name
            tableName = tableName.replaceAll("[^a-zA-Z0-9_]", "_");

            Part filePart = request.getPart("file");
            String fileName = filePart.getSubmittedFileName();

            // Path where the file will be uploaded
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir(); // Create the directory if it does not exist
            }

            // Save the file to the specified path
            File file = new File(uploadDir, fileName);
            filePart.write(file.getAbsolutePath());

            // Create table and insert data from the uploaded file
            createTableAndInsertData(file, tableName, out);

        } catch (ClassNotFoundException e) {
            out.println("<h3>MySQL JDBC Driver not found!</h3>");
            e.printStackTrace(out);
        } catch (IOException e) {
            out.println("<h3>Error reading the file: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        }
    }

    private void createTableAndInsertData(File file, String tableName, PrintWriter out) {
        try (BufferedReader br = new BufferedReader(new FileReader(file));
             Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             Statement statement = connection.createStatement()) {

            // Read the first line to get column names
            String firstLine = br.readLine();
            if (firstLine == null) {
                out.println("<h3>File is empty!</h3>");
                return;
            }

            String[] columns = firstLine.split(","); // Assuming the file is comma-separated

            // Construct the CREATE TABLE SQL query
            StringJoiner createTableQuery = new StringJoiner(", ", "CREATE TABLE " + tableName + " (", ")");
            for (String column : columns) {
                // Sanitize column name
                String sanitizedColumn = column.trim().replaceAll("[^a-zA-Z0-9_]", "_");
                createTableQuery.add(sanitizedColumn + " VARCHAR(255)"); // Assuming VARCHAR(255) for all columns
            }

            // Execute the CREATE TABLE query
            statement.executeUpdate(createTableQuery.toString());
            out.println("<h3>Table " + tableName + " created successfully with columns: " + String.join(", ", columns) + "</h3>");

            // Prepare the INSERT query based on the number of columns
            String insertSQL = generateInsertQuery(tableName, columns.length);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);

            // Read the rest of the lines and insert the data
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Assuming values are comma-separated
                for (int i = 0; i < values.length; i++) {
                    preparedStatement.setString(i + 1, values[i].trim()); // Set values for each column
                }
                preparedStatement.executeUpdate();
            }

            out.println("<h3>Data inserted into " + tableName + " successfully!</h3>");

        } catch (IOException | SQLException e) {
            out.println("<h3>Error processing the file: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        }
    }

    private String generateInsertQuery(String tableName, int columnCount) {
        StringJoiner placeholders = new StringJoiner(", ", "INSERT INTO " + tableName + " VALUES (", ")");
        for (int i = 1; i <= columnCount; i++) {
            placeholders.add("?");
        }
        return placeholders.toString();
    }
}
 