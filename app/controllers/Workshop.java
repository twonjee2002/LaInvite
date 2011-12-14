package controllers;

import play.*;
import play.mvc.*;
import play.data.validation.*;

import java.util.*;

import models.*;

public class Workshop extends Application {
    
    @Before
    static void checkUser() {
        if(connected() == null) {
            flash.error("Please log in first");
            Application.index();
        }
        }
        public static void home() {
            render();
        }
        public static void settings() {
            render();
        }   
        public static void active(){
        	render();
        }
       
        public static void invite(){
        	render();
        }
        public static void index(){
        	render();
        }
       public static void getPicture(){
        	render();
        }
       public static void picture(){
       	render();
       }
       public static void design(){
          	render();
          }
       
            }
    


