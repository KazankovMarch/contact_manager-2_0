package ru.icmit.adkazankov.domain;

import ru.icmit.adkazankov.annotations.*;
import ru.icmit.adkazankov.dao.GenericDAO;
import ru.icmit.adkazankov.dao.PhoneDAO;

@Table(name="phone", generator = "phone_seq")
public class Phone extends Entity {

    @ManyToOne
    @Column(name="contact_id")
    private Contact contact;

    @ManyToOne
    @Column(name="phonetype_id")
    private PhoneType phoneType;

    @Column(name="phonenumber")
    private String phoneNumber;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString(){
        return id + ": " + phoneNumber +", "+ (phoneType!=null?phoneType.getName():"[null]") +
                ", "+ (contact!=null?contact.getFullName():"[never man]");
    }

    @Override
    public GenericDAO getDAO() {
        return PhoneDAO.getInstance();
    }
}
