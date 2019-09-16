package com.example.jiyeong;

public class ExpenseDTO {

    private int resld;
    private String date;
    private String context;
    private String charge;
    private String option;
    private String group;




    public ExpenseDTO(String context,String group,String charge){
        this.context=context;
        this.group=group;
        this.charge=charge;

    }

    /*public ExpenseDTO(String date,String context,String charge,String option,String group){
        this.date=date;
        this.context=context;
        this.charge=charge;
        this.option=option;
        this.group=group;
    }*/

    public int getResld() {
        return resld;
    }

    public void setResld(int resld) {
        this.resld = resld;
    }

    public String getDate(){return date;}
    public void setDate(java.lang.String date) { this.date = date; }

    public String getContext(){return context;}
    public void setContext(String context){this.context=context;}

    public String getCharge(){return  charge;}
    public void setCharge(String charge){this.charge=charge;}

    public String getOption(){return option;}
    public void  setOption(String option){this.option=option;}

    public  String getGroup(){return group;}
    public  void setGroup(String group){this.group=group;}



}


