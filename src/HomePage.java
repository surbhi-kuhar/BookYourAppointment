import java.sql.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Scanner;

public class HomePage {
    private static final String url ="jdbc:mysql://localhost:3306/bookyourappointment";
    private static final String username = "root";
    private static final String password = "Supergirl7)";
    public static void main(String args[]){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in);

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            SignUp patient = new SignUp(connection,sc);
            Doctor doctor = new Doctor(connection,sc);
            System.out.println("**************************************** WELCOME !! BOOK YOUR APPOINTMENT NOW ************************************");
            System.out.println("------------------------------------- Enter 1 to login if your account already exists------------------------------");
            System.out.println("--------------------------------------------- Enter 2 to sign up---------------------------------------------------");
            int choice = sc.nextInt();
            switch (choice){
                case 1:
                    int id = SignUp.login(sc,connection);
                    System.out.println("Enter 1 for booking appointment");
                    System.out.println("Enter 2 to view previous appointments");
                    int input = sc.nextInt();
                    if(input == 1){
                        listOfDepartments(sc,patient,doctor,id);
                    }
                    else{
                        SignUp.previousAppointments(id,connection,sc,patient,doctor);
                    }

                    break;
                case 2:
                    int pid = SignUp.signup(sc,connection);
                    System.out.println("Enter 1 for booking appointment");
                    System.out.println("Enter 2 to view previous appointments");
                    int choose = sc.nextInt();
                    if(choose == 1){
                        listOfDepartments(sc,patient,doctor,pid);
                    }
                    else{
                        SignUp.previousAppointments(pid,connection,sc,patient,doctor);
                    }

                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }


    }

    static void listOfDepartments(Scanner sc, SignUp patient, Doctor doctor, int id){
        System.out.println("Book your appointment in any of the following departments:");
        System.out.println("-----------------Enter 1 for Cardiology-----------------");
        System.out.println("-----------------Enter 2 for Neurology------------------");
        System.out.println("-----------------Enter 3 for Pediatrics-----------------");
        System.out.println("-----------------Enter 4 for Radiology------------------");
        System.out.println("-----------------Enter 5 for Internal Medicine----------");
        System.out.println("-----------------Enter 6 for Gastroenterology-----------");
        System.out.println("-----------------Enter 7 for Nephrology-----------------");
        System.out.println("-----------------Enter 8 for Psychiatry-----------------");
        System.out.println("-----------------Enter 9 for Urology--------------------");
        System.out.println("-----------------Enter 10 for Dermatology---------------");

        int choice = sc.nextInt();

        switch (choice){
            case 1:
                Doctor.displayDoctors("Cardiology",patient,doctor,id);
                break;
            case 2:
                Doctor.displayDoctors("Neurology",patient,doctor,id);
                break;
            case 3:
                Doctor.displayDoctors("Pediatrics",patient,doctor,id);
                break;
            case 4:
                Doctor.displayDoctors("Radiology",patient,doctor,id);
                break;
            case 5:
                Doctor.displayDoctors("Internal Medicine",patient,doctor,id);
                break;
            case 6:
                Doctor.displayDoctors("Gastroenterology",patient,doctor,id);
                break;
            case 7:
                Doctor.displayDoctors("Nephrology",patient,doctor,id);
                break;
            case 8:
                Doctor.displayDoctors("Psychiatry",patient,doctor,id);
                break;
            case 9:
                Doctor.displayDoctors("Urology",patient,doctor,id);
                break;
            case 10:
                Doctor.displayDoctors("Dermatology",patient,doctor,id);
                break;
        }

    }

    static synchronized void bookAppointment(Connection connection, Scanner sc, SignUp patient, Doctor doctor, String department, int pid)
    {

            int patient_id = 0;
            boolean validPatientId = false;

            // Loop until a valid patient id is entered
            while (!validPatientId) {
                System.out.println("\nEnter patient id");
                patient_id = sc.nextInt();

                if (patient_id != pid) {
                    System.out.println("Enter your own patient id");
                } else {
                    validPatientId = true;
                }
            }


            boolean validDoctorInfo = false;

            String doctor_name = null;
            int doctor_id=0;

            // Loop until a valid doctor id is entered
            while (!validDoctorInfo) {
                sc.nextLine(); // Consume the newline character before reading the doctor name
                System.out.println("\nEnter name of doctor (note: enter the same name as shown)");
                doctor_name = sc.nextLine();

                System.out.println("\nEnter doctor id");
                doctor_id = sc.nextInt();

                if (!doctor.getDoctorByName(doctor_name, department, doctor_id)) {
                    System.out.println("You entered wrong doctor id or doctor name");
                } else {
                    validDoctorInfo = true;
                }
            }

            LocalDate appointment_date = null;
            boolean validAppointmentDate = false;
            String appointment_date_str = null;

            // Loop for a valid appointment date
            while (!validAppointmentDate) {
                System.out.println("\nEnter appointment date (YYYY-MM-DD):");
                appointment_date_str = sc.next();

                try {
                    appointment_date = LocalDate.parse(appointment_date_str);
                    LocalDate now = LocalDate.now();

                    if (appointment_date.isAfter(now)) {
                        validAppointmentDate = true;
                    } else {
                        System.out.println("Enter a date that is after today's date.");
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please enter the date in the format (YYYY-MM-DD).");
                }
            }

                // check if patient and doctor exist
                if (patient.getPatientById(patient_id)) {
                    // check if doctor is available or not
                    if (doctor.getDoctorByName(doctor_name, department, doctor_id)) {
                        if (Doctor.checkDoctorAvailability(doctor_name, appointment_date_str, connection)) {

                            if(!checkAppointmentOnTheGivenDate(patient_id,appointment_date_str,department,connection)) {

                                String appointment_query = "INSERT INTO appointments(patient_id,doctor_id,appointment_date, department,doctor_name) VALUES (?,?,?,?,?)";
                                try {
                                    PreparedStatement preparedStatement = connection.prepareStatement(appointment_query);
                                    preparedStatement.setInt(1, patient_id);
                                    preparedStatement.setInt(2, doctor_id);
                                    preparedStatement.setString(3, appointment_date_str);
                                    preparedStatement.setString(4, department);
                                    preparedStatement.setString(5, doctor_name);
                                    int rowsAffected = preparedStatement.executeUpdate();

                                    if (rowsAffected > 0) {
                                        System.out.println("\n\n------------------------------Appointment Booked-----------------------------");
                                        System.out.println("View previous appointments? (Yes/No)");

                                        String input = sc.next();
                                        if (input.equalsIgnoreCase("Yes")) {
                                            SignUp.previousAppointments(patient_id, connection, sc, patient, doctor);
                                        }

                                    } else {
                                        System.out.println("Failed to book appointment");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                System.out.println("You already have an appointment booked on this date in this department. Try booking on some other date.");
                            }
                        } else {
                            System.out.println("\nOops!! Doctor is not available on this date. Try on some other date.");
                        }
                    } else {
                        System.out.println("Doctor does not exist");
                    }
                } else {
                    System.out.println("Patient does not exist");
                }
    }

    private static boolean checkAppointmentOnTheGivenDate(int patient_id, String appointment_date, String department, Connection connection){
        String query = "select * from appointments where patient_id=? and appointment_date=? and department=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,patient_id);
            preparedStatement.setString(2, appointment_date);
            preparedStatement.setString(3,department);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
            return false;
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

}
