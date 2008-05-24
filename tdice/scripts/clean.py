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
import cgi
import datetime
import logging
import model.roll
import model.game
import os;
import random;
import string
import wsgiref.handlers
import logging


"""
script to clean old entries from the db

we need to limit the number of entities read to 1000 so
limit reads.  we do not have to delete everything in one go, as
long as multiple calls to clean will eventually remove
everything we are fine
"""
class MainPage(webapp.RequestHandler):
      
  def get(self):
    
    days = 60
    if( self.request.get('days')):
      days = int(self.request.get('days'))
      if(days < 60 ) :
        days = 60
          
    games = db.GqlQuery(
      "SELECT * FROM Game where lastUpdate < :1  ",
      datetime.datetime.now() - datetime.timedelta(days=days)
      )
    
    MAX_DELETE = 500
    count = 0
    for game in games :
      
      if count > 900 :
        break
      
      
      if game.count > MAX_DELETE :
        
        #delete some of the rolls
        lowestToDelete = game.count - MAX_DELETE
        
        logging.info("Deleting partial from game:%s, count:%s lowestToDelete:%s" % (game.gameUuid, game.count, lowestToDelete))
        
        rolls = db.GqlQuery(
          "SELECT * FROM Roll where gameUuid = :1  and  rollNumber >= :2 ",
          game.gameUuid, lowestToDelete    
        )
        
        count = count + rolls.count()
        for roll in rolls:
          roll.delete()
                  
        game.count = lowestToDelete
        game.put()
        break
      else :
        
        logging.info("Deleting all from game:%s, count:%s" % (game.gameUuid, game.count))
        
        rolls = db.GqlQuery(
          "SELECT * FROM Roll where gameUuid = :1 ",
          game.gameUuid    
        )
        count = count + rolls.count()
        for roll in rolls:
          roll.delete()
        game.delete()
        count = count + 1
      
    self.response.headers['Content-Type'] = 'text/plain'
    self.response.out.write('deleting %s rolls for the last %s days' % (count,days)  );

def main():
  application = webapp.WSGIApplication(
                                       [('/clean', MainPage)],
                                       debug=True)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == "__main__":
  main()