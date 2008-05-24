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

import os
import random
import string
import logging

def create_dice(request):
  """Reads a request, and returns an array of ints"""
  
  #make sure the request is from TripleA
  assert request.headers['User-Agent'].startswith('triplea/')
  
  numdice = int(request.get("numdice"))
  numsides = int(request.get("numsides"))
    
  assert numdice > 0
  assert numsides > 0
    
  random.seed(os.urandom(8));
       
  dice = []
    
  for i in range(0, numdice):
    dice.append(str(random.randint(1, numsides)))
    
  return dice
  
  
def write_dice(response, dice):
  response.headers['Content-Type'] = 'text/plain' 
  response.out.write('<dice>');    
  response.out.write(string.join(dice, ','));
  response.out.write('</dice>');

def write_error(response,msg):
  response.headers['Content-Type'] = 'text/plain' 
  response.out.write('<error>');    
  response.out.write(msg);
  response.out.write('</error>');
  