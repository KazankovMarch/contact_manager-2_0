package ru.icmit.adkazankov.domain;

import ru.icmit.adkazankov.annotations.*;
import ru.icmit.adkazankov.dao.GenericDAO;
import ru.icmit.adkazankov.dao.PhoneTypeDAO;

@Table(name = "dict_phonetype", generator = "phonetype_seq")
public class PhoneType extends DictionaryType{

    @Override
    public GenericDAO getDAO() {
        return PhoneTypeDAO.getInstance();
    }
}
