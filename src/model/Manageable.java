package model;

/**
 * Interface demonstrating ABSTRACTION via contract.
 * Any entity that can be managed must implement these methods.
 */
public interface Manageable {

    boolean validate();          // Validate before saving
    String getSummary();         // One-line summary for table display
    String getUniqueIdentifier(); // Returns the unique key for this record
}
