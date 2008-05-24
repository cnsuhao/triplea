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

import scripts.util
import model.roll
import model.game
import datetime
import logging

"""Script to roll dice, and store the results in the datastore.
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
      
    uuid = self.request.headers['X-Triplea-Game-UUID']  
    subject = self.request.get("subject")
    #store the roll in the database
    
    if(len(uuid) > 0 and uuid != 'test-roll') :
      game = getCreateGame(uuid)
            
      roll = model.roll.Roll()            
      roll.gameUuid = uuid
      roll.rollNumber = game.count
      if(subject.startswith('TripleA:')) :
        subject = subject[len('TripleA:') : len(subject)]
      roll.subject= subject
      roll.dice = string.join(dice, ',')
      roll.put()
  
    logging.info("dice stored, game id:%s, dice:%s, subject:%s" % (uuid, string.join(dice,',') ,subject))
  
    scripts.util.write_dice(self.response, dice) 
      
def getCreateGame(uuid):
      games = db.GqlQuery(
      "SELECT * FROM Game where gameUuid = :1",
      uuid
      )
      
      if(games.count() == 0) :
        game = model.game.Game()
        game.gameUuid = uuid
        game.count = 1
        game.put()
        return game
      else :        
        game = games[0]
        game.lastUpdate = datetime.datetime.today()
        game.count= game.count + 1
        game.put()
        return game
      
      
def main():
  application = webapp.WSGIApplication(
                                       [('/web', MainPage)],
                                       debug=True)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == "__main__":
  main()