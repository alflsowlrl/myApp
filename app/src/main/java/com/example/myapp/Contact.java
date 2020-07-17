package com.example.myapp;

class Contact {
    private String Name;
    private String PhoneNumber;
    private String id;

    public Contact(String _Name, String _PhoneNumber)
    {
        this.Name = _Name;
        this.PhoneNumber = _PhoneNumber;
    }

    public String getName() {
        return Name;
    }

    public String getNumber() {
        return PhoneNumber;
    }

//    public String getId() { return id;}
}
