package sqlconnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    String UserID = "";
    String TrackName = "";
    String Genre = "";
    String Artist = "";
    String Length = "";
    String MoodID = "";
    
     if(args.length == 8)
     {
         MusicTrackStatus = args[1];
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
        Statement SQLStatement = DatabaseConnection.createStatement();
        ResultSet rs;
      
        //TEST
        MusicTrackStatus = "Start";
        UserID = "1";
        TrackName = "Musical Adventures7";
        Genre = "Adventure";
        Artist = "B-Cool";
        Length = "4:45";
        MoodID = "5";
         //TEST
        
        if (MusicTrackStatus == "Start")
        {
            TrackStarted(UserID, TrackName, Genre, Artist, Length,
                 SQLStatement);
        }
        else if (MusicTrackStatus == "End")
        {
            TrackEnded(MoodID, SQLStatement);
        }
        else if (MusicTrackStatus == "UserAlert")
        {
            //Get the Moods and Emoticons
        }

      
        String queryString="select ";
        queryString+="* FROM MOODSCORE";

        //print the query string to the screen
        System.out.println("\nQuery string:");
        System.out.println(queryString);

        //execute the query
        rs=SQLStatement.executeQuery(queryString);

        while (rs.next())
        {
            printResultSetRow(rs);
        }    

        //close the result set
        rs.close();
        System.out.println("Closing database connection");

        //close the database connection
        DatabaseConnection.close();
    }
    catch (SQLException err)
    {
       System.err.println("Error connecting to the database");
       err.printStackTrace(System.err);
       System.exit(0);
    }
    System.out.println("Program finished");
}

    private static void printResultSetRow(ResultSet rs) throws SQLException
    {
        //Use the column name alias as specified in the above query
	String OrganizationName= rs.getString("MoodScoreID");
	String ParentOrganizationName= rs.getString("Mood");
	String CurrencyName= rs.getString("Score");
	System.out.println(ParentOrganizationName+"\t|\t"+ OrganizationName + "\t|\t" + CurrencyName);  
    }
    
    private static int AddTrack(String TrackName, String Genre, String Artist,
            String Length, Statement SQLStatement)
    {
        try
        {
            /*Verify that we haven't already inserted this record before.
            If we have then just get the ID*/
            String SQLQuery = "SELECT TrackID FROM MusicTrack WHERE TrackName = '"
                    + TrackName + "'" ;
            ResultSet rs = SQLStatement.executeQuery(SQLQuery);
        
            String TrackIDString = "-1";
            if (rs.next())
            {
                TrackIDString = rs.getString("TrackID");
            }    
        
            int TrackID = Integer.parseInt(TrackIDString);
            if (TrackID == -1)
            {
                /*Create a new record in the MusicTrack table and get back the
                ID of the newly inserted record*/
                SQLQuery = "SET NOCOUNT ON; INSERT INTO MusicTrack (TrackName, Genre, Artist, Length)\n" +
                        "VALUES('" + TrackName + "', '" + Genre + "', '" +
                        Artist + "', '" + Length + "'); "
                        + "SELECT SCOPE_IDENTITY() AS NewTrackID";
                
                rs = SQLStatement.executeQuery(SQLQuery);
                if (rs.next())
                {
                    TrackIDString = rs.getString("NewTrackID");
                    TrackID = Integer.parseInt(TrackIDString);
                } 
            }
            rs.close();
            return TrackID;      
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return -1;
        }
    }
    
    private static void AddTracksToPlaylist(String UserID, Statement SQLStatement)
    {
        /*Count all rows in the UserMood table that have a match UserID
        and where HasBeenRecommended = “No”.*/
        String SQLQuery = "SELECT Count(UserID) FROM UserMood WHERE UserID = " + "'" +
                UserID + "' AND HasBeenRecommended = 'No'";

        int NonRecommendedCount = 11;

        if (NonRecommendedCount > 10)
        {
            //Checks if the user wants the system to make recommendations.
            SQLQuery = "SELECT Count(UserID) FROM UserSettings WHERE UserID = " + "'" +
                    UserID + "' AND MakeRecommendations = 'Yes'";

            int RecommendationsOn = 1;
            if (RecommendationsOn > 0)
            {
                int TrackIDs[] = MakeRecommendation(UserID, SQLStatement);
                int UserTrackIDs[] = MakeRecommendationUsers(SQLStatement);

                /*Insert a record into the PlayList table with the current UserID, PlayListName
                as ‘Music To Make You Feel Better’ and RecommendedBy as ‘System’.
                Get the PlayListID of this newly inserted record.*/
                SQLQuery = "INSERT INTO PlayList (UserID, PlayListName, RecommendedBy)\n" +
                "VALUES('" + UserID + "', " + "'Music To Make You Feel Better'" + ", '" +
                        "System" + "')\n\nSELECT SCOPE_IDENTITY()";

                int PlayListID = 5;

                for (int i = 0; i < TrackIDs.length; i++)
                {
                    int TrackIDToInsert = TrackIDs[i];

                    /*Insert a record into the TracksInPlayList table with the current
                    UserID, the PlayListID and the RecommendedTrackID from the array.*/
                    SQLQuery = "INSERT INTO TracksInPlayList (UserID, PlayListID, TrackID)\n" +
                            "VALUES('" + UserID + "', '" + PlayListID +
                            "', '" + TrackIDToInsert + "')";
                }

                /*Insert a record into the PlayList table with the current UserID, PlayListName
                as ‘Music that others are listening to’ and RecommendedBy as ‘Users’.
                Get the PlayListID of this newly inserted record.*/
                SQLQuery = "INSERT INTO PlayList (UserID, PlayListName, RecommendedBy)\n" +
                        "VALUES('" + UserID + "', " + "'Music that others are listening to'" +
                        ", '" + "Users" + "')\n\nSELECT SCOPE_IDENTITY()";

                for (int i = 0; i < UserTrackIDs.length; i++)
                {
                    int TrackIDToInsert = UserTrackIDs[i];

                    /*Insert a record into the TracksInPlayList table with the current
                    UserID, the PlayListID and the UserTrackID from the array.*/
                    SQLQuery = "INSERT INTO TracksInPlayList (UserID, PlayListID, TrackID)\n" +
                            "VALUES('" + UserID + "', '" + PlayListID +
                            "', '" + TrackIDToInsert + "')";
                }
            }
        }
    }
    
    private static boolean CheckMoodEntry(String UserID, Statement SQLStatement)
    {
        try
        {
            /*Get the MoodFrequency (String) parameter from the UserSettings
            database table.*/
            String SQLQuery = "SELECT MoodFrequency FROM UserSettings WHERE UserID = "
                    + "'" + UserID + "'";

            ResultSet rs = SQLStatement.executeQuery(SQLQuery);
            
            String MoodFrequency = "";
            if (rs.next())
            {
                MoodFrequency = rs.getString("MoodFrequency");
                MoodFrequency = MoodFrequency.toUpperCase();
            } 

            /*Get the MoodAfterTime (Datetime) of the last entry into the UserMood table.*/
            SQLQuery = "SELECT TOP (1) MoodAfterTime FROM UserMood WHERE UserID = " + "'" +
                    UserID + "'" +
            " ORDER BY MoodAfterTime DESC";
            rs = SQLStatement.executeQuery(SQLQuery);
            
            String MoodAfterTimeString = "";
            if (rs.next())
            {
                MoodAfterTimeString = rs.getString("MoodAfterTime");
            } 

            try
            {
                Date MoodAfterTime = DateTimeFromStringSQLFormat(MoodAfterTimeString);
                Date CurrentDate = new Date();

                long DateDifference = CurrentDate.getTime() - MoodAfterTime.getTime();
                long MinutesDifference = DateDifference / (60 * 1000);

                switch(MoodFrequency)
                {
                    case "ONCE PER TRACK" :
                        return true;
                    case "ONCE EVERY 15 MINUTES":
                        if (MinutesDifference > 15)
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    case "ONCE PER HOUR":
                        if (MinutesDifference > 60)
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    case "ONCE PER 24 HOURS":
                        //1440 - Number of Minutes in a day
                        if (MinutesDifference > 1440)
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    case "NEVER" :
                        return false;
                    default :
                        return false;
                }
            } catch (ParseException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return false;
        }
    }
    
    private static int ConvertMoodToNumber(String MoodName,
            Statement SQLStatement)
    {
        int Score = 0;

        //Select the score from the MoodScore table.
        String SQLQuery = "SELECT Score FROM MoodScore WHERE Mood = " + "'" + MoodName + "'";

        String MoodScore = "2";
        Score = Integer.parseInt(MoodScore);

        return Score;
    }
    
    //Gets a string and formats it into the format used by SQL Server.
    private static Date DateTimeFromStringSQLFormat(String DateString)
            throws ParseException
    {
        SimpleDateFormat SQLServerDateFormat = new
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date FormattedDate = SQLServerDateFormat.parse(DateString);
            return FormattedDate;
        } catch (ParseException e)
        {
            e.printStackTrace();
            throw new ParseException("Invalid Datetime SQL Format", -1);
        }
    }
    
    public static int GetArrayIndexFromString(String[] Array,
            String SearchString)
    {
        int Index=0;
        for(int i=0; i<Array.length; i++)
        {
            if(Array[i].equals(SearchString))
            {
                Index=i;
                break;
            }
        }
        return Index;
    }
    
    //Gets the index place for the highest number in a floating point array.
    public static int GetIndexOfMaximumFloatValue(float[] FloatArray)
    {
        float MaximumValue = FloatArray[0];
        int i;
        for (i = 1; i < FloatArray.length - 1; i++)
        {
            if (FloatArray[i] > MaximumValue)
            {
                MaximumValue = FloatArray[i];
            }
        }
        return i;
    }

    private static int[] MakeRecommendation(String UserID, Statement SQLStatement)
    {
        String[][] GenresAndScores = SetRecommendationScoreForGenre(UserID,
                SQLStatement);

        //Need to convert the strings back to numbers.
        float GenreScores[] = new float[GenresAndScores.length];
        for (int i = 0; i < GenresAndScores.length; i++)
        {
            GenreScores[i] = Float.valueOf(GenresAndScores[1][i]);
        }

        //Now we check which of these genres has the highest number and get its place in the index.
        int Index = GetIndexOfMaximumFloatValue(GenreScores);

        //We then get the string value of this genre from the index.
        String HighestGenre= GenresAndScores[0][Index];

        /*Select 10 unique music and random tracks that belong to this genre. Note ORDER BY newid()
        should make the selection random*/
        String SQLQuery = "SELECT TOP (10) TrackID FROM MusicTrack WHERE Genre = " + "'" +
                HighestGenre + "' ORDER BY newid()";

        int TrackIDs[] = {1,2,3,50,34,43,4,5,9,67};

        return TrackIDs;
    }

    private static int[] MakeRecommendationUsers(Statement SQLStatement)
    {
        String[][] GenresAndScores = SetRecommendationScoreForGenre("", 
                SQLStatement);

        //Need to convert the strings back to numbers.
        float GenreScores[] = new float[GenresAndScores.length];
        for (int i = 0; i < GenresAndScores.length; i++)
        {
            GenreScores[i] = Float.valueOf(GenresAndScores[1][i]);
        }

        //Now we check which of these genres has the highest number and get its place in the index.
        int Index = GetIndexOfMaximumFloatValue(GenreScores);

        //We then get the string value of this genre from the index.
        String HighestGenre= GenresAndScores[0][Index];

        /*Select 10 unique music and random tracks that belong to this genre. Note ORDER BY newid()
        should make the selection random*/
        String SQLQuery = "SELECT TOP (10) TrackID FROM MusicTrack WHERE Genre = '" +
                HighestGenre + "' ORDER BY newid()";

        int TrackIDs[] = {1,2,3,50,34,43,4,5,9,67};

        return TrackIDs;
    }

    private static String[][] SetRecommendationScoreForGenre(String UserID,
            Statement SQLStatement)
    {
        String SQLQuery = "";

        /*Get the list of all unique genres from the MusicTrack table (as long as the genre has
        been listened to at least once by the user)*/
        if (!UserID.equals(""))
        {
            SQLQuery = "SELECT DISTINCT Genre FROM MusicTrack INNER JOIN UserMood ON " +
                    "MusicTrack.TrackID = UserMood.TrackID WHERE UserID = '" + UserID + "'";
        }
        else /*Or get all Genres that all users have ever listened to*/
        {
            SQLQuery = "SELECT DISTINCT Genre FROM MusicTrack";
        }

        int GenreSize = 10;
        String Genres[] = new String[GenreSize];
        for (int i = 0; i < GenreSize; i++)
        {
            Genres[i] = "";
        }

        /*Create a  float Array of the same size as Genres called GenreScores,
        initialize all values as 0.*/
        float GenreScores[] = new float[GenreSize];
        for (int i = 0; i < GenreSize; i++)
        {
            GenreScores[i] = 0;
        }

        /*Create int Array of the same size as Genres called GenreTrackCount, initialize
        all values as 0.*/
        int GenreTrackCount[] = new int[GenreSize];
        for (int i = 0; i < GenreSize; i++)
        {
            GenreTrackCount[i] = 0;
        }

        int TrackArray[] = {1, 2, 4, 23, 12};
        int TrackArraySize = 0;

        /*Get the list of all unique TrackIDs from the UserMood table with a matching UserID.*/
        if (!UserID.equals(""))
        {
            SQLQuery = "SELECT DISTINCT TrackID FROM UserMood WHERE UserID = " +
                    "'" + UserID + "'";
            TrackArraySize = TrackArray.length;
        }
        /*Get the list of all unique TrackIDs from the UserMood table.*/
        else
        {
            SQLQuery = "SELECT DISTINCT TrackID FROM UserMood";
            TrackArraySize = TrackArray.length;
        }

        //Create an empty second float array of the same size as TrackArray called TrackScores.
        float TrackScores[] = new float[TrackArraySize];
        for (int i = 0; i < TrackArraySize; i++)
        {
            /*Get the average mood score for every music track we have.*/
            TrackScores[i] = SetRecommendationScoreForTrack(UserID,
                    String.valueOf(TrackArray[i]), SQLStatement);

            /*For the nth TrackID in the TrackArray, match the TrackID with the Genre through the
            MusicTrack table.*/
            SQLQuery = "SELECT DISTINCT Genre FROM MusicTrack WHERE TrackID = " +
                    "'" + TrackArray[i] + "'";

            String TheGenre = "TEST";

            /*Make sure that the Genre Scores correspond to where the string was originally
            added in the Genre array (so that the Classical Music score should go where
            Classical Music was added)*/
            int GenreIndex = GetArrayIndexFromString(Genres, TheGenre);

            /*Add up the scores for each music track in the same genre and count how many music
            tracks are in that genre.*/
            GenreScores[GenreIndex] = GenreScores[GenreIndex] + TrackScores[i];
            GenreTrackCount[GenreIndex] = GenreTrackCount[GenreIndex] + 1;
        }

        String[][] AverageGenreScores = new String[GenreSize][GenreSize];
        for (int i = 0; i < GenreSize; i++)
        {
            /*We now get the average genre score for each genre, which is all of the track scores
            per genre divided by the number of tracks in that genre.*/
            float TrackCount = GenreTrackCount[i];

            //Avoid Divide by Zero Error
            if (TrackCount == 0.0)
            {
                TrackCount = 1;
            }

            float Average = GenreScores[i]/TrackCount;
            String TheGenre = Genres[i];
            AverageGenreScores[0][i] = TheGenre;
            AverageGenreScores[1][i] = String.valueOf(Average);
        }
        return AverageGenreScores;
    }

    private static float SetRecommendationScoreForTrack(String UserID, String TrackID,
            Statement SQLStatement)
    {
        float Score = 0;
        String SQLQuery = "";
        int MoodTotal = 0;

        if (!UserID.equals(""))
        {
            //Count the number of rows we need to go through.
            SQLQuery = "SELECT Count(UserID) FROM UserMood WHERE UserID = '" + UserID + "' " +
                    "AND TrackID = '" + TrackID + "'";

            int RowCount = 4;

            /*Get the MoodBefore and MoodAfter strings from the UserMood table by matching the
            record to the UserID and TrackID. Note this gets the nth record in the database as
            we want all the accumulated mood scores*/
            int i;
            for (i = 1; i < RowCount; i++)
            {
                SQLQuery = "SELECT TOP(1) MoodBefore FROM (SELECT ROW_NUMBER() OVER" +
                        "(ORDER BY MoodBefore ASC) AS rownumber, MoodBefore FROM UserMood " +
                        "WHERE UserID = '" + UserID + "' AND TrackID = '" + TrackID + "')" +
                        "AS Mood WHERE rownumber = " + i;

                String BeforeMood = "Happy";

                SQLQuery = "SELECT TOP(1) MoodAfter FROM (SELECT ROW_NUMBER() OVER" +
                        "(ORDER BY MoodAfter ASC) AS rownumber, MoodAfter FROM UserMood " +
                        "WHERE UserID = '" + UserID + "' AND TrackID = '" + TrackID + "')" +
                        "AS Mood WHERE rownumber = " + i;

                String AfterMood = "Sad";

                int MoodBeforeNum  = ConvertMoodToNumber(BeforeMood,
                        SQLStatement);
                int MoodAfterNum  = ConvertMoodToNumber(AfterMood,
                        SQLStatement);
                MoodTotal = MoodTotal + (MoodAfterNum - MoodBeforeNum);
            }
            return (float) MoodTotal/(float) i;
        }
        else
        {
            //Count the number of rows we need to go through.
            SQLQuery = "SELECT Count(UserID) FROM UserMood WHERE " +
                    "TrackID = '" + TrackID + "'";

            int RowCount = 4;

            /*Get the MoodBefore and MoodAfter strings from the UserMood table by matching the
            record to the TrackID (getting all tracks regardless of user). Note this gets the nth
            record in the database as we want all the accumulated mood scores*/
            int i;
            for (i = 1; i < RowCount; i++)
            {
                SQLQuery = "SELECT TOP(1) MoodBefore FROM (SELECT ROW_NUMBER() OVER" +
                        "(ORDER BY MoodBefore ASC) AS RowNumber, MoodBefore FROM UserMood " +
                        "WHERE TrackID = '" + TrackID + "')" +
                        " AS Mood WHERE RowNumber = " + i;

                String BeforeMood = "Happy";

                SQLQuery = "SELECT TOP(1) MoodAfter FROM (SELECT ROW_NUMBER() OVER" +
                        "(ORDER BY MoodAfter ASC) AS RowNumber, MoodAfter FROM UserMood " +
                        "WHERE TrackID = '" + TrackID + "')" +
                        " AS Mood WHERE RowNumber = " + i;

                String AfterMood = "Sad";

                int MoodBeforeNum  = ConvertMoodToNumber(BeforeMood,
                        SQLStatement);
                int MoodAfterNum  = ConvertMoodToNumber(AfterMood,
                        SQLStatement);
                MoodTotal = MoodTotal + (MoodAfterNum - MoodBeforeNum);
            }
            return (float) MoodTotal/(float) i;
        }
    }
    
    private static int UserEnterMoodBefore(String UserID, int TrackID,
            Statement SQLStatement)
    {
        try
        {
            if (CheckMoodEntry(UserID, SQLStatement))
            {
                //ALERT TO USER - ENTER MOOD
                boolean UserEnteredMood = true;
                String BeforeMood = "Happy";

                String MoodBeforeTime = "2018-05-23 06:34:46";
                //ALERT TO USER - ENTER MOOD

                if (UserEnteredMood)
                {
                    /*System gets current Date/Time, BeforeMood, UserID and
                    Track ID and adds these to UserMood database table.
                    Get the newly inserted MoodID as well.*/
                        String SQLQuery = "SET NOCOUNT ON;"
                                + "INSERT INTO UserMood (UserID, TrackID, MoodBefore," +
                                "MoodBeforeTime)\n" +
                                "VALUES('" + UserID + "', '" + TrackID + "', '" +
                                BeforeMood + "', '" + MoodBeforeTime + "')\n;" +
                                "SELECT SCOPE_IDENTITY() AS MoodID";

                    ResultSet rs = SQLStatement.executeQuery(SQLQuery);

                    String MoodIDString = "";
                    int MoodID = 0;

                    if (rs.next())
                    {
                        MoodIDString = rs.getString("MoodID");
                        MoodID = Integer.parseInt(MoodIDString);
                    } 
                    return MoodID;
                }
                else
                {
                    return -1;
                }
            }
            else
            {
                return -1;
            }
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return -1;
        }
    }
    
    private static boolean UserEnterMoodAfter(int MoodID, Statement SQLStatement)
    {
        //ALERT TO USER - ENTER AFTER MOOD
        String AfterMood = "Sad";
        String UserLiked = "Yes";
        String MoodAfterTime = "2018-05-23 06:34:46";
        //ALERT TO USER - ENTER AFTER MOOD

        boolean UserEnteredMood = true;

        if (UserEnteredMood)
        {
            /*System to get the BeforeMood from the table by matching this with MoodID.*/
            String SQLQuery = "SELECT MoodBefore FROM UserMood WHERE MoodID = " + "'" +
                    MoodID + "'";

            /*System to get the UserID from the table by matching this with MoodID.*/
            SQLQuery = "SELECT UserID FROM UserMood WHERE MoodID = " + "'" +
                    MoodID + "'";

            String UserID = "1";

            String BeforeMood = "Happy";
            int BeforeScore = ConvertMoodToNumber(BeforeMood, SQLStatement);
            int AfterScore = ConvertMoodToNumber(AfterMood, SQLStatement);
            int ScoreDiff = AfterScore - BeforeScore;

            ScoreDiff = 6; //TEST

            if (ScoreDiff < -3 || ScoreDiff > 3)
            {
                //OPEN DIARY ALERT
                String DiaryEntryText = "Dear Diary...";
                String DiaryEntryTime = "2018-05-23 06:34:46";
                //OPEN DIARY ALERT

                //INSERT DIARY ENTRY
                SQLQuery = "INSERT INTO UserDiary (UserID, DiaryEntryDate, DiaryEntryText)\n" +
                        "VALUES('" + UserID + "', '" + DiaryEntryTime + "', '" +
                        DiaryEntryText +"')";
            }

        /*System gets current Date/Time, AfterMood, UserLiked, MoodID and updates the UserMood
        database table with these parameters where MoodID matches.*/
            SQLQuery = "UPDATE UserMood SET MoodAfter = '" + AfterMood + "', " +
                    "MoodAfterTime = '" + MoodAfterTime + "', " + "UserLiked = '" +
                    UserLiked + "', " + "HasBeenRecommended = '" + "No" + "'\n" +
                    "WHERE MoodID = '" + MoodID + "'";

            AddTracksToPlaylist(UserID, SQLStatement);

            return true;
        }
        return false;
    }
    
    private static int TrackStarted(String UserID, String TrackName, String Genre,
            String Artist, String Length, Statement SQLStatement)
    {
        int TrackID = AddTrack(TrackName, Genre, Artist, Length,
                SQLStatement);
        int MoodID = UserEnterMoodBefore(UserID, TrackID, SQLStatement);
        return MoodID;
    }
    
    public static void TrackEnded(String MoodID, Statement SQLStatement)
    {
        int MoodIDNum = Integer.parseInt(MoodID);
        if (MoodIDNum > 1)
        {
            UserEnterMoodAfter(MoodIDNum, SQLStatement);
        }
    }
}