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

import model.roll
import model.game
import cgi


"""Script to list dice for a game
"""
class MainPage(webapp.RequestHandler):
  def get(self):    
    self.response.headers['Content-Type'] = 'text/html'
    
    self.response.out.write(
"""<html><body>

<p>
Displays the last 200 rolls for a game.
</p>
<p>
Use the menu View->Game UUID to get the Game UUID.  Rolls are stored for up to 60 days.
</p>
<form method="post"> 

  <label for="uuid">Game UUID: </label>
  <input type="text" name="uuid"  id="uuid"><br>  
  <input type="submit" value="Send">
</form>

</html></body>
""")
             
  def post(self):
    self.response.headers['Content-Type'] = 'text/html'
    
    uuid = self.request.get("uuid")
    
    games = db.GqlQuery(
      "SELECT * FROM Game where gameUuid = :1  ",
      uuid
      )
    
    if games.count() == 0 : 
      self.response.out.write('nothing known about that game')
      return
    
    game = games[0]
    lowestIndex = game.count - 200
    if lowestIndex < 0 :
      lowestIndex = 0
      
    
    rolls = db.GqlQuery(
      "SELECT * FROM Roll where gameUuid = :1  and rollNumber >= :2 ORDER BY rollNumber ASC ",
      uuid, lowestIndex
      )
    
    self.response.out.write('<h3>rolls for  %s</h3>' % (cgi.escape(uuid)))
    
    self.response.out.write("<table border=\"1\">")
    for roll in rolls:
      self.response.out.write('<tr><td>%s</td><td>%s</td><td>%s</td></tr>' % 
                              (roll.dice, cgi.escape(roll.subject), roll.date.strftime('%A %B %d, %H:%M GMT')))
   
   
    self.response.out.write("</table>")
          

def main():
  application = webapp.WSGIApplication(
                                       [('/list', MainPage)],
                                       debug=True)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == "__main__":
  main()