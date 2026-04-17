package Patient;
public class DailyHabits implements Printable {
    private boolean smoking;
    private int sleepHours;
    private boolean exercises;
    private int streakCounter;
    private String patientId;

    public DailyHabits() {
        smoking = false;
        sleepHours = 0;
        exercises = false;
        streakCounter = 0;
    }

    public DailyHabits(String patientId, boolean smoker, int sleepHours, boolean exercise) {
        this.patientId = patientId;
        updateHabits(smoker, sleepHours, exercise);
    }


    public void updateHabits(boolean smoking, int sleepHours, boolean exercises) {
        this.smoking = smoking;
        this.sleepHours = (sleepHours >= 0 && sleepHours <= 24) ? sleepHours : 0;
        this.exercises = exercises;
        updateStreak();
    }


    public String getPatientId() {
        return patientId;
    }

    private void updateStreak() {
        // Check if all good habits are met
        boolean goodHabits = !smoking && exercises && sleepHours >= 7 && sleepHours <= 9;

        if (goodHabits) {
            streakCounter++; // Increment streak when all conditions are met
            System.out.println("✅ Good habits maintained! Streak: " + streakCounter + " days");
        } else {
            streakCounter = 0; // Reset streak when any condition fails
            System.out.println("❌ Streak broken! Good habits reset to 0 days");
        }
    }

    // Reset streak to specific value (useful for testing or manual updates)
    public void resetStreak(int newStreak) {
        if (newStreak >= 0) {
            this.streakCounter = newStreak;
            System.out.println("Streak reset to: " + newStreak + " days");
        }
    }

    public int getStreakCounter() {
        return streakCounter;
    }

    // Getter methods for testing
    public boolean isSmoking() { return smoking; }
    public int getSleepHours() { return sleepHours; }
    public boolean isExercises() { return exercises; }

    @Override
    public void printInfo() {
        System.out.println("Daily Habits → Smoking: " + (smoking ? "Yes" : "No") +
                ", Sleep Hours: " + sleepHours +
                ", Exercises: " + (exercises ? "Yes" : "No") +
                ", Streak: " + streakCounter + " days");
    }

    // Helper method to check if current habits are healthy
    public boolean hasHealthyHabits() {
        return !smoking && exercises && sleepHours >= 7 && sleepHours <= 9;
    }
}
