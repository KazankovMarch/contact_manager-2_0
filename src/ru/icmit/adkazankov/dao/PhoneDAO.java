package ru.icmit.adkazankov.dao;

import ru.icmit.adkazankov.annotations.*;
import ru.icmit.adkazankov.domain.*;
import ru.icmit.adkazankov.dao.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PhoneDAO extends GenericDAOImpl<Phone> {

    private static PhoneDAO instance = new PhoneDAO();

    private PhoneDAO(){}

    public static PhoneDAO getInstance(){
        if(instance == null){
            instance = new PhoneDAO();
        }
        return instance;
    }

    public  ArrayList<Phone> getByContact(Contact contact){
        String sql = "SELECT * FROM "+getTableName()+" WHERE contact_id = "+contact.getId();

        return executeGetMany(sql);
    }

    public  ArrayList<Phone> getByPhoneType(PhoneType type){
        String sql = "SELECT * FROM "+getTableName()+" WHERE phonetype = "+type.getId();
        return executeGetMany(sql);
    }


    @Override
    public Class getTClass() {
        return Phone.class;
    }

}
