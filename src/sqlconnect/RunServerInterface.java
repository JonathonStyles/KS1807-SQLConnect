package sqlconnect;
import java.io.*;
import java.util.regex.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RunServerInterface
{
    private static final String JDBCDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String ServerConnectionType = "jdbc:sqlserver";
    
    private static String GetConfigurationDetails()
    {
        // The name of the file to open.
        String ConfigurationFileName = "src\\sqlconnect\\SQLConnectionInfo.ini";
        String Line;
        String[] ConfigurationItems = new String[5];
        String Text = System.getProperty("user.dir");
        
        try
        {
            FileReader fileReader = new FileReader(ConfigurationFileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int i = 0;
            //Read first five lines of the configuration file only
            while((Line = bufferedReader.readLine()) != null && i < 5)
            {
                ConfigurationItems[i] = Line;
                i++;
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("Unable to open server configuratioon file '" +
                    ConfigurationFileName + "'");                
        }
        catch(IOException ex)
        {
            System.out.println(
                "Error reading server configuratioon file '" +
                        ConfigurationFileName + "'");                  
        }
        
        //Get the part of the configuration line between the single quotes.
        String[] ConfigStrings = new String[5];
        Pattern p = Pattern.compile("'([^']*)'");
        for (int i = 0; i < 5; i++)
        {
            Matcher m = p.matcher(ConfigurationItems[i]);
            if (m.find())
            {
                ConfigStrings[i] = m.group();
                //Get rid of the single quotes from the string.
                ConfigStrings[i] = ConfigStrings[i].replace("'", "");
            }
        }
        
        String ServerName = ConfigStrings[0];
        String ServerPort = ConfigStrings[1];
        String DatabaseName = ConfigStrings[2];
        String DBUser = ConfigStrings[3];
        String DBUserPassword = ConfigStrings[4];

        String JDBC_URL = ServerConnectionType + "://" +
            ServerName + ":" + ServerPort + ";databasename=" + DatabaseName +
            ";user=" + DBUser + ";password=" + DBUserPassword + ";";
        return JDBC_URL;
    }
    
    public static void main(String[] args)
    {
        //NEED TO WORK ON THIS - WHAT DATA DO WE PASS INTO THIS PROGRAM AND HOW?
        String QueryType = "";
        int UserID = 0;
        String TrackName = "";
        String Genre = "";
        String Artist = "";
        String Length = "";
        String MoodID = "";
        
        //User Test Data
        String FirstName = "Joseph";
        String LastName= "Zhang";
        String EmailAddress = "test@test.com";
        String DateOfBirth = "09-26-2000 05:23:34";
        String Gender = "Female";
        String UserPassword = "Testing";
        
        String PreferredPlatform = "Spotify";
        String MusicQuestionOne = "Quest1";
        String MusicQuestionTwo = "Quest2";
        String MusicQuestionThree = "Quest3";
        
        String MakeRecommendations = "Yes";
        String MoodFrequency = "Once Per Hour";

         if(args.length == 8)
         {
             QueryType = args[1];
             UserID = Integer.parseInt(args[2]);
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
            Connection DatabaseConnection = DriverManager.getConnection(GetConfigurationDetails());

            //Create the statement object and result set for SQL Server.
            Statement SQLStatement = DatabaseConnection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs;

            //TEST
            QueryType = "UpdateUserSecondPage";
            UserID = 1;
            TrackName = "Musical Adventures7";
            Genre = "Adventure";
            Artist = "B-Cool";
            Length = "4:45";
            MoodID = "5";
             //TEST
             
             GeneratePlayLists Playlist = new GeneratePlayLists();
             ApplicationUserQueries UserQuery = new ApplicationUserQueries();

            switch (QueryType)
            {
                case "TrackStarted":
                    Playlist.TrackStarted(UserID, TrackName, Genre, Artist,
                            Length, SQLStatement);
                    break;
                case "TrackEnded":
                    Playlist.TrackEnded(MoodID, SQLStatement);
                    break;
                case "GetMusicHistory":
                    UserQuery.GetMusicHistory(UserID, SQLStatement);
                    break; 
                case "GetUserDetailsRegistration":
                    UserQuery.GetUserDetailsRegistration(UserID, SQLStatement);
                    break;
                case "GetUserDetails":
                    UserQuery.GetUserDetails(UserID, SQLStatement);
                    break;
                case "GetUserID":
                    UserQuery.GetUserID(EmailAddress, SQLStatement);
                    break;
                case "GetUserSettings":
                    UserQuery.GetUserSettings(UserID, SQLStatement);
                    break;
                case "IsEmailAddressUnique":
                    UserQuery.IsEmailAddressUnique(EmailAddress, SQLStatement);
                    break;
                case "InsertNewUser":
                    UserQuery.InsertNewUser(FirstName, LastName, EmailAddress,
                            DateOfBirth, Gender, UserPassword, SQLStatement);
                    break;   
                case "UpdatePassword":
                        UserQuery.UpdatePassword(UserPassword, UserID,
                                SQLStatement);
                    break;
                case "UpdateNewUser":
                    UserQuery.UpdateNewUser(FirstName, LastName, EmailAddress,
                            DateOfBirth, Gender, UserPassword, UserID,
                            SQLStatement);
                    break;
                case "UpdateUserSecondPage":
                    UserQuery.UpdateUserSecondPage(PreferredPlatform,
                            MusicQuestionOne, MusicQuestionTwo,
                            MusicQuestionThree, UserID, SQLStatement);
                    break;
                case "UpdateUser":
                        UserQuery.UpdateUser(FirstName, LastName, EmailAddress,
                            DateOfBirth, Gender, UserID, SQLStatement);
                    break;
                case "UpdateSettings":
                        UserQuery.UpdateSettings(MakeRecommendations,
                                MoodFrequency, UserID, SQLStatement);
                    break;
                case "VerifyLogin":
                        UserQuery.VerifyLogin(EmailAddress, UserPassword,
                                SQLStatement);
                    break;
                case "VerifyPassword":
                        UserQuery.VerifyPassword(UserID, UserPassword,
                                SQLStatement);
                    break;
                default:
                    break;
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