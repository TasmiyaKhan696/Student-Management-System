package model;

/**
 * Abstract base class demonstrating ABSTRACTION and ENCAPSULATION.
 * All people in the system share these core fields.
 */
public abstract class Person {

    // Encapsulated fields with private access
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    // --- Constructors (overloaded) ---

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

    // --- Abstract method: subclasses must override ---
    public abstract String getRole();

    // --- Polymorphic display method: overridden in subclasses ---
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    // --- Getters and Setters (Encapsulation) ---

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public String getFirstName()          { return firstName; }
    public void setFirstName(String fn)   { this.firstName = fn; }

    public String getLastName()           { return lastName; }
    public void setLastName(String ln)    { this.lastName = ln; }

    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    public String getPhone()              { return phone; }
    public void setPhone(String phone)    { this.phone = phone; }

    @Override
    public String toString() {
        return getRole() + ": " + getDisplayName() + " <" + email + ">";
    }
}
