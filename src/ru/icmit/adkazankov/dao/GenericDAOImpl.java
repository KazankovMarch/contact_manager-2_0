package ru.icmit.adkazankov.dao;

import ru.icmit.adkazankov.Main;
import ru.icmit.adkazankov.annotations.*;
import ru.icmit.adkazankov.domain.*;
import ru.icmit.adkazankov.util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public abstract class GenericDAOImpl<T extends Entity> implements GenericDAO<T> {

    protected DbWork db = DbWork.getInstance();
    protected GenericDAOImpl(){}
    protected List<Field> columns = getColumns();

    @Override
    public T create(T o) {
        try {
            if(o.getId()==null){

                o.setId(generateId());
            }
            StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ").append(getTableName()).append(" VALUES ('");
            for(Field col : columns){
                col.setAccessible(true);
                if(col.getAnnotation(ManyToOne.class)==null) {
                    sqlBuilder.append(col.get(o)).append("', '");
                }
                else {
                    sqlBuilder.append(((Entity)col.get(o)).getId()).append("', '");
                }
            }
            sqlBuilder.delete(sqlBuilder.length()-4,sqlBuilder.length()-1);
            sqlBuilder.append(");");
            executeUpdate(sqlBuilder.toString());
            return o;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void update(T o) {
        if(o.getId()==null){
            create(o);
        }
        else {
            try {
                StringBuilder sqlBuilder = new StringBuilder("UPDATE ").append(getTableName()).append(" SET ");
                Field idColumn = null;
                for (Field col : columns) {
                    if (col.getAnnotation(Id.class) == null) {
                        col.setAccessible(true);
                        if(col.getAnnotation(ManyToOne.class)==null)
                            sqlBuilder.append(col.getAnnotation(Column.class).name()).append(" = '").append(col.get(o)).append("', ");
                        else{
                            sqlBuilder.append(col.getAnnotation(Column.class).name()).append(" = ");
                            sqlBuilder.append(((Entity)col.get(o)).getId()).append(", "); //it's OK? idk :-(
                        }
                    } else {
                        idColumn = col;
                    }
                }
                sqlBuilder.deleteCharAt(sqlBuilder.length()-2);
                idColumn.setAccessible(true);
                sqlBuilder.append("WHERE ").append(idColumn.getAnnotation(Column.class).name()).append(" = ").append(idColumn.get(o));
                executeUpdate(sqlBuilder.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(T o){
        String sql = "DELETE FROM "+getTableName()+" WHERE ID = "+o.getId();
        try(Connection connection = db.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.executeUpdate();
        } catch (SQLException e) {
            if(e.getMessage().contains("violates foreign key constraint")){
                Main.showError("Some objects are not deleted","because deletion violates foreign key constraint");
            }
            else {
                e.printStackTrace();
            }
        }
    }

    private List<Field> getColumns() {
        HashMap<String, Field> map = new HashMap<>();
        getColumns(getTClass(),map);
        try(Statement statement = db.getConnection().createStatement()){
            ResultSet rs = statement.executeQuery("select column_name from information_schema.columns where information_schema.columns.table_name='"+getTableName()+"'");
            List<Field> result = new ArrayList<>();
            while (rs.next()){
                result.add(map.get(rs.getString(1)));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getColumns(Class cl, Map<String, Field> map) {
        Field[] sfields = cl.getDeclaredFields();
        for(Field f : sfields){
            if (f.isAnnotationPresent(Id.class)) {
                map.put(f.getAnnotation(Column.class).name(), f);
            }
            else if (f.isAnnotationPresent(Column.class)) {
                map.put(f.getAnnotation(Column.class).name(), f);
            }
        }
        if(cl != Entity.class){
            Class superCl = cl.getSuperclass();
            getColumns(superCl,map);
        }
    }

    public abstract Class getTClass();

    @Override
    public ArrayList<T> getAll() {
        String sql = "SELECT * FROM "+getTableName();
        return executeGetMany(sql);
    }

    @Override
    public T getByKey(Long id) {
        String sql = "SELECT * FROM "+getTableName()+" WHERE ID = ?";
        ResultSet resultSet = null;
        try(Connection connection = db.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1,id);
            System.out.println(statement.toString());
            resultSet = statement.executeQuery();
            if(resultSet.next())
                return getObjectFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int updateFromFile(File file, String separator) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String s;
        //reader.read();
        int count = 0;
        while((s = reader.readLine()) != null){
            String[] split = s.split(separator);
            System.out.println(Arrays.toString(split));
            update(getObjectFromStringArray(split));
            count++;
        }
        return count;
    }


    public int executeUpdate(String sql){
        System.out.println("EXECUTE UPDATE: "+sql);
        try (PreparedStatement st = db.getConnection().prepareStatement(sql)){
            return st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ArrayList<T> executeGetMany(String sql){
        ResultSet resultSet = null;
        ArrayList<T> result = new ArrayList<>();
        try(Connection connection = db.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                result.add(getObjectFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public T getObjectFromStringArray(String[] split) {
        try {
            T res = (T) getTClass().newInstance();
            int i = 0;
            for(Field col : columns){
                col.setAccessible(true);
                col.set(res, split[i]);
                i++;
            }
            return res;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T getObjectFromResultSet(ResultSet rs){
        try {
            T res = (T) getTClass().newInstance();
            for(Field col : columns){
                col.setAccessible(true);
                if(col.getAnnotation(ManyToOne.class)==null) {
                    col.set(res, rs.getObject(col.getAnnotation(Column.class).name()));
                }
                else {
                    Long l = rs.getLong(col.getAnnotation(Column.class).name());
                    Entity entity = (Entity) col.getType().newInstance();
                    entity = (Entity) entity.getDAO().getByKey(l);
                    col.set(res, entity);
                }
            }
            return res;
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName(){
        Table t = (Table) getTClass().getAnnotation(Table.class);
        return t.name();
    }
    private Long generateId() {
        Table t = (Table) getTClass().getAnnotation(Table.class);
        return db.generateId(t.generator());
    }

}
