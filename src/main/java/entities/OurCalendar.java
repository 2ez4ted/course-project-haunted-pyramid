package entities;

import java.time.LocalDateTime;
import java.util.*;
import java.time.YearMonth;


public class OurCalendar {

    private List<Event> conflictEvent; // All the objects that are conflicting
    private boolean conflict;  // if the calendar has any conflicted information
    private Map<Integer, List<Event>> calendarMap; // map of calendar
    private List<Integer> dateInfo; //in the form of [year, month, # of days in the month]


    // if provided a year and month and date, create a calendar that matches that year and month
    public OurCalendar(int year, int month) {
        YearMonth tempYearMonth = YearMonth.of(year, month);
        int daysInMonth = tempYearMonth.lengthOfMonth(); // # of days in the given month
        // store the [year, month, # of days in the month] information
        this.dateInfo = new ArrayList<>() {
            {
                add(year);
                add(month);
                add(daysInMonth);
            }
        };

        // no conflict because empty
        this.conflict = false;
        // empty list of conflicted object to start with
        this.conflictEvent = new ArrayList<>();
        // create an empty calendar map with keys of days and values of list of events (empty to start with) for the
        // provided year and month
        Map<Integer, List<Event>> tempMap = new HashMap<>();
        for (int i = 1; i <= daysInMonth; i++) {
            tempMap.put(i, new ArrayList<>());
        }
        this.calendarMap = tempMap;
    }
    // if no argument, create the current month calendar
    public OurCalendar(){
        GregorianCalendar temp = new GregorianCalendar();
        OurCalendar tempCalendar = new OurCalendar(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH) + 1);
        this.conflictEvent = tempCalendar.conflictEvent;
        this.conflict = tempCalendar.conflict;
        this.calendarMap = tempCalendar.calendarMap;
        this.dateInfo = tempCalendar.dateInfo;
    }

    /**
     * Check if there is any conflict for the current calendar
     * @return true if there exists conflict
     */
    public boolean isConflict(){
        return this.conflict;
    }

    /**
     * get conflictObject
     * @return conflictObject
     */
    public List<Event> getConflictEvent(){
        return this.conflictEvent;
    }

    /**
     * get dateInfo
     * @return dateInfo
     */
    public List<Integer> getDateInfo(){
        return this.dateInfo;
    }

    /**
     * get calendarMap
     * @return calendarMap
     */
    public Map<Integer, List<Event>> getCalendarMap(){
        return this.calendarMap;
    }

    /**
     * Add the event to a calendar for the appropriate dates
     *
     * == Representation Invariant ==
     *
     * the event is set up for the same month as the schedule
     *
     * @param event the event that wants to be added
     */

    public void addEvent(Event event){
        String startInfo = event.getStartString();
        String endInfo = event.getEndString();
        int startDate = Integer.parseInt(startInfo.substring(8, 10));
        int endDate = Integer.parseInt(endInfo.substring(8, 10));
        List<Integer> applicableDates = new ArrayList<>();
        for (int i = startDate; i <= endDate; i++){
            applicableDates.add(i);
        }
        for (int dates : applicableDates){
            this.calendarMap.get(dates).add(event);
        }
    }

    public void checkConflict(){
        for (int i = 0; i < this.dateInfo.get(2); i++){
            List<List<Double>> timeInfo = new ArrayList<>();
            for (Event item :this.calendarMap.get(i)) {
                List<Double> individualTimeInfo = new ArrayList<>();
                if (!(Integer.parseInt(item.getStartString().substring(8, 10)) == i)
                        && !(Integer.parseInt(item.getEndString().substring(8, 10)) == i)) {
                    individualTimeInfo.add(0.0);
                    individualTimeInfo.add(2400.0);
                }
                else if (!(Integer.parseInt(item.getStartString().substring(8, 10)) == i)) {
                    individualTimeInfo.add(0.0);
                    individualTimeInfo.add(item.endTimeDouble());
                }
                else {
                    individualTimeInfo.add(item.startTimeDouble());
                    individualTimeInfo.add(item.startTimeDouble() + item.getLength() * 100);
                }
                timeInfo.add(individualTimeInfo);
            }
            for (int j = 0; j < (timeInfo.size() - 1); j++){
                for (List<Double> timePair : timeInfo.subList(j + 1, timeInfo.size())){
                    boolean check = OurCalendar.isOverlapped(timeInfo.get(j), timePair);
                    if (check){
                        this.conflict = true;
                        if (!(this.conflictEvent.contains(this.calendarMap.get(i).get(j)))){
                            this.conflictEvent.add(this.calendarMap.get(i).get(j));
                        }
                        if (!(this.conflictEvent.contains(this.calendarMap.get(i).get(j + 1)))){
                            this.conflictEvent.add(this.calendarMap.get(i).get(j + 1));
                        }
                    }
                }
            }
        }
    }

    private static boolean isOverlapped(List<Double> ex1, List<Double> ex2){
        if ((ex1.get(0) <= ex2.get(0)) && (ex2.get(0) <= ex1.get(1))){
            return true;
        }
        else return (ex1.get(0) <= ex2.get(1)) && (ex2.get(1) <= ex1.get(1));
    }
    /**
     * Remove an event
     * If there is no such event, do nothing
     * @param event event that will be removed
     */
    public void removeEvent(Event event){
        for (int i = 0; i < this.dateInfo.get(2); i++){
            for (Event item :this.calendarMap.get(i)){
                if (item == event){
                    this.calendarMap.get(i).remove(event);
                }
            }
        }
    }



    public static void main(String[] args) {
        OurCalendar a = new OurCalendar();
        System.out.println(a.getCalendarMap()); // should create a map of calendar for a month
        System.out.println(a.isConflict()); // should return false
        System.out.println(a.getConflictEvent()); // should return empty list
        System.out.println(a.getDateInfo()); // should return a list of [year, month, # of dates]

        Event b = new Event(1, "My entities.Event",
                LocalDateTime.of(2020, 1, 1, 1, 0, 0),
                LocalDateTime.of(2020, 1, 1, 3, 30, 0));
        // tests to be added

    }
}

