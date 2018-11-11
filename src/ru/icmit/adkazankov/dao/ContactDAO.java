package ru.icmit.adkazankov.dao;



import ru.icmit.adkazankov.domain.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactDAO extends GenericDAOImpl<Contact> {

    private static ContactDAO instance = new ContactDAO();

    private ContactDAO(){}

    public static ContactDAO getInstance() {
        if(instance==null){
            instance = new ContactDAO();
        }
        return instance;
    }


    @Override
    public Class getTClass() {
        return Contact.class;
    }

}
