package com.example.myapp;

import android.telephony.PhoneNumberUtils;

import androidx.annotation.NonNull;

class Contact {
    private String Name;
    private String PhoneNumber;
    private boolean isSelected;

    public Contact(String _Name, String _PhoneNumber, boolean isSelected)
    {
        this.Name = _Name;
        this.PhoneNumber = _PhoneNumber.replaceAll("-", "");
        this.isSelected = isSelected;
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

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    //    public String getId() { return id;}
}
