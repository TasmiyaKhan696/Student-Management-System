package model;

public abstract class Person {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public Person() {}

    public Person(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
    }

    public Person(int id, String firstName, String lastName, String email, String phone) {
        this.id        = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.phone     = phone;
    }

    public abstract String getRole();

    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }
    public String getFirstName()        { return firstName; }
    public void setFirstName(String fn) { this.firstName = fn; }
    public String getLastName()         { return lastName; }
    public void setLastName(String ln)  { this.lastName = ln; }
    public String getEmail()            { return email; }
    public void setEmail(String e)      { this.email = e; }
    public String getPhone()            { return phone; }
    public void setPhone(String p)      { this.phone = p; }

    @Override
    public String toString() {
        return getRole() + ": " + getDisplayName() + " <" + email + ">";
    }
}