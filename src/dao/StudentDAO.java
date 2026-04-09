package dao;

import model.Student;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // ── Change these three to match your MySQL setup ──────────────────────────
    private static final String URL  = "jdbc:mysql://localhost:3306/student_ms?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "yourpassword";
    // ──────────────────────────────────────────────────────────────────────────

    private Connection getConnection() throws SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new SQLException("Driver not found", e); }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // CREATE
    public boolean addStudent(Student s) {
        String sql = "INSERT INTO students (student_id,first_name,last_name,email,phone,grade,department,enroll_date,address) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getGrade());
            ps.setString(7, s.getDepartment());
            ps.setDate  (8, s.getEnrollDate() != null ? Date.valueOf(s.getEnrollDate()) : null);
            ps.setString(9, s.getAddress());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // READ ALL
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY last_name";
        try (Connection c = getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // SEARCH
    public List<Student> search(String keyword) {
        List<Student> list = new ArrayList<>();
        String like = "%" + keyword + "%";
        String sql  = "SELECT * FROM students WHERE first_name LIKE ? OR last_name LIKE ? OR student_id LIKE ? OR department LIKE ? OR email LIKE ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) ps.setString(i, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // UPDATE
    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET first_name=?,last_name=?,email=?,phone=?,grade=?,department=?,enroll_date=?,address=? WHERE id=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getGrade());
            ps.setString(6, s.getDepartment());
            ps.setDate  (7, s.getEnrollDate() != null ? Date.valueOf(s.getEnrollDate()) : null);
            ps.setString(8, s.getAddress());
            ps.setInt   (9, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // DELETE
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // COUNT
    public int count() {
        try (Connection c = getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Student map(ResultSet rs) throws SQLException {
        Date d = rs.getDate("enroll_date");
        return new Student(
            rs.getInt("id"), rs.getString("student_id"),
            rs.getString("first_name"), rs.getString("last_name"),
            rs.getString("email"), rs.getString("phone"),
            rs.getString("grade"), rs.getString("department"),
            d != null ? d.toLocalDate() : null
        );
    }
}