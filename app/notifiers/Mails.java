package notifiers;
 
import play.*;
import play.mvc.*;
import java.util.*;

import models.User;
 
public class Mails extends Mailer {
 
   public static void welcome(User user) {
      setSubject("Welcome %s", user.name);
      addRecipient(user.email);
      setFrom("Me <twonjee2002@gmail.com>");
     // EmailAttachment attachment = new EmailAttachment();
      //attachment.setDescription("A pdf document");
      //attachment.setPath(Play.getFile("rules.pdf").getPath());
      //addAttachment(attachment);
      send(user);
   }
 
   public static void lostPassword(User user) {
      String newpassword = user.password;
     // addFrom("Robot <robot@thecompany.com>");
      setSubject("Your password has been reset");
      addRecipient(user.email);
      send(user, newpassword);
   }
 
}