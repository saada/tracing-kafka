package chat.tracing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class Message {
  private String id;
  private String author;
  private String message;
  private String room;
  private String date;

  public void init() {
    this.id = UUID.randomUUID().toString();
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    df.setTimeZone(tz);
    this.date = df.format(new Date());
  }

  public String getId() {
    return this.id;
  }

  public String getAuthor() {
    return this.author;
  }

  public String getRoom() {
    return this.room;
  }

  public String getMessage() {
    return this.message;
  }

  public String getDate() {
    return this.date;
  }
}