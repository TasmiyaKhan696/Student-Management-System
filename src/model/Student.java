package model;

import java.time.LocalDate;

public class Student extends Person implements Manageable {

    private String studentId;
  
    private String department;
    private LocalDate enrollDate;
    private String address;

    // 🔥 NEW FIELDS
    private String libraryStatus; // Borrowed / Returned / Not Borrowed
    private boolean feesPaid;
    private int sem1Marks;
    private int sem2Marks;

    public Student() { 
        super();
        this.libraryStatus = "Not Borrowed";
        this.feesPaid = false;
    }

    public Student(String studentId, String firstName, String lastName,
                   String email, String phone,
                   String department, LocalDate enrollDate) {
        super(firstName, lastName, email);
        this.studentId  = studentId;
        
        this.department = department;
        this.enrollDate = enrollDate;
        setPhone(phone);

        // 🔥 DEFAULTS
        this.libraryStatus = "Not Borrowed";
        this.feesPaid = false;
    }

    public Student(int id, String studentId, String firstName, String lastName,
                   String email, String phone,
                   String department, LocalDate enrollDate) {
        super(id, firstName, lastName, email, phone);
        this.studentId  = studentId;
        
        this.department = department;
        this.enrollDate = enrollDate;

        // 🔥 DEFAULTS
        this.libraryStatus = "Not Borrowed";
        this.feesPaid = false;
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
             + " | " + department 
             + " | " + libraryStatus
             + " | " + (feesPaid ? "Fees Paid" : "Fees Pending")
             + " | Sem1: " + sem1Marks
             + " | Sem2: " + sem2Marks;
    }

    @Override
    public String getUniqueIdentifier() { return studentId; }

    

   

    // 🔹 EXISTING GETTERS/SETTERS
    public String getStudentId()              { return studentId; }
    public void setStudentId(String sid)      { this.studentId = sid; }

    public String getDepartment()             { return department; }
    public void setDepartment(String d)       { this.department = d; }
    public LocalDate getEnrollDate()          { return enrollDate; }
    public void setEnrollDate(LocalDate d)    { this.enrollDate = d; }
    public String getAddress()                { return address; }
    public void setAddress(String a)          { this.address = a; }

    // 🔥 NEW GETTERS/SETTERS

    public String getLibraryStatus() { return libraryStatus; }
    public void setLibraryStatus(String libraryStatus) { this.libraryStatus = libraryStatus; }

    public boolean isFeesPaid() { return feesPaid; }
    public void setFeesPaid(boolean feesPaid) { this.feesPaid = feesPaid; }

    public int getSem1Marks() { return sem1Marks; }
    public void setSem1Marks(int sem1Marks) { this.sem1Marks = sem1Marks; }

    public int getSem2Marks() { return sem2Marks; }
    public void setSem2Marks(int sem2Marks) { this.sem2Marks = sem2Marks; }

    @Override
    public String toString() { return getSummary(); }
}