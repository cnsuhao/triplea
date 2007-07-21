/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package games.strategy.triplea.delegate;

import games.strategy.engine.data.Unit;
import games.strategy.triplea.TripleAUnit;

import java.util.Comparator;

public class UnitComparator 
{
    public static Comparator<Unit> getDecreasingMovementComparator() 
    {
        return new Comparator<Unit>()
        {

            public int compare(Unit u1, Unit u2)
            {
                int left1 = TripleAUnit.get(u1).getMovementLeft();
                int left2 = TripleAUnit.get(u2).getMovementLeft();

                if (left1 == left2)
                    return 0;
                if (left1 > left2)
                    return 1;
                return -1;
            }
        };    
    }
    
    public static Comparator<Unit> getIncreasingMovementComparator() 
    {
        return new Comparator<Unit>()
        {

            public int compare(Unit u1, Unit u2)
            {
                int left1 = TripleAUnit.get(u1).getMovementLeft();
                int left2 = TripleAUnit.get(u2).getMovementLeft();

                if (left1 == left2)
                    return 0;
                if (left1 < left2)
                    return 1;
                return -1;
            }
        };    
    }

   



}
