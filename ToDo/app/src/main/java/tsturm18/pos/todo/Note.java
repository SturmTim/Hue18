package tsturm18.pos.todo;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {

    public String note;
    public String dateTime;
    public String details;
    public boolean isOver;

    public Note(String note, String dateTime, String details) {
        this.note = note;
        this.dateTime = dateTime;
        this.details = details;
        setIsOver();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setIsOver(){
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime ldtDue = LocalDateTime.parse(dateTime, formatter);
        if (ldtDue.isBefore(ldt)){
            isOver=true;
        }
        else{
            isOver=false;
        }
    }

    public boolean getIsOver(){
        return isOver;
    }

    //TODO toString
}
