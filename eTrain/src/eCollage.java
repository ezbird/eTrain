package sweetdream;

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
import javax.imageio.*;
import java.net.*;
import org.apache.commons.lang.StringEscapeUtils;
import static org.imgscalr.Scalr.*;
 


public class eCollage {

	    private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
        	
            int keyCode = e.getKeyCode();
        	
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                      
         	 switch( keyCode ) { 
         	 case KeyEvent.VK_UP:
         	 	 // handle up 
         	 	 if (collageVideoFrame.isVisible()) {
         	 	 	 collageVideoFrame.setVisible(false); collageMusicFrame.setVisible(true);
         	 	 }
         	 	 else if (collageMusicFrame.isVisible()) {
         	 	 	 collageVideoFrame.setVisible(true); collageMusicFrame.setVisible(false); 
         	 	 }
         	 	 break;
         	 case KeyEvent.VK_DOWN:
         	 	 // handle down 
         	 	 if (collageVideoFrame.isVisible()) {
         	 	 	 collageVideoFrame.setVisible(false); collageMusicFrame.setVisible(true);
         	 	 }
         	 	 else if (collageMusicFrame.isVisible()) {
         	 	 	 collageVideoFrame.setVisible(true); collageMusicFrame.setVisible(false); 
         	 	 }
         	 	 break;
         	 case KeyEvent.VK_LEFT:
         	 	 // handle left
         	 	 if (collageVideoFrame.isVisible()) {
         	 	 	 goToCollagePage("Video",currentVideoPanelNum - 1); 
         	 	 }
         	 	 else if (collageMusicFrame.isVisible()) {
         	 	 	 goToCollagePage("Music",currentMusicPanelNum - 1); 
         	 	 }
         	 	 break;
         	 case KeyEvent.VK_RIGHT :
         	 	 // handle right
         	 	 if (collageVideoFrame.isVisible()) {
         	 	 	 goToCollagePage("Video",currentVideoPanelNum + 1); 
         	 	 }
         	 	 else if (collageMusicFrame.isVisible()) {
         	 	 	 goToCollagePage("Music",currentMusicPanelNum + 1); 
         	 	 }
         	 	 break;
         	 case KeyEvent.VK_N :
         	 	 // NEXT RANDOM!
         	 	 eFunctions.reset();
         	 	 if (collageVideoFrame.isVisible() && !addCollageFrame.isVisible()) {
         	 	 	 try { randomizerCollage();} catch(SQLException sq) { sq.printStackTrace(); }
         	 	 }
         	 	 break;
         	 }
            } 
            //else if (e.getID() == KeyEvent.KEY_RELEASED) {
            //    System.out.println("2test2");
            //} else if (e.getID() == KeyEvent.KEY_TYPED) {
            //    System.out.println("3test3");
            //}
            return false;
        }
    }
    
    
	
static int collageVideoRows = 2;
static int collageVideoCols = 2;
static int collageMusicRows = 2;
static int collageMusicCols = 2;
static int collagePlaylistRows = 2;
static int collagePlaylistCols = 2;
static int totalInCollage = 0;                       
static int currentVideoPanelNum = 1;
static int currentMusicPanelNum = 1;
static int currentPlaylistPanelNum = 1;
static int collageVideoNumCards = 1;
static int collageMusicNumCards = 1;
static int collagePlaylistNumCards = 1;
static int temp = 0;

static boolean collageRandomStart = false;
static boolean decorated = true;

static String MusicDirectoryPath = "",VideoDirectoryPath = "";
static String collageItemSize = "large"; //small, medium, large
static String[] numbers = { "1","2","3","4","5","6","7","8","9","10" };

static JPanel collageMusicPanel = new JPanel(new CardLayout());
static JPanel collageVideoPanel = new JPanel(new CardLayout());
static JPanel collagePlaylistPanel = new JPanel(new CardLayout());

static javax.swing.JFrame collageMusicFrame = new JFrame("Collage");
static javax.swing.JFrame collageVideoFrame = new JFrame("Collage");
static javax.swing.JFrame collagePlaylistFrame = new JFrame("Collage");
static Frame addCollageFrame = new Frame("Add to Collage");

static Random generator = new Random();
static eDatabase db = null;
static long startupTimeStart = 0;
static long startupTimeEnd = 0;

static final JButton addCollageBrowseBtn = new JButton("...");
static final JButton addCollageBrowseImageBtn = new JButton("...");
static final JButton collageAddBtn = new JButton("Add");

static final TextField addCollageFileBox = new TextField("", 30);
static final TextField addCollageImageBox = new TextField("", 30);
static final TextField addCollageCategoryBox = new TextField("", 30);
static final TextField addCollageNameBox = new TextField("", 30);

static JComboBox addCollageTypeSelectBox = new JComboBox(new String[] { "Video", "Music", "Playlist" });



public void goToCollagePage(String whichCollage, int number) {
	if (whichCollage.equals("Video")) {
	  	currentVideoPanelNum = number;
            	CardLayout cl = (CardLayout)(collageVideoPanel.getLayout());
            	    
            	if (currentVideoPanelNum > collageVideoNumCards) { currentVideoPanelNum = 1; cl.first(collageVideoPanel); return; }
            	else if (currentVideoPanelNum == 0) { currentVideoPanelNum = collageVideoNumCards; cl.last(collageVideoPanel); return; }
                else cl.next(collageVideoPanel);
                
        } else if (whichCollage.equals("Music")) {
        	currentMusicPanelNum = number;
            	CardLayout cl = (CardLayout)(collageMusicPanel.getLayout());
            	    
            	if (currentMusicPanelNum > collageMusicNumCards) { currentMusicPanelNum = 1; cl.first(collageMusicPanel); return; }
            	else if (currentMusicPanelNum == 0) { currentMusicPanelNum = collageMusicNumCards; cl.last(collageMusicPanel); return; }
                else cl.next(collageMusicPanel);
        }
                }
                
public eCollage() {
	
	  KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

  JPanel addCollagePane = new JPanel(new GridLayout(6, 3, 3, 7));
	addCollagePane.add(new JLabel("  Choose collage: "));
	addCollagePane.add(addCollageTypeSelectBox);
	addCollagePane.add(new JLabel(""));
	
	addCollagePane.add(new JLabel("  Name: "));
	addCollagePane.add(addCollageNameBox);
	addCollagePane.add(new JLabel(""));
	
	addCollagePane.add(new JLabel("  Choose File/Folder: "));
	addCollagePane.add(addCollageFileBox);
	addCollagePane.add(addCollageBrowseBtn);
		addCollageBrowseBtn.setMaximumSize(new Dimension(30, 20));
		
//	addCollagePane.add(new JLabel("  Collage Image: "));
//	addCollagePane.add(addCollageImageBox);
//	addCollagePane.add(addCollageBrowseImageBtn);
//		addCollageBrowseImageBtn.setMaximumSize(new Dimension(30, 20));
	addCollagePane.add(new JLabel("  Collage Category: "));
	addCollagePane.add(addCollageCategoryBox);
	addCollagePane.add(new JLabel(""));
	addCollagePane.add(new JLabel(""));
	addCollagePane.add(new JLabel("Add images to folder: "));
	addCollagePane.add(new JLabel("eTrain/images/collage/[Category]/[Name]"));

	addCollagePane.add(new JLabel(""));
	addCollagePane.add(collageAddBtn);

	addCollageFrame.add(addCollagePane);
	addCollageFrame.setSize(400, 210);
	addCollageFrame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
	
	WindowAdapter frameCloser = new WindowAdapter() {
            public void windowClosing(WindowEvent evt)
            {
                Frame frame = (Frame)evt.getSource();
                frame.setVisible(false);
                frame.dispose();
            }
        };
	addCollageFrame.addWindowListener(frameCloser);
	
/* Video */
    //  Container glassPaneVideo = (Container) collageVideoFrame.getRootPane().getGlassPane();
   //   glassPaneVideo.setVisible(true);
     // glassPaneVideo.setLayout(new GridBagLayout());

                    
	collageVideoFrame.setExtendedState(collageVideoFrame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
	collageVideoFrame.add(collageVideoPanel);
	collageVideoFrame.setJMenuBar(createMenuBar("VideoCollage"));
	collageVideoFrame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
	collageVideoFrame.setUndecorated(true);

/* end Video */

/* Music */
  //  Container glassPaneMusic = (Container) collageMusicFrame.getRootPane().getGlassPane();
   
	collageMusicFrame.setExtendedState(collageMusicFrame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
	collageMusicFrame.add(collageMusicPanel);
	collageMusicFrame.setJMenuBar(createMenuBar("Music Collage"));
	collageMusicFrame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
	collageMusicFrame.setUndecorated(true);
/* end Music */

/* Playlist */
      Container glassPanePlaylist = (Container) collagePlaylistFrame.getRootPane().getGlassPane();
      glassPanePlaylist.setVisible(true);
      glassPanePlaylist.setLayout(new GridBagLayout());
      GridBagConstraints gbcPlaylist = new GridBagConstraints();
      gbcPlaylist.weightx = 1.0;
      gbcPlaylist.weighty = 1.0;
      gbcPlaylist.fill = GridBagConstraints.NONE;
      gbcPlaylist.insets = new Insets(0, 10, 400, 0);
      gbcPlaylist.anchor = GridBagConstraints.SOUTHWEST;   

                    
	collagePlaylistFrame.setExtendedState(collagePlaylistFrame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
	collagePlaylistFrame.add(collagePlaylistPanel);
	collagePlaylistFrame.setJMenuBar(createMenuBar(" Playlist Collage"));
	collagePlaylistFrame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
	collagePlaylistFrame.setUndecorated(true);
/* end Playlist */


       addCollageBrowseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                eCollage.addCollageFileBox.setText(eFunctions.browseDirectory().replace(Sweetdream.VideoDirectoryPath,""));
            }
        });
        addCollageBrowseImageBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                eCollage.addCollageImageBox.setText(eFunctions.browseDirectory());
            }
        });                       
     collageAddBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
	       //Submit new collage item into database
	       addCollageFrame.setVisible(false);

	        try { 
		  Sweetdream.db.update("INSERT INTO collage(Type,Name,FilePath,Category) VALUES('" + addCollageTypeSelectBox.getSelectedItem().toString() + "','" 
		  	  								+ StringEscapeUtils.escapeSql(addCollageNameBox.getText()) + "','" 
		  	  								+ StringEscapeUtils.escapeSql(addCollageFileBox.getText().replace(VideoDirectoryPath,"").replace(addCollageCategoryBox.getText(),""))
		  	  								+ "','" + addCollageCategoryBox.getText() + "')"); 
		  eFunctions.refreshDatabaseTable("collage");
		  refreshCollage("Video");
		}
                catch(SQLException ex2)  { ex2.printStackTrace(); }
               
               if(addCollageImageBox.getText().toString().equals("")) eFunctions.openBrowser(addCollageNameBox.getText().toString());
	       else {
	       	       try {
	       	       	     eFunctions.copyFile(new File(addCollageImageBox.getText()), 
	       		          new File("images" + File.separator + "collage" + File.separator + addCollageCategoryBox.getText() + File.separator + addCollageNameBox.getText() + File.separator + addCollageImageBox.getText().toString().substring(addCollageImageBox.getText().toString().lastIndexOf(File.separator))));       
	       	       }                       
	       	       catch(IOException e1) { }
	       	       try { eCollage.refreshCollage("Video");}
	       	       catch(SQLException ex2)  { ex2.printStackTrace(); }
               }
	    }
	  });
	}
	
	
	    /* USE FOR BUILDING STAND-ALONE eCollage
	        
	    
	    MAIN 
    public static void main(String args[])
    {
	startupTimeStart = System.currentTimeMillis();
	    
        try
        {
            System.out.println("Voice recognition: " + args[0]);
        }
        catch(ArrayIndexOutOfBoundsException ex1) { }
        try
        {
            eCollage theCollage = new eCollage();
            db = new eDatabase("eTrainDatabase");
            
	        }
        catch(Exception ex1) { ex1.printStackTrace(); return; }
	
	startupTimeEnd = System.currentTimeMillis();
	System.out.println("Startup Time: " + ((startupTimeEnd - startupTimeStart)) + " ms");
	try {refreshVideoCollage();} catch(SQLException ex1) { ex1.printStackTrace(); return; }
    }
    */
    
    	    
public static void refreshPlaylistCollage() throws SQLException
    {
    	    
    } 	    
    
 
    
    
    
    /* whichCollage: Music, Video   future: Playlist */
    public static void refreshCollage(String whichCollage) throws SQLException
    {
    	    /* Originally outputted only what was in database
    	       Now, outputs all files in video directory to further facilitate adding images */
        String imagePath = null,Name = null,FilePath = null,Category = null,sortBy = null,order = null,theDirectoryPath = "";
        JPanel thePanel = null;
        ResultSet rs = null;
        Object o = null;
        GridBagConstraints c = new GridBagConstraints();
	totalInCollage = 0;
	int numColumns = 0,numRows = 0,q = 0,w = 0,z=0,currentNum = 0;
        
        
	if (whichCollage.equals("Video")) {
		theDirectoryPath = VideoDirectoryPath;
		thePanel = collageVideoPanel;
		numColumns = collageVideoCols;
        	numRows = collageVideoRows;
	}
	else if (whichCollage.equals("Music")) {
		theDirectoryPath = MusicDirectoryPath;
		thePanel = collageMusicPanel;
		numColumns = collageMusicCols;
        	numRows = collageMusicRows;
	}
	
	int numPerPage = (numColumns * numRows); // when to switch to new panels (all positions filled)
	thePanel.removeAll();


	/* ---------------------    ----------------- */
	/* ---------------------MUSIC ----------------- */
	/* ---------------------    ----------------- */
   if (whichCollage.equals("Music")) {
	File ArtistsDir = new File("images" + File.separator + "artists");
        String tmp[] = ArtistsDir.list();
            
        totalInCollage = tmp.length;
        
        System.out.println("Collage: (total) " + totalInCollage + " (numPerPage) " + numPerPage);
        
            /* Add enough "cards" to collageVideoPanel to accommodate the totalInCollage */
			int numCards = (totalInCollage / numPerPage) + 1;
			collageMusicNumCards = numCards;
			//System.out.println("numCards: " + numCards);
				for (int i = 0; i < numCards; i++) {
					 collageMusicPanel.add(createComponent(String.valueOf(i)), String.valueOf(i));
				}
					 
			
				
				
	/* SHOULD LOOP OVER ARTISTS TABLE IN DATABASE INSTEAD */
            if(ArtistsDir.list() != null && tmp.length > 0)
            {
            	    
            // Loop through all artist folders
            for(int i = 0; i < tmp.length; i++) {
            	    
                int temp = 0;
                int randomNum = 0;
                
                // In an artist's folder, pick Artist Image at random
                String thisArtist = tmp[i].toString();
                File theArtistFolder = new File("images" + File.separator + "artists" + File.separator + thisArtist);
                String tmp2[] = theArtistFolder.list();
                
                if (tmp2 == null || tmp2.length == 0) 
                	   continue;
        
            // rows and columns of grid
            if(q == numColumns)
            {
                w++;
                q = 0;
            }
            //if (w > numRows) return;
            c.gridx = q;
            c.gridy = w;
            q++;
                 

                	if (tmp2.length == 1)
                	   randomNum = 0;
                	else
                	   randomNum = generator.nextInt(tmp2.length);
                
                
                    imagePath = "images" + File.separator + "artists" + File.separator + thisArtist + File.separator + tmp2[randomNum].toString();
                    
                    if(imagePath.contains("humbs.db")) thisArtist = "";   // "Thumbs" silly windows nonsense
                    else
                    {
                        if (!thisArtist.equals("")) {
                        	
					if(currentNum > numPerPage) {  //start new panel if we've reached the last row
						z++; currentNum=0; }
					else 
						currentNum++;
					
					    ((JPanel)collageMusicPanel.getComponent(z)).add(addToCollage("Music", imagePath, thisArtist, collageItemSize), c); 
								
				
			}
               	    }
 
                }
                     
            }
    }
    
    	/* ---------------------    ----------------- */
	/* ---------------------VIDEO ----------------- */
	/* ---------------------    ----------------- */
	else if (whichCollage.equals("Video")) {
		
        int random1 = generator.nextInt(4);
        int random2 = generator.nextInt(7);
	
             if(random1 == 0) sortBy = "Name";
        else if(random1 == 1) sortBy = "FilePath";
        else if(random1 == 2) sortBy = "CollageID";
        else 		      sortBy = "Category";
        
        if(random2 < 4) order = "asc";  // to mix it up
        else order = "desc";
        
        rs = Sweetdream.db.query("SELECT * FROM collage WHERE Type = '" + whichCollage + "' ORDER BY " + sortBy + " " + order);
        
            
        
        while(rs.next()) 
        {
        	totalInCollage++;
        }
            
        System.out.println("Collage: (total) " + totalInCollage + " (numPerPage) " + numPerPage);
        
                	/* Add enough "cards" to collageVideoPanel to accommodate the totalInCollage */
			int numCards = (totalInCollage / numPerPage) + 1;
			collageVideoNumCards = numCards;
			 for (int i = 0; i < numCards; i++) {
			 	 collageVideoPanel.add(createComponent(String.valueOf(i)), String.valueOf(i));
			 }
			  
			 
	/* We pull the list of files from the Video Directory, and add an item to the collage for each one */
        File theDir = new File(Sweetdream.VideoDirectoryPath + "/Movies");
	String tmpt[] = theDir.list();
	
	// Shuffle collage
	if (theDir.list() != null) tmpt = shuffleArray(tmpt); 
	else return; // otherwise there's a null pointer

        int x = -1;
        int xy = 0;
        
        rs = Sweetdream.db.query("SELECT * FROM collage WHERE Type = '" + whichCollage + "' ORDER BY " + sortBy + " " + order);
        

            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(0, 0, 0, 0);  //This is the spacing between collage elements
	
       
       while(rs.next())
        {
        	x++;
        	xy = 1;
            for(int i = 0; i < 5; i++)
            { 
                o = rs.getObject(i + 1);
                     if(i == 2) Name = o.toString();
                else if(i == 3) FilePath = o.toString();
                else if(i == 4) Category = o.toString();
            }
        
        if (xy == 0) { FilePath = "";Name="";Category=""; }
	    
	    // rows and columns of grid
            if(q == numColumns)
            {
                w++;
                q = 0;
            }

            c.gridx = q;
            c.gridy = w;
            q++;
	    String dir = "";
	    String folder = "";

       	         dir = "images" + File.separator + "collage" + File.separator + Category + File.separator + Name;
       	         FilePath = Category + File.separator + FilePath; 

            File collageImagesDir = new File(dir);
            if(!collageImagesDir.exists())
            {
		System.out.println("Creating new collage image folder: " + dir);
                collageImagesDir.mkdir();
            }
            String tmp[] = collageImagesDir.list();
            
            if(collageImagesDir.list() != null && tmp.length > 0)
            {

                    int randomNum = generator.nextInt(tmp.length);
                    imagePath = dir + File.separator + tmp[randomNum].toString();		
					//System.out.println("numCards: " + (totalInCollage / numPerPage));
					//System.out.println("numPerPage: " + numPerPage);
					//System.out.println("totalInCollage: " + totalInCollage);
					//System.out.println("imagePath: " + imagePath);
					//System.out.println("FilePath: " + FilePath);
					//System.out.println("~~~~~~~~~~~~~~~");
					
					if (FilePath.equals("/")) { imagePath = "images/etrain/ePlanet.jpg"; FilePath = tmpt[x].toString(); }
					
  				if (currentNum < numPerPage)
					((JPanel)thePanel.getComponent(0)).add(addToCollage(whichCollage, imagePath, FilePath, eCollage.collageItemSize), c);	 
				else if(currentNum < (numPerPage*2))   //start new panel if we've reached the last row
					((JPanel)thePanel.getComponent(1)).add(addToCollage(whichCollage, imagePath, FilePath, eCollage.collageItemSize), c); //start new panel if we've reached the last row
				else if(currentNum < (numPerPage*3)) 
					((JPanel)thePanel.getComponent(2)).add(addToCollage(whichCollage, imagePath, FilePath, eCollage.collageItemSize), c);
				else if(currentNum < (numPerPage*4)) 
					((JPanel)thePanel.getComponent(3)).add(addToCollage(whichCollage, imagePath, FilePath, eCollage.collageItemSize), c);
				else if(currentNum >= (numPerPage*4))  
					((JPanel)thePanel.getComponent(4)).add(addToCollage(whichCollage, imagePath, FilePath, eCollage.collageItemSize), c);

                currentNum++;
            }
            
            else    /* No images saved for this video collage file */
            {
			
            	    if (currentNum < numPerPage)
					((JPanel)thePanel.getComponent(0)).add(addToCollage(whichCollage, "images/etrain/ePlanet.jpg", FilePath, eCollage.collageItemSize), c);	 
			else if(currentNum < (numPerPage*2))   //start new panel if we've reached the last row
					((JPanel)thePanel.getComponent(1)).add(addToCollage(whichCollage, "images/etrain/ePlanet.jpg", FilePath, eCollage.collageItemSize), c); //start new panel if we've reached the last row
			else if(currentNum < (numPerPage*3)) 
					((JPanel)thePanel.getComponent(2)).add(addToCollage(whichCollage, "images/etrain/ePlanet.jpg", FilePath, eCollage.collageItemSize), c);
			else if(currentNum >= (numPerPage*3))  
					((JPanel)thePanel.getComponent(3)).add(addToCollage(whichCollage, "images/etrain/ePlanet.jpg", FilePath, eCollage.collageItemSize), c);
            	    
            	    currentNum++;
            }
            
	}
	
	
	}
	CardLayout cl = (CardLayout)(thePanel.getLayout());
                cl.first(thePanel);        
    }
    
                                                                         
public static String[] shuffleArray(String[] array) {
{
	//if (array == null) return;
   Random rng = new Random();   // i.e., java.util.Random.
   int n = array.length;        // The number of items left to shuffle (loop invariant).
   while (n > 1)
   {
      int k = rng.nextInt(n);  // 0 <= k < n.
      n--;                     // n is now the last pertinent index;
      String temp = array[n];     // swap array[n] with array[k] (does nothing if k == n).
      array[n] = array[k];
      array[k] = temp;
   }
   return array;
}
    }
    
   public static Image iconToImage(Icon icon) {
   if (icon instanceof ImageIcon) {
      return ((ImageIcon)icon).getImage();
   } 
   else {
      int w = icon.getIconWidth();
      int h = icon.getIconHeight();
      GraphicsEnvironment ge = 
      GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gd = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gd.getDefaultConfiguration();
      BufferedImage image = gc.createCompatibleImage(w, h);
      Graphics2D g = image.createGraphics();
      icon.paintIcon(null, g, 0, 0);
      g.dispose();
      return image;
   }
 }
    
 
 // What would every be the difference coding-wise between the Music and Video collages?
 
 
  /* Collage, Image path, File Path, Collage item size */
public static JLabel addToCollage(String whichCollage, String path, String FilePath2, String Size)
    {
      //  long timeStart = System.currentTimeMillis();
        
        final JLabel newLabel = new JLabel();
        final String imagepath = path;
        final String FilePath = FilePath2;
        int randomWidthStartPoint = 5;
        int randomHeightStartPoint = 5;
        int maxWidth = 210;                                          
        int maxHeight = 175;
        
        if (Size.equals("large")) {
        	maxWidth = 600;
        	maxHeight = 600;
        } else if (Size.equals("medium")) {
        	maxWidth = 500;
        	maxHeight = 500;
        } else if (Size.equals("small")) {
        	maxWidth = 400;
        	maxHeight = 400;
        }
        
        BufferedImage image = null;
  
      
      try {
      try {
      	      image = ImageIO.read(new File(imagepath));
      } catch (MalformedURLException ex ) {}
      } catch (IOException ex ) {}
      
      
      if (image == null) return new JLabel("");
      
      	 /* Small image, resize up */
         if (image.getWidth() <= maxWidth) {  		/* Resize to fill up the collage label width-wise */
         	 image = resize(image, Method.SPEED, maxWidth*3, OP_ANTIALIAS);  // COOL !  OP_GRAYSCALE  ... Scalr.Mode.FIT_TO_WIDTH
                 image = crop(image, maxWidth, maxHeight);  //  <<<<< SOMETIMES THIS CROP BREAKS
         }
         /* Big image, resize down & crop */
         else {		// if (image.getWidth() > maxWidth) && image.getHeight() > maxHeight)
         	 
         	 randomWidthStartPoint = generator.nextInt(image.getWidth()-maxWidth);  
               //  randomHeightStartPoint = generator.nextInt(image.getHeight());	 //-(maxHeight/2)
                 if (image.getHeight() < maxHeight) image = resize(image, Method.SPEED, maxWidth*3, OP_ANTIALIAS);
                 image = crop(image,randomWidthStartPoint, 0, maxWidth, maxHeight, OP_ANTIALIAS); //
         }

         newLabel.setIcon(new ImageIcon(image));
        
        /* Adjust Size Border Color ~~~~~~~~~~~~~~~~~~~~~~~*/
        newLabel.setBorder(new LineBorder(Color.black, 1));
        
	final JPopupMenu rightClickMenu = new JPopupMenu();
        	JMenuItem item0 = new JMenuItem("Add Image");
        	rightClickMenu.add(item0);
        	
          item0.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {	    
		    JMenuItem source = (JMenuItem)(e.getSource());
		    String s = source.getText().trim();

	   if(s.equals("Add Image")) {
	   	   addCollageFileBox.setText(FilePath);           
                   addCollageNameBox.setText("");
		   addCollageFrame.setVisible(true);
		}
		}
           });
        	
         MouseListener collageClickMusic = new MouseAdapter() {

            public void mouseEntered(MouseEvent e)
            {
                newLabel.setBorder(new LineBorder(Color.white, 1));
                collageMusicFrame.setCursor(new Cursor(12));
            }
            public void mouseExited(MouseEvent e)
            {
                newLabel.setBorder(new LineBorder(Color.black, 1));
                collageMusicFrame.setCursor(new Cursor(0));
            }
            public void mousePressed(MouseEvent e)
            {
                newLabel.setBorder(new LineBorder(Color.green, 1));
                if(SwingUtilities.isLeftMouseButton(e))
                {
			Sweetdream.usingSearch = FilePath;
			Sweetdream.themeDisplay.setText(Sweetdream.usingSearch);
			Sweetdream.nextTrack("Music collage clicked.");
			collageMusicFrame.setVisible(false);
			Sweetdream.frame.setVisible(false);
			Sweetdream.imageFrame.setVisible(true);
                }
            }
        };
        
        
        MouseListener collageClickVideo = new MouseAdapter() {

            public void mouseEntered(MouseEvent e)
            {
                newLabel.setBorder(new LineBorder(Color.white, 1));
                collageVideoFrame.setCursor(new Cursor(12));        
            }
            public void mouseExited(MouseEvent e)
            {                
                newLabel.setBorder(BorderFactory.createRaisedBevelBorder());
                newLabel.setBorder(new LineBorder(Color.black, 1));
                collageVideoFrame.setCursor(new Cursor(0));
            }
            public void mousePressed(MouseEvent e)
            {
                newLabel.setBorder(new LineBorder(Color.green, 1));
                if(SwingUtilities.isLeftMouseButton(e))
                {
                	eFunctions.reset();
                	String fileToPlayPath = FilePath; 
                 
		    /* Play specific file collage file */
                    if(fileToPlayPath.contains("."))
                    {
                    	   	    
                    	    
                     /* Build the file path for the collage file */
                     if(fileToPlayPath.contains("ftp"))    fileToPlayPath = fileToPlayPath.substring(1);
                     else fileToPlayPath = Sweetdream.VideoDirectoryPath + File.separator + fileToPlayPath;
            
                     System.out.println("playing collage file: " + fileToPlayPath);
                    
                     collageVideoFrame.dispose();
                     eFunctions.launchMovie(fileToPlayPath,"");
                     Sweetdream.frame.setVisible(true);
  
		    /*OR play random item in collage item's folder*/
                    } else
                    {
                        File specificCollageDir = new File(Sweetdream.VideoDirectoryPath + File.separator + fileToPlayPath);
                        String fullFilePath = null;
                        String tmp[] = specificCollageDir.list();
                        if(specificCollageDir.list() != null && tmp.length > 0)
                        {
                            int temp = 1;
                            do
                            {
                                int randomNum = generator.nextInt(tmp.length);
                                fullFilePath = specificCollageDir + File.separator + tmp[randomNum].toString();
                                if(fullFilePath.contains("humbs.db"))
					temp = 0;
                                else
                                {
                                    temp = 1;
                                    System.out.println("collage, directory: " + fullFilePath.replaceAll("\\s", "\\ "));
                                    eFunctions.launchMovie(fullFilePath,"");///
                                    //eVideo.playTheme = "which category?";
                                    collageVideoFrame.setVisible(false);
                                    Sweetdream.frame.setVisible(true);
                                }
                            } while(temp == 0);
			   
			} else System.out.println("No collage item found.");
			
			
                    } 
                }
                
                //RIGHT CLICK on collage item
                else {
                	rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
                }
                
                newLabel.setBorder(new LineBorder(Color.black, 2));
            }
        };
       
        
       if (whichCollage.equals("Video")) newLabel.addMouseListener(collageClickVideo);
       else if (whichCollage.equals("Music")) newLabel.addMouseListener(collageClickMusic);
       
     //   System.out.println("addToCollage: " + (System.currentTimeMillis() - timeStart) + " --  " + path);
        return newLabel;
    
    }
 
 

    
    /* SPECIAL FEATURE: Selects a random file from collage and plays it */
    public static void randomizerCollage() throws SQLException {
    	    
    	ResultSet rs = null;
        Object o = null;
        
 if (collageMusicFrame.isVisible()) { 
 	 
 } else if (collageVideoFrame.isVisible()) { 
        rs = Sweetdream.db.query("SELECT FilePath FROM collage WHERE Type = 'Video' ORDER BY RAND() LIMIT 1");
        ResultSetMetaData meta = rs.getMetaData();

        while(rs.next()) 
        {
         o = rs.getObject(1);     	    
        }
            
            System.out.println("randomizer path: " + o);
            
    	       String fileToPlayPath = o.toString(); 
		    /*Play specific file collage item*/
                    if(fileToPlayPath.contains("."))
                    {
                    	    
                   /* Build the file path for the collage file */
                     if(fileToPlayPath.contains("ftp"))    fileToPlayPath = fileToPlayPath.substring(1);
                     else fileToPlayPath = Sweetdream.VideoDirectoryPath + File.separator + "Movies" + File.separator + fileToPlayPath;
            
                     System.out.println("playing collage file: " + fileToPlayPath);
                     
                     collageVideoFrame.setVisible(false);
                     collageVideoFrame.dispose();
                     eFunctions.launchMovie(fileToPlayPath,"");
                     Sweetdream.frame.setVisible(true);                     
                        
		    /*OR play random item in collage item's folder*/
                    }
                    
 	}
                    
    }
    
    
    public static Icon resizeIcon(String imagePath, Image theimage, String whereFrom)
    {
    	//long timeStart = System.currentTimeMillis();
        Icon icon = null;
        int maxWidth = 0;
        int maxHeight = 0;
        if (!imagePath.equals("")) {
          try  {
            icon = createImageIcon(imagePath);
          }
          catch(NullPointerException ex1)
          {
            ex1.printStackTrace();
            return null;
        }
        
        } else icon = new ImageIcon(theimage);
        
        if(icon == null) { System.out.println("no icon to resize!"); return null; }
        
        if(whereFrom.equals("Collage"))
        {
            maxWidth = 501;
            maxHeight = 501;
        }
        if(whereFrom.equals("CollageEnlarge"))
        {
            maxWidth = 250;
            maxHeight = 320;
        }
        if(whereFrom.equals("Artist Image"))
        {
            maxWidth = 270;
            maxHeight = 270;
        }
	if(whereFrom.equals("Photo Tree"))
        {
            maxWidth = 500;
            maxHeight = 500;
        }
        Dimension largestDimension = new Dimension(maxWidth, maxHeight);
        if((icon.getIconWidth() > maxWidth || icon.getIconHeight() > maxHeight) || whereFrom.equals("Collage"))   //changing this to && made it so it always resized collages to fill up space, but messed up aspect ratio
        {													  // fix was to add   """|| whereFrom.equals("Collage")"""
            int iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();
            float aspectRation = (float)iconWidth / (float)iconHeight;
            if((float)largestDimension.width / (float)largestDimension.height > aspectRation)
                largestDimension.width = (int)Math.ceil((float)largestDimension.height * aspectRation);
            else
                largestDimension.height = (int)Math.ceil((float)largestDimension.width / aspectRation);
            
        }     
            Image image = Toolkit.getDefaultToolkit().createImage(imagePath).getScaledInstance(largestDimension.width, largestDimension.height, 4);
                 
            //System.out.println("resize time: " + (System.currentTimeMillis() - timeStart) + " --  " + imagePath);
            return new ImageIcon(image);
    }
    
    
     protected static Icon createImageIcon(String path)
    {
        if(path != null)
          return new ImageIcon(path);
        else {
            System.err.println("Couldnt find file: " + path);
            return null;
        }
    }
    
            /* updateCollageDimensions("Rows" or "Cols", number of rows/cols); */
        public void updateCollageDimensions(String RowsOrCols, int number) {
        	String collage = "",theVariable = "";
        	
        	     if (collageMusicFrame.isVisible()) { 
        	     	collage = "Music"; theVariable = "collage" + collage + RowsOrCols; if (theVariable.equals("collageMusicCols")) collageMusicCols = number; else collageMusicRows = number; 
        	     }
        	     else if (collageVideoFrame.isVisible()) { 
        	     	collage = "Video"; theVariable = "collage" + collage + RowsOrCols; if (theVariable.equals("collageVideoCols")) collageVideoCols = number; else collageVideoRows = number; 
        	     }
        	     else if (collagePlaylistFrame.isVisible()) { 
        	     	collage = "Playlist"; theVariable = "collage" + collage + RowsOrCols; if (theVariable.equals("collagePlaylistCols")) collagePlaylistCols = number; else collagePlaylistRows = number; 
        	     }
        	
        	
        	
        	try {
        	    if (collageMusicFrame.isVisible()) {
            	     	     refreshCollage("Music");Sweetdream.db.update("UPDATE variables SET Value = '"+number+"' WHERE variablename = '"+theVariable+"'");  
            	    }
            	    else if (collagePlaylistFrame.isVisible()) {
            	    	     refreshCollage("Playlist");Sweetdream.db.update("UPDATE variables SET Value = '"+number+"' WHERE variablename = '"+theVariable+"'");
            	    }
            	    else {
            	    	     refreshCollage("Video");Sweetdream.db.update("UPDATE variables SET Value = '"+number+"' WHERE variablename = '"+theVariable+"'");
            	    }
            	} catch(SQLException sq) { sq.printStackTrace(); } 
        }
        
    public JMenuBar createMenuBar(String whichFrame) {
	/*Collage Menu Bar*/
	JMenuBar menuBar = new JMenuBar();;
        JMenu menu;
        JMenuItem menuItem;
	
        


        menu = new JMenu("Collage");
        menu.setMnemonic(KeyEvent.VK_V);
	menuBar.add(menu);
	
	menuItem = new JMenuItem("Video");
		menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
		collageVideoFrame.setVisible(true);
		collageVideoPanel.setVisible(true);
		collageMusicFrame.setVisible(false);
		collagePlaylistFrame.setVisible(false);
            }
        });
        menu.add(menuItem);
        menuItem.setBackground(Color.gray);
        	menuItem = new JMenuItem("Music");
		menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
		collageVideoFrame.setVisible(false);
		collageMusicFrame.setVisible(true);
		collagePlaylistFrame.setVisible(false);
            }
        });
        menu.add(menuItem);
        	menuItem = new JMenuItem("Playlist");
		menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
		collagePlaylistFrame.setVisible(true);
		collagePlaylistPanel.setVisible(true);
		collageMusicFrame.setVisible(false);
		collageVideoFrame.setVisible(false);
            }
        });
        menu.add(menuItem);


	
        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
	menuBar.add(menu);

	menuItem = new JMenuItem("Refresh");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try { 
                	if (collageVideoFrame.isVisible()) { refreshCollage("Video"); } else if (collageMusicFrame.isVisible()) { refreshCollage("Music"); }
		 } catch(SQLException ex2)  { ex2.printStackTrace(); }
            }
        });
        menu.add(menuItem);
	menu.addSeparator();
	menuItem = new JMenuItem("Database");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
               // eFunctions.printActivity("Open collage database.");
		Sweetdream.optionsFrame.setVisible(true);
		Sweetdream.tree_options.setSelectionRow(4);
		Sweetdream.tabbedPaneViewDatabase.setSelectedIndex(4);
            }
        });
        menu.add(menuItem);
	menuBar.add(menu);
	
	menu = new JMenu("Size");
        menu.setMnemonic(KeyEvent.VK_V);
	menuBar.add(menu);
	
	JMenu chSubMenu = new JMenu("Columns");
	
	menuItem = new JMenuItem("1");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	    updateCollageDimensions("Cols",1); 
            }
            });
	chSubMenu.add(menuItem);
	menuItem = new JMenuItem("2");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	  updateCollageDimensions("Cols",2);  
                }
            });
	chSubMenu.add(menuItem);
	menuItem = new JMenuItem("3");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Cols",3);
                }
            });
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("4");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Cols",4);
                    
                }
            });
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("5");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Cols",5);
                    
                }
            });                                   
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("6");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Cols",6);
                    
                }
            });
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("7");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Cols",7);
                    
                }
            });
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("8");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Cols",8);
                    
                }
            });
        chSubMenu.add(menuItem);
        
        		menuItem = new JMenuItem("9");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Cols",9);
                    
                }
            });
        chSubMenu.add(menuItem);
        
        		menuItem = new JMenuItem("10");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Cols",10);
                    
                }
            });
        chSubMenu.add(menuItem);
        menu.add(chSubMenu);     
        
        
        
        
        chSubMenu = new JMenu("Rows");
	
	menuItem = new JMenuItem("1");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Rows",1);
                    
                }
            });
	chSubMenu.add(menuItem);
	menuItem = new JMenuItem("2");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Rows",2);
                    
                }
            });
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("3");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Rows",3);
                    
                }
            });
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("4");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Rows",4);
                    
                }
            });
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("5");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Rows",5);
                    
                }
            });
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("6");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Rows",6);
                      
                }
            });
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("7");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    updateCollageDimensions("Rows",7);
                    
                }
            });
	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("8");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) { 
                    updateCollageDimensions("Rows",8);
                    
                }
            });
        chSubMenu.add(menuItem);
        
        	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("9");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) { 
                    updateCollageDimensions("Rows",9);
                    
                }
            });
        chSubMenu.add(menuItem);
        
        	chSubMenu.add(menuItem);
		menuItem = new JMenuItem("10");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) { 
                    updateCollageDimensions("Rows",10);
                    
                }
            });
        chSubMenu.add(menuItem);
       menu.add(chSubMenu);  
        
	
       	
	menu = new JMenu("Options");
        menu.setMnemonic(KeyEvent.VK_N);
	menuBar.add(menu);
	
        menu.getAccessibleContext().setAccessibleDescription("Random starting position");
	menuItem = new JCheckBoxMenuItem("Random starting position");
	if (collageRandomStart) menuItem.setSelected(true);
	
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	    if (eCollage.collageRandomStart)
            	    	    eCollage.collageRandomStart = false;
            	    else    eCollage.collageRandomStart = true;
            try
            {
                Sweetdream.db.update("UPDATE variables SET Value = '" + eCollage.collageRandomStart + "' WHERE variablename = 'collageRandomStart'");
            } catch(SQLException ex2) { ex2.printStackTrace(); }
            }
        });
        menu.add(menuItem);
	menuBar.add(menu);
	chSubMenu = new JMenu("Image Size");

	
	menuItem = new JMenuItem("Small");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    eCollage.collageItemSize = "small";
                    try { Sweetdream.db.update("UPDATE variables SET Value = '" + eCollage.collageItemSize + "' WHERE variablename = 'collageItemSize'");
                    	  if (collageVideoFrame.isVisible()) { refreshCollage("Video"); } else if (collageMusicFrame.isVisible()) { refreshCollage("Music"); }
                    		}
                    	catch(SQLException ex2)  { ex2.printStackTrace(); }
                    
                }
            });
	chSubMenu.add(menuItem);
	menuItem = new JMenuItem("Medium");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    eCollage.collageItemSize = "medium";
                    try { Sweetdream.db.update("UPDATE variables SET Value = '" + eCollage.collageItemSize + "' WHERE variablename = 'collageItemSize'");
                    	if (collageVideoFrame.isVisible()) { refreshCollage("Video"); } else if (collageMusicFrame.isVisible()) { refreshCollage("Music"); }
                    }
                    	catch(SQLException ex2)  { ex2.printStackTrace(); }
                    
                }
            });
	chSubMenu.add(menuItem);
	chSubMenu.add(menuItem);
	menuItem = new JMenuItem("Large");
	menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    eCollage.collageItemSize = "big";
                    try { Sweetdream.db.update("UPDATE variables SET Value = '" + eCollage.collageItemSize + "' WHERE variablename = 'collageItemSize'");
                    	    if (collageVideoFrame.isVisible()) { refreshCollage("Video"); } else if (collageMusicFrame.isVisible()) { refreshCollage("Music"); }
                    }
                    	catch(SQLException ex2)  { ex2.printStackTrace(); }
                    
                }
            });
	chSubMenu.add(menuItem);
	
	
        menu.add(chSubMenu);
	//menuBar.add(menu);
	
	menu = new JMenu("Add");
        menu.setMnemonic(KeyEvent.VK_N);
	menuBar.add(menu);
	
        menu.getAccessibleContext().setAccessibleDescription("Add Video");
	menuItem = new JMenuItem("New Video");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                eCollage.addCollageImageBox.setText("");
                eCollage.addCollageCategoryBox.setText("Movies"); 
                eCollage.addCollageFileBox.setText("");           
                eCollage.addCollageNameBox.setText("");
		eCollage.addCollageFrame.setVisible(true);
            }
        });
       // menu.add(menuItem);
	
	menuItem = new JMenuItem("File...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	    String asdf = eFunctions.browseDirectory();
            	    
            	eCollage.addCollageImageBox.setText("");
                eCollage.addCollageCategoryBox.setText("Movies"); 
                asdf = asdf.substring(asdf.lastIndexOf("/")+1);
                eCollage.addCollageFileBox.setText(asdf);    
                asdf = asdf.substring(0,asdf.length()-4);
                eCollage.addCollageNameBox.setText(asdf);
		eCollage.addCollageFrame.setVisible(true);
            }
        });
        menu.add(menuItem);
	
	menuItem = new JMenuItem("Folder...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                 	    String asdf = eFunctions.browseDirectory();
            	    
            	eCollage.addCollageImageBox.setText("");
                eCollage.addCollageCategoryBox.setText(""); 
                asdf = asdf.substring(asdf.lastIndexOf("/")+1);
                eCollage.addCollageFileBox.setText(asdf);    
                eCollage.addCollageNameBox.setText(asdf);
		eCollage.addCollageFrame.setVisible(true);
            }
        });
        menu.add(menuItem);
	menuBar.add(menu);
	
	
	menu = new JMenu("Random");
        menu.setMnemonic(KeyEvent.VK_N);
	menuBar.add(menu);
	
        menu.getAccessibleContext().setAccessibleDescription("GO!");
	menuItem = new JMenuItem("GO!");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	    // Pick collage item randomly
            	    try { randomizerCollage();} catch(SQLException sq) { sq.printStackTrace(); }    
            }
        });
        menu.add(menuItem);
	menuBar.add(menu);
	
	
	
	
	
	menu = new JMenu("Scroll");
        menu.setMnemonic(KeyEvent.VK_N);

	
	menu = new JMenu("Exit");
        menu.setMnemonic(KeyEvent.VK_A);
	menuItem = new JMenuItem("Close Collage");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
		collageVideoFrame.setVisible(false);
		collageMusicFrame.setVisible(false);
		collagePlaylistFrame.setVisible(false);
            }
        });
	menu.add(menuItem);
	
	menuItem = new JMenuItem("Close Collage and Restore eTrain");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
		collageVideoFrame.setVisible(false);
		collageMusicFrame.setVisible(false);
		collagePlaylistFrame.setVisible(false);
		Sweetdream.frame.setVisible(true);
            }
        });
	menu.add(menuItem);
	menuBar.add(menu);

	//for (int i = 0; i < menuBar.getMenuCount(); i++)
	//menuBar.getMenu(i).setBackground(Color.gray);
	menuBar.setForeground(Color.white);
	menuBar.setFont(new Font("Dialog", 0, 25));
	menuBar.setBackground(Color.gray);
	menuBar.setBorderPainted(false);

        menuBar.add(menu);
	
	return menuBar;
    
    }

    private static JComponent createComponent(String s) {
        JPanel l = new JPanel(new GridBagLayout());
        l.setBackground(Color.black);
        return l;
    }
    
    public static BufferedImage resizeImage(BufferedImage img, int width) {
    // USES IMPORTED LIBRARY: org.imgscalr.Scalr.*;
    // Create quickly, then smooth and brighten it.
    //   img = resize(img, Method .SPEED, width,org.imgscalr.Scalr.OP_GRAYSCALE);
 
  // Let's add a little border before we return result.
  //return pad(img, 4);
  return img;
}
   
}



	

	

