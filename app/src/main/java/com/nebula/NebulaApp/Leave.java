package com.nebula.NebulaApp;

public class Leave {
    private String Title, Dates, TotalDays, Status;
    private String RequestedBy;
    private String Description;

    public Leave() {}

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getDates() {
        return Dates;
    }

    public void setDates(String Dates) {
        this.Dates = Dates;
    }

    public String getTotaldays() {
        return TotalDays;
    }

    public void setTotaldays(String TotalDays) {
        this.TotalDays = TotalDays;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getRequestedBy() {
        return RequestedBy;
    }

    public void setRequestedBy(String RequestedBy) {
        this.RequestedBy = RequestedBy;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }
}
