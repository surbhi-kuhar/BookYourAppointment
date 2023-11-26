import java.sql.*;
import java.text.DateFormat;
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
            System.out.println("WELCOME !! BOOK YOUR APPOINTMENT NOW");
            System.out.println("Enter 1 to login if your account already exists");
            System.out.println("Enter 2 to sign up");
            int choice = sc.nextInt();
            switch (choice){
                case 1:
                    login(sc,connection);
                    listOfDepartments(sc,connection,patient,doctor);
                    break;
                case 2:
                    signup(sc,connection);
                    listOfDepartments(sc,connection,patient,doctor);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }


    }

    static void listOfDepartments(Scanner sc, Connection connection,SignUp patient, Doctor doctor){
        System.out.println("Book your appointment in any of the following departments");
        System.out.println("Enter 1 for Cardiology");
        System.out.println("Enter 2 for Neurology");
        System.out.println("Enter 3 for Pediatrics");
        System.out.println("Enter 4 for Radiology");
        System.out.println("Enter 5 for Internal Medicine");
        System.out.println("Enter 6 for Gastroenterology");
        System.out.println("Enter 7 for Nephrology");
        System.out.println("Enter 8 for Psychiatry");
        System.out.println("Enter 9 for Urology");
        System.out.println("Enter 10 for Dermatology");

        int choice = sc.nextInt();

        switch (choice){
            case 1:
                displayDoctors("Cardiology",connection,sc,patient,doctor);
                break;
            case 2:
                displayDoctors("Neurology",connection,sc,patient,doctor);
                break;
            case 3:
                displayDoctors("Pediatrics",connection,sc,patient,doctor);
                break;
            case 4:
                displayDoctors("Radiology",connection,sc,patient,doctor);
                break;
            case 5:
                displayDoctors("Internal Medicine",connection,sc,patient,doctor);
                break;
            case 6:
                displayDoctors("Gastroenterology",connection,sc,patient,doctor);
                break;
            case 7:
                displayDoctors("Nephrology",connection,sc,patient,doctor);
                break;
            case 8:
                displayDoctors("Psychiatry",connection,sc,patient,doctor);
                break;
            case 9:
                displayDoctors("Urology",connection,sc,patient,doctor);
                break;
            case 10:
                displayDoctors("Dermatology",connection,sc,patient,doctor);
                break;
        }

    }

    static void displayDoctors(String departmentName, Connection connection,Scanner sc,SignUp patient, Doctor doctor){
        System.out.println("Doctors in " + departmentName + " department");
        String query = "select * from doctors where specialization=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,departmentName);
            ResultSet resultSet  = preparedStatement.executeQuery();
            if(resultSet.next()){
                String name = resultSet.getString("name");
                System.out.println(name);
            }
            System.out.println("Start Booking appointment");
            bookAppointment(connection,sc,patient,doctor,departmentName);

        }
        catch (SQLException e){
            e.printStackTrace();
        }


    }

    public static void login(Scanner sc, Connection connection){
        System.out.println("Enter your patient id");
        int pid = sc.nextInt();
        String query = "select * from logindetails where ID=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,pid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                String name = resultSet.getString("Name");
                System.out.println("Logged in successfully");
                System.out.println("Welcome " + name.toUpperCase());
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void signup(Scanner sc, Connection connection){
        System.out.println("Enter patient's name: ");
        String name = sc.next();
        System.out.println("Enter patient's age: ");
        int age = sc.nextInt();
        System.out.println("Enter patient's gender: ");
        String gender = sc.next();

        try{
            String query = "INSERT INTO logindetails(name,age,gender) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2,age);
            preparedStatement.setString(3,gender);

            int affectedRows = preparedStatement.executeUpdate();   // returns number of rows affected

            if(affectedRows>0){
                System.out.println("Signed Up Successfully!!");
            }
            else{
                System.out.println("Failed to sign up");
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Connection connection, Scanner sc, SignUp patient, Doctor doctor,String department){
        System.out.println("Enter patient id");
        int patient_id = sc.nextInt();
        System.out.println("Enter doctor name");
        String doctor_name = sc.next();
        System.out.println("Enter appointment date (YYYY-MM-DD):");
        String appointment_date = sc.next();

        // check if patient and doctor exists
        if(patient.getPatientById(patient_id) && doctor.getDoctorByName(doctor_name.trim())){
            // check if doctor is available or not
            if(checkDoctorAvailability(doctor_name,appointment_date,connection)){
                String appointment_query = "INSERT INTO appointments(patient_id, doctor_name, appointment_date, department) VALUES (?,?,?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointment_query);
                    preparedStatement.setInt(1, patient_id);
                    preparedStatement.setString(2, doctor_name);
                    preparedStatement.setString(3, appointment_date);
                    preparedStatement.setString(4, department);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked");
                    }
                    else{
                        System.out.println("Failed to book appointment");
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Doctor is not available on this date");
            }
        }
        else{
            System.out.println("Either doctor or patient does not exist");
        }

    }

    public static boolean checkDoctorAvailability(String doctor_name, String appointment_date,Connection connection){
        String query = "select COUNT(*) from appointments where doctor_name=? AND appointment_date=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, doctor_name);
            preparedStatement.setString(2, appointment_date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count<=1){
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

}
