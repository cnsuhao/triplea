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

/*
 * Version.java
 *
 * Created on January 18, 2002, 3:31 PM
 */

package Global;

import MainProgram.Console.ErrorConsole;
import java.io.Serializable;
import java.util.*;

/**
 * Represents a version string, and is of the form major.minor.point.micro.
 */
public class Version implements Serializable, Comparable
{
    //Maintain compatability with old versions
    static final long serialVersionUID = -4770210855326775333L;

    private final int m_major;
    private final int m_minor;
    private final int m_point;
    private final int m_micro;

    public Version(int major, int minor)
    {
        this(major, minor, 0);
    }

    public Version(int major, int minor, int point)
    {
        this(major, minor, point, 0);
    }

    public Version(int major, int minor, int point, int micro)
    {
        m_major = major;
        m_minor = minor;
        m_point = point;
        m_micro = micro;
    }

	/**
	 *  Version must be of the form xx.xx.xx.xx or xx.xx.xx or
	 *  xx.xx or xx where xx is a positive integer
	 */
	public Version(String version)
	{
		StringTokenizer tokens = new StringTokenizer(version, ".", false);

        if (tokens.countTokens() < 1)
            throw new IllegalArgumentException("Invalid version string: " + version);

        try
        {
            m_major = Integer.parseInt(tokens.nextToken());

            if (tokens.hasMoreTokens())
                m_minor = Integer.parseInt(tokens.nextToken());
            else
                m_minor = 0;

            if (tokens.hasMoreTokens())
                m_point = Integer.parseInt(tokens.nextToken());
            else
                m_point = 0;

            if (tokens.hasMoreTokens())
                m_micro = Integer.parseInt(tokens.nextToken());
            else
                m_micro = 0;
        }
        catch (NumberFormatException ex)
        {
            ErrorConsole.getConsole().appendError(ex);
            throw new IllegalArgumentException("invalid version string:" + version);
        }
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Version))
            return false;
        Version v = (Version) o;
        if (v.m_major == m_major && v.m_minor == m_minor && v.m_point == m_point && v.m_micro == m_micro)
            return true;
        return false;
    }

    /*
     * Returns 1 if this is newer than o, 0 if they are equal or incomparable, -1 if this is older than o.
     * Here's a message from the sorting class I made:
     *   Think of the compare method like this: the integer returned tells java the position of the first object in relation to the second...
     *   If -1 is return, java puts the first object before the second, 0 means they're equal(not sure which would come first), 1 tells java to put the second object before the first
     */
    public int compareTo(Object o)
    {
        if (o == null)
            return -1;
        if (!(o instanceof Version))
            return -1;

        Version other = (Version) o;

        if (m_major > other.m_major)
            return 1;
        if (m_major < other.m_major)
            return -1;
        else if (m_minor > other.m_minor)
            return 1;
        else if (m_minor < other.m_minor)
            return -1;
        else if (m_point > other.m_point)
            return 1;
        else if (m_point < other.m_point)
            return -1;
        else if (m_micro > other.m_micro)
            return 1;
        else if (m_micro < other.m_micro)
            return -1;
        else
            return 0;
    }

	public int hashCode()
	{
		return this.toString().hashCode();
	}

	public String toString()
	{
		return m_major + "." + m_minor + "." + m_point + "." + m_micro;
	}
}
