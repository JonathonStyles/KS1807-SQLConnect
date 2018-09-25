package sqlconnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GeneratePlaylists
{
    private static final String JDBCDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String ServerConnectionType = "jdbc:sqlserver";
    private static final String ServerName = "PE-KS1807\\SQLEXPRESS";
    private static final String ServerPort = "1433";
    private static final String DatabaseName = "MFMHDatabase_UAT";
    private static final String DBUser = "TestUser";
    private static final String DBUserPassword = "P@ssw0rd1";

    private static final String JDBC_URL = ServerConnectionType + "://" +
            ServerName + ":" + ServerPort + ";databasename=" + DatabaseName +
            ";user=" + DBUser + ";password=" + DBUserPassword + ";";
    
public static void main(String[] args)
{
    //NEED TO WORK ON THIS - WHAT DATA DO WE PASS INTO THIS PROGRAM AND HOW?
    String MusicTrackStatus = "";
    
     if(args.length == 2)
     {
         MusicTrackStatus = args[1];
     }
    
    /*First try loading the drivers that connect to the server*/
    try
    {
       Class.forName(JDBCDriver).getConstructor().newInstance();
       System.out.println("JDBC driver loaded");
    }
    catch (Exception err)
    {
       System.err.println("Error loading JDBC driver");
       err.printStackTrace(System.err);
       System.exit(0);
    }

    Connection databaseConnection= null;
    try
    {
      //Connect to the database with the connection string.
      databaseConnection = DriverManager.getConnection(JDBC_URL);

      //Create the statement object and result set for SQL Server.
      Statement SQLStatement = databaseConnection.createStatement();
      ResultSet rs;

      
      String queryString="select ";
      queryString+="* FROM MOODSCORE";

      //print the query string to the screen
      System.out.println("\nQuery string:");
      System.out.println(queryString);

      //execute the query
      rs=SQLStatement.executeQuery(queryString);

      //print a header row
      System.out.println("\nParentOrganizationName\t|\tOrganizationName\t|\tCurrencyName");
      System.out.println("----------------------\t|\t----------------\t|\t------------");

      //loop through the result set and call method to print the result set row
      while (rs.next())
      {
            printResultSetRow(rs);
      }    

      //close the result set
      rs.close();
      System.out.println("Closing database connection");

      //close the database connection
      databaseConnection.close();
    }
    catch (SQLException err)
    {
       System.err.println("Error connecting to the database");
       err.printStackTrace(System.err);
       System.exit(0);
    }
    System.out.println("Program finished");
}

public static void printResultSetRow(ResultSet rs) throws SQLException
{
    //Use the column name alias as specified in the above query
	String OrganizationName= rs.getString("MoodScoreID");
	String ParentOrganizationName= rs.getString("Mood");
	String CurrencyName= rs.getString("Score");
	System.out.println(ParentOrganizationName+"\t|\t"+ OrganizationName + "\t|\t" + CurrencyName);  
}
}