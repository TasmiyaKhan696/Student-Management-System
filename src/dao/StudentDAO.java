package dao;

import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class StudentDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public boolean addStudent(Student s) {
        String sql = "INSERT INTO students (student_id,first_name,last_name,email,phone,department,enroll_date,address,library_status,fees_paid) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getDepartment());
            ps.setDate  (7, s.getEnrollDate() != null ? Date.valueOf(s.getEnrollDate()) : null);
            ps.setString(8, s.getAddress());
            ps.setString(9, s.getLibraryStatus());
            ps.setBoolean(10, s.isFeesPaid());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
            return false;
        }
    }

    // ── READ ALL ──────────────────────────────────────────────────────────────

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY last_name";
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────

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

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET first_name=?,last_name=?,email=?,phone=?,department=?,enroll_date=?,address=?,library_status=?,fees_paid=? WHERE id=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getDepartment());
            ps.setDate  (6, s.getEnrollDate() != null ? Date.valueOf(s.getEnrollDate()) : null);
            ps.setString(7, s.getAddress());
            ps.setString(8, s.getLibraryStatus());
            ps.setBoolean(9, s.isFeesPaid());
            ps.setInt(10, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public boolean deleteStudent(int id) {
        String getSid = "SELECT student_id FROM students WHERE id=?";
        String deleteMarks = "DELETE FROM student_marks WHERE student_id=?";
        String deleteStudent = "DELETE FROM students WHERE id=?";

        try (Connection c = getConnection()) {

            String studentId = null;

            // get student_id
            try (PreparedStatement ps = c.prepareStatement(getSid)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) studentId = rs.getString("student_id");
            }

            // delete marks first
            try (PreparedStatement ps = c.prepareStatement(deleteMarks)) {
                ps.setString(1, studentId);
                ps.executeUpdate();
            }

            // then delete student
            try (PreparedStatement ps = c.prepareStatement(deleteStudent)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── COUNT ─────────────────────────────────────────────────────────────────

    public int count() {
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ── MARKS: INSERT (new) ───────────────────────────────────────────────────

    public boolean addMarks(String studentId, int sem, int marks) {
        String sql = "INSERT INTO student_marks (student_id, semester, marks) VALUES (?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, sem);
            ps.setInt(3, marks);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── MARKS: UPDATE existing row ────────────────────────────────────────────

    public boolean updateMarks(String studentId, int sem, int marks) {
        String sql = "UPDATE student_marks SET marks=? WHERE student_id=? AND semester=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, marks);
            ps.setString(2, studentId);
            ps.setInt(3, sem);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── MARKS: check if row exists ────────────────────────────────────────────

    public boolean marksExist(String studentId, int sem) {
        String sql = "SELECT 1 FROM student_marks WHERE student_id=? AND semester=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, sem);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── MAP ResultSet → Student ───────────────────────────────────────────────

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student(
            rs.getInt("id"),
            rs.getString("student_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("department"),
            rs.getDate("enroll_date") != null ? rs.getDate("enroll_date").toLocalDate() : null
        );

        try { s.setLibraryStatus(rs.getString("library_status")); } catch (Exception ignored) {}
        try { s.setFeesPaid(rs.getBoolean("fees_paid")); }          catch (Exception ignored) {}

        s.setSem1Marks(getMarks(s.getStudentId(), 1));
        s.setSem2Marks(getMarks(s.getStudentId(), 2));
        return s;
    }

    private int getMarks(String studentId, int sem) {
        String sql = "SELECT marks FROM student_marks WHERE student_id=? AND semester=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, sem);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("marks");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}
