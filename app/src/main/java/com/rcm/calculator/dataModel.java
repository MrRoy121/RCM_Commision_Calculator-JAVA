package com.rcm.calculator;

public class dataModel {
    String Name;
    double totalpercent;
    public dataModel( String name) {
        Name = name;
        double pv = 0.0;

        if(name.length()>0){
            pv = Double.parseDouble(name);
        }
        if((pv >= 100) && (pv<= 4999)){
            totalpercent = 10.0;
        }else if((pv >= 5000) && (pv<= 9999)){
            totalpercent = 12.0;
        }else if((pv >= 10000) && (pv<= 19999)){
            totalpercent = 14.0;
        }else if((pv >= 20000) && (pv<= 39999)){
            totalpercent = 16.50;
        }else if((pv >= 40000) && (pv<= 69999)){
            totalpercent = 19.0;
        }else if((pv >= 70000) && (pv<= 114999)){
            totalpercent = 21.50;
        }else if((pv >= 115000) && (pv<= 169999)){
            totalpercent = 24.0;
        }else if((pv >= 170000) && (pv<= 259999)){
            totalpercent = 26.50;
        }else if((pv >= 160000) && (pv<= 349999)){
            totalpercent = 29.0;
        }else if(pv >= 350000){
            totalpercent = 32.0;
        }

    }

}
