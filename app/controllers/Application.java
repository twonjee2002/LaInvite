package controllers;

import play.*;
import play.data.Upload;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.JPA;
import play.mvc.*;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.persistence.Query;

import org.h2.util.IOUtils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

//import com.sun.java.util.jar.pack.Package.Class.Method;

import notifiers.Mails;

import models.*;

public class Application extends Controller {
	 @Before
	    static void addUser() {
	        User user = connected();
	        if(user != null) {
	            renderArgs.put("user", user);
	        }
	    }
	
 static User connected() {
     if(renderArgs.get("user") != null) {
         return renderArgs.get("user", User.class);
     }
     String username = session.get("user");
     if(username != null) {
         return User.find("byUsername", username).first();
     } 
     return null;
 }
 public static void index() {
     if(connected() != null) {
         Application.index();
     }
     render();
 }
       public static void register(String myName, String myPassword) {
        render(myName, myPassword);
    }
    public static void login(String username, String password) {
        User user = User.find("byUsernameAndPassword", username, password).first();
        if(user != null) {
            session.put("user", user.username);
            flash.success("Welcome, " + user.name);
            Workshop.active();         
        }
        // Oops
        flash.put("username", username);
        flash.error("Login failed");
        index();
        }
    public static void saveUser(@Valid User user, String verifyPassword, Mails mail) {
        validation.required(verifyPassword);
        validation.equals(verifyPassword, user.password).message("Your password doesn't match");
        if(validation.hasErrors()) {
            render("@register", user, verifyPassword);
        }
        mail.welcome(user);
        user.create();
        user.save();
        session.put("user", user.username);
        flash.success("Welcome, " + user.name + " Please check your mail");
        Workshop.home();
    }
    public static void pickPictures() {
        Query query = JPA.em().createQuery("select * from Picture");
        List<Picture> picture = query.getResultList();
        render(picture);
    }
      public static void uploadPicture(Picture picture) {
         	  picture.save();
      Workshop.design();
        
    }
     
      public static void getPicture(long id) {
          Picture picture = Picture.findById(id);
          response.setContentTypeIfNotSet(picture.image.type());
          renderBinary(picture.image.get());
      }
  
    public static void deletePicture(long id) {
    	   final Picture picture = Picture.findById(id);
    	   //picture.image.getFile().delete();
    	   picture.delete();
    	   Workshop.design();
    	}
           
         private static void createThumbnail(String filename, int thumbWidth, int thumbHeight, int quality, String outFilename)
         throws InterruptedException, FileNotFoundException, IOException
     {
         // load image from filename
         Image image = Toolkit.getDefaultToolkit().getImage(filename);
         MediaTracker mediaTracker = new MediaTracker(new Container());
         mediaTracker.addImage(image, 0);
         mediaTracker.waitForID(0);
         // use this to test for errors at this point: System.out.println(mediaTracker.isErrorAny());
  
         // determine thumbnail size from WIDTH and HEIGHT
         double thumbRatio = (double)thumbWidth / (double)thumbHeight;
         int imageWidth = image.getWidth(null);
         int imageHeight = image.getHeight(null);
         double imageRatio = (double)imageWidth / (double)imageHeight;
         if (thumbRatio < imageRatio) {
             thumbHeight = (int)(thumbWidth / imageRatio);
         } else {
             thumbWidth = (int)(thumbHeight * imageRatio);
         }
  
         // draw original image to thumbnail image object and
         // scale it to the new size on-the-fly
         BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
         Graphics2D graphics2D = thumbImage.createGraphics();
         graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
  
         // save thumbnail image to outFilename
         BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFilename));
         JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
         JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(thumbImage);
         quality = Math.max(0, Math.min(quality, 100));
         param.setQuality((float)quality / 100.0f, false);
         encoder.setJPEGEncodeParam(param);
         encoder.encode(thumbImage);
         out.close();
     }
         public static void externalimages(String building_code, String ts_code) throws IOException {
        	    renderBinary(new File("public/images/externalimages/" + building_code + "_" + ts_code.charAt(0)));
        	}

    
        public static void logout() {
            session.clear();
            index();
        }
    }