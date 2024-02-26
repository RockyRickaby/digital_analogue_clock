public class DigitalClockString {
    private static final String[] AM_PM = {"AM", "PM"};

    private DigitalClockString() {
    }

    public static String toString(int hours, int minutes, boolean h24) {
        if (hours < 0 || minutes < 0) {
            throw new IllegalArgumentException("Hours and/or minutes cannot be negative.");
        }
        int amOrpmIdx = (hours % 24) / 12;
        if (!h24) {
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

        if (!h24) {
            str.append(" ")
               .append(AM_PM[amOrpmIdx]);
        }

        return str.toString();
    }
}
