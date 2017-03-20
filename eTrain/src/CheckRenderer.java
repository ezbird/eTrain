package sweetdream;

import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.border.LineBorder;

// Referenced classes of package sweetdream:
//            CheckNode, Sweetdream

public class CheckRenderer extends JPanel
    implements TreeCellRenderer
{
    public class TreeLabel extends JLabel
    {

        boolean isSelected;
        boolean hasFocus;

        public void setBackground(Color color)
        {
            if(color instanceof ColorUIResource)
            {
                color = null;
            }
            //super.setBackground(color);
        }

        public void paint(Graphics g)
        {
            String str;
            if((str = getText()) != null && 0 < str.length())
            {
                if(isSelected)
                {
                    g.setColor(Color.green);  //UIManager.getColor("Tree.selectionBackground")
                } else
                {
                    g.setColor(new Color(195, 150, 220));
                }
                Dimension d = getPreferredSize();
                int imageOffset = 0;
                Icon currentI = getIcon();
                if(currentI != null)
                {
                    imageOffset = currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
                }
                g.fillRect(imageOffset, 0, d.width - 1 - imageOffset, d.height);
                if(hasFocus)
                {
                    g.setColor(UIManager.getColor("Tree.selectionBorderColor"));
                    g.drawRect(imageOffset, 0, d.width - 1 - imageOffset, d.height - 1);
                }
            }
            super.paint(g);
        }

        public Dimension getPreferredSize()
        {
            Dimension retDimension = super.getPreferredSize();
            if(retDimension != null)
            {
                retDimension = new Dimension(retDimension.width + 3, retDimension.height);
            }
            return retDimension;
        }

        public void setSelected(boolean isSelected)
        {
            this.isSelected = isSelected;
        }

        public void setFocus(boolean hasFocus)
        {
            this.hasFocus = hasFocus;
        }

        public TreeLabel()
        {
            super();
        }
    }


    protected JCheckBox check;
    protected TreeLabel label;
    
    boolean exists = false;
        
    public CheckRenderer()
    {
        setLayout(null);
        add(check = new JCheckBox());
        add(label = new TreeLabel());
        
                //renderer.setBackgroundNonSelectionColor(Color.YELLOW);
        //renderer.setBackgroundSelectionColor(Color.ORANGE);
        //renderer.setTextNonSelectionColor(Color.RED);
        //renderer.setTextSelectionColor(Color.BLUE);
        
     //   check.setBackground(UIManager.getColor("Tree.textBackground"));
     //   label.setForeground(UIManager.getColor("Tree.textForeground"));
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);
        setEnabled(tree.isEnabled());
        check.setSelected(((CheckNode)value).isSelected());
        label.setFont(Sweetdream.font10);
        //System.out.println("THE FOOOOONT!!:"+Sweetdream.font10);
        label.setSelected(isSelected);
        label.setFocus(hasFocus);
      //  System.out.println("stringValue: " + stringValue);
        exists = false;
        
     int num = 0;
     int fontSize = 12;
     
     if (tree == Sweetdream.tree) {   //if we are dealing with the Music tree   TAKES 10 SECONDS LONGER TO LOAD ETRAIN W/O THIS
    
     try {
     	     num = eFunctions.checkForNewArtist(stringValue.substring(1));
     	     if(num != -1) { 
     	     	    // label.setForeground(Color.yellow);
     	     	     label.setText("<html><b>"+stringValue+"</b></html>");
     	     	     if (Sweetdream.enableFrequencyFont) 
     	     	     	    fontSize = eFunctions.calculateFrequencyFont(num);      // Set font size based on numbers of times played
     	     	     else fontSize = Sweetdream.libraryFontSize;
     	      }
     	     else { label.setText("<html><font color='black'>"+stringValue+"</font></html>"); } //label.setForeground(Color.black);
     	     
      //  label.setFont(new Font("Monospaced", 0, fontSize));
      label.setFont(Sweetdream.font10);
        //label.setBackground(new Color(Sweetdream.themeColorRed,Sweetdream.themeColorGreen,Sweetdream.themeColorBlue));
     }
     catch(SQLException ex2) { ex2.printStackTrace(); }
     }
     else { label.setText("<html><font color='purple'>"+stringValue+"</font></html>");
     		label.setForeground(Color.yellow); }
   
     
        if (leaf)          label.setIcon(Sweetdream.musicNotesIcon);
        else if (expanded) label.setIcon(UIManager.getIcon("Tree.openIcon"));
        else 		   label.setIcon(UIManager.getIcon("Tree.closedIcon"));
           
        return this;
    }

    public Dimension getPreferredSize()
    {
        Dimension d_check = check.getPreferredSize();
        Dimension d_label = label.getPreferredSize();
        return new Dimension(d_check.width + d_label.width, d_check.height >= d_label.height ? d_check.height : d_label.height);
    }

    public void doLayout()
    {
        Dimension d_check = check.getPreferredSize();
        Dimension d_label = label.getPreferredSize();
        int y_check = 0;
        int y_label = 0;
        if(d_check.height < d_label.height)
        {
            y_check = (d_label.height - d_check.height) / 2;
        } else
        {
            y_label = (d_check.height - d_label.height) / 2;
        }
        check.setLocation(0, y_check);
        check.setBounds(0, y_check, d_check.width, d_check.height);
        label.setLocation(d_check.width, y_label);
        label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
    }

    public void setBackground(Color color)
    {
        if(color instanceof ColorUIResource)
        {
            color = null; //new Color(229, 229, 255);
        }
        //super.setBackground(color);
    }
}

