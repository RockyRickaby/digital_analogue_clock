public class DigitalClockString {
    private static final String[] AM_PM = {"AM", "PM"};

    private DigitalClockString() {
    }

    /**
     * Returns a String representation of the given time. It may be a representation
     * of a 12 or 24-hour clock, which is specifiec by the parameter {@code is24hourClock}.
     * @param hours the number of hours.
     * @param minutes the number of minutes.
     * @param is24hourClock whether this is a 24-hour clock or not.
     * @return the String representation of the given time.
     */
    public static String toString(int hours, int minutes, boolean is24hourClock) {
        if (hours < 0 || minutes < 0) {
            throw new IllegalArgumentException("Hours and/or minutes cannot be negative.");
        }
        int amOrpmIdx = (hours % 24) / 12;
        if (!is24hourClock) {
            int auxHour = hours % 12;
            if (auxHour == 0) {
                auxHour = 12;
            }
            hours = auxHour;
        }

        StringBuilder str = new StringBuilder();
        str.append(String.format("%02d", hours))
           .append(":")
           .append(String.format("%02d", minutes));

        if (!is24hourClock) {
            str.append(" ")
               .append(AM_PM[amOrpmIdx]);
        }

        return str.toString();
    }
}
