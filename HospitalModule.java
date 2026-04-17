package Hospital;

import Patient.Patient;
import Patient.DailyHabits;
import Patient.MedicalHistory;

import java.util.Scanner;

public class HospitalModule {

    private Hospital hospital;
    private Scanner scanner;

    public HospitalModule() {
        this.scanner = new Scanner(System.in);
        this.hospital = new Hospital("Smart Hospital", "Cairo");
        seedRooms();
    }

    // add some default rooms
    private void seedRooms() {
        hospital.addRoom(new Room(101, 2));
        hospital.addRoom(new Room(102, 3));
        hospital.addRoom(new Room(103, 1));
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    checkBeds();
                    break;
                case "2":
                    addPatient();
                    break;
                case "3":
                    showRooms();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("❌ Invalid choice");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n🏥 Hospital Module");
        System.out.println("1️⃣ Check bed availability");
        System.out.println("2️⃣ Add patient");
        System.out.println("3️⃣ Show rooms");
        System.out.println("0️⃣ Back");
        System.out.print("Choose: ");
    }

    private void checkBeds() {
        if (hospital.checkBedAvailability()) {
            System.out.println("✅ Beds available");
        } else {
            System.out.println("❌ No beds available");
        }
    }

    private void addPatient() {
        System.out.print("Patient name: ");
        String name = scanner.nextLine();

        System.out.print("Age: ");
        int age = Integer.parseInt(scanner.nextLine());

        System.out.print("National ID: ");
        String id = scanner.nextLine();

        Patient patient = new Patient(
                name,
                age,
                id,
                new MedicalHistory(),
                new DailyHabits()
        );

        hospital.addPatient(patient);
    }

    private void showRooms() {
        for (Room room : hospital.getRooms()) {
            System.out.println("Room " + room.getRoomNumber()
                    + " | Total Beds: " + room.getTotalBeds()
                    + " | Available: " + room.getAvailableBeds());
        }
    }
}