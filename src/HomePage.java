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
            Doctor doctor = new Doctor(connection);
            System.out.println("**************************************** WELCOME !! BOOK YOUR APPOINTMENT NOW ************************************");
            System.out.println("------------------------------------- Enter 1 to login if your account already exists------------------------------");
            System.out.println("--------------------------------------------- Enter 2 to sign up---------------------------------------------------");
            int choice = sc.nextInt();
            switch (choice){
                case 1:
                    int id = login(sc,connection);
                    System.out.println("Enter 1 for booking appointment");
                    System.out.println("Enter 2 to view previous appointments");
                    int input = sc.nextInt();
                    if(input == 1){
                        listOfDepartments(sc,connection,patient,doctor,id);
                    }
                    else{
                        previousAppointments(id,connection,sc,patient,doctor);
                    }

                    break;
                case 2:
                    int pid = signup(sc,connection);
                    System.out.println("Enter 1 for booking appointment");
                    System.out.println("Enter 2 to view previous appointments");
                    int choose = sc.nextInt();
                    if(choose == 1){
                        listOfDepartments(sc,connection,patient,doctor,pid);
                    }
                    else{
                        previousAppointments(pid,connection,sc,patient,doctor);
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

    private static void listOfDepartments(Scanner sc, Connection connection,SignUp patient, Doctor doctor,int id){
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
                displayDoctors("Cardiology",connection,sc,patient,doctor,id);
                break;
            case 2:
                displayDoctors("Neurology",connection,sc,patient,doctor,id);
                break;
            case 3:
                displayDoctors("Pediatrics",connection,sc,patient,doctor,id);
                break;
            case 4:
                displayDoctors("Radiology",connection,sc,patient,doctor,id);
                break;
            case 5:
                displayDoctors("Internal Medicine",connection,sc,patient,doctor,id);
                break;
            case 6:
                displayDoctors("Gastroenterology",connection,sc,patient,doctor,id);
                break;
            case 7:
                displayDoctors("Nephrology",connection,sc,patient,doctor,id);
                break;
            case 8:
                displayDoctors("Psychiatry",connection,sc,patient,doctor,id);
                break;
            case 9:
                displayDoctors("Urology",connection,sc,patient,doctor,id);
                break;
            case 10:
                displayDoctors("Dermatology",connection,sc,patient,doctor,id);
                break;
        }

    }

    private static void displayDoctors(String departmentName, Connection connection,Scanner sc,SignUp patient, Doctor doctor,int pid){
        System.out.println("\nDoctors in " + departmentName + " department");
        System.out.println("+-------------+--------------------------+");
        System.out.println("| Doctor ID   | Name                     |");
        System.out.println("+-------------+--------------------------+");

        String query = "select * from doctors where specialization=?";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, departmentName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.printf("|%-13s|%-26s|\n",id, name);
                System.out.println("+-------------+--------------------------+");
            }


            System.out.println("\n\nStart Booking appointment:");
            bookAppointment(connection,sc,patient,doctor,departmentName,pid);

        }
        catch (SQLException e){
            e.printStackTrace();
        }


    }

    private static int login(Scanner sc, Connection connection){
        System.out.println("\nEnter your patient id");
        int pid = sc.nextInt();
        String query = "select * from logindetails where ID=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,pid);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                String name = resultSet.getString("Name");
                System.out.println("Logged in successfully !!\n");
                System.out.println("-----------------------------Welcome " + name.toUpperCase()+"-----------------------------\n");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return pid;
    }

    private static int signup(Scanner sc, Connection connection) {
        System.out.println("\nEnter patient's name: ");
        String name = sc.next();
        System.out.println("\nEnter patient's age: ");
        int age = sc.nextInt();
        System.out.println("\nEnter patient's gender: ");
        String gender = sc.next();

        int patientId = 0;
        try {
            String query = "INSERT INTO logindetails(name,age,gender) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);


            int affectedRows = preparedStatement.executeUpdate();   // returns number of rows affected

            if (affectedRows > 0) {
                // Retrieve the generated keys (patient ID)
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    patientId = generatedKeys.getInt(1);
                    System.out.println("Signed Up Successfully!! Your Patient ID is: " + patientId + "\n" + " You will need it next time you login.");

                }
            } else {
                System.out.println("Failed to sign up\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patientId;
    }

    private static synchronized void bookAppointment(Connection connection, Scanner sc, SignUp patient, Doctor doctor, String department, int pid) {

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
                        if (checkDoctorAvailability(doctor_name, appointment_date_str, connection)) {

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
                                            previousAppointments(patient_id, connection, sc, patient, doctor);
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

    private static boolean checkDoctorAvailability(String doctor_name, String appointment_date,Connection connection){
        String query = "select COUNT(*) from appointments where doctor_name=? AND appointment_date=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, doctor_name);
            preparedStatement.setString(2, appointment_date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count<20){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;

    }

    private static void previousAppointments(int pid, Connection connection, Scanner sc,SignUp patient, Doctor doctor) {

        System.out.println("-----------------------------YOUR PREVIOUS APPOINTMENTS---------------------------------------------");

        System.out.println("+-------------+-------------+----------------+---------------------------+-------------------------+");
        System.out.println("| Patient ID  | Doctor ID   | Doctor Name    | Department                | Appointment Date        |");
        System.out.println("+-------------+-------------+----------------+---------------------------+-------------------------+");

        String query = "select * from appointments where patient_id=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, pid);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int pat_id = resultSet.getInt("patient_id");
                int doc_id = resultSet.getInt("doctor_id");
                String doctorName = resultSet.getString("doctor_name");
                String department = resultSet.getString("department");
                Date app_date = resultSet.getDate("appointment_date");

                System.out.printf("|%-13s|%-13s|%-16s|%-27s|%-25s|\n", pat_id, doc_id, doctorName, department, app_date);
                System.out.println("+-------------+-------------+----------------+---------------------------+-------------------------+");
            }

            System.out.println("Want to book appointment ? (Yes/No)");
            String input = sc.next();
            if (input.equalsIgnoreCase("Yes")) {
                listOfDepartments(sc, connection, patient, doctor, pid);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // one user should be able to make only one appointment per day

}
