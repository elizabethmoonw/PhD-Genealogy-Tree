package a4;

import java.util.*;

/** A PhDTree is a tree representing people who have received a PhD degree; each node
 *  represents a person, and the edges represent advisor-advisee relationships, since
 *  PhD students almost always have an advisor who mentors them.
 */
public class PhDTree {
    // You will not need to modify these fields. They are used to help test and
    // print out your output

    /** The String that marks the start of children in toString() */
    public static final String START_ADVISEE_DELIMITER = "[";

    /** The String that divides children in toString() */
    public static final String DELIMITER = ", ";

    /** The String that marks the end of children in toString() */
    public static final String END_ADVISEE_DELIMITER = "]";

    /**
     * The Professor at the root of this PhDTree.
     * i.e. the Professor at this node of this PhDTree.
     * All nodes of a PhDTree have a different Professor in them. The names of
     * professors are all distinct: there are no duplicates.
     */
    private Professor prof;

    /**
     * Year in which this node's professor was awarded their PhD.
     */
    private int phdYear;

    /**
     * The advisees of this PhDTree node.
     * Each element of this set is an advisee of the Professor at this
     * node.  It is the empty set if this node is a leaf. The set of
     * PhDTree nodes reachable via advisees forms a tree.
     */
    private SortedSet<PhDTree> advisees;

    /** Returns false or throws an assertion error if
     *  the class invariant is not satisfied. Requires:
     *  assertion checking is enabled.
     */
    private boolean classInv() {
        Set<Professor> seenProfs = new HashSet<>();
        Set<PhDTree> seenNodes = new HashSet<>();
        return classInvTraverse(seenProfs, seenNodes);
    }

    /**
     * Helper method for classInv. Traverses the tree from this node,
     * adding all Professors and nodes seen to the respective seen sets. Things added must
     * not already be in the set; it would imply the data structure is not a tree. Returns false
     * or throws an assertion error if these conditions are not met.
     */
    private boolean classInvTraverse(Set<Professor> seenProfs, Set<PhDTree> seenNodes) {
        assert !seenProfs.contains(prof) : "prof " + prof + " is not unique";
        assert !seenNodes.contains(this) : "node " + this + " is not unique";
        seenProfs.add(prof);
        seenNodes.add(this);
        for (PhDTree child: advisees) {
            if (!child.classInvTraverse(seenProfs, seenNodes)) return false;
        }
        return true;
    }

    /**
     * Creates: a new PhDTree with root Professor p and no children.
     */
    public PhDTree(Professor p, int year) throws IllegalArgumentException {
        assert p != null;
        prof = p;
        phdYear = year;
        advisees = new TreeSet<>((x, y) -> x.prof.compareTo(y.prof));
        assert classInv();
    }

    /** The Professor at the root of this PhDTree. */
    public Professor prof() {
        return prof;
    }

    /** The number of direct advisees of the professor at the root of the PhDTree. */
    public int numAdvisees() {
        // TODO 1
        return advisees.size();
    }

    /**
     * Returns the number of nodes in this PhDTree.
     * Note: If this is a leaf, the size is 1 (just the root)
     */
    public int size() {
        // TODO 2
        // This method must be recursive.
        // State whether this is a searching or a counting method:
        // A counting methods was implemented

        int sum = 0;
        for (PhDTree subadviseeTree : advisees){
            sum += subadviseeTree.size();
        }
        return sum + 1;
    }

    /**
     * The maximum depth of this PhDTree,
     * i.e. the longest path from the root to a leaf.
     * Example: If this PhDTree has only one node, returns 0.
     */
    public int maxDepth() {
        // TODO 3
        if (this.numAdvisees() == 0) {
            return 0;}
        int curr = 0;
        for (PhDTree advisee : advisees){
            if (advisee.maxDepth() > curr) {
                curr = advisee.maxDepth();
            }
        }
        return curr + 1;
    }

    /**
     * Returns the subtree with p at the root. Throws NotFound
     * if p is not in the tree.
     */
    public PhDTree findTree(Professor p) throws NotFound {
        // TODO 4
        // You will need to use recursion and to catch the NotFound exception.
        PhDTree subadvisees = null;
        if (this.prof.equals(p)) { return this;}
        for (PhDTree advisee : this.advisees){
            try { subadvisees = advisee.findTree(p);
            } catch (NotFound e) {
                // System.out.println("Not Found");
            }
            }
        if (subadvisees != null) {return subadvisees;}
        throw new NotFound();
    }

    /** Returns true if this PhDTree contains a node with Professor p. */
    public boolean contains(Professor p) {
        try {
            findTree(p);
            return true;
        } catch (NotFound exc) {
            return false;
        }
    }
    /**
     * Effect: Extend the tree rooted at Professor p with a new node for
     * the new advisee, Professor a, who received their PhD in the year
     * year.
     * Checks: p is in this PhDTree, and a is not already in this PhDTree.
     *
     * @return
     */
    public void insert(Professor p, Professor a, int year) throws NotFound {
        // TODO 5
        // This method should not be recursive.
        // Use method findTree(), above, and use no methods that are below.
        // DO NOT traverse the tree twice looking for the same professor
        // --don't duplicate work.
        assert classInv();
        // assert !this.contains(a);
        PhDTree pTree = findTree(p);
        PhDTree aTree = new PhDTree(a, year);
        pTree.advisees.add(aTree);

    }

    /**
     * Returns the immediate advisor of p, or throws NotFound if
     * p is not a descendant of the root node of this tree.
     */
    public Professor findAdvisor(Professor p) throws NotFound {
        // TODO 6
        Professor k = null;
        for (PhDTree advisee : advisees) {
            if (advisee.prof.equals(p)) {
                return this.prof;
            } else {
                try {
                    k = advisee.findAdvisor(p);
                } catch (NotFound e) {
                }
            }
            if (k != null) {
                return k;
            }
        }
        throw new NotFound();
    }

    /**
     * Returns: The path between "here" (the root of this PhDTree) to
     * professor descendant p. Throws NotFound if there is no such path.
     */
    public List<Professor> findAcademicLineage(Professor p) throws NotFound {
        // TODO 7
        List<Professor> lineage = new LinkedList<>();
        List<Professor> profList = new LinkedList<>();
        profList.add(this.prof);
        if (this.prof.equals(p)) {
            return profList;
        }
        for (PhDTree advisee : advisees) {
            try {
                lineage = advisee.findAcademicLineage(p);
            } catch (NotFound e) {

            }
        }
        if (lineage.size() > 0) {
            profList.addAll(lineage);
            return profList;
        }
        throw new NotFound();
    }

    /**
     * Returns: The professor at the root of the smallest subtree of
     * this PhDTree that contains prof1 and prof2, if such a subtree
     * exists. Otherwise, throws NotFound.
     */
    public Professor commonAncestor(Professor prof1, Professor prof2) throws NotFound {
        // TODO 8
        List<Professor> p1List = findAcademicLineage(prof1);
        List<Professor> p2List = findAcademicLineage(prof2);
        int i = 0;
        while (i < p1List.size() && i < p2List.size() && p1List.get(i).equals(p2List.get(i))){
            i ++;
        }
        return p1List.get(i - 1);
    }

    /**
     * Return a (single line) String representation of this PhDTree.
     * If this PhDTree has no advisees (it is a leaf), return the root's
     * substring.
     * Otherwise, return
     * ... root's substring + START_ADVISEE_DELIMITER + each
     * advisees's toString, separated by DELIMITER, followed by
     * END_ADVISEE_DELIMITER.
     *
     * Thus, for the following tree:
     *
     * <pre>
     * Depth:
     *   0      Maya_Leong
     *            /     \
     *   1 Matthew_Hui  Curran_Muhlberger 
     *           /          /         \
     *   2 Amy_Huang    Tomer_Shamir   Andrew_Myers  
     *           \
     *   3    David_Gries
     *
     * Maya_Leong.toString() should print:
     * Maya Leong[Matthew Hui[Amy Huang[David Gries]]],Curran Muhlberger[Tomer Shamir,Andrew Myers]]
     *
     * Matthew_Hui.toString() should print:
     * Matthew Hui[Amy Huang[David Gries]]
     *
     * Andrew_Myers.toString() should print:
     * Andrew Myers
     * </pre>
     */
    @Override
    public String toString() {
        if (advisees.isEmpty())
            return prof.toString();
        StringBuilder s = new StringBuilder();
        s.append(prof.toString())
         .append(START_ADVISEE_DELIMITER);
        boolean first = true;
        for (PhDTree dt : advisees) {
            if (!first) s.append(DELIMITER);
            first = false;
            s.append(dt.toString());
        }
        s.append(END_ADVISEE_DELIMITER);
        return s.toString();
    }

    /**
     * Return a verbose (multi-line) string representing this PhDTree with
     * the professors [first name last name - year].
     * Each professor in the tree is on its own line (there are no spaces at the
     * beginning or end of each new line)
     * Each line is terminated by a newline character ('\n').
     */
    public String toStringVerbose() {
        StringBuilder s = new StringBuilder();
        // TODO 9
        // Use a StringBuilder to implement this method (Hint: look at toString)
        if (advisees.isEmpty())
            return prof.toString() + " - " + phdYear + "\n";
        s.append(prof.toString() + " - " + phdYear + "\n");
        for (PhDTree advisee : advisees) {
            s.append(advisee.toStringVerbose());
        }
        return s.toString();
    }
}