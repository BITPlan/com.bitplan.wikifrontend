/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikifrontend open source project
 *
 * Copyright 2017 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.wikifrontend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

/**
 * test the Form helper class
 * @author wf
 *
 */
public class TestForm {
//a form with fields
 class Form {
   Captcha captcha;
   boolean de; // true if things are to be displayed in german
   Map<String,Field> fieldMap=new LinkedHashMap<String,Field>();
   MultivaluedMap<String, String> formParams;
   String title;
   String title_de;
   String success;
   String success_de;
   
   public Form(boolean de,PostService postService,String postToken,Field[] fields) {
     this.de=de;
     for (Field field:fields) {
       this.addField(field);
     }
     captcha=new Captcha();
     addField(new TextField("captcha",captcha.task,captcha.task_de,"question-sign",1));
     addField(new HiddenField("expected",""+captcha.expected));
     if (postService!=null) {
       addField(new HiddenField("postToken",postService.getPostToken()));
       fromPost(postService,postToken);
     }
   }

   public String asMail() { 
     String mail="";
     for (Field field:getFields()) {
       mail+=String.format("%s=%s\n",field.name,field.value);
     }
     return mail;
   }

   // constructor
   public void fromPost(PostService postService,String postToken) {
     if (postToken!=null) {
        formParams=postService.getPostData(postToken);
        if (formParams!=null) {
          for (Field field:getFields()) {
            field.value=formParams.getFirst(field.name);
          }
        }
     }
   }

   public void setTitle(String title, String title_de) {
     this.title=title;
     this.title_de=title_de;    
   }

   public void setSuccess(String success, String success_de) {
     this.success=success;
     this.success_de=success_de;    
   }
   
   public Collection<Field> getFields() {
     return fieldMap.values();
   }
   
   public void addField(Field field) {
     fieldMap.put(field.name,field);
   }
   
   public Field getField(String fieldName) {
     Field field=fieldMap.get(fieldName);
     return field;
   }
 }
 class SelectField extends Field {
   List<String> selections=new ArrayList<String>();
   public void addSelection(String selection) {
     selections.add(selection);
   }

   public void addSelections(String[] pselections) {
     selections.addAll(Arrays.asList(pselections));
   }

   public SelectField(String name,String label_en, String label_de, String placeholder_en,String placeholder_de, String icon, int min) {
     super(name,label_en,label_de,placeholder_en,placeholder_de,icon,min);
   }
   public SelectField(String name,String label_en, String label_de, String icon, int min) {
     super(name,label_en,label_de,label_en,label_de,icon,min);
   }
 }

 class HiddenField extends Field {
   public HiddenField(String name,String value) {
     super(name,name,name,"flash",0);
     super.hidden=true;
     super.value=value;
   }
 }
 class TextField extends Field {
   public TextField(String name,String label_en, String label_de, String placeholder_en,String placeholder_de, String icon, int min) {
     super(name,label_en,label_de,placeholder_en,placeholder_de,icon,min);
   }
   public TextField(String name,String label_en, String label_de, String icon, int min) {
     super(name,label_en,label_de,label_en,label_de,icon,min);
   }
 }

 // a field
 class Field {
    boolean hidden=false;
    String id;
    String name;
    String label_en;
    String label_de; 
    String placeholder_en;
    String placeholder_de;
    String icon;
    String value;
    int min;

    public Field(String name,String label_en, String label_de, String placeholder_en,String placeholder_de, String icon, int min) {
       this.name=name;
       this.id=name;
       this.label_en=label_en;
       this.label_de=label_de;
       this.placeholder_de=placeholder_de;
       this.placeholder_en=placeholder_en;
       this.icon=icon;
       this.min=min;
    }

    public Field(String name,String label_en, String label_de, String icon, int min) {
      this(name,label_en,label_de,label_en,label_de,icon,min);
    }

 }

 class Captcha {
   int expected;
   String task;
   String task_de;

   public Captcha() {
     int a1=(int)(Math.random() * 10 + 1);
     int a2=(int)(Math.random() * 10 + 1);
     expected=a1+a2;
     task_de="Was ist "+a1+"+"+a2+"?";
     task="What is "+a1+"+"+a2+"?";
   }
 } // Captcha
 
 @Test
 public void testForm() {
   PostManager postService=PostManager.getInstance();
   boolean de=true;
   String postToken=postService.getPostToken();
   Field fields[]= {new TextField("name","name","name","name",2)};
   Form form=new Form(de, postService, postToken, fields);
   //MultivaluedMap<String, String> formParams=new MultivaluedHashMap<String,String>();
   // postService.handle(postToken, formParams);
 }
}
