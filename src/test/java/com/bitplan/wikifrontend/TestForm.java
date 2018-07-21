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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * test the Form helper class
 * 
 * @author wf
 *
 */
public class TestForm {
  // a form with fields
  class Form {
    boolean debug = false;
    Captcha captcha;
    boolean de; // true if things are to be displayed in german
    Map<String, Field> fieldMap = new LinkedHashMap<String, Field>();
    MultivaluedMap<String, String> formParams;

    String title;
    String title_de;

    String success;
    String success_de;

    String subject = "Contact Form Request";

    String submit = "Send";
    String submit_de = "Absenden";

    String id = "contact_form";

    boolean submitted = false;

    /**
     * construct the form
     * 
     * @param de
     *          - german?
     * @param postService
     * @param postToken
     * @param fields
     */
    public Form(boolean de, PostService postService, String postToken,
        Field[] fields) {
      this.de = de;
      // get the form parameters from the postService
      this.getFormParams(postService, postToken);
      // add all fields
      for (Field field : fields) {
        this.addField(field);
      }
      captcha = new Captcha(formParams, de);
      for (Field field : captcha.getFields()) {
        addField(field);
      }

      if (postService != null) {
        addField(new HiddenField("postToken", postService.getPostToken()));
        fromPost();
      }
    }

    // true if the form has never been submitted yet
    public boolean isNew() {
      return !submitted;
    }

    // check whether the form is complete
    // and the captcha has been entered
    public String robotCheck() {
      String msg = null;
      if (!submitted)
        return msg;
      Field captchaField = getField("captcha");
      Field expectedField = getField("expected");
      // check that captcha and expected Field are ok.
      if (captchaField != null && expectedField != null) {
        String captchaValue = captchaField.value;
        String expectedValue = expectedField.value;
        boolean ok = captchaValue != null && expectedValue != null
            && !expectedValue.isEmpty() && expectedValue.equals(captchaValue);
        if (!ok) {
          String msg_de = "Bitte geben Sie das richtige Ergebnis für die Aufgabe "
              + captcha.task + " ein.";
          String msg_en = "Please enter the correct result for the task "
              + captcha.task + ".";
          msg = de ? msg_de : msg_en;
        }
      }
      Field robotField = getField("whatami");
      if (robotField != null) {
        String robotValue = robotField.value;
        boolean noRobot = robotValue.equals("Human")
            || robotValue.equals("Mensch");
        if (!noRobot) {
          String msg_de = "Als Roboter geht es hier nicht weiter ...";
          String msg_en = "As a robot there is no progress here ...";
          msg = de ? msg_de : msg_en;
        }
      }
      return msg;
    }

    public String asMail() {
      String mail = "";
      for (Field field : getFields()) {
        mail += String.format("%s=%s\n", field.name, field.value);
      }
      return mail;
    }

    //
    public void getFormParams(PostService postService, String postToken) {
      if (postToken != null) {
        formParams = postService.getPostData(postToken);
        if (formParams != null) {
          submitted = true;
        }
      }
    }

    /**
     * get my fields from the post
     */
    public void fromPost() {
      if (submitted) {
        for (Field field : getFields()) {
          field.value = formParams.getFirst(field.name);
        } // for
      } // if
    }

    public void setTitle(String title, String title_de) {
      this.title = title;
      this.title_de = title_de;
    }

    public void setSubmit(String submit, String submit_de) {
      this.submit = submit;
      this.submit_de = submit_de;
    }

    public void setSuccess(String success, String success_de) {
      this.success = success;
      this.success_de = success_de;
    }

    public void setSubject(String subject) {
      this.subject = subject;
    }

    public Collection<Field> getFields() {
      return fieldMap.values();
    }

    public void addField(Field field) {
      fieldMap.put(field.name, field);
    }

    public Field getField(String fieldName) {
      Field field = fieldMap.get(fieldName);
      return field;
    }
  }

  class SelectField extends Field {
    List<String> selections = new ArrayList<String>();

    public void addSelection(String selection) {
      selections.add(selection);
    }

    public void addSelections(String[] pSelections) {
      selections.addAll(Arrays.asList(pSelections));
    }

    public SelectField(String name, String label_en, String label_de,
        String placeholder_en, String placeholder_de, String icon, int min) {
      super(name, label_en, label_de, placeholder_en, placeholder_de, icon,
          min);
    }

    public SelectField(String name, String label_en, String label_de,
        String icon, int min) {
      super(name, label_en, label_de, label_en, label_de, icon, min);
    }
  }

  class HiddenField extends Field {
    public HiddenField(String name, String value) {
      super(name, name, name, "flash", 0);
      super.hidden = true;
      super.value = value;
    }
  }

  class TextAreaField extends Field {
    public TextAreaField(String name, String label_en, String label_de,
        String placeholder_en, String placeholder_de, String icon, int min) {
      super(name, label_en, label_de, placeholder_en, placeholder_de, icon,
          min);
    }

    public TextAreaField(String name, String label_en, String label_de,
        String icon, int min) {
      super(name, label_en, label_de, label_en, label_de, icon, min);
    }
  }

  class TextField extends Field {
    public TextField(String name, String label_en, String label_de,
        String placeholder_en, String placeholder_de, String icon, int min) {
      super(name, label_en, label_de, placeholder_en, placeholder_de, icon,
          min);
    }

    public TextField(String name, String label_en, String label_de, String icon,
        int min) {
      super(name, label_en, label_de, label_en, label_de, icon, min);
    }
  }

  // a field
  class Field {
    boolean hidden = false;
    String id;
    String name;
    String label_en;
    String label_de;
    String labelClass = "";

    String placeholder_en;
    String placeholder_de;
    String icon;
    String value;
    int min;

    public Field(String name, String label_en, String label_de,
        String placeholder_en, String placeholder_de, String icon, int min) {
      this.name = name;
      this.id = name;
      this.label_en = label_en;
      this.label_de = label_de;
      this.placeholder_de = placeholder_de;
      this.placeholder_en = placeholder_en;
      this.icon = icon;
      this.min = min;
    }

    public Field(String name, String label_en, String label_de, String icon,
        int min) {
      this(name, label_en, label_de, label_en, label_de, icon, min);
    }

  }

  /**
   * turing test by performing a mathematical task
   */
  class Captcha {
    String expected;
    String task;
    List<Field> fields = new ArrayList<Field>();

    /**
     * create a captcha
     * 
     * @param formParams
     */
    public Captcha(MultivaluedMap<String, String> formParams, boolean de) {
      if (formParams!=null && formParams.containsKey("expected")) {
        expected = formParams.getFirst("expected");
        task = formParams.getFirst("task");
      } else {
        int a1 = (int) (Math.random() * 10 + 1);
        int a2 = (int) (Math.random() * 10 + 1);
        expected = "" + (a1 + a2);
        String task_de = "Was ist " + a1 + "+" + a2 + "?";
        String task_en = "What is " + a1 + "+" + a2 + "?";
        task = de ? task_de : task_en;
      }
      // not quite ok since we have the language preselected
      fields.add(new TextField("captcha", task, task, "question-sign", 1));
      fields.add(new HiddenField("task", task));
      fields.add(new HiddenField("expected", expected));
      SelectField robotField = new SelectField("whatami", "What am i?",
          "Was bin ich?", "Human", "Mensch", "book", 0);
      String[] robots_en = { "Human", "Robot" };
      String[] robots_de = { "Mensch", "Roboter" };
      if (de) {
        robotField.addSelections(robots_de);
      } else {
        robotField.addSelections(robots_en);
      }
      fields.add(robotField);
    }

    /**
     * get the fields that are needed for the Captcha/Robot check
     * 
     * @return
     */
    public List<Field> getFields() {
      return fields;
    }
  } // Captcha

  @Test
  public void testForm() {
    boolean debug=false;
    String captchas[] = { "7", "5" };
    String whatamis[] = { "Mensch", "Roboter" };
    String expectedRobotCheck[] = { null, "Bitte geben Sie das richtige Ergebnis für die Aufgabe 2+5 ein.","Als Roboter geht es hier nicht weiter ...","Als Roboter geht es hier nicht weiter ..."};
    int i=0;
    for (String whatami : whatamis) {
      for (String captcha : captchas) {
        PostManager postService = PostManager.getInstance();
        boolean de = true;
        String postToken = postService.getPostToken();
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        formParams.putSingle("name", "name");
        formParams.putSingle("task", "2+5");
        formParams.putSingle("captcha", captcha);
        formParams.putSingle("expected", "7");
        formParams.putSingle("postToken", postToken);
        formParams.putSingle("whatami", whatami);
        postService.handle(postToken, formParams);
        Field formfields[] = {
            new TextField("name", "name", "name", "name", 2) };
        Form form = new Form(de, postService, postToken, formfields);
        Collection<Field> fields = form.getFields();
        if (debug)
        for (Field field : fields) {
          System.out.println(String.format("%s=%s", field.name, field.value));
        }
        assertFalse(form.isNew());
        assertEquals(" "+i+": captcha: "+captcha+" whatami: "+whatami,expectedRobotCheck[i++], form.robotCheck());
      }
    }
  }
}
