package client;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.Timer;
import java.util.*;

import client.gui.*;
import common.*;

/**
 * This class manages displaying the current play area
 * @author dvanhumb
 */
public class ClientViewArea extends JComponent implements MouseMotionListener, MouseListener, KeyListener, ActionListener, Constants
{
	private static final long serialVersionUID = 23498751L;
	public static int TIMER_TICK = 75;
	
	// Drawing-related variables
	protected boolean antialiasing;
	protected Color playerColor;
	protected float scale;
	
	// Game-related variables
	protected Vector<Widget> widgetList;
	protected Player player;
	protected WeightedPosition viewTracker;
	//protected Map map;
	
	// Temporary testing stuff:
	protected Timer gameTimer;
	protected long lastTime;
	protected boolean[] keysPressed;
	
	public ClientViewArea()
	{
		Dimension d = new Dimension(640, 480);
		setMinimumSize(d);
		setPreferredSize(d);
		setMaximumSize(d);
		
		setBackground(Color.black);
		setForeground(Color.white);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		widgetList = new Vector<Widget>();
		playerColor = Color.green;
		scale = 50;
		
		gameTimer = new Timer(TIMER_TICK, this);
		gameTimer.start();
		lastTime = System.currentTimeMillis();
		
		keysPressed = new boolean[1024];
		antialiasing = false;
	}
	
	public void setPlayer(Player p)
	{
		player = p;
		if (viewTracker == null)
			viewTracker = new WeightedPosition(player.getX(), player.getY());
		viewTracker.setTarget(player);
		repaint();
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void addWidget(Widget w)
	{
		widgetList.add(w);
	}
	
	public void removeWidget(Widget w)
	{
		widgetList.remove(w);
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		if (antialiasing)
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		else
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		Rectangle clip = g2.getClipBounds(); // The clipping bounds, so we don't draw stuff over again
		int offset_x, offset_y;
		offset_x = getWidth() / 2;
		offset_y = getHeight() / 2;
		
		// Save temporary copies of parameters changed
		Color oldColor = g2.getColor();
		
		// Draw the background
		g2.setColor(getBackground());
		g2.fill(clip);
		
		if (viewTracker != null)
		{
			offset_x -= viewTracker.getX();
			offset_y -= viewTracker.getY();
		}
		
		// TEMP: Draw a simple grid:
		g2.setColor(Color.gray);
		int t;
		for (int x = Math.round(getWidth() / scale); x >= 0; x--)
		{
			t = Math.round(offset_x + x * scale);
			g2.drawLine(t, clip.y, t, clip.y + clip.height);
			t = Math.round(offset_x - x * scale);
			g2.drawLine(t, clip.y, t, clip.y + clip.height);
		}
		g2.setColor(Color.darkGray);
		for (int y = Math.round(getHeight() / scale); y >= 0; y--)
		{
			t = Math.round(offset_y + y * scale);
			g2.drawLine(clip.x, t, clip.x + clip.width, t);
			t = Math.round(offset_y - y * scale);
			g2.drawLine(clip.x, t, clip.x + clip.width, t);
		}
		
		// Draw the player
		if (player != null)
		{
			float x = player.getX();
			float y = player.getY();
			
			g2.setColor(playerColor);
			g2.fillOval(offset_x - Math.round(scale / 2 - x), offset_y
					- Math.round(scale / 2 - y), Math.round(scale), Math
					.round(scale));
		}
		
		// Draw the walls
		
		int width = getWidth(), height = getHeight();
		// Draw the widgets
		for (Widget w : widgetList)
			w.draw(g2, width, height);
		
		// Restore all parameters changed
		g2.setColor(oldColor);
	}
	
	// Called when the mouse is moved with at least one button down
	public void mouseDragged(MouseEvent e)
	{
		int width = getWidth(), height = getHeight(), x = e.getX(), y = e.getY();
		for (Widget w : widgetList)
		{
			if (!(w instanceof InteractiveWidget)) continue;
			InteractiveWidget iw = (InteractiveWidget) w;
			if (iw.containsFixed(x, y, width, height))
			{
				if (iw.getState() == InteractiveWidget.STATE_UNHOVERED)
				{
					iw.setState(InteractiveWidget.STATE_HOVERED);
					repaint(iw.getBounds());
				}
			}
			else
			{
				if (iw.getState() == InteractiveWidget.STATE_HOVERED)
				{
					iw.setState(InteractiveWidget.STATE_UNHOVERED);
					repaint(iw.getBounds());
				}
			}
		} // end for (w in widgetList)
	} // end mouseDragged()
	
	public void mouseMoved(MouseEvent e)
	{
		int width = getWidth(), height = getHeight(), x = e.getX(), y = e.getY();
		for (Widget w : widgetList)
		{
			if (!(w instanceof InteractiveWidget)) continue;
			InteractiveWidget iw = (InteractiveWidget) w;
			if (iw.containsFixed(x, y, width, height))
			{
				if (iw.getState() != InteractiveWidget.STATE_HOVERED)
				{
					iw.setState(InteractiveWidget.STATE_HOVERED);
					repaint(iw.getBounds());
				}
			}
			else
			{
				if (iw.getState() != InteractiveWidget.STATE_READY)
				{
					iw.setState(InteractiveWidget.STATE_READY);
					repaint(iw.getBounds());
				}
			}
		} // end for (Widget)
	} // end mouseMouse()
	
	public void mouseClicked(MouseEvent e)
	{
		
	}
	
	public void mouseEntered(MouseEvent e)
	{
		
	}
	
	public void mouseExited(MouseEvent e)
	{
		
	}
	
	public void mousePressed(MouseEvent e)
	{
		
	}
	
	// Called when the mouse button is released
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() != MouseEvent.BUTTON1) return;
		
		for (Widget w : widgetList)
		{
			if (!(w instanceof InteractiveWidget)) continue;
			InteractiveWidget iw = (InteractiveWidget) w;
			if (iw.getState() == InteractiveWidget.STATE_HOVERED) iw.trigger(e.getButton());
		}
	}
	
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
		else if (e.getKeyCode() == KeyEvent.VK_WINDOWS)
		{
			if (playerColor == Color.green)
				playerColor = Color.orange;
			else
				playerColor = Color.green;
			repaint();
		}
		else if (e.getKeyCode() == KeyEvent.VK_INSERT)
		{
			antialiasing = !antialiasing;
			repaint();
		}
		
		keysPressed[e.getKeyCode()] = true;
	}
	
	public void keyReleased(KeyEvent e)
	{
		keysPressed[e.getKeyCode()] = false;
	}
	
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(gameTimer))
		{
			long thisTime = System.currentTimeMillis();
			float dTime = 0.001f * (thisTime - lastTime);
			boolean repaint = false;
			
			if (keysPressed[KeyEvent.VK_LEFT])
				player.accelerate(-PLAYER_ACCELERATION, 0);
			if (keysPressed[KeyEvent.VK_RIGHT])
				player.accelerate(PLAYER_ACCELERATION, 0);
			if (keysPressed[KeyEvent.VK_UP])
				player.accelerate(0, -PLAYER_ACCELERATION);
			if (keysPressed[KeyEvent.VK_DOWN])
				player.accelerate(0, PLAYER_ACCELERATION);
			
			if (player.animate(dTime)) repaint = true;
			if (viewTracker.animate(dTime)) repaint = true;
			
			if (repaint) repaint();
			lastTime = thisTime;
		}
	} // end actionPerformed()
} // end ClientViewArea clas
