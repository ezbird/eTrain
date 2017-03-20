package sweetdream;

import java.io.PrintStream;
import java.sql.*;


public class eDatabase
{

    Connection conn;

    public eDatabase(String db_file_name_prefix)
        throws Exception
    {
        Class.forName("org.hsqldb.jdbcDriver");
        try
        {
	    String theDatabasePath = "jdbc:hsqldb:file:database/eTrainDatabase";
            conn = DriverManager.getConnection(theDatabasePath, "SA", "");
        }
        catch(SQLException sql)
        {
            System.out.println("Database RUNNING!");
            System.exit(0);
        }
    }

    public void shutdown()
        throws SQLException
    {
        Statement st = conn.createStatement();
        st.execute("SHUTDOWN");
        conn.close();
    }

    public synchronized ResultSet query(String expression)
        throws SQLException
    {
        Statement st = null;
        ResultSet rs = null;
        st = conn.createStatement();
        rs = st.executeQuery(expression);
        st.close();
        return rs;
    }

    public synchronized void update(String expression)
        throws SQLException
    {
        Statement st = null;
        st = conn.createStatement();
        
        int i = st.executeUpdate(expression);
        if(i == -1)
        {
            System.out.println("db error : " + expression);
        }
        st.close();
    }
}

