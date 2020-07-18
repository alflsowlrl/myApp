package com.example.myapp;

import android.telephony.PhoneNumberUtils;

import androidx.annotation.NonNull;

class Contact {
    private String Name;
    private String PhoneNumber;

    public Contact(String _Name, String _PhoneNumber)
    {
        this.Name = _Name;
        this.PhoneNumber = _PhoneNumber.replaceAll("-", "");
    }

    public String getName() {
        return Name;
    }

    public String getNumber() {
        return PhoneNumberUtils.formatNumber(PhoneNumber);
    }

    @NonNull
    @Override
    public String toString() {
        super.toString();
        return "name: " + Name + " phone: " + PhoneNumber;
    }

    //    public String getId() { return id;}
}
