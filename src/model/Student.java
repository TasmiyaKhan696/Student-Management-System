package model;

import java.time.LocalDate;

/**
 * Student class demonstrating INHERITANCE (extends Person)
 * and POLYMORPHISM (overrides getRole, getDisplayName).
 * Also implements the Manageable interface.
 */
public class Student extends Person implements Manageable {

    private String studentId;    // e.g. "STU-2024-001"
    private String grade;        // e.g. "A", "B+", "3.8 GPA"
    private String department;
    private LocalDate enrollDate;
    private String address;

    // --- Constructors (overloaded — METHOD OVERLOADING) ---

    public Student() {
        super();
    }

    /** Constructor for creating a new student (no DB id yet) */
    public Student(String studentId, String firstName, String lastName,
                   String email, String phone, String grade,
                   String department, LocalDate enrollDate) {
        super(firstName, lastName, email);
        this.studentId  = studentId;
        this.grade      = grade;
        this.department = department;
        this.enrollDate = enrollDate;
        setPhone(phone);
    }

    /** Constructor for loading from DB (has DB id) */
    public Student(int id, String studentId, String firstName, String lastName,
                   String email, String phone, String grade,
                   String department, LocalDate enrollDate) {
        super(id, firstName, lastName, email, phone);
        this.studentId  = studentId;
        this.grade      = grade;
        this.department = department;
        this.enrollDate = enrollDate;
    }

    // --- Overriding abstract method from Person ---

    @Override
    public String getRole() {
        return "Student";
    }

    /** METHOD OVERRIDING: richer display for students */
    @Override
    public String getDisplayName() {
        return "[" + studentId + "] " + getFirstName() + " " + getLastName();
    }

    // --- Manageable interface implementation ---

    @Override
    public boolean validate() {
        return studentId != null && !studentId.isBlank()
            && getFirstName() != null && !getFirstName().isBlank()
            && getLastName()  != null && !getLastName().isBlank()
            && getEmail()     != null && getEmail().contains("@")
            && department     != null && !department.isBlank();
    }

    @Override
    public String getSummary() {
        return studentId + " | " + getFirstName() + " " + getLastName()
             + " | " + department + " | " + (grade != null ? grade : "N/A");
    }

    @Override
    public String getUniqueIdentifier() {
        return studentId;
    }

    // --- Overloaded helper methods (METHOD OVERLOADING) ---

    /** Format grade as simple string */
    public String getGradeLabel() {
        return grade != null ? grade : "Not assigned";
    }

    /** Format grade with department context */
    public String getGradeLabel(boolean includeContext) {
        if (!includeContext) return getGradeLabel();
        return department + " — " + getGradeLabel();
    }

    // --- Getters and Setters ---

    public String getStudentId()              { return studentId; }
    public void setStudentId(String sid)      { this.studentId = sid; }

    public String getGrade()                  { return grade; }
    public void setGrade(String grade)        { this.grade = grade; }

    public String getDepartment()             { return department; }
    public void setDepartment(String dept)    { this.department = dept; }

    public LocalDate getEnrollDate()          { return enrollDate; }
    public void setEnrollDate(LocalDate d)    { this.enrollDate = d; }

    public String getAddress()                { return address; }
    public void setAddress(String address)    { this.address = address; }

    @Override
    public String toString() {
        return getSummary();
    }
}
