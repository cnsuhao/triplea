#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

from google.appengine.ext import db
from google.appengine.ext import webapp
import os;
import random;
import string
import wsgiref.handlers
import logging

import scripts.util
import model.roll

from google.appengine.api import mail


"""Script to roll dice, and send the results via email
"""
class MainPage(webapp.RequestHandler):
  
  def get(self):
    self.response.headers['Content-Type'] = 'text/html'     
    self.response.out.write(
"""PBEM Dice Roller for <a href="http://triplea.sourceforge.net">TripleA</a>.
"""
) 
      
  def post(self):
    
    dice = scripts.util.create_dice(self.request) 
    
    subject = self.request.get("subject")
    to = self.request.get("roller")
    cc = self.request.get("gm")    
    
    if(len(subject)  >  200):
     scripts.util.write_error(self.response,"subject too long, must be less than 200 characters")
     return 
  
    if not mail.is_email_valid(to) :
     scripts.util.write_error(self.response, mail.invalid_email_reason(to,"to"))
     return 
      
    if cc and not mail.is_email_valid(cc) :
     scripts.util.write_error(self.response, mail.invalid_email_reason(cc,"cc"))
     return
    
    message = mail.EmailMessage(sender = "noreply.tdice@gmail.com",subject=subject)    
    message.to = to,
    if(cc) :
      message.cc = cc
    message.body = "Your dice are:%s\nHave a nice day.\n\nThis is an automatically created email. Please don't reply to it." %(
              string.join(dice, ',') )    
    
    message.send()
    
    logging.info("dice sent, to:%s, cc:%s, dice:%s, subject:%s" % (to,cc, string.join(dice,',') ,subject))
    
    scripts.util.write_dice(self.response, dice)     
  

def main():
  application = webapp.WSGIApplication(
                                       [('/email', MainPage)],
                                       debug=True)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == "__main__":
  main()