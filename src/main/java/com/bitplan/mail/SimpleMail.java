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
package com.bitplan.mail;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.simplejavamail.mailer.Mailer;

import com.bitplan.wikifrontend.BackendWiki;

public class SimpleMail {

  /**
   * get a mailer initialized from the mail.ini configuration file in the
   * .wikibackend directory of the user running this Server
   * 
   * @return - the Mailer
   * @throws Exception
   */
  public static Mailer getMailer() throws Exception {
    File propertyDir = BackendWiki.getPropertyDir();
    File propertyFile = new File(propertyDir, "mail.ini");
    if (propertyFile.exists()) {
      Properties props = new Properties();
      props.load(new FileReader(propertyFile));
      String server = props.getProperty("mail.server");
      String username = props.getProperty("mail.username");
      int port = Integer.parseInt(props.getProperty("mail.port", "25"));
      String password = props.getProperty("mail.password");
      Mailer mailer = new Mailer(server, port, username, password);
      return mailer;
    } else
      return null;
  }
}
