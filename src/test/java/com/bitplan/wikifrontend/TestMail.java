/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikifrontend open source project
 *
 * Copyright 2017-2021 BITPlan GmbH https://github.com/BITPlan
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

import org.junit.Ignore;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;

import com.bitplan.mail.SimpleMail;

/**
 * test sending mail
 * @author wf
 *
 */
public class TestMail {

  /**
   * uncomment this test and adapt to your needs to check that your
   * configuration is o.k.
   * the ini file should have
   * mail.server=
   * mail.username=
   * mail.password=               
   * @throws Exception
   */
  //@Test
  @Ignore
  public void testMail() throws Exception {
    Mailer mailer=SimpleMail.getMailer();
    if (mailer!=null) {
      // see http://www.simplejavamail.org/#/features
      Email email = EmailBuilder.startingBlank()
          .from("Michel Baker", "m.baker@mbakery.com")
          .to("mom", "jean.baker@hotmail.com")
          .to("dad", "StevenOakly1963@hotmail.com")
          .withSubject("My Bakery is finally open!")
          .withPlainText("Mom, Dad. We did the opening ceremony of our bakery!!!")
          .buildEmail();
      mailer.sendMail(email);
      System.out.println("mail sent");
    }
  }
}
