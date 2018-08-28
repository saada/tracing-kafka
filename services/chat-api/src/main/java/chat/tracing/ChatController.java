package chat.tracing;

import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@CrossOrigin(maxAge = 3600)
@RestController
public class ChatController {

  @RequestMapping(value = "/message", method = RequestMethod.GET)
  public @ResponseBody List<Message> index(@RequestParam(value = "room", defaultValue = "lobby") String room) throws UnknownHostException {
    List<Message> messages = Message.getMessages(room);
    System.out.println(messages.toString());
    return messages;
  }

  @RequestMapping(value = "/message",  consumes = {"application/json"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST)
  public ResponseEntity<Message> postMessage(@RequestBody Message msg) throws UnknownHostException {
    msg.create();
    return new ResponseEntity<Message>(msg, HttpStatus.OK);
  }
}