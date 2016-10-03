package com.example.sai_praneeth7777.hasportal.Objects;

/**
 * Created by sai_praneeth7777 on 18-Jun-16.
 */
public class ThreadObject {
    private String subject,body,date,time,user,id;
    private String messName;
    private String solved;
    private String solved_by;


    public ThreadObject(String subject, String body, String date, String time, String user, String thread_id, String messName, String solved, String solved_by){
        this.setSubject(subject);
        this.setBody(body);
        this.setUser(user);
        this.setDate(date);
        this.setTime(time);
        this.setId(thread_id);
        this.setMessName(messName);
        this.setSolved(solved);
        this.setSolved_By(solved_by);
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setSubject(String subject) {
            this.subject = subject;
        }

    public void setDate(String date) {
            this.date = date;
        }

    public void setBody(String body) {
        this.body = body;
    }

    public void setId(String id) {this.id = id;}

    public void setSolved(String solved) {this.solved = solved;}

    public void setSolved_By(String solved_by) {this.solved_by = solved_by;}

    public String getSolved(){return solved;}

    public String getSolved_by() {return solved_by;}

    public String getId(){return id;}

    public String getMessName() {return messName;}

    public void setMessName(String messName){this.messName = messName;}


}
