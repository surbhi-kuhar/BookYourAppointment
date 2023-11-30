import java.sql.*;
import java.util.Date;
import java.util.Scanner;

public class SignUp {
    private Connection connection;
    private Scanner scanner;
    public SignUp(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    static int login(Scanner sc, Connection connection){
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

    static int signup(Scanner sc, Connection connection) {
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


    public boolean getPatientById(int id){
        String query = "select * from logindetails where id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
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

    static void previousAppointments(int pid, Connection connection, Scanner sc, SignUp patient, Doctor doctor) {

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
                HomePage.listOfDepartments(sc, patient, doctor, pid);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
