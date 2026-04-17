package schedule;
import schedule.ShiftSchedule;
public interface Schedule {
        void updateSchedule(ShiftSchedule s);
        ShiftSchedule getShift();
    }

