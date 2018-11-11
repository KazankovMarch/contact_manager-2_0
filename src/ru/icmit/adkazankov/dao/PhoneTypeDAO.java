package ru.icmit.adkazankov.dao;


import ru.icmit.adkazankov.domain.*;

public class PhoneTypeDAO extends DictionaryTypeDAO<PhoneType> {

    private static PhoneTypeDAO instance = new PhoneTypeDAO();

    private PhoneTypeDAO(){}

    @Override
    public Class getTClass() {
        return PhoneType.class;
    }

    public static PhoneTypeDAO getInstance(){
        if(instance == null){
            instance = new PhoneTypeDAO();
        }
        return instance;
    }
}
