package com.axon.cloud.cityrecords.bean;



import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class CityInfo implements Writable {

    private String cityName;
    private String date;
    private String telNum;
    
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(cityName);
        out.writeUTF(date);
        out.writeUTF(telNum);
    }
    @Override
    public void readFields(DataInput in) throws IOException {
        this.cityName = in.readUTF();
        this.date = in.readUTF();
        this.telNum = in.readUTF();
        
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTel() {
        return telNum;
    }

    public void setTel(String telNum) {
        this.telNum = telNum;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.telNum.hashCode() + this.cityName.hashCode()
                + this.date.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CityInfo) {
            CityInfo ci = (CityInfo) obj;
            return this.date.equals(ci.date) && this.cityName.equals(ci.cityName)
                    && this.telNum.equals(ci.telNum);
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        return telNum + "\t" + cityName;
    }

    public void set(String cityName, String date, String telNum) {
        this.cityName = cityName;
        this.date = date;
        this.telNum = telNum;
    }
}
