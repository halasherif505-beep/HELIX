package Hospital;

import Patient.Patient;
import Patient.Printable;

import java.util.ArrayList;
import java.util.List;

public class Billing implements Printable {

    private Patient patient;
    private List<BillingItem> items;
    private double total;

    public Billing(Patient patient) {
        this.patient = patient;
        this.total = 0;
        this.items = new ArrayList<>();
    }

    public void addItem(String description, double cost) {
        if (cost > 0) {
            items.add(new BillingItem(description, cost));
        }
    }

    public double calculateTotal() {
        total = 0;
        for (BillingItem item : items) {
            total += item.getCost();
        }
        return total;
    }

    @Override
    public void printInfo() {
        System.out.println("💳 Patient Billing Info: " + patient.getName());
        for (BillingItem item : items) {
            item.printInfo();
        }
        System.out.println("🔢 Total: " + calculateTotal() + " EGP");
    }

    public List<BillingItem> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }
}