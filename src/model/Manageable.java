package model;

public interface Manageable {
    boolean validate();
    String getSummary();
    String getUniqueIdentifier();
}