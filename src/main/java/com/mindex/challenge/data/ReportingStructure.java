package com.mindex.challenge.data;

public class ReportingStructure {

    public ReportingStructure(){}
    public ReportingStructure(String employee, long numberOfReports){
        this.employee = employee;
        this.numberOfReports = numberOfReports;
    }

    private String employee;
    private long numberOfReports;

    public String getEmployee(){
        return employee;
    }

    public void setEmployee(String employee){
        this.employee = employee;
    }

    public long getNumberOfReports(){
        return numberOfReports;
    }

    public void setNumberOfReports(long numberOfReports){
        this.numberOfReports = numberOfReports;
    }

    public ReportingStructure add(ReportingStructure underlings){
        this.numberOfReports = this.numberOfReports + underlings.numberOfReports +1;
        return this;
    }

}
