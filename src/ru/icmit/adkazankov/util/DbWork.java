package ru.icmit.adkazankov.util;

import ru.icmit.adkazankov.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbWork {

    private static DbWork dbWork;
    private Connection connection;

    public static DbWork getInstance(){
        if (dbWork==null){
            dbWork = new DbWork();
        }
        return dbWork;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()){
            Properties p = getProperties();
            String host = p.getProperty("db_ip");
            String port = p.getProperty("db_port");
            String dbName = p.getProperty("db_name");
            String user = p.getProperty("db_user");
            String password = p.getProperty("db_password");
            String url = "jdbc:postgresql://"+host+":"+port +"/"+dbName;
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    private Properties getProperties(){
        Properties prop = new Properties();;
        File f = new File("dad.properties");
        if (f.exists()) {
            try {
                prop.load(new FileInputStream(f));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return prop;
    }

    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long generateId(String sequenceName){

        Long id = null;
        String sql = "select nextval( ? ) as id";

        try (PreparedStatement st = getConnection().prepareStatement(sql)){

            st.setString(1, sequenceName);

            ResultSet rs = st.executeQuery();

            if (rs.next()){
                id = rs.getLong("id");
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void showAll(Class cl){
        if (cl.isAnnotationPresent(Table.class)){
            Table t = (Table) cl.getAnnotation(Table.class);
            String tName = t.name();
            System.out.println("tableName = "+t.name());
            Class superCl = cl.getSuperclass();
            Field[] sfields = superCl.getDeclaredFields();
            List<String> fList = new ArrayList<>();
            for(Field f : sfields){
                if (f.isAnnotationPresent(Id.class)) {
                    System.out.println(f.getName() + " is Id");
                    fList.add(f.getName());
                }
                else if (f.isAnnotationPresent(Column.class)) {
                    System.out.println(f.getName() + " is column of table, name: ");
                    fList.add(f.getName());
                }
            }
            Field[] fields = cl.getDeclaredFields();
            for(Field f : fields){
                if (f.isAnnotationPresent(Id.class)) {
                    System.out.println(f.getName() + " is Id");
                    fList.add(f.getName());
                }
                if (f.isAnnotationPresent(Column.class)) {
                    Column c = f.getAnnotation(Column.class);
                    fList.add(f.getName());
                    System.out.println(f.getName() + " is column of table, name: "+c.name());
                }
            }
            String sql = "select * from "+tName;
            try (PreparedStatement st = getConnection().prepareStatement(sql)){
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    for (String name : fList) {

                    }
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else{
            throw new IllegalStateException("not a table");
        }
    }

}
