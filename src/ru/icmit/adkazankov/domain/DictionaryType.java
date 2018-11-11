package ru.icmit.adkazankov.domain;

import ru.icmit.adkazankov.annotations.*;

public abstract class DictionaryType extends Entity {

    @Column(name="name")
    private String name;

    @Column(name="fullname")
    private String fullName;

    @Column(name="code")
    private String code;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    @Override
    public String toString(){
        return id + ": " + name + ", " + fullName + ", " + code;
    }
}
