package Hospital;
public class BillingItem {

    private String description;
    private double cost;

    public BillingItem(String description, double cost) {
        this.description = description;
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }
    public void printInfo() {
        System.out.println("• " + description + " : " + cost + " EGP");
    }

    public String getDescription() {
        return description;
    }
}

