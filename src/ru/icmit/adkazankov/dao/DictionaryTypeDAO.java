package ru.icmit.adkazankov.dao;



import ru.icmit.adkazankov.domain.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DictionaryTypeDAO<T extends DictionaryType> extends GenericDAOImpl<T> {

    public T getByName(String name){
        String sql = "SELECT * FROM "+getTableName()+" WHERE name = "+name;
        try(PreparedStatement st = db.getConnection().prepareStatement(sql)){
            ResultSet resultSet = st.executeQuery();
            resultSet.next();
            return getObjectFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T getByCode(String code){
        String sql = "SELECT * FROM "+getTableName()+" WHERE code = "+code;
        try(PreparedStatement st = db.getConnection().prepareStatement(sql)){
            ResultSet resultSet = st.executeQuery();
            resultSet.next();
            return getObjectFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
