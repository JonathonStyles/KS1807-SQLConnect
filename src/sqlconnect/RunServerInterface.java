package sqlconnect;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RunServerInterface
{
    private static final String JDBCDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String ServerConnectionType = "jdbc:sqlserver";
    
    //May need to pass these parameters in through a configuration file
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
        String QueryType = "";
        String UserID = "";
        String TrackName = "";
        String Genre = "";
        String Artist = "";
        String Length = "";
        String MoodID = "";
        
        //
        String FirstName = "Joseph";
        String LastName= "Zhang";
        String EmailAddress = "test@test.com";
        String DateOfBirth = "09-26-2000 05:23:34";
        String Gender = "Female";
        String UserPassword = "Testing";

         if(args.length == 8)
         {
             QueryType = args[1];
             UserID = args[2];
             TrackName = args[3];
             Genre = args[4];
             Artist = args[5];
             Length = args[6];
             MoodID = args[7];
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

        try
        {
            //Connect to the database with the connection string.
            System.out.println(JDBC_URL);
            Connection DatabaseConnection = DriverManager.getConnection(JDBC_URL);

            //Create the statement object and result set for SQL Server.
            Statement SQLStatement = DatabaseConnection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs;

            //TEST
            QueryType = "InsertUser";
            UserID = "1";
            TrackName = "Musical Adventures7";
            Genre = "Adventure";
            Artist = "B-Cool";
            Length = "4:45";
            MoodID = "5";
             //TEST
             
             GeneratePlayLists Playlist = new GeneratePlayLists();
             ApplicationUserQueries UserQuery = new ApplicationUserQueries();

            if (QueryType.equals("MusicTrackStart"))
            {
                Playlist.TrackStarted(UserID, TrackName, Genre, Artist, Length,
                     SQLStatement);
            }
            else if (QueryType.equals("MusicTrackEnd"))
            {
                Playlist.TrackEnded(MoodID, SQLStatement);
            }
            else if (QueryType.equals("InsertUser"))
            {
                UserQuery.InsertNewUser(FirstName, LastName, EmailAddress,
                        DateOfBirth, Gender, UserPassword, SQLStatement);
            }
            else if (QueryType.equals("UpdateUser"))
            {
                
            }
            //close the database connection
            DatabaseConnection.close();
        }
        catch (SQLException err)
        {
           System.err.println("Error connecting to the database");
           err.printStackTrace(System.err);
           System.exit(0);
        }
    }
    
    
}