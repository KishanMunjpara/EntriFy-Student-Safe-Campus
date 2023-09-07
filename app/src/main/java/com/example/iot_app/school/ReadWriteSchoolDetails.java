package com.example.iot_app.school;

public class ReadWriteSchoolDetails {
    public String school,pincode,mobile;


    //constructor
    public ReadWriteSchoolDetails(){};
    public ReadWriteSchoolDetails(String textSchoolName,String textPincode,String textMobile){
        this.school=textSchoolName;
        this.pincode=textPincode;
        this.mobile=textMobile;
    }
}
