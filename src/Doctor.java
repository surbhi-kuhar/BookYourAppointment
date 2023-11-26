import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor {

    private Connection connection;
    public Doctor(Connection connection){
        this.connection = connection;
    }

    public void viewDoctors(){
        String query = "select * from doctors";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("+-------------+--------------------------+------------------------+");
            System.out.println("| Doctor ID   | Name                     |  Specialization        |");
            System.out.println("+-------------+--------------------------+------------------------+");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("|%-13s|%-26s|%-24s|\n",id, name, specialization);
                System.out.println("+-------------+--------------------------+------------------------+");
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean getDoctorByName(String name,String department){
        String query = "select * from doctors where name = ? AND specialization= ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,department);
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
