package model;

import java.time.LocalDate;

public class Student extends Person implements Manageable {

    private String studentId;
    private String grade;
    private String department;
    private LocalDate enrollDate;
    private String address;

    public Student() { super(); }

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

    public Student(int id, String studentId, String firstName, String lastName,
                   String email, String phone, String grade,
                   String department, LocalDate enrollDate) {
        super(id, firstName, lastName, email, phone);
        this.studentId  = studentId;
        this.grade      = grade;
        this.department = department;
        this.enrollDate = enrollDate;
    }

    @Override
    public String getRole() { return "Student"; }

    @Override
    public String getDisplayName() {
        return "[" + studentId + "] " + getFirstName() + " " + getLastName();
    }

    @Override
    public boolean validate() {
        return studentId != null && !studentId.trim().isEmpty()
            && getFirstName() != null && !getFirstName().trim().isEmpty()
            && getLastName()  != null && !getLastName().trim().isEmpty()
            && getEmail()     != null && getEmail().contains("@")
            && department     != null && !department.trim().isEmpty();
    }

    @Override
    public String getSummary() {
        return studentId + " | " + getFirstName() + " " + getLastName()
             + " | " + department + " | " + (grade != null ? grade : "N/A");
    }

    @Override
    public String getUniqueIdentifier() { return studentId; }

    public String getGradeLabel() {
        return grade != null ? grade : "Not assigned";
    }

    public String getGradeLabel(boolean includeContext) {
        if (!includeContext) return getGradeLabel();
        return department + " — " + getGradeLabel();
    }

    public String getStudentId()              { return studentId; }
    public void setStudentId(String sid)      { this.studentId = sid; }
    public String getGrade()                  { return grade; }
    public void setGrade(String g)            { this.grade = g; }
    public String getDepartment()             { return department; }
    public void setDepartment(String d)       { this.department = d; }
    public LocalDate getEnrollDate()          { return enrollDate; }
    public void setEnrollDate(LocalDate d)    { this.enrollDate = d; }
    public String getAddress()                { return address; }
    public void setAddress(String a)          { this.address = a; }

    @Override
    public String toString() { return getSummary(); }
}