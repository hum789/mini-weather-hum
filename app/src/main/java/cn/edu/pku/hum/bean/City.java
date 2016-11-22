package cn.edu.pku.hum.bean;

/**
 * Created by hum on 2016/10/18.
 */
public class City {
    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFristPY;

    public City(String province, String city, String number, String
            firstPY, String allPY, String allFristPY) {
        this.province = province;
        this.city = city;
        this.number = number;
        this.firstPY = firstPY;
        this.allPY = allPY;
        this.allFristPY = allFristPY;
    }

    public String getProvince() {
        return province;
    }
    public String getNumber() {
        return number;
    }
    public String getFirstPY() {
        return firstPY;
    }
    public String getAllPY() {
        return allPY;
    }
    public String getAllFristPY() {
        return allFristPY;
    }
    public String getCity() {
        return city;
    }

    public void setProvince(String city) {
        this.province = province;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setNumber(String city) {
        this.number = number;
    }
    public void setFirstPY(String city) {
        this.firstPY = firstPY;
    }
    public void setAllPY(String city) {
        this.allPY = allPY;
    }
    public void setAllFristPY(String city) {
        this.allFristPY = allFristPY;
    }

}
