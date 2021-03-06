/* 
 * Copyright (C) 2015 Jeremy Wildsmith.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package io.github.jevaengine.builder.ui;

import io.github.jevaengine.IDisposable;
import io.github.jevaengine.math.Vector2D;
import io.github.jevaengine.ui.Button;
import io.github.jevaengine.ui.Button.IButtonPressObserver;
import io.github.jevaengine.ui.IWindowFactory;
import io.github.jevaengine.ui.IWindowFactory.WindowConstructionException;
import io.github.jevaengine.ui.NoSuchControlException;
import io.github.jevaengine.ui.TextArea;
import io.github.jevaengine.ui.Window;
import io.github.jevaengine.ui.WindowBehaviourInjector;
import io.github.jevaengine.ui.WindowManager;
import io.github.jevaengine.util.IObserverRegistry;
import io.github.jevaengine.util.Observers;
import java.net.URI;

public final class MessageBoxFactory
{
	private static final URI WINDOW_LAYOUT = URI.create("local:///ui/windows/messagebox.jwl");

	private final WindowManager m_windowManager;
	private final IWindowFactory m_windowFactory;
	
	public MessageBoxFactory(WindowManager windowManager, IWindowFactory windowFactory)
	{
		m_windowManager = windowManager;
		m_windowFactory = windowFactory;
	}
	
	public MessageBox create(String message) throws WindowConstructionException
	{
		Observers observers = new Observers();
		Window window = m_windowFactory.create(WINDOW_LAYOUT, new MessageBoxBehaviourInjector(observers, message));
		m_windowManager.addWindow(window);
		
		window.center();
		return new MessageBox(observers, window);
	}
	
	public static final class MessageBox implements IDisposable
	{
		private final IObserverRegistry m_observers;
		private final Window m_window;
		
		private MessageBox(IObserverRegistry observers, Window window)
		{
			m_observers = observers;
			m_window = window;
		}
		
		@Override
		public void dispose()
		{
			m_window.dispose();
		}
		
		public void setLocation(Vector2D location)
		{
			m_window.setLocation(location);
		}
		
		public void setVisible(boolean isVisible)
		{
			m_window.setVisible(isVisible);
		}
		
		public IObserverRegistry getObservers()
		{
			return m_observers;
		}

		public void setTopMost(boolean b) {
			m_window.setTopMost(b);
		}
	}
	
	public interface IMessageBoxObserver
	{
		void okay();
	}
	
	private class MessageBoxBehaviourInjector extends WindowBehaviourInjector
	{
		private final Observers m_observers;
		private final String m_message;
		
		public MessageBoxBehaviourInjector(Observers observers, String message)
		{
			m_observers = observers;
			m_message = message;
		}
		
		@Override
		protected void doInject() throws NoSuchControlException
		{
			getControl(Button.class, "btnOkay").getObservers().add(new IButtonPressObserver() {		
				@Override
				public void onPress() {
					m_observers.raise(IMessageBoxObserver.class).okay();
				}
			});
			
			getControl(TextArea.class, "txtMessage").setText(m_message);
		}
	}
}
