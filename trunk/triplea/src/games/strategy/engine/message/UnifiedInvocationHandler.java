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
package games.strategy.engine.message;

import java.lang.reflect.*;

/**
 * Invocation handler for the UnifiedMessenger
 *
 *
 * @author Sean Bridges
 */
/**
 * Handles the invocation for a channel
 */
class UnifiedInvocationHandler implements InvocationHandler
{
    private final UnifiedMessenger m_messenger;
    private final String m_endPointName;
    private final boolean m_ignoreResults;
      
    public UnifiedInvocationHandler(final UnifiedMessenger messenger, final String endPointName, final boolean ignoreResults)
    {
        m_messenger = messenger;
        m_endPointName = endPointName;
        m_ignoreResults = ignoreResults;
    }
    
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        RemoteMethodCall remoteMethodMsg = new RemoteMethodCall(m_endPointName, method.getName(), args, method.getParameterTypes());
        if(m_ignoreResults)
        {
            m_messenger.invoke(m_endPointName, remoteMethodMsg);
            return null;
        }
        else
        {
            RemoteMethodCallResults response = m_messenger.invokeAndWait(m_endPointName, remoteMethodMsg);

            if(response.getException() != null)
            {
                if(response.getException() instanceof ConnectionLostException)
                {
                    ConnectionLostException cle = (ConnectionLostException) response.getException();
                    cle.fillInInvokerStackTrace();
                    
                }
                throw response.getException();
            }
            return response.getRVal();
        }
    }   
}