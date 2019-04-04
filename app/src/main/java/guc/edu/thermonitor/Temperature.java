package guc.edu.thermonitor;

public class Temperature {
    private String celsius;
    private String fahrenheit;

    public Temperature(){
    }

    public Temperature(String celsius, String fahrenheit){
        this.celsius = celsius;
        this.fahrenheit=fahrenheit;
    }

    public String getCelsius() {

        return celsius;
    }

    public void setCelsius(String celsius) {

        this.celsius = celsius;
    }
    public String getFahrenheit() {
        return fahrenheit;
    }

    public void setFahrenheit(String fahrenheit) {
        this.fahrenheit = fahrenheit;
    }
}
