/**
 * @author Prakhar Mittal
 * @version 2.0
 * This class is a wrapper over different traits of a Startup Idea
 */
public class StartUpIdea implements Comparable<StartUpIdea> {
    private String problem;
    private String targetCustomer;
    private int customerNeed;
    private int feasibility;
    private int knownPeopleWithProblem;
    private int targetMarketSize;
    private String competitors;
    private int score;

    /**
     * 7-arg constructor
     * @param problem description
     * @param targetCustomer target customer
     * @param customerNeed 1-10 rating of need
     * @param feasibility 1-10 rating of feasibility
     * @param knownPeopleWithProblem people you know with the problem
     * @param targetMarketSize number of potential customer
     * @param competitors current competitors/solutions
     */
    public StartUpIdea(String problem, String targetCustomer, int customerNeed,
                       int feasibility, int knownPeopleWithProblem,
                       int targetMarketSize, String competitors) {
        this.problem = problem;
        this.targetCustomer = targetCustomer;
        this.customerNeed = customerNeed;
        this.knownPeopleWithProblem = knownPeopleWithProblem;
        this.feasibility = feasibility;
        this.targetMarketSize = targetMarketSize;
        this.competitors = competitors;
        this.score = (customerNeed + feasibility) * (targetMarketSize + knownPeopleWithProblem * 10000);
    }

    /**
     * Returns full string representation for use in FileUtil
     * @return full string representation of StartUpIdea
     */
    public String toFullString() {
        String str = "";
        str += "Problem: " + problem + "\n";
        str += "Target Customer: " + targetCustomer + "\n";
        str += "Customer Need: " + customerNeed + "\n";
        str += "Feasibility: " + feasibility + "\n";
        str += "Known People With Problem: " + knownPeopleWithProblem + "\n";
        str += "Target Market Size: " + targetMarketSize + "\n";
        str += "Competitors: " + competitors + "\n";
        return str;
    }

    /**
     * Returns short string for use in ObservableList
     * @return short string representation of StartUpIdea
     */
    public String toString() {
        return problem + " (Score: " + (int) (Math.pow(score, 0.4) * 10.0 / 52)  + ")";
    }

    /**
     * This StartUpIdea is less than other StartUpIdea if it is valued higher
     * @param other StartUpIdea to be compared
     * @return a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object.
     */
    public int compareTo(StartUpIdea other) {
        return other.score - this.score;
    }

    /**
     * Checks if two startup ideas have the same instance variables
     * @param o reference object for comparison
     * @return true if variables are equal; false otherwise
     */
    public boolean equals(Object o) {
        if (o instanceof StartUpIdea) {
            StartUpIdea s = (StartUpIdea) o;
            return problem.equals(s.problem) && targetCustomer.equals(s.targetCustomer)
                && customerNeed == s.customerNeed && feasibility == s.feasibility
                && knownPeopleWithProblem == s.knownPeopleWithProblem
                && targetMarketSize == s.targetMarketSize && competitors.equals(s.competitors);
        }
        return false;
    }

    /**
     * Getter for problem
     * @return problem
     */
    public String getProblem() {
        return problem;
    }

    /**
     * Getter for targetCustomer
     * @return targetCustomer
     */
    public String getTargetCustomer() {
        return targetCustomer;
    }

    /**
     * Getter for customerNeed
     * @return customerNeed
     */
    public int getCustomerNeed() {
        return customerNeed;
    }

    /**
     * Getter for feasibility
     * @return feasibility
     */
    public int getFeasibility() {
        return feasibility;
    }

    /**
     * Getter for knownPeopleWithProblem
     * @return knownPeopleWithProblem
     */
    public int getKnownPeopleWithProblem() {
        return knownPeopleWithProblem;
    }

    /**
     * Getter for targetMarketSize
     * @return targetMarketSize
     */
    public int getTargetMarketSize() {
        return targetMarketSize;
    }

    /**
     * Getter for competitors
     * @return competitors
     */
    public String getCompetitors() {
        return competitors;
    }
}