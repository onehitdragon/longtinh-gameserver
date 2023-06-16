package yoyo.service.tools.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import yoyo.core.event.AbsEvent;

public class DBEvent extends AbsEvent
{
    {
        this.setDest("dbservice");
    }

    private Connection         conn;
    private Statement          stmt;
    private ResultSet          rs;

    private List<List<Object>> list;

    public Connection getConnection ()
    {
        Connection conn = null;
        try
        {
            if (conn == null)
            {
                conn = DriverManager.getConnection("proxool.conn");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return conn;
    }

    public List select(String sql)
    {
        conn = getConnection();
        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            list = new ArrayList<List<Object>>();
            while (rs.next())
            {
                List<Object> obj = new ArrayList<Object>();
                for (int i = 1; i <= columnCount; i++)
                {
                    int type = rsmd.getColumnType(i);
                    switch (type)
                    {
                        case Types.BIGINT:
                        {
                            obj.add(rs.getLong(i));
                            break;
                        }
                        case Types.FLOAT:
                        {
                            obj.add(rs.getFloat(i));
                            break;
                        }
                        case Types.VARCHAR:
                        {
                            obj.add(rs.getString(i));
                            break;
                        }
                        case Types.CHAR:
                        {
                            obj.add(rs.getString(i));
                            break;
                        }
                        case Types.INTEGER:
                        {
                            obj.add(rs.getInt(i));
                            break;
                        }
                        case Types.BOOLEAN:
                        {
                            obj.add(rs.getBoolean(i));
                            break;
                        }
                        case Types.BIT:
                        {
                            obj.add(rs.getByte(i));
                            break;
                        }
                        default:
                            break;
                    }
                }
                list.add(obj);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        close();
        return list;
    }

    public int update (String sql)
    {
        int state = 0;
        try
        {
            conn = getConnection();
            stmt = conn.createStatement();
            state = stmt.executeUpdate(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
        }
        close();
        return state;
    }

    public boolean delete(String sql)
    {
        boolean state = false;
        try
        {
            conn = getConnection();
            stmt = conn.createStatement();
            state = stmt.execute(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        close();
        return state;
    }

    public boolean insert (String sql)
    {
        boolean state = false;
        try
        {
            conn = getConnection();
            stmt = conn.createStatement();
            state = stmt.execute(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        close();
        return state;
    }

    private void close ()
    {
        closeResultSet();
        closeStatement();
        closeConnection();
    }

    private void closeConnection ()
    {
        try
        {
            if (conn != null)
            {
                conn.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void closeStatement ()
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void closeResultSet ()
    {
        try
        {
            if (rs != null)
            {
                rs.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
