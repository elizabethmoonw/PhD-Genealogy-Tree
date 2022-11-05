package a4;

/**
 * A professor, with attributes such as their name and the year they received their PhD.
 */
public final class Professor implements Comparable<Professor> {
    /** First name of this professor */
    private final String firstName;

    /** Last name of this professor */
    private final String lastName;

    /**
     * Constructor: A new Professor
     *
     * @param first Their first name
     * @param last Their last name
     */
    public Professor(String first, String last) {
        firstName = first;
        lastName = last;
    }

    /**
     * The first name of this professor.
     */
    public String firstName() {
        return firstName;
    }

    /**
     * The last name of this professor.
     */
    public String lastName() {
        return lastName;
    }

    /**
     * Returns a String representation of this Professor with a space between
     * their first and last names.
     */
    public @Override String toString() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null || !this.getClass().equals(ob.getClass()))
            return false;

        Professor other = (Professor) ob;
        return this.firstName.equals(other.firstName()) &&
                this.lastName.equals(other.lastName());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /** Compare two professors. They are ordered alphabetically
     * by last name and then by first name.
     */
    @Override
    public int compareTo(Professor p) {
        int c = lastName.compareTo(p.lastName);
        if (c != 0) return c;
        return firstName.compareTo(p.firstName);
    }
}