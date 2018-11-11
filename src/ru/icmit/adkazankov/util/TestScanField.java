package ru.icmit.adkazankov.util;

import ru.icmit.adkazankov.dao.*;
import ru.icmit.adkazankov.domain.*;

public class TestScanField {
    public static void main(String[] args){
//        DbWork.getInstance().showAll(Contact.class);
//        DbWork.getInstance().showAll(PhoneType.class);
//        DbWork.getInstance().showAll(Phone.class);
        for(int i = 0; i < 20; i++)
            System.out.println(ContactDAO.getInstance().getByKey((long) i));
    }
}
