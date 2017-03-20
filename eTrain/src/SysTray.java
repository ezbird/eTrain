package sweetdream;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import javax.swing.*;
import java.sql.SQLException;

// Referenced classes of package sweetdream:
//            Sweetdream, eFunctions

public class SysTray
{

    static TrayIcon trayIcon = null;

    public SysTray()
    {
        if(SystemTray.isSupported())
        {
            SystemTray tray = SystemTray.getSystemTray();
            java.awt.Image image = Toolkit.getDefaultToolkit().getImage((sweetdream.Sweetdream.class).getResource("images/system/eTrain.gif"));
            java.awt.event.MouseListener mouseListener = new MouseAdapter() {

                public void mouseClicked(MouseEvent e)
                {
                    if(SwingUtilities.isLeftMouseButton(e))
                    {
                        if(Sweetdream.frame.isVisible())
                        {
                            Sweetdream.frame.setVisible(false);
                        } else
                        {
                            Sweetdream.frame.setVisible(true);
                     //       Sweetdream.frame.toFront();
                            Sweetdream.imageFrame.setVisible(false);
                        }
                    }
                }

                public void mouseEntered(MouseEvent e)
                {
                    System.out.println("Tray Icon - Mouse entered!");
                }

                public void mouseExited(MouseEvent e)
                {
                    System.out.println("Tray Icon - Mouse exited!");
                }

                public void mousePressed(MouseEvent e)
                {
                    System.out.println("Tray Icon - Mouse pressed!");
                }

                public void mouseReleased(MouseEvent e)
                {
                    System.out.println("Tray Icon - Mouse released!");
                }

            
    //        {
    //            super();
    //        }
            };
            ActionListener exitListener = new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
			// Shutdown database here?  connection.close?
			try { 
				Sweetdream.db.update("UPDATE variables SET value = '" + Sweetdream.tabbedPaneLibrary.getSelectedIndex() + "' WHERE variablename = 'lastLibraryTab'"); 
				Sweetdream.db.update("SHUTDOWN");
				
			} catch(SQLException ex2) { ex2.printStackTrace(); }
                    System.exit(0);
                }

            
    //        {
    //            super();
    //        }
            };
            ActionListener stopListener = new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
                    eFunctions.reset();
                }

            
    //        {
    //            super();
    //        }
            };
            ActionListener nextTrackListener = new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
                    eFunctions.reset();
                    Sweetdream.nextTrack("From System tray menu");
                }

            
    //        {
    //            super();
    //        }
            };
            final CheckboxMenuItem setRandom = new CheckboxMenuItem("Random");
            final CheckboxMenuItem setSequential = new CheckboxMenuItem("Sequential");
            ItemListener setRandomListener = new ItemListener() {
                public void itemStateChanged(ItemEvent e)
                {
                    setRandom.setState(true);
                    setSequential.setState(false);
                    Sweetdream.playTypeSelectBox.setSelectedIndex(0);
                }
            };
            ItemListener setSequentialListener = new ItemListener() {
                public void itemStateChanged(ItemEvent e)
                {
                    setRandom.setState(false);
                    setSequential.setState(true);
                    Sweetdream.playTypeSelectBox.setSelectedIndex(1);
                }
            };
            PopupMenu popup = new PopupMenu();
            CheckboxMenuItem setStayOnTop = new CheckboxMenuItem("Stay on Top");
            CheckboxMenuItem setPause = new CheckboxMenuItem("Pause");
            popup.add(setStayOnTop);
            PopupMenu chSubMenu = new PopupMenu("Play Options");
            setRandom.addItemListener(setRandomListener);
            setSequential.addItemListener(setSequentialListener);
            setSequential.setState(false);
            setRandom.setState(true);
            chSubMenu.add(setSequential);
            chSubMenu.add(setRandom);
            popup.add(chSubMenu);
            ItemListener setStayOnTopListener = new ItemListener() {

                public void itemStateChanged(ItemEvent e)
                {
                    if(e.getStateChange() == 1)
                    {
                        Sweetdream.frame.setAlwaysOnTop(true);
                    } else
                    {
                        Sweetdream.frame.setAlwaysOnTop(false);
                    }
                }
            };
            ItemListener setPauseListener = new ItemListener() {

                public void itemStateChanged(ItemEvent e)
                {
                    if (e.getStateChange() == 1) 
			 eFunctions.setPaused(true);
                    else eFunctions.setPaused(false);
                }
            };
            setStayOnTop.addItemListener(setStayOnTopListener);
            setPause.addItemListener(setPauseListener);
            popup.add(setPause);
            MenuItem ItemStop = new MenuItem("Stop");
            ItemStop.addActionListener(stopListener);
            popup.add(ItemStop);
            popup.addSeparator();
            MenuItem ItemExit = new MenuItem("Exit");
            ItemExit.addActionListener(exitListener);
            popup.add(ItemExit);
            MenuItem ItemNextTrack = new MenuItem("Next Track");
            ItemNextTrack.addActionListener(nextTrackListener);
            popup.add(ItemNextTrack);
            trayIcon = new TrayIcon(image, "eTrain", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(mouseListener);
            try
            {
                tray.add(trayIcon);
            }
            catch(AWTException e)
            {
                System.err.println("TrayIcon could not be added.");
            }
        } else
        {
            System.err.println("System tray is currently not supported.");
        }
    }

    public static void changeToolTip(String newText)
    {
        trayIcon.setToolTip(newText);
        //System.out.println("new tooltip: " + newText);
    }

}

