package application;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class PetCareScheduler {
    private static final String SER_FILE = "src/java/petcare.ser";
    private static final String PETS_CSV = "src/java/pets.csv";
    private static final String APPOINTMENTS_CSV = "src/java/appointments.csv";

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm", Locale.ENGLISH);

    private static final Scanner input = new Scanner(System.in);

    private static HashMap<String, Pet> petsInTheList = new HashMap<>();

    public static void main(String[] args) {
        loadObjectData();
        boolean running = true;
        while (running) {
            System.out.println("\n=== PetCare Appointment Scheduler ===");
            System.out.println("1. Register a Pet");
            System.out.println("2. Add an appointment for a pet");
            System.out.println("3. Store data to a file");
            System.out.println("4. Display the records");
            System.out.println("5. Generate Reports");
            System.out.println("6. Exit the program");
            System.out.print("Choose an option: ");

            String choice = input.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    if (registerPet()) {
                        System.out.println("Registration Successful!");
                    } else {
                        System.out.println("Registration Unsuccessful! Try Again!!");
                    }
                }
                case "2" -> {
                    if (addAppointment()) {
                        System.out.println("Appointment Added Successfully!");
                    } else {
                        System.out.println("Adding Appointment Unsuccessful! Try Again!!");
                    }
                }
                case "3" -> saveData();
                case "4" -> displayRecords();
                case "5" -> generateReports();
                case "6" -> {
                    saveData();
                    running = false;
                }
                default -> System.out.println("Invalid Choice! Try Again!");
            }
        }
    }

    // ---------------------------------------------------------------
    // Loading (ONLY from the serialized file)
    // ---------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private static void loadObjectData() {
        File file = new File(SER_FILE);
        if (!file.exists()) {
            System.out.println("No saved data found. Starting with an empty list.");
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            petsInTheList = (HashMap<String, Pet>) in.readObject();
            System.out.println("Loaded " + petsInTheList.size() + " pet(s) from " + SER_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load saved data: " + e.getMessage());
            petsInTheList = new HashMap<>();
        }
    }

    // ---------------------------------------------------------------
    // Saving (serialized object + two CSV files)
    // ---------------------------------------------------------------
    private static void saveData() {
        saveObjectData();
        saveCsvFiles();
    }

    private static void saveObjectData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SER_FILE))) {
            out.writeObject(petsInTheList);
            System.out.println("Serialized data saved to " + SER_FILE);
        } catch (IOException e) {
            System.out.println("Failed to save serialized data: " + e.getMessage());
        }
    }

    private static void saveCsvFiles() {
        try (PrintWriter petWriter = new PrintWriter(new FileWriter(PETS_CSV));
             PrintWriter apptWriter = new PrintWriter(new FileWriter(APPOINTMENTS_CSV))) {

            petWriter.println("petId,name,specieOrBreed,age,ownerName,registrationDate");
            apptWriter.println("petId,appointmentType,appointmentDateAndTime,notes");

            for (Pet p : petsInTheList.values()) {
                petWriter.println(String.join(",",
                        csv(p.getPetId()),
                        csv(p.getName()),
                        csv(p.getSpecieOrBreed()),
                        String.valueOf(p.getAge()),
                        csv(p.getOwnerName()),
                        p.getRegistrationDate().toString(),
                        p.getEmail(),
                        p.getPhoneNumber()));

                for (Appointments a : p.getAppointmentsList()) {
                    apptWriter.println(String.join(",",
                            csv(p.getPetId()),
                            csv(a.getAppointmentType()),
                            a.getAppointmentDateAndTime().toString(),
                            csv(a.getNotes())));
                }
            }
            System.out.println("CSV data saved to " + PETS_CSV + " and " + APPOINTMENTS_CSV);
        } catch (IOException e) {
            System.out.println("Failed to save CSV files: " + e.getMessage());
        }
    }

    /** Wraps a value in quotes and escapes embedded quotes so commas in text don't break the CSV. */
    private static String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    // ---------------------------------------------------------------
    // Reports & display
    // ---------------------------------------------------------------
    private static void generateReports() {
        if (petsInTheList.isEmpty()) {
            System.out.println("No Pets Added In The List..");
            return;
        }

        System.out.println(" --- Entering Pet Report Generation --- ");
        boolean running = true;
        while (running) {
            System.out.println(" --- Menu Options --- ");
            System.out.println("a. General Report (Pets with their appointments, total pets, total appointments)");
            System.out.println("b. Pets with upcoming appointments in the next week");
            System.out.println("c. Pets are overdue for a vet visit (last visit > 6 months)");
            System.out.println("e. Exit report generation");

            String choice = input.nextLine();

            switch (choice) {
                case String ch when ch.equalsIgnoreCase("a") -> {
                    Pet top = null;
                    int totalAppointments = 0;
                    for (Pet p : petsInTheList.values()) {
                        if (top == null || p.totalAppointments() > top.totalAppointments()) {
                            top = p;
                        }
                        totalAppointments += p.totalAppointments();
                    }

                    System.out.println(" ---- Pet With Highest Number Of Appointments ---- ");
                    System.out.println(top);
                    System.out.println("Total appointments for the above Pet: " + top.totalAppointments());

                    System.out.println(" ---- Total Pets & Appointments ---- ");
                    System.out.println("Total Pets: " + petsInTheList.size());
                    System.out.println("Total Appointments: " + totalAppointments);
                }

                case String ch when ch.equalsIgnoreCase("b") -> {
                    System.out.println(" --- Upcoming Appointments for next week for each pet --- ");
                    petWithUpcomingAppointmentsInNextWeek();
                }

                case String ch when ch.equalsIgnoreCase("c") ->
                        overDueForAVestVisit();

                case String ch when ch.equalsIgnoreCase("e") ->
                        running = false;


                default -> throw new IllegalStateException("Unexpected value: " + choice);
            }
        }


        displayUpcomingAppointmentsForAllPets();

        System.out.println("\n ---  Past Appointments --- ");
        displayPastAppointmentHistoryForEachPet();
    }

    private static void overDueForAVestVisit() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (Pet pet : petsInTheList.values()) {
            System.out.println("Past (6 months before) appointments for Pet: " + pet);
            for (Appointments a : pet.getAppointmentsList()) {
                if (a.getAppointmentType().equalsIgnoreCase("vet")
                        && a.getAppointmentDateAndTime().isBefore(currentDateTime.minusMonths(3))) {
                    System.out.println(a);
                }
            }
        }
    }

    private static void petWithUpcomingAppointmentsInNextWeek() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime sevenDaysFromToday = currentDateTime.plusDays(7);

        for (Pet pet : petsInTheList.values()) {
            System.out.println("Appointments in this week for Pet: " + pet);
            for (Appointments a : pet.getAppointmentsList()) {
                if (a.getAppointmentDateAndTime().isAfter(currentDateTime) &&
                        a.getAppointmentDateAndTime().isBefore(sevenDaysFromToday)) {
                    System.out.println(a);
                }
            }
        }
    }

    private static void displayRecords() {
        if (petsInTheList.isEmpty()) {
            System.out.println("No Pet Data Found");
            return;
        }

        System.out.println(" --- Entering Pet Information Display --- ");
        boolean running = true;
        while (running) {
            System.out.println(" --- Menu Options --- ");
            System.out.println("a. All registered pets");
            System.out.println("b. All appointments for a specific pet");
            System.out.println("c. Upcoming appointments for all pets");
            System.out.println("d. Past appointment history for each pet");
            System.out.println("e. Exit information display");

            String choice = input.nextLine();
            switch (choice) {
                case String ch when ch.equalsIgnoreCase("a") -> {
                    for (Pet pet : petsInTheList.values()) {
                        System.out.println("\n" + pet);
                    }
                }

                case String ch when ch.equalsIgnoreCase("b") -> {
                    int max = getMaximumId();
                    String petId = "P-" + readInt("Pet ID", 1, max);
                    Pet pet = petsInTheList.get(petId);
                    if (pet == null) {
                        System.out.println("Pet does not exist");
                    } else {
                        System.out.println("   Appointments for " + pet.getName() + ":");
                        for (Appointments a : pet.getAppointmentsList()) {
                            System.out.println("   - " + a);
                        }
                    }
                }

                case String ch when ch.equalsIgnoreCase("c") ->
                    displayUpcomingAppointmentsForAllPets();


                case String ch when ch.equalsIgnoreCase("d") ->
                    displayPastAppointmentHistoryForEachPet();

                case String ch when ch.equalsIgnoreCase("e") ->
                    running = false;

                default -> throw new IllegalStateException("Unexpected value: " + choice);
            }

        }
    }

    private static void displayPastAppointmentHistoryForEachPet() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        for (Pet pet : petsInTheList.values()) {
            System.out.println("Past appointments for Pet: " + pet);
            for (Appointments a : pet.getAppointmentsList()) {
                if (a.getAppointmentDateAndTime().isBefore(currentDateTime)) {
                    System.out.println(a);
                }
            }
        }
    }

    private static void displayUpcomingAppointmentsForAllPets() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        for (Pet pet : petsInTheList.values()) {
            System.out.println("Upcoming appointments for Pet: " + pet);
            for (Appointments a : pet.getAppointmentsList()) {
                if (a.getAppointmentDateAndTime().isAfter(currentDateTime)) {
                    System.out.println(a);
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // Actions
    // ---------------------------------------------------------------
    private static boolean addAppointment() {
        if (petsInTheList.isEmpty()) {
            System.out.println("No pets registered yet. Register a pet first.");
            return false;
        }
        int max = getMaximumId();
        String petId = "P-" + readInt("Pet ID", 1, max);
        Pet pet = petsInTheList.get(petId);
        if (pet == null) {
            System.out.println("Pet does not exist");
            return false;
        }
        String type = readText("appointment type (checkup, vaccination, grooming etc): ");
        LocalDateTime dateTime = readDateTime();
        String notes = readText("notes: ");
        pet.getAppointmentsList().add(new Appointments(type, dateTime, notes));
        return true;
    }

    private static boolean registerPet() {
        String petId = "P-" + (getMaximumId() + 1);
        System.out.println("Assigned Pet ID: " + petId);

        String name = readText("pet name: ");
        String specieOrBreed = readText("species or breed: ");
        int age = readInt("pet age", 0, 100);
        String emailAddress = readText("email address: ");
        String phoneNumber = readText("phone number: ");
        String ownerName = readText("owner name: ");

        petsInTheList.put(petId,
                new Pet(petId, name, specieOrBreed, age, ownerName, emailAddress, phoneNumber));
        return true;
    }

    /** Highest numeric part of the existing IDs (P-1, P-2, ...). Returns 0 if there are no pets. */
    private static int getMaximumId() {
        int max = 0;
        for (String id : petsInTheList.keySet()) {
            try {
                max = Math.max(max, Integer.parseInt(id.substring(2)));
            } catch (NumberFormatException | StringIndexOutOfBoundsException ignored) {
                // skip IDs that don't follow the P-<number> pattern
            }
        }
        return max;
    }

    // ---------------------------------------------------------------
    // Input helpers
    // ---------------------------------------------------------------
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print("Enter " + prompt + " (between " + min + " and " + max + "): ");
            try {
                int value = Integer.parseInt(input.nextLine().trim());
                if (value < min || value > max) {
                    System.out.println("Number must be between " + min + " and " + max + "!");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Not a valid number!");
            }
        }
    }

    public static String readText(String prompt) {
        while (true) {
            System.out.print("Enter " + prompt);
            String inputValue = input.nextLine();
            if (inputValue.isBlank()) {
                System.out.println("Input Text Can't Be Empty Or Blank..");
            } else {
                return inputValue.trim();
            }
        }
    }

    private static LocalDateTime readDateTime() {
        while (true) {
            System.out.print("Enter the appointment date and time (format: 25 Dec, 2026 14:30): ");
            try {
                LocalDateTime dateTime = LocalDateTime.parse(input.nextLine().trim(), DATE_TIME_FORMAT);
                if (dateTime.isBefore(LocalDateTime.now())) {
                    System.out.println("Appointment cannot be in the past!");
                } else {
                    return dateTime;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid Date Format!!");
            }
        }
    }
}