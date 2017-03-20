package sweetdream;

import edu.cmu.sphinx.util.props.PropertyException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.speech.recognition.GrammarException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import org.apache.commons.lang.StringEscapeUtils;
import org.blinkenlights.jid3.*;
import org.blinkenlights.jid3.v1.*;
import org.blinkenlights.jid3.v2.*;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;

public class eFunctions
{

    static int numMp3Files = 0;

    public eFunctions()
    { }
    
    public static void updateTimesPlayed(String artist) {
    	int numTimes = 0;
    	int o = 0;
    	ResultSet rs = null;
    	try{
    	rs = Sweetdream.db.query("SELECT TimesPlayed FROM artists WHERE ArtistName = '" + StringEscapeUtils.escapeSql(artist) + "'");           
    	
    	for(; rs.next();)
        {
        	o = Integer.parseInt(rs.getObject(1).toString());
        }
        numTimes = 1 + o;
        
        if (numTimes > 0)
                Sweetdream.db.update("UPDATE artists SET TimesPlayed = '" + numTimes + "' WHERE artistname = '" + StringEscapeUtils.escapeSql(artist) + "'");
             
        } catch(SQLException ex2) { ex2.printStackTrace(); }   
        //System.out.println("Updated to " + numTimes);
    }
    
    
    public static void playNextPlaylist()
    {
        Sweetdream.playlistIndexToPlay++;
        if(Sweetdream.playlistIndexToPlay >= Sweetdream.currentPlaylistVector.size())
		Sweetdream.playlistIndexToPlay = 0;
        
        String thisElement = Sweetdream.currentPlaylistVector.get(Sweetdream.playlistIndexToPlay).toString();
        printActivity("playlist song to play: " + thisElement);
        Sweetdream.getLyrics();
        try
        {
            String unformatted = thisElement.replace(", /","/").trim();
            Sweetdream.mainSoundPlayer.open(Sweetdream.openFile(Sweetdream.MusicDirectoryPath + File.separator + unformatted));
            Sweetdream.mainSoundPlayer.play();
        }
        catch(BasicPlayerException e1)
        {
            Sweetdream.nextTrack("ERROR: BasicPlayerException (Playing current playlist)");
        }
    }
    
    public static void playNextMood()
    {
        Sweetdream.playlistIndexToPlay++;
        if(Sweetdream.playlistIndexToPlay >= Sweetdream.currentMoodVector.size())
		Sweetdream.playlistIndexToPlay = 0;
        
        String thisElement = Sweetdream.currentMoodVector.get(Sweetdream.playlistIndexToPlay).toString();
        printActivity("Artist in the Mood to play: " + thisElement);
        
	Sweetdream.usingSearch = thisElement;
	
        try
        {
        String unformatted = thisElement.substring(1,thisElement.length()-1).replace(", /","/").trim(); 
	
            Sweetdream.mainSoundPlayer.open(Sweetdream.openFile(Sweetdream.MusicDirectoryPath + File.separator + unformatted));
            Sweetdream.mainSoundPlayer.play();
        } 
        catch(BasicPlayerException e1)
        {
            Sweetdream.nextTrack("ERROR: BasicPlayerException (Playing current mood)");
        }
    }

    public static void playNextDirectly(String theTree)   // double clicked the JTree
    {
        printActivity("playNextDirectly: " + theTree);
        JTree whichTree = null;
        String whichDirectory = "";
	Vector whichVector = new Vector();
        int whichDirIndex = -1, whichFileIndex = -1, theFileIndex = -1, q = 0;
        
        Sweetdream.currentPlayingTree = theTree;
        
	if(theTree.equals("Music")) {
		whichTree = Sweetdream.tree;
		 whichVector = Sweetdream.musicCompleteVector; }
	else if(theTree.equals("Audio")) {
		whichTree = Sweetdream.tree_audio;
	 	 whichVector = Sweetdream.audioCompleteVector; }
	else if(theTree.equals("Video")) {
		whichTree = Sweetdream.tree_video;
	 	 whichVector = Sweetdream.videoCompleteVector; }
	else if(theTree.equals("Touch")) {
		whichTree = Sweetdream.tree_touch;
	 	 whichVector = Sweetdream.videoCompleteVector; }
		 
	TreePath thisPath = whichTree.getSelectionPath();
        whichDirIndex = whichTree.getRowForPath(thisPath.getParentPath());
        String unformatted = thisPath.getParentPath().toString();
        String filenameEdited = unformatted.substring(1,unformatted.length()-1).replace("Music, ","Music").replace(", /","/").replace(", \\","\\") + File.separator + thisPath.getLastPathComponent().toString(); 
	
	// Loop through complete vector and find musicFileIndex.
	// We need this for Sequential mode, plus its nice not having to expand the JTree. 
	printActivity("thisPath.getLastPathComponent().toString(): " + thisPath.getLastPathComponent().toString());
	do {
	   q++;
	   
	
	   if (whichVector.get(q).toString().contains(thisPath.getLastPathComponent().toString()))
		{ theFileIndex = q; q = 100000; }
	} while (q < whichVector.size());
	
	printActivity("q : " + q + " -  theFileIndex: " + theFileIndex);
	
        if(theTree.equals("Music"))
        {
            Sweetdream.musicFileIndex = theFileIndex;
            whichDirectory = Sweetdream.MusicDirectoryPath;
            whichDirIndex = Sweetdream.musicDirIndex;
            whichFileIndex = Sweetdream.musicFileIndex;
        } else
        if(theTree.equals("Audio"))
        {
		expandTree("Audio");
            Sweetdream.audioFileIndex = theFileIndex; //Sweetdream.tree_audio.getMinSelectionRow() - 1;
            whichDirectory = Sweetdream.AudioDirectoryPath;
            whichDirIndex = Sweetdream.audioDirIndex;
            whichFileIndex = Sweetdream.audioFileIndex;
        } else
        if(theTree.equals("Touch"))
        {
            Sweetdream.musicFileIndex = Sweetdream.tree_touch.getMinSelectionRow() - 1;
            whichDirectory = Sweetdream.MusicDirectoryPath;
            whichDirIndex = Sweetdream.musicDirIndex;
            whichFileIndex = Sweetdream.musicFileIndex;
        }
      
        if(!filenameEdited.contains("ogg") && !filenameEdited.contains("oga") && !filenameEdited.contains("wav") && !filenameEdited.contains("flac"))
        {
            filenameEdited = filenameEdited + ".mp3";
        }
   //     whichFileIndex = whichTree.getMinSelectionRow() - 1;
        try
        {
	    Sweetdream.playTypeSelectBox.setSelectedIndex(1); // set play to sequential
	    
	    Sweetdream.mainSoundPlayer.open(Sweetdream.openFile(filenameEdited));
        Sweetdream.mainSoundPlayer.play();
       }
        catch(BasicPlayerException e1)
      {
            Sweetdream.InfiniteLoopPreventor++;
            if(Sweetdream.InfiniteLoopPreventor > 10)
            {
                printActivity("Infinite Loop Prevention!");
                reset();
                return;
            }
            Sweetdream.nextTrack("ERROR: BasicPlayerException (Play Direct)");
        }
     
    }

    public static void playNextSequential(String whichTree)
    {
        printActivity("playNextSequential: " + whichTree);
        String filePath = "", tempPath = "";
        int tempTreeIndex = -1;
        Vector tempCompleteVector2 = new Vector();
	
        if(whichTree.equals("Music"))
        {
            Sweetdream.musicFileIndex++;
            tempTreeIndex = Sweetdream.musicFileIndex;
            tempCompleteVector2 = Sweetdream.musicCompleteVector;
            tempPath = Sweetdream.MusicDirectoryPath;
        } else
        if(whichTree.equals("Audio"))
        {
            Sweetdream.audioFileIndex++;
            tempTreeIndex = Sweetdream.audioFileIndex;
            tempCompleteVector2 = Sweetdream.audioCompleteVector;
            tempPath = Sweetdream.AudioDirectoryPath;
        } else
        if(whichTree.equals("Video"))
        {
            Sweetdream.videoFileIndex++;
            tempTreeIndex = Sweetdream.videoFileIndex;
            tempCompleteVector2 = Sweetdream.videoCompleteVector;
            tempPath = Sweetdream.VideoDirectoryPath;
        }
        printActivity("tempTreeIndex: " + tempTreeIndex);
        filePath = tempCompleteVector2.get(tempTreeIndex).toString();
        if(filePath.lastIndexOf(File.separator) == -1)
        {
            int i = 0;
            do
            {
                i++;
                filePath = tempCompleteVector2.get(tempTreeIndex + i).toString();
            } while(filePath.lastIndexOf(File.separator) == -1);
            //tempTreeIndex += i;
                if(whichTree.equals("Music")) Sweetdream.musicFileIndex += i;
                if(whichTree.equals("Audio")) Sweetdream.audioFileIndex += i;
        }
        try
        {
	    if(whichTree.equals("Music") && Sweetdream.collapseAfterPlayFinish) collapseTree("Music");// seems tacky without collapsing
	    if(whichTree.equals("Audio") && Sweetdream.collapseAfterPlayFinish) collapseTree("Audio");// seems tacky without collapsing
	    
	    Sweetdream.updateTracksPlayedVector(filePath);
	    
            Sweetdream.mainSoundPlayer.open(Sweetdream.openFile(tempPath + filePath));
            Sweetdream.mainSoundPlayer.play();
        }
        catch(BasicPlayerException e1) { Sweetdream.nextTrack("ERROR: BasicPlayerException (Sequential track)"); }
    }

    static CheckNode populateTree(String theDirectoryPath, CheckNode curTop, String whichTree, String firstTimeThrough)
    {
        Vector tempCompleteVector = new Vector();
             if(whichTree.equals("Music")) tempCompleteVector = Sweetdream.musicCompleteVector;
        else if(whichTree.equals("Touch")) tempCompleteVector = Sweetdream.musicCompleteVector;
        else if(whichTree.equals("Audio")) tempCompleteVector = Sweetdream.audioCompleteVector;
        else if(whichTree.equals("Video")) tempCompleteVector = Sweetdream.videoCompleteVector;
	else if(whichTree.equals("Photo")) tempCompleteVector = Sweetdream.photoCompleteVector;
        
        File dir = new File(theDirectoryPath);
        
       
        String curPath = dir.getPath();
        String tempPath = curPath.toString();
        String tempPath2 = tempPath; //.replace("'", "&quot;");
        String tempPath3 = "";
        
	if(tempPath2.equals(Sweetdream.MusicDirectoryPath) || tempPath2.equals(Sweetdream.AudioDirectoryPath) || tempPath2.equals(Sweetdream.VideoDirectoryPath) || tempPath2.equals(Sweetdream.PhotoDirectoryPath))
		tempPath3 = tempPath2; //
        else if(tempPath2.indexOf(File.separator) != tempPath2.lastIndexOf(File.separator))  // if its a folder, no need to show the parent path
		tempPath3 = tempPath2.substring(tempPath2.lastIndexOf(File.separator));
        else
        	tempPath3 = tempPath2;

        
        CheckNode curDir = new CheckNode(tempPath3);
        curTop.add(curDir);
        curTop.setSelected(true);
        Vector TopDirectoriesVector = new Vector();
        String tmp[] = dir.list();
        if(dir.list() != null)
        {
            for(int i = 0; i < tmp.length; i++){
            	//if (!tmp[i].toString().contains("."))   // <--- TOYED with this when having "random checkbox offsets", turned out to be single MP3 in main Music folder
		   TopDirectoriesVector.addElement(tmp[i]);  //if (new File(tmp[i]).isDirectory())  //if (!tmp[i].toString().contains(".")) 
		   
		   
	    }
        }
        Collections.sort(TopDirectoriesVector, String.CASE_INSENSITIVE_ORDER);
        
        
	if(TopDirectoriesVector.indexOf("desktop.ini") != -1)
		TopDirectoriesVector.remove(TopDirectoriesVector.indexOf("desktop.ini"));
        
             if(whichTree.equals("Music") && firstTimeThrough.equals("Yes")) { Sweetdream.musicDirVector = TopDirectoriesVector; Sweetdream.numFolders = TopDirectoriesVector.size(); }
        else if(whichTree.equals("Audio") && firstTimeThrough.equals("Yes")) Sweetdream.audioDirVector = TopDirectoriesVector;
        else if(whichTree.equals("Video") && firstTimeThrough.equals("Yes")) Sweetdream.videoDirVector = TopDirectoriesVector;
	else if(whichTree.equals("Photo") && firstTimeThrough.equals("Yes")) Sweetdream.photoDirVector = TopDirectoriesVector;
        
        Vector files = new Vector();
        for(int i = 0; i < TopDirectoriesVector.size(); i++)
        {
            String thisObject = (String)TopDirectoriesVector.elementAt(i);
            String newPath = "";
            if(curPath.equals("."))
            {
                newPath = thisObject;
                continue;
            }
            newPath = curPath + File.separator + thisObject;
            File f;
            if((f = new File(newPath)).isDirectory())
            {
            	    
                if(newPath.length() > 1)
                {
                    
                    String asdf = newPath.substring(newPath.lastIndexOf(File.separator)).substring(1);
                  //  System.out.println(asdf);
                    tempCompleteVector.add(asdf);
                }
                populateTree(newPath, curDir, whichTree, "notFirstTime");
            } else
            {
            	    
                files.addElement(thisObject);
                tempCompleteVector.add(newPath.replace(Sweetdream.MusicDirectoryPath, "").replace(Sweetdream.AudioDirectoryPath, ""));
            }
        }

        for(int fnum = 0; fnum < files.size(); fnum++)
        {
            int temp = files.elementAt(fnum).toString().lastIndexOf(File.separator);
            String temp2 = files.elementAt(fnum).toString().substring(temp + 1);
            String temp3;
            if(temp2.toString().endsWith("mp3"))
            {   Sweetdream.numFiles++;
                temp3 = temp2.substring(0, temp2.length() - 4);
            } else
            {
                if(temp2.toString().endsWith("ini")) return curDir; // to avoid Desktop.ini in Windows
                temp3 = temp2;
            }
            curDir.add(new CheckNode(temp3));
        }

        for(; tempCompleteVector.indexOf("desktop.ini") != -1; tempCompleteVector.remove(tempCompleteVector.indexOf("desktop.ini"))) { }
        return curDir;
    }

    
// Copies src file to dst file.
// If the dst file does not exist, it is created
public static void copyFile(File src, File dst) throws IOException {
	
	System.out.println("copy file: source = " + src);
	System.out.println("copy file: dest = " + dst);
	
    InputStream in = new FileInputStream(src);
    OutputStream out = new FileOutputStream(dst);

    // Transfer bytes from in to out
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
    }
    in.close();
    out.close();
}

    
    
    public static void togglePlaylistView(String mode)
    {
        if(mode.equals("Normal"))
        {
            Sweetdream.playlistActionSelectBox.setModel(new DefaultComboBoxModel(new String[] {
                "-- options --", "View", "Simple Mode", "Copy to MP3 Player", "Close"
            }));
            Sweetdream.controlsPane.setVisible(true);
        } else
        if(mode.equals("Simple"))
        {
            Sweetdream.playlistActionSelectBox.setModel(new DefaultComboBoxModel(new String[] {
                "-- options --", "View", "Normal Mode", "Copy to MP3 Player", "Close"
            }));
            Sweetdream.controlsPane.setVisible(false);
        }
        Sweetdream.playlistActionSelectBox.setSelectedIndex(0);
        packFrame();
    }

    public static void refreshDatabaseTable(String whichTable) throws SQLException
    {
             if(whichTable.equals("artists")) 	displayTable(null, "tableArtists");
        else if(whichTable.equals("albums")) 	displayTable(null, "tableAlbums");
        else if(whichTable.equals("events")) 	displayTable(null, "tableEvents");
        else if(whichTable.equals("collage")) 	displayTable(null, "tableCollage");
	else if(whichTable.equals("playlists")) reloadPlaylists();
        else if(whichTable.equals("all"))
        {
			displayTable(null, "tableArtists");
			displayTable(null, "tableAlbums");
			displayTable(null, "tableEvents");
			displayTable(null, "tableCollage");
        }
    }

    public static void refreshTree(String theTree)
    {
        if(theTree == null) return;
        
        if(theTree.equals("Music") || theTree.equals("All"))
        {
            try { populateLast25Tree(); } catch(SQLException e) { }
            Sweetdream.musicCompleteVector.clear();
            Sweetdream.musicDirVector.clear();
            Sweetdream.librarytreeModel = new DefaultTreeModel(populateTree(Sweetdream.MusicDirectoryPath, Sweetdream.root_music, "Music", "Yes"));
            if (!Sweetdream.MusicDirectoryPath.equals(""))
                Sweetdream.tree.setModel(Sweetdream.librarytreeModel);
        //    Sweetdream.tree.revalidate();
        }
//	if(theTree.equals("Touch") || theTree.equals("All"))
  //      {
 //           Sweetdream.touchtreeModel = new DefaultTreeModel(populateTree(Sweetdream.MusicDirectoryPath, Sweetdream.root_touch, "Music", "Yes"));
  //          Sweetdream.tree_touch.setModel(Sweetdream.touchtreeModel);
          //  Sweetdream.tree_touch.revalidate();
  //      }
	if(theTree.equals("Moods") || theTree.equals("All"))
        {
		try { populateMoodsTree(); } catch(SQLException ex2) { ex2.printStackTrace(); }
        }
	if(theTree.equals("Audio") || theTree.equals("All"))
        {
            Sweetdream.audioCompleteVector.clear();
            Sweetdream.audioDirVector.clear();
            Sweetdream.audiotreeModel = new DefaultTreeModel(populateTree(Sweetdream.AudioDirectoryPath, Sweetdream.root_audio, "Audio", "Yes"));
            Sweetdream.tree_audio.setModel(Sweetdream.audiotreeModel);
        }
	if(theTree.equals("Video") || theTree.equals("All"))
        {
            Sweetdream.videotreeModel = new DefaultTreeModel(populateTree(Sweetdream.VideoDirectoryPath, Sweetdream.root_video, "Video", "Yes"));
            Sweetdream.tree_video.setModel(Sweetdream.videotreeModel);
        } 
	if(theTree.equals("Photo") || theTree.equals("All"))
        {
            Sweetdream.phototreeModel = new DefaultTreeModel(populateTree(Sweetdream.PhotoDirectoryPath, Sweetdream.root_photo, "Photo", "Yes"));
            Sweetdream.tree_photo.setModel(Sweetdream.phototreeModel);
        }
	if(theTree.equals("mp3player") || theTree.equals("All"))
        {
            DefaultMutableTreeNode mp3Vector = new DefaultMutableTreeNode("Contents:");
            File dir = new File(Sweetdream.MP3PlayerDirectoryPath);
            String tmp[] = dir.list();
            if(dir.list() != null)
            {
                for(int i = 0; i < tmp.length; i++)
			mp3Vector.add(new DefaultMutableTreeNode(tmp[i]));

            } else return;
            try
            {
                Sweetdream.tree_mp3player.setModel(new DefaultTreeModel(mp3Vector));
           //     Sweetdream.tree_mp3player.revalidate();
            }
            catch(NullPointerException e) { }
        }
    }
    
    
    public static void populateMoodsTree() throws SQLException {
	DefaultMutableTreeNode top = new DefaultMutableTreeNode("Moods (click to play)");
        DefaultMutableTreeNode cat1 = new DefaultMutableTreeNode();
        DefaultMutableTreeNode subcat1 = new DefaultMutableTreeNode();
	ResultSet rs = null, rs2 = null;
        String tempArtist = null,tempPath = null;
	
        for(rs = Sweetdream.db.query("SELECT MoodName FROM moods WHERE MoodName <> '' AND Path = ''"); rs.next(); top.add(cat1))
        {
            tempArtist = rs.getObject(1).toString();
            cat1 = new DefaultMutableTreeNode(tempArtist);
	    	for(rs2 = Sweetdream.db.query("SELECT Path FROM moods WHERE MoodName = '" + tempArtist + "' AND Path <> ''"); rs2.next(); cat1.add(subcat1))
		{
			tempPath = rs2.getObject(1).toString();
			subcat1 = new DefaultMutableTreeNode(tempPath);
		}
	}
	Sweetdream.categorytreeModel = new DefaultTreeModel(top);
        Sweetdream.tree_audiocategory.setModel(Sweetdream.categorytreeModel);
        Sweetdream.tree_audiocategory.revalidate();
    }
    
    public static void populateLast25Tree() throws SQLException {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Recently Played");
		DefaultMutableTreeNode cat1 = new DefaultMutableTreeNode();
		ResultSet rs = null, rs2 = null;
		String tempArtist = null,tempPath = null;
		
			for(rs = Sweetdream.db.query("SELECT Name FROM LAST25 WHERE Name <> '' ORDER BY LASTID desc"); rs.next(); top.add(cat1))
			{
				tempArtist = rs.getObject(1).toString();
				cat1 = new DefaultMutableTreeNode(tempArtist);
			}
			Sweetdream.last25treeModel = new DefaultTreeModel(top);
			Sweetdream.tree_last25.setModel(Sweetdream.last25treeModel);
			Sweetdream.tree_last25.revalidate();
    }
    
    
    public static void populatePlaylistTree(String thePlaylist) throws SQLException {
    	DefaultMutableTreeNode top = new DefaultMutableTreeNode("Playlist: " + thePlaylist);
        DefaultMutableTreeNode cat1 = new DefaultMutableTreeNode();
        ResultSet rs = null, rs2 = null;
        String tempString = null,tempPath = null;
	
        for(rs = Sweetdream.db.query("SELECT TRACKPATH FROM playlist_contents WHERE PlaylistName = '" + thePlaylist + "'"); 
        	rs.next(); 
        	top.add(cat1))
        {
            tempString = rs.getObject(1).toString();
            cat1 = new DefaultMutableTreeNode(tempString);
        }
		Sweetdream.playlisttreeModel = new DefaultTreeModel(top);
        Sweetdream.tree_playlist.setModel(Sweetdream.playlisttreeModel);
        Sweetdream.tree_playlist.revalidate();
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

    public static void Acknowledged()
    {
        try
        {
            Sweetdream.mainSoundPlayer.stop();
        }
        catch(BasicPlayerException ex) { ex.printStackTrace(); }
        try
        {
            Sweetdream.mainSoundPlayer.open(Sweetdream.ackSound);
            Sweetdream.mainSoundPlayer.play();
        }
        catch(BasicPlayerException ew) { ew.printStackTrace(System.out); }
        printActivity("Acknowledged.");
    }

    public static void savePlaylist(Vector currentPlaylistVector, String PlaylistName)
    {
        if(PlaylistName.equals(""))  // Saving a new playlist, insert into database
        {
            PlaylistName = (String)JOptionPane.showInputDialog(new JFrame(), "Enter a name for this new playlist:", "Save Playlist", -1, null, null, "");
	    if(PlaylistName == null || PlaylistName.length() < 1) return;
	    
            try
            {
                Sweetdream.db.update("INSERT INTO playlists(PlaylistName) VALUES('" + StringEscapeUtils.escapeSql(PlaylistName) + "')");
                reloadPlaylists();
            }
            catch(SQLException ex2) { ex2.printStackTrace(); }
        }
        else {		// Adding to existing playlist, so delete all the contents of the playlist and insert it fresh
            try
            {
                Sweetdream.db.update("DELETE FROM playlist_contents WHERE PlaylistName = '" + StringEscapeUtils.escapeSql(PlaylistName) + "'");
                reloadPlaylists();
            }
            catch(SQLException ex2) { ex2.printStackTrace(); }
            
        }
        String completeFileName = "";
        if(Sweetdream.currentSongPath != "" && (Sweetdream.tree.getMinSelectionRow() < 0 || Sweetdream.tree.getSelectionPath().getLastPathComponent().toString().contains(File.separator)))
        {
            completeFileName = Sweetdream.currentSongPath.replace(Sweetdream.MusicDirectoryPath, "");
        }
        else {
            CheckNode node = (CheckNode)Sweetdream.tree.getSelectionPath().getLastPathComponent();
            String artistName = node.getParent().getParent().toString();
            String songPath = Sweetdream.tree.getSelectionPath().getParentPath().getLastPathComponent().toString();
            String songName = Sweetdream.tree.getSelectionPath().getLastPathComponent().toString();
            String songToAdd = "";
            String extension = "";
            if(!songName.endsWith("ogg") && !songName.endsWith("oga") && !songName.endsWith("flac") && !songName.endsWith("wav"))
		    extension = ".mp3";
	    
            completeFileName = artistName + songPath + File.separator + songName + extension;
        }
        currentPlaylistVector.add(completeFileName.replace(Sweetdream.MusicDirectoryPath, ""));

        try
        {
            for(int i = 0; i < currentPlaylistVector.size(); i++)
		    Sweetdream.db.query("INSERT INTO playlist_contents(TrackPath,PlaylistName) VALUES('" + StringEscapeUtils.escapeSql(currentPlaylistVector.get(i).toString()) + "', '" + StringEscapeUtils.escapeSql(PlaylistName) + "')");
        }
        catch(SQLException ex2) { ex2.printStackTrace(); }
        Sweetdream.currentPlaylistPane.setBorder(BorderFactory.createTitledBorder(PlaylistName));
        Sweetdream.statusDisplay.setText("Playlist saved.");
    }
    
    public static Vector getCheckedArtists()
    {		
	    Vector tempMoodVector = new Vector();
	        collapseTree("Music");
	    	int i = 0;
	    	CheckNode node1 = (CheckNode)Sweetdream.tree.getPathForRow(i).getLastPathComponent();
		
		do {
			if (node1.isSelected()) {
				eFunctions.printActivity(" Selected: " + node1 + "tree.getRowCount(): " + (Sweetdream.tree.getRowCount()-1));
				tempMoodVector.add(node1); 
			}
			i++;
			node1 = (CheckNode)Sweetdream.tree.getPathForRow(i).getLastPathComponent();
                } while(node1 != null && i < (Sweetdream.tree.getRowCount()-1));
		
		return tempMoodVector;
    }
    
    public static void saveMood(String ArtistToAdd, String MoodName)
    {
	// CREATE NEW MOOD (if necessary)
        if(MoodName.equals(""))  
        {
	    MoodName = (String)JOptionPane.showInputDialog(new JFrame(), "Checked artists will become a Mood named: ", "Save Mood", -1, null, null, "");
	    if(MoodName == null || MoodName.length() < 1) return;
	    
            try
            {
            	System.out.println("MoodName: " + MoodName);
                Sweetdream.db.query("INSERT INTO moods(Path,MoodName) VALUES('', '" + StringEscapeUtils.escapeSql(MoodName) + "')");
                populateMoodsTree();
                Sweetdream.tree.addMouseListener(new Sweetdream.LibraryPopup());
            }
            catch(SQLException ex2) { ex2.printStackTrace(); }
        }

      Vector currentMoodVector = getCheckedArtists();
      if (currentMoodVector.size() > 1 && ArtistToAdd.equals(""))
      
          try
          {
            for(int i = 0; i < currentMoodVector.size(); i++)
            {
                Sweetdream.db.query("INSERT INTO moods(Path,MoodName) VALUES('" + StringEscapeUtils.escapeSql(currentMoodVector.elementAt(i).toString()) + "', '" + StringEscapeUtils.escapeSql(MoodName) + "')");
            }
          }
        catch(SQLException ex2) { ex2.printStackTrace(); }
	
	else {
	  try {
                Sweetdream.db.query("INSERT INTO moods(Path,MoodName) VALUES('" + StringEscapeUtils.escapeSql(ArtistToAdd) + "', '" + StringEscapeUtils.escapeSql(MoodName) + "')");
          }
        catch(SQLException ex2) { ex2.printStackTrace(); }
	}
        Sweetdream.currentPlaylistPane.setBorder(BorderFactory.createTitledBorder(MoodName));
        Sweetdream.statusDisplay.setText("Mood saved.");
	refreshTree("Moods");
    }

    public static void openBrowser(String urlOpen)
    {
        try
        {
            printActivity(System.getProperty("os.name"));
            if(System.getProperty("os.name").startsWith("Win"))
            {
                String browserName = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
                Runtime.getRuntime().exec(new String[] {
                    browserName, urlOpen
                });
            } else
            {
		// MUST use this array of strings to avoid problems with filenames containing spaces!
                Runtime.getRuntime().exec(new String[] { "/usr/bin/google-chrome-stable", urlOpen } );
            }
        }
        catch(IOException exc) { exc.printStackTrace(); }
    }

    public static void openBrowsingWindow(String urlOpen)
    {
	String temp = "C:\\eTrain\\" + urlOpen;
	printActivity("Opening folder: " + urlOpen);
        String explorerPath = "C:\\WINDOWS\\explorer.exe";
        try {
		if(System.getProperty("os.name").startsWith("Win")) {
		 Runtime.getRuntime().exec(new String[] { explorerPath, temp });
		}
            else // MUST use this array of strings to avoid problems with filenames containing spaces!
                 Runtime.getRuntime().exec(new String[] { "caja", urlOpen } );
        }
        catch(IOException exc) { exc.printStackTrace(); }
    }

    public static String convertSimple(int i) {
    	    System.out.println("Random starting place: " + i);
    	    return "" + i;
    }
    public static void launchMovie(String movieOpen, String otherText)
    {
	    printActivity("Opening movie: " + movieOpen);
	      int startPosition = 0;
	      //if (otherText == "override random starting position"){ 
	      		if (eCollage.collageRandomStart) startPosition = Sweetdream.generator.nextInt(4200);

        try
        {
            if(System.getProperty("os.name").startsWith("Win"))
            {
                String moviePlayer = "C:\\Program Files\\VLC\\vlc.exe";                        
                Runtime.getRuntime().exec(new String[] { moviePlayer, movieOpen,"--start-time", convertSimple(startPosition), "--fullscreen", "--no-video-title-show" });
            } else
            {
            	    
            	    
		// MUST use this array of strings to avoid problems with filenames containing spaces!
		//    String[] cmd = { "/usr/bin/cvlc",  movieOpen,"--rc-fake-tty","--start-time","45" };
		//   Runtime.getRuntime().exec(cmd);

    class StreamGobbler extends Thread {
		InputStream is;
		boolean discard;
		StreamGobbler(InputStream is, boolean discard) {
		  this.is = is;
		  this.discard = discard;
		}
	
		public void run() {
		  try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line=null;
			while ( (line = br.readLine()) != null)
			  if(!discard)
				System.out.println(line);    
			}
		  catch (IOException ioe) {
			ioe.printStackTrace();  
		  }
		}
	}
  
  System.out.println("Video starting position (s):"+ startPosition);
  String[] cmd = { "/usr/bin/vlc",  movieOpen,"--start-time", convertSimple(startPosition), "--fullscreen", "--no-video-title-show" };
  
   File f = new File("/usr/bin/vlc");
    if (!(f.exists()&&f.isFile())) {
      System.out.println("Incorrect path or not a file");
      return;
    }
    Runtime rt = Runtime.getRuntime();
    try {
      Process proc = rt.exec(cmd);
      StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), false);
      StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), false);
      errorGobbler.start();
      outputGobbler.start();
      System.out.println("\n"+proc.waitFor());
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
		
		
		
		
            }
	}
        catch(Exception exc) { exc.printStackTrace(); }
    }
    
    
  public static void launchMovie2(String movieOpen)
    {
	printActivity("Opening movie: " + movieOpen);
        eVideo runPlayer = new eVideo(movieOpen);
        //MyPlayer runPlayer = new MyPlayer().start(cmd);
    }
    
    

    public static void toggleShowLibrary(String whichone)
    {
	    if (whichone.equals("")){
                if(Sweetdream.boxRightside.isVisible())
                    {
                        Sweetdream.boxRightside.setVisible(false);
			Sweetdream.boxLibraryButtons.setVisible(false);
			Sweetdream.showLibraryBtn.setIcon(Sweetdream.nextButtonIcon);
                    } else
                    {
                        Sweetdream.boxRightside.setVisible(true);
			Sweetdream.boxLibraryButtons.setVisible(true);
			Sweetdream.showLibraryBtn.setIcon(Sweetdream.prevButtonIcon);
                    }
	    }
	    else if (whichone.equals("hide")) { Sweetdream.boxRightside.setVisible(false); Sweetdream.boxLibraryButtons.setVisible(false); Sweetdream.showLibraryBtn.setIcon(Sweetdream.nextButtonIcon); }
	    else if (whichone.equals("show")) { Sweetdream.boxRightside.setVisible(true); Sweetdream.boxLibraryButtons.setVisible(true); Sweetdream.showLibraryBtn.setIcon(Sweetdream.prevButtonIcon); }
                    eFunctions.packFrame();
    }

    public static void deleteFile(String fileToDelete, String whichtree)
    {
        printActivity("fileToDelete: " + fileToDelete);
        File file = new File(fileToDelete);
        boolean isdeleted = file.delete();
        Sweetdream.statusDisplay.setText("File deleted.");
        refreshTree(whichtree);
        if((Sweetdream.songTimeDisplay.getText().startsWith("0:0") || Sweetdream.songTimeDisplay.getText().equals("")) && Sweetdream.computerVoice)
        {
            try
            {
                Sweetdream.mainSoundPlayer.open(Sweetdream.deletedSound);
                Sweetdream.mainSoundPlayer.play();
            }
            catch(BasicPlayerException e1) { }
        }
    }

    public static void deleteSomeMuvoFiles(JTree thetree)
    {
        TreePath selected[] = Sweetdream.tree_mp3player.getSelectionPaths();
        for(int i = 0; i < selected.length; i++)
        {
            TreePath sel = selected[i];
            CheckNode node = (CheckNode)sel.getLastPathComponent();
            String fileToDelete = sel.toString().substring(16).replace("]", ".mp3");
            printActivity(fileToDelete);
            File file = new File(Sweetdream.MP3PlayerDirectoryPath + "\\" + fileToDelete);
            boolean isdeleted = file.delete();
        }

        Sweetdream.tree_mp3player.expandRow(1);
        try
        {
            Sweetdream.mainSoundPlayer.open(Sweetdream.deletedSound);
            Sweetdream.mainSoundPlayer.play();
        }
        catch(BasicPlayerException e1) { }
    }

    public static void shutdownComputer(String GOorSTOP)
    {
        if(Sweetdream.computerVoice)
        {
            try
            {
                if(GOorSTOP.equals("go")) Sweetdream.mainSoundPlayer.open(Sweetdream.shutdownSound);
                else 			  Sweetdream.mainSoundPlayer.open(Sweetdream.affirmSound);
                
                Sweetdream.mainSoundPlayer.play();
            }
            catch(BasicPlayerException eq) { eq.printStackTrace(); }
        }
        if(System.getProperty("os.name").startsWith("Win"))
        {
            String exeFile = "C:\\WINDOWS\\system32\\shutdown.exe";
            String whatToDo;
            if(GOorSTOP.equals("go")) 
		 whatToDo = "-s";
            else whatToDo = "-a";
            
            try
            {
                Runtime.getRuntime().exec(new String[] { exeFile, whatToDo });
            }
            catch(IOException exc) { exc.printStackTrace(); }
        } else
        {
            String whatToDo;
            if(GOorSTOP.equals("go"))
		    whatToDo = "-P";
            else
		    whatToDo = "-c";
            
            try
            {
                Runtime.getRuntime().exec("/sbin/shutdown " + whatToDo + " 1");
            }
            catch(IOException exc) { exc.printStackTrace(); }
        }
        Object options[] = { "Proceed", "Cancel Shutdown" };
        int n = JOptionPane.showOptionDialog(new JFrame(), "Shutting down in 2 minutes.", "Shutting down...", 0, 3, null, options, options[1]);
        if(n == 1)
        {
            try
            {
                Runtime.getRuntime().exec("sudo /sbin/shutdown -c");
            }
            catch(IOException exc) { exc.printStackTrace(); }
            if(Sweetdream.computerVoice)
            {
                try
                {
                    Sweetdream.mainSoundPlayer.open(Sweetdream.affirmSound);
                    Sweetdream.mainSoundPlayer.play();
                }
                catch(BasicPlayerException eq) { eq.printStackTrace(); }
            }
        }
    }

    public static void expandTree(String whichTree)
    {
        JTree theTree = null;
        int row = 0;
             if(whichTree.equals("Music")) theTree = Sweetdream.tree;
        else if(whichTree.equals("Moods")) theTree = Sweetdream.tree_audiocategory;
        else if(whichTree.equals("Audio")) theTree = Sweetdream.tree_audio;
        else if(whichTree.equals("Video")) theTree = Sweetdream.tree_video;
        else return;
        
        for(; row < theTree.getRowCount(); row++)
		theTree.expandRow(row);
        
   //     theTree.scrollRowToVisible(theTree.getRowForPath(theTree.getSelectionPath()));
        Sweetdream.expandCollapseBtn.setIcon(Sweetdream.minusIcon);
        Sweetdream.expandCollapseBtn.setToolTipText("Collapse library");
        Sweetdream.expandCollapseBtn.setMnemonic(67);
        packFrame();
    }

    public static void collapseTree(String whichTree)
    {
        JTree theTree = null;
             if(whichTree.equals("Music")) theTree = Sweetdream.tree;
        else if(whichTree.equals("Moods")) theTree = Sweetdream.tree_audiocategory;
        else if(whichTree.equals("Audio")) theTree = Sweetdream.tree_audio;
        else if(whichTree.equals("Video")) theTree = Sweetdream.tree_video;
        else return;
	
        int row = theTree.getRowCount() - 1;
        try
        {
            while(row >= 1) 
            {
                theTree.collapseRow(row);
                row--;
            }
//            theTree.scrollRowToVisible(theTree.getRowForPath(theTree.getSelectionPath()));
            Sweetdream.expandCollapseBtn.setIcon(Sweetdream.plusIcon);
            Sweetdream.expandCollapseBtn.setToolTipText("Expand library");
            Sweetdream.expandCollapseBtn.setMnemonic(69);
            packFrame();
        }
        catch(ArrayIndexOutOfBoundsException ii) { ii.printStackTrace(); }
    }

    public static void semiCollapseAll(String whichTree)
    {
        JTree theTree = null;
             if(whichTree.equals("Music")) theTree = Sweetdream.tree;
        else if(whichTree.equals("Moods")) theTree = Sweetdream.tree_audiocategory;
        else if(whichTree.equals("Audio")) theTree = Sweetdream.tree_audio;
        else if(whichTree.equals("Video")) theTree = Sweetdream.tree_video;
        else return;
	
        collapseTree(whichTree);
        int row = theTree.getRowCount();
        printActivity("row: " + row);
        for(; row >= 1; row--)
		theTree.expandRow(row);
    }

    public static void reset()
    {
        try
        {
            Sweetdream.mainSoundPlayer.stop();
        }
        catch(BasicPlayerException ex) { ex.printStackTrace(); }
        
        Sweetdream.currentSongPath = "";
        Sweetdream.InfiniteLoopPreventor = 0;
        Sweetdream.playBtn.setIcon(Sweetdream.playButtonIcon);
        Sweetdream.secondsAmount = 0L;
        Sweetdream.progressBar.setValue(0);
        Sweetdream.songTimeDisplay.setText("0:00");
        Sweetdream.statusDisplay.setText("Ready");
    }

    public static void saveTxtFile(File aFile, String aContents)
        throws FileNotFoundException, IOException
    {
        Writer output;
        if (aFile == null) throw new IllegalArgumentException("File should not be null.");
        
        output = null;
        output = new BufferedWriter(new FileWriter(aFile));
        output.write(aContents);
        if(output != null) output.close();
    }

    
    public static void updateStartupLog(String string) throws IOException
    {
    	FileWriter out = new FileWriter("startuplog.txt", true); // true is to APPEND
    	out.write("\r\n" + string);                                                
    	out.close();
    	eFunctions.printActivity(string);
    }
    
    
    public static String openTxtFile(File aFile)
    {
        StringBuffer buf = null;
        if(aFile.exists())
        {
            String record = null;
            buf = new StringBuffer();
            try
            {
                FileReader fr = new FileReader(aFile);
                BufferedReader br = new BufferedReader(fr);
                record = new String();
                int preventEndless = 0;
                do
                {
                    if((record = br.readLine()) == null) break;

                    printActivity("readLine" + preventEndless);
                    preventEndless++;
                    buf.append(record + "\n");
                } while(preventEndless <= 500);
            }
            catch(IOException eq)
            {
                printActivity("Uh oh, got an IOException error!");
                eq.printStackTrace();
            }
        }
        return buf.toString();
    }

    public static void updateOptionsTextBoxes()
    {
        Sweetdream.MusicDirectoryTextBox.setText(Sweetdream.MusicDirectoryPath);
        Sweetdream.AudioDirectoryTextBox.setText(Sweetdream.AudioDirectoryPath);
        Sweetdream.VideoDirectoryTextBox.setText(Sweetdream.VideoDirectoryPath);
        Sweetdream.PhotoDirectoryTextBox.setText(Sweetdream.PhotoDirectoryPath);
        Sweetdream.MP3PlayerDirectoryTextBox.setText(Sweetdream.MP3PlayerDirectoryPath);
        Sweetdream.artistImageTypeSelectBox.setSelectedItem(Sweetdream.ArtistImageType);
        Sweetdream.libraryFontSelectBox.setSelectedItem(Sweetdream.libraryFont);
        Sweetdream.libraryFontSizeSelectBox.setSelectedItem(Integer.toString(Sweetdream.libraryFontSize));
        Sweetdream.libraryVisibleRowsSelectBox.setSelectedItem(Integer.toString(Sweetdream.libraryVisibleRows));
        Sweetdream.showLibraryOnStartupSelectBox.setSelectedItem(Sweetdream.showLibraryOnStartup);
        if(!Sweetdream.computerVoice) Sweetdream.computerVoiceSelectBox.setSelectedIndex(1);
    }

    public static void setPaused(boolean which)
    {
        if(which)
        {
            try {
                Sweetdream.isPaused = true;
                Sweetdream.mainSoundPlayer.pause();
            }
            catch(BasicPlayerException ex) { ex.printStackTrace(); }
        }
	else {
            try {
                Sweetdream.isPaused = false;
                Sweetdream.mainSoundPlayer.resume();
            }
            catch(BasicPlayerException ex) { ex.printStackTrace(); }
        }
    }

    
    public static String getID3Info(String filepath, String returnWhich) throws ID3Exception
    {
	File oSourceFile = new File(filepath);
	String result = "";
        // create an MP3File object representing our chosen file
        MediaFile oMediaFile = new MP3File(oSourceFile);

        // any tags read from the file are returned, in an array, in an order which you should not assume
        ID3Tag[] aoID3Tag = oMediaFile.getTags();
        // let's loop through and see what we've got
        // (NOTE:  we could also use getID3V1Tag() or getID3V2Tag() methods, if we specifically want one or the other)
        for (int i=0; i < aoID3Tag.length; i++)
        {
            // check to see if we read a v1.0 tag, or a v2.3.0 tag (just for example..)
            if (aoID3Tag[i] instanceof ID3V1_0Tag)
            {
                ID3V1_0Tag oID3V1_0Tag = (ID3V1_0Tag)aoID3Tag[i];
                // does this tag happen to contain a title?
                if (oID3V1_0Tag.getTitle() != null && returnWhich.equals("Title"))
			result = oID3V1_0Tag.getTitle();
		if (oID3V1_0Tag.getAlbum() != null && returnWhich.equals("Album"))
			result = oID3V1_0Tag.getAlbum();
		if (oID3V1_0Tag.getArtist() != null && returnWhich.equals("Artist"))
			result = oID3V1_0Tag.getArtist();

            }
            else if (aoID3Tag[i] instanceof ID3V2_3_0Tag)
            {
                ID3V2_3_0Tag oID3V2_3_0Tag = (ID3V2_3_0Tag)aoID3Tag[i];
                // check if this v2.3.0 frame contains a title, using the actual frame name
                if (oID3V2_3_0Tag.getTIT2TextInformationFrame() != null && returnWhich.equals("Title"))
                   result = oID3V2_3_0Tag.getTIT2TextInformationFrame().getTitle();
	   	if (oID3V2_3_0Tag.getTIT2TextInformationFrame() != null && returnWhich.equals("Album"))
                   result = oID3V2_3_0Tag.getAlbum();
	   	if (oID3V2_3_0Tag.getTIT2TextInformationFrame() != null && returnWhich.equals("Artist"))
                   result = oID3V2_3_0Tag.getArtist();
                
                // but check using the convenience method if it has a year set (either way works)
                try
                {
                    System.out.println("Year = " + oID3V2_3_0Tag.getYear());  // reads TYER frame
                }
                catch (ID3Exception e) { System.out.println("Could get read year from tag: " + e.toString()); }
                // etc.
            }
        }
        System.out.println("get ID3 result: " + result);
	return result;
    }
    
    public static void editID3(String theFile, String theArtist, String theTitle, String theAlbum) throws ID3Exception
    {
        // the file we are going to modify
        String fileToEdit = "/home/ezra/Music/Soul Coughing/Irresistible Bliss/05 Lazybones.mp3"; // source;
        // the file we are going to modify
        File oSourceFile = new File(theFile);
        // create an MP3File object representing our chosen file
        MediaFile oMediaFile = new MP3File(oSourceFile);

        // create a v1.0 tag object, and set some values
        ID3V1_0Tag oID3V1_0Tag = new ID3V1_0Tag();
        oID3V1_0Tag.setAlbum(theAlbum);
        oID3V1_0Tag.setArtist(theArtist);
       // oID3V1_0Tag.setComment("Comment");
       // oID3V1_0Tag.setGenre(ID3V1Tag.Genre.Blues);
        oID3V1_0Tag.setTitle(theTitle);
       // oID3V1_0Tag.setYear("1999");
       
        // set this v1.0 tag in the media file object
        oMediaFile.setID3Tag(oID3V1_0Tag);
       
        // create a v2.3.0 tag object, and set some frames
        ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
        TPE1TextInformationID3V2Frame oTPE1 = new TPE1TextInformationID3V2Frame(theArtist);
        oID3V2_3_0Tag.setTPE1TextInformationFrame(oTPE1);
        TRCKTextInformationID3V2Frame oTRCK = new TRCKTextInformationID3V2Frame(3, 9);
        oID3V2_3_0Tag.setTRCKTextInformationFrame(oTRCK);
        TIT2TextInformationID3V2Frame oTIT2 = new TIT2TextInformationID3V2Frame(theTitle);
        oID3V2_3_0Tag.setTIT2TextInformationFrame(oTIT2);
        oID3V2_3_0Tag.setAlbum(theAlbum);
       
        // set this v2.3.0 tag in the media file object
        oMediaFile.setID3Tag(oID3V2_3_0Tag);
       
        // update the actual file to reflect the current state of our object 
        oMediaFile.sync();
    }

    public static void updateThemeColor(int red, int green, int blue)
    {
    	Color theColor = new Color(red,green,blue);
	    Sweetdream.currentColorLabel.setBackground(theColor);
	    Sweetdream.panelOfEverything.setBorder(new LineBorder(theColor, 5));
	    //Sweetdream.tree.setBackground(theColor);
    }
    public static void playingStartsStuffToDo()
    {
        Sweetdream.nextMinutePushes = 0;
        
        //Update LAST 25
        try {
            Sweetdream.db.update("INSERT INTO LAST25(Name,Path,Artist) VALUES('" + StringEscapeUtils.escapeSql(Sweetdream.currentPlayingSong) + "','" + "adsf" + "','" + StringEscapeUtils.escapeSql(Sweetdream.currentPlayingArtist) + "')");
            Sweetdream.db.update("DELETE FROM LAST25 WHERE LASTID in (select min(LastID) as pos from LAST25)");
            populateLast25Tree();
            }
        catch(SQLException ex2) { ex2.printStackTrace(); }  
       
        Sweetdream.statusDisplay.setText("<html><center><b>" + Sweetdream.currentPlayingSong + "</b><br>" + Sweetdream.currentPlayingArtist);
        updateLog("playing " + Sweetdream.currentPlayingSong + " by " + Sweetdream.currentPlayingArtist);
        SysTray.changeToolTip(Sweetdream.currentPlayingSong + " by " + Sweetdream.currentPlayingArtist);
        Sweetdream.progressBar.setValue(0);
        
        
        if(Sweetdream.isMute)
        {
            try { Sweetdream.mainSoundPlayer.setGain(0.0D); } catch(BasicPlayerException ex) { ex.printStackTrace(); }
        }
        else   //re-adjust volume to globally set volume variable (BasicPlayer resets volume each time called)
        	adjustVolume(Sweetdream.currentVolumeLevel); 
        
        Sweetdream.playBtn.setIcon(Sweetdream.pauseButtonIcon);
        Sweetdream.usingSearch2 = "";
        Sweetdream.playDirectly = false;
        Sweetdream.timeTravelSelectBox.setSelectedIndex(0);
        Sweetdream.imageFrame.pack();
        packFrame();
    }

    
    public static void updateTreeFont()
    {
    	    Sweetdream.tree.setFont(new Font(Sweetdream.libraryFont, 0, Sweetdream.libraryFontSize));
    //	    Sweetdream.tree.setRowHeight(Sweetdream.libraryFontSize + 4);
    	    Sweetdream.tree_audio.setFont(new Font(Sweetdream.libraryFont, 0, Sweetdream.libraryFontSize));
    	    Sweetdream.tree_audio.setRowHeight(Sweetdream.libraryFontSize + 4);
    	    Sweetdream.tree_audiocategory.setFont(new Font(Sweetdream.libraryFont, 0, 18));
    	    Sweetdream.tree_audiocategory.setRowHeight(Sweetdream.libraryFontSize + 4);
    	    Sweetdream.tree_video.setFont(new Font(Sweetdream.libraryFont, 0, Sweetdream.libraryFontSize));
    	    Sweetdream.tree_video.setRowHeight(Sweetdream.libraryFontSize + 4);
    	    Sweetdream.tree_photo.setFont(new Font(Sweetdream.libraryFont, 0, Sweetdream.libraryFontSize));
    	    Sweetdream.tree_photo.setRowHeight(Sweetdream.libraryFontSize + 4);
    }
    
    public static void startupStuffToDo(String OnOff, String collageOnOff, String ShowHide) //String homeDirectory
        throws SQLException
    {

    	getTotalTimesPlayed();  //populate global variable
        retrieveVariables(); // Pull saved variables from database

        Sweetdream.tabbedPaneLibrary.setSelectedIndex(Sweetdream.lastLibraryTab); // Show Playlist tab

	
        /* apply settings from database variables */
        //LIBRARY FONT and SIZE
        eFunctions.updateTreeFont();
	
        //LIBRARY VISIBLE ROWS
        Sweetdream.scrollViewMusicLibrary.setPreferredSize(new Dimension(320, Sweetdream.libraryVisibleRows * 20));
        //SHOW LIBRARY ON STARTUP
	
		//COLOR
		//updateThemeColor(Sweetdream.themeColor);
	
        reloadPlaylists();

        Sweetdream.actionsListVector.add("-- actions --");
        Sweetdream.actionsListVector.add("Mute");
        Sweetdream.actionsListVector.add("Floating Image Mode");
        Sweetdream.actionsListVector.add("Close Mood");
        Sweetdream.actionsListVector.add("Transfer to MP3 Player");
        Sweetdream.actionsListVector.add("Clear MP3 Player");
        Sweetdream.actionsListVector.add("Start Timer");
        Sweetdream.actionsListVector.add("Show/Hide Library");
        Sweetdream.actionsListVector.add("Show/Hide Song Display");
        Sweetdream.actionsListVector.add("Open Channel");
        Sweetdream.actionsListVector.add("Close Channel");
        Sweetdream.actionsListVector.add("Semi-Collapse Library");
        Sweetdream.actionsListVector.add("Override Shutdown");
        Sweetdream.ActionSelectBox.setModel(new DefaultComboBoxModel(Sweetdream.actionsListVector));
	
        if(collageOnOff.equals("on"))
        {
        	eCollage.refreshCollage("Video"); eCollage.refreshCollage("Music");  // these take 1.5 to 2 seconds
        } else Sweetdream.collageDisplayBtn.setVisible(false);
        Sweetdream.themeTextBox.requestFocus();
        refreshTree("All");    //takes about 10 seconds!!!
        displayArtistImage("eTrain");
        updateThemeColor(Sweetdream.themeColorRed,Sweetdream.themeColorGreen,Sweetdream.themeColorBlue);
        
        if(OnOff.equals("off"))
        {
            Sweetdream.statusDisplay.setText("Ready");
            Sweetdream.viewCommandsTxtBtn.setVisible(false);
            //if(ShowHide.equals("show")) Sweetdream.frame.setVisible(true);
        }	
        
		// Pick random place for music library scrollbar     /// ayyye doesn't work with Nimbus?  
		Sweetdream.scrollViewMusicLibrary.getVerticalScrollBar().setValue(Sweetdream.generator.nextInt(Sweetdream.scrollViewMusicLibrary.getVerticalScrollBar().getMaximum()));
		checkForVariables(); // See if Music or Video folder path has been set, if not, new installation?
		packFrame();
		Sweetdream.frame.setVisible(true);
		// if(ShowHide.equals("hide")) Sweetdream.frame.setVisible(false);
		// Sweetdream.tabbedPaneLibrary.setSelectedIndex(1);  // SAVE IN DATABASE TODO

    }                                                 
    

    
    public static void startMicrophone()
    {
        try
        {
            eVoice voiceCommandClass = new eVoice();
            voiceCommandClass.execute();
        }
        catch(IOException ioe) { printActivity("I/O Error " + ioe); }
        catch(PropertyException e) { printActivity("Problem configuring recognizer" + e); }
        catch(InstantiationException e) { printActivity("Problem creating components " + e); }
        catch(GrammarException e) { printActivity("Problem with Grammar " + e); }
        catch(SQLException e) { printActivity("Problem with SQL " + e); }
    }

    public static void updateLog(String logText)
    {
        Calendar cal = new GregorianCalendar();
        int hour12 = cal.get(10);
        int min = cal.get(12);
        int sec = cal.get(13);
        int ampm = cal.get(9);
        String theTime;
	String minutes = Sweetdream.formatter.format(min); // 2 decimal places
	String seconds = Sweetdream.formatter.format(sec); // 2 decimal places
	
        if(ampm == 1)
            theTime = hour12 + ":" + minutes + "." + seconds + " pm";
        else
            theTime = hour12 + ":" + minutes + "." + seconds + " am";
    
        if(logText.contains("program_ready"))
            return;
        else
        {
            Sweetdream.logVector.add("\n" + theTime + " - " + logText);  // 0, to add to beginning
            Sweetdream.viewLogHistory.setText(Sweetdream.logVector.toString().replace("[", "").replace("]", ""));
	    Sweetdream.viewLogHistory.setCaretPosition(Sweetdream.viewLogHistory.getDocument().getLength());
            return;
        }
    }
    
  public static void printActivity(String theText)
    {
	    Sweetdream.activityVector.add("\n" + theText);  // 0, to add to beginning
	    System.out.println(theText);
    }
    
    
  public static void downloadFile(String FTPfolderpath, String FTPfilename, String saveAs)
    {
        eFTP example = new eFTP(FTPfolderpath, FTPfilename, saveAs);
        example.connect();
        example.listDirectory();
        Sweetdream.statusDisplay.setText("downloading update...");
        example.getFile();
        example.close();
        JOptionPane.showMessageDialog(Sweetdream.frame, "Download complete. Please restart eTrain.");
        openBrowsingWindow("bin");
    }

  public static void checkForVariables()
    {
        int n = 2;
        String musicDir = Sweetdream.MusicDirectoryPath;
        File videoDir = new File(Sweetdream.VideoDirectoryPath);

        if(musicDir.equals("") || musicDir == null)
		n = JOptionPane.showConfirmDialog(new JFrame(), "The paths to your music and video folders have not been set. Update them now?", "Music and Video Directory", 0);
        
        if(!videoDir.exists())
		Sweetdream.tabbedPaneLibrary.removeTabAt(1);
        
        if(n == 0)
		Sweetdream.optionsFrame.setVisible(true);
    }

    public static void closePlaylist()
    {
        Sweetdream.controlsPane.setBorder(BorderFactory.createTitledBorder(""));
        if(Sweetdream.ActionSelectBox.getSelectedItem().toString().equals("-- playlist options --"))
        {
            Sweetdream.actionsListVector.remove(0);
            Sweetdream.actionsListVector.remove(0);
            Sweetdream.actionsListVector.remove(0);
            Sweetdream.actionsListVector.remove(0);
            Sweetdream.actionsListVector.remove(0);
        }
        Sweetdream.ActionSelectBox.setModel(new DefaultComboBoxModel(Sweetdream.actionsListVector));
        Sweetdream.currentLoadedPlaylist = "";
        Sweetdream.currentPlaylistVector.clear();
        Sweetdream.tabbedFrontPanel.setVisible(false);
        Sweetdream.tabbedFrontPanel.setPreferredSize(new Dimension(Sweetdream.boxRightside.getWidth(), 85));
        Sweetdream.playCurrentPlaylist = false;
        Sweetdream.playlistIndexToPlay = -1;
        Sweetdream.currentPlaylistPane.setVisible(false);
        Sweetdream.currentPlaylistTextArea.setText("");
        Sweetdream.playlistSelectBox.setSelectedIndex(0);
        Sweetdream.currentPlaylistPane.setBorder(BorderFactory.createTitledBorder("Current Playlist"));
        
        if (Sweetdream.tabbedPaneLibrary.getSelectedIndex() == 2) Sweetdream.tabbedPaneLibrary.setSelectedIndex(0);
        
        packFrame();
    }

    public static JTree getSelectedTree() {
            String whichTree = Sweetdream.tabbedPaneLibrary.getTitleAt(Sweetdream.tabbedPaneLibrary.getSelectedIndex());
            JTree theTree = new JTree();
            System.out.println("whichTree: " + whichTree);
            
        if(whichTree.contains("Music"))
			theTree = Sweetdream.tree;
	    else if(whichTree.contains("Audio"))
			theTree = Sweetdream.tree_audio;
            else if(whichTree.contains("Video"))
                	theTree = Sweetdream.tree_video;
	    else if(whichTree.contains("Photo"))
                	theTree = Sweetdream.tree_photo;
        return theTree;
    }
    public static String browseDirectory()
    {
    	Sweetdream.getImageFileChooser.setCurrentDirectory(new File(Sweetdream.VideoDirectoryPath));
        Sweetdream.getImageFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int retval = Sweetdream.getImageFileChooser.showOpenDialog(Sweetdream.frame);
        String theFolder = "";
	
        if(retval == 0) theFolder = Sweetdream.getImageFileChooser.getSelectedFile().toString();
        
        Sweetdream.optionsFrame.toFront();
        return theFolder;
    }

    public static void addRowToDatabase(int rowIndex, String table)
    {
        Vector temp = new Vector();
        if(table.equals("artists"))
        {
            int answer = JOptionPane.showConfirmDialog(new JFrame(), "Save new artist to database?");
            if(answer == 0)
            {
                for(int i = 0; i < 4; i++)
			temp.addElement(Sweetdream.artistTableModel.getValueAt(rowIndex, i));

                try
                {
                    Sweetdream.db.update("INSERT INTO artists(ArtistName,VoiceCommand) VALUES('" + StringEscapeUtils.escapeSql(temp.get(1).toString()) + "','" + temp.get(2) + "')");
		    refreshDatabaseTable("artists");
                }
                catch(SQLException ex2) { ex2.printStackTrace(); }
            }
        } else
        if(table.equals("albums"))
        {
            int answer = JOptionPane.showConfirmDialog(new JFrame(), "Save new album to database?");
            if(answer == 0)
            {
                for(int i = 0; i < 8; i++)
			temp.addElement(Sweetdream.albumTableModel.getValueAt(rowIndex, i));

                try
                {
                    Sweetdream.db.update("INSERT INTO albums(ArtistName,AlbumName,VoiceCommand,CoverImage,ReleaseYear,FrequencyRating) VALUES('"
			    + StringEscapeUtils.escapeSql(temp.get(1).toString()) + "','" + temp.get(2) + "','" + temp.get(3) + "','" + temp.get(4) + "','" + temp.get(5) + "','" + temp.get(6) + "')");
		    refreshDatabaseTable("albums");
                }
                catch(SQLException ex2) { ex2.printStackTrace(); }
            }
        }
	else if(table.equals("events"))
        {
            int answer = JOptionPane.showConfirmDialog(new JFrame(), "Save new event to database?");
            if(answer == 0)
            {
                for(int i = 0; i < 4; i++)
			temp.addElement(Sweetdream.eventTableModel.getValueAt(rowIndex, i));
                try
                {
                    Sweetdream.db.update("INSERT INTO events(Time,Type,Value) VALUES('" + temp.get(1) + "','" + temp.get(2) + "','" + temp.get(3) + "')");
		    refreshDatabaseTable("events");
                }
                catch(SQLException ex2) { ex2.printStackTrace(); }
            }
        }
	else if(table.equals("collage"))
        {
            int answer = JOptionPane.showConfirmDialog(new JFrame(), "Save new collage item to database?");
            if(answer == 0)
            {
                for(int i = 0; i < 5; i++)
			temp.addElement(Sweetdream.collageTableModel.getValueAt(rowIndex, i));
                try
                {
                    Sweetdream.db.update("INSERT INTO collage(Type,Name,FilePath,Category) VALUES('" + temp.get(1) + "','" + StringEscapeUtils.escapeSql(temp.get(2).toString()) + "','" + StringEscapeUtils.escapeSql(temp.get(3).toString()) + "','" + temp.get(4) + "')");
		    refreshDatabaseTable("collage");
                }
                catch(SQLException ex2) { ex2.printStackTrace(); }
            }
        }
    }

    public static void optionsSwitcher(String optionName)
    {
        Sweetdream.boxOptionsA.setVisible(false);
        Sweetdream.boxOptionsB.setVisible(false);
        Sweetdream.boxOptionsC.setVisible(false);
        Sweetdream.boxOptionsD.setVisible(false);
        Sweetdream.boxOptionsE.setVisible(false);
		Sweetdream.boxOptionsF.setVisible(false);
		Sweetdream.boxOptionsG.setVisible(false);
		Sweetdream.boxOptions_Updates.setVisible(false);
	
        if(optionName.equals("Locations"))
           Sweetdream.boxOptionsA.setVisible(true);
        else if(optionName.equals("Display"))
           Sweetdream.boxOptionsB.setVisible(true);
   	else if(optionName.equals("MP3 Player")) {
            Sweetdream.boxOptionsC.setVisible(true);
	    
	    if ((new File(Sweetdream.MP3PlayerDirectoryPath + File.separator)).exists()) 
	         { Sweetdream.noMP3PlayerDisplay.setVisible(false); Sweetdream.mp3playerAddTracksBtn.setVisible(true); Sweetdream.mp3playerDeleteBtn.setVisible(true); Sweetdream.scrollViewMP3Player.setVisible(true); refreshTree("mp3player"); }
	    else { Sweetdream.noMP3PlayerDisplay.setVisible(true); Sweetdream.mp3playerAddTracksBtn.setVisible(false); Sweetdream.mp3playerDeleteBtn.setVisible(false); Sweetdream.scrollViewMP3Player.setVisible(false); }
	}
        else if(optionName.equals("Database"))
        {
            Sweetdream.boxOptionsD.setVisible(true);
            try {
                refreshDatabaseTable("all");
            }
            catch(SQLException ex2) { ex2.printStackTrace(); }
        }
	else if(optionName.equals("Lyrics"))
           Sweetdream.boxOptionsE.setVisible(true);
        else if(optionName.equals("Updates"))
	   Sweetdream.boxOptions_Updates.setVisible(true);
	else if(optionName.equals("Shortcuts"))
           Sweetdream.boxOptionsF.setVisible(true);
   	else if(optionName.equals("Activity")) {
	   Sweetdream.viewActivityTextArea.setText(Sweetdream.activityVector.toString().replace("[", "").replace("]", ""));
	   Sweetdream.viewActivityTextArea.setCaretPosition(Sweetdream.viewActivityTextArea.getDocument().getLength());
   	   Sweetdream.boxOptionsG.setVisible(true); }
        else if(optionName.equals("Save / Exit"))
        {
            try {
                saveOptions(); eFunctions.refreshTree("All");
            }
            catch(SQLException ex2) { ex2.printStackTrace(); }
        }
    }

    public static void saveOptions() throws SQLException
    {
        Sweetdream.optionsFrame.setVisible(false);
        if(System.getProperty("os.name").startsWith("Win"))
        {
            try
            {
                Sweetdream.db.update("UPDATE variables SET Value = '" + StringEscapeUtils.escapeSql(Sweetdream.MusicDirectoryTextBox.getText()) + "' WHERE variablename = 'MusicDirectoryPath'");
				Sweetdream.db.update("UPDATE variables SET Value = '" + StringEscapeUtils.escapeSql(Sweetdream.AudioDirectoryTextBox.getText()) + "' WHERE variablename = 'AudioDirectoryPath'");
				Sweetdream.db.update("UPDATE variables SET Value = '" + StringEscapeUtils.escapeSql(Sweetdream.VideoDirectoryTextBox.getText()) + "' WHERE variablename = 'VideoDirectoryPath'");
				Sweetdream.db.update("UPDATE variables SET Value = '" + StringEscapeUtils.escapeSql(Sweetdream.PhotoDirectoryTextBox.getText()) + "' WHERE variablename = 'PhotoDirectoryPath'");
				Sweetdream.db.update("UPDATE variables SET Value = '" + StringEscapeUtils.escapeSql(Sweetdream.MP3PlayerDirectoryTextBox.getText()) + "' WHERE variablename = 'MP3PlayerDirectoryPath'");
			}
            catch(SQLException ex2) { ex2.printStackTrace(); }
        } 
        else {
            try
            {
                Sweetdream.db.update("UPDATE variables SET Value = '" + StringEscapeUtils.escapeSql(Sweetdream.MusicDirectoryTextBox.getText()) + "' WHERE variablename = 'MusicLinuxPath'");
				Sweetdream.db.update("UPDATE variables SET Value = '" + StringEscapeUtils.escapeSql(Sweetdream.AudioDirectoryTextBox.getText()) + "' WHERE variablename = 'AudioLinuxPath'");
				Sweetdream.db.update("UPDATE variables SET Value = '" + StringEscapeUtils.escapeSql(Sweetdream.VideoDirectoryTextBox.getText()) + "' WHERE variablename = 'VideoLinuxPath'");
				Sweetdream.db.update("UPDATE variables SET Value = '" + StringEscapeUtils.escapeSql(Sweetdream.PhotoDirectoryTextBox.getText()) + "' WHERE variablename = 'PhotoLinuxPath'");
				Sweetdream.db.update("UPDATE variables SET Value = '" + StringEscapeUtils.escapeSql(Sweetdream.MP3PlayerDirectoryTextBox.getText()) + "' WHERE variablename = 'MP3LinuxPath'");
			}
            catch(SQLException ex2) { ex2.printStackTrace(); }
        }
        try { 
		  Sweetdream.db.update("UPDATE variables SET Value = '" + eCollage.collageVideoCols + "' WHERE variablename = 'collageVideoCols'");
		  Sweetdream.db.update("UPDATE variables SET Value = '" + eCollage.collageVideoRows + "' WHERE variablename = 'collageVideoRows'");
		  Sweetdream.db.update("UPDATE variables SET Value = '" + eCollage.collageMusicCols + "' WHERE variablename = 'collageMusicCols'");
		  Sweetdream.db.update("UPDATE variables SET Value = '" + eCollage.collageMusicRows + "' WHERE variablename = 'collageMusicRows'");
		  Sweetdream.db.update("UPDATE variables SET Value = '" + Sweetdream.libraryFontSelectBox.getSelectedItem().toString() + "' WHERE variablename = 'libraryFont'");
		  Sweetdream.db.update("UPDATE variables SET Value = '" + Sweetdream.libraryFontSizeSelectBox.getSelectedItem().toString() + "' WHERE variablename = 'libraryFontSize'");
		  
		  Sweetdream.db.update("UPDATE variables SET Value = '" + Sweetdream.themeColorRed + "' WHERE variablename = 'themeColorRed'");
		  Sweetdream.db.update("UPDATE variables SET Value = '" + Sweetdream.themeColorGreen + "' WHERE variablename = 'themeColorGreen'");
		  Sweetdream.db.update("UPDATE variables SET Value = '" + Sweetdream.themeColorBlue + "' WHERE variablename = 'themeColorBlue'");
		  
		  Sweetdream.db.update("UPDATE variables SET Value = '" + Sweetdream.libraryVisibleRowsSelectBox.getSelectedItem().toString() + "' WHERE variablename = 'libraryRows'");
		  Sweetdream.db.update("UPDATE variables SET Value = '" + Sweetdream.showLibraryOnStartupSelectBox.getSelectedItem().toString() + "' WHERE variablename = 'showLibraryOnStartup'");
		  Sweetdream.db.update("UPDATE variables SET Value = '" + Sweetdream.computerVoiceSelectBox.getSelectedItem().toString() + "' WHERE variablename = 'computerVoice'");
          Sweetdream.db.update("UPDATE variables SET Value = '" + Sweetdream.setThemeSelectBox.getSelectedItem().toString() + "' WHERE variablename = 'theme'");
          Sweetdream.db.update("UPDATE variables SET Value = '" + Sweetdream.artistImageTypeSelectBox.getSelectedItem().toString() + "' WHERE variablename = 'artistImageType'");

 	} catch(SQLException ex2) { ex2.printStackTrace(); }
        try { retrieveVariables(); }  catch(SQLException ex2) { ex2.printStackTrace(); }
    }

    public static void displayPlaylistImage(String PlaylistName) throws SQLException
    {
        String imagePath = null;
        String dir = "images" + File.separator + "playlists" + File.separator + PlaylistName;
        File playlistimagesDir = new File(dir);
        if(!playlistimagesDir.exists())
        {
            printActivity("Creating new playlist image folder.");
            playlistimagesDir.mkdir();
            Sweetdream.coverLabel.setIcon(null);
        }
        String tmp[] = playlistimagesDir.list();
        if(playlistimagesDir.list() != null && tmp.length > 0)
        {
            int temp = 1;
            do
            {
                int randomNum = Sweetdream.generator.nextInt(tmp.length);
                imagePath = dir + File.separator + tmp[randomNum].toString();
                
		if(imagePath.contains("humbs.db")) temp = 0;
                else
                {
                    temp = 1;
                    Sweetdream.showPicture("coverLabel", imagePath, Sweetdream.currentAlbumName, "");
                }
            } while(temp == 0);
        } else                                    
        {
            Sweetdream.coverLabel.setIcon(null);
            Sweetdream.imageFrameLabel.setIcon(createImageIcon("images/system/eTrain.gif"));
            Sweetdream.imageFrame.pack();
        }
    }

    public static Vector getPlaylistVector(String PlaylistName) throws SQLException
    {
        ResultSet rs = null;
        Object o = null;
        String tempString = null;
        Vector tempPlaylistVector = new Vector();
        for(rs = Sweetdream.db.query("SELECT * FROM playlist_contents WHERE PlaylistName = '" + PlaylistName + "'"); rs.next(); tempPlaylistVector.addElement(tempString))
        {
            o = rs.getObject(2);
            tempString = o.toString();
        }
        Collections.shuffle(tempPlaylistVector, new Random());
        return tempPlaylistVector;
    }
    
    public static Vector getMoodVector(String MoodName) throws SQLException
    {
        ResultSet rs = null;
        String tempString = null;
        Vector tempMoodVector = new Vector();
	
        for(rs = Sweetdream.db.query("SELECT * FROM moods WHERE MoodName = '" + MoodName + "' AND Path <> ''"); rs.next(); tempMoodVector.addElement(tempString))
		tempString = rs.getObject(3).toString();
        
        Collections.shuffle(tempMoodVector, new Random());
        return tempMoodVector;
    }

    public static void displayPlaylist(String PlaylistName) throws SQLException
    {
        if(PlaylistName != null)
        {
            Sweetdream.currentPlaylistVector = getPlaylistVector(PlaylistName);
            displayPlaylistImage(PlaylistName);
            Sweetdream.currentPlaylistPane.setBorder(BorderFactory.createTitledBorder(PlaylistName));
            Sweetdream.currentLoadedPlaylist = PlaylistName;
            Sweetdream.currentPlaylistPane.setVisible(true);
            Sweetdream.playCurrentPlaylist = true;
            Sweetdream.tabbedFrontPanel.setSelectedIndex(0);
            Sweetdream.actionsListVector.add(0, Sweetdream.playlistActions[4]);
            Sweetdream.actionsListVector.add(0, Sweetdream.playlistActions[3]);
            Sweetdream.actionsListVector.add(0, Sweetdream.playlistActions[2]);
            Sweetdream.actionsListVector.add(0, Sweetdream.playlistActions[1]);
            Sweetdream.actionsListVector.add(0, Sweetdream.playlistActions[0]);
            Sweetdream.ActionSelectBox.setModel(new DefaultComboBoxModel(Sweetdream.actionsListVector));
            Sweetdream.ActionSelectBox.setSelectedIndex(0);
            Sweetdream.controlsPane.setBorder(BorderFactory.createTitledBorder("Playlist: "+ Sweetdream.currentLoadedPlaylist));
            populatePlaylistTree(PlaylistName);
            packFrame();
        }
    }

    public static void displayTable(String variable, String whichTable) throws SQLException
    {
        ResultSet rs = null;
        Object o = null;
        String a = null;
        String b = null;
        String c = null;
        String d = null;
        String e = null;
        String f = null;
        String g = null;
        int num = 1, num2 = 0;
        System.out.println("displaying table " + whichTable);
	
        if(whichTable.equals("tableArtists"))
        {
            rs = Sweetdream.db.query("SELECT * FROM artists ORDER BY ArtistName");
            Sweetdream.artistTableModel.setRowCount(0);
            Sweetdream.artistTableModel.insertRow(0, new Object[] {
                a, c, b, d, "Add"
            });
        } else if(whichTable.equals("tableAlbums"))
        {
            rs = Sweetdream.db.query("SELECT * FROM albums ORDER BY ArtistName");
            Sweetdream.albumTableModel.setRowCount(0);
            Sweetdream.albumTableModel.insertRow(0, new Object[] {
                a, b, c, g, d, e, f, "Add"
            });
        } else if(whichTable.equals("tablePlaylists"))
        {
            rs = Sweetdream.db.query("SELECT TrackPath FROM playlist_contents WHERE PlaylistName = '" + variable + "'");
            num = 0;
            Sweetdream.playlistTableModel.setRowCount(0);
	    ResultSet rs2 = Sweetdream.db.query("SELECT voicecommand FROM playlists WHERE PlaylistName = '" + variable + "'");
	    for(; rs2.next(); num2++)
	    {
               o = rs2.getObject(1);
	    }
	    if(o != null) Sweetdream.playlistVoiceCommandBox.setText(o.toString());
	    a = null; o = null;
	    
        } else if(whichTable.equals("tableEvents"))
        {
            rs = Sweetdream.db.query("SELECT * FROM events ORDER BY Time");
            num = 0;
            Sweetdream.eventTableModel.setRowCount(0);
            Sweetdream.eventTableModel.insertRow(0, new Object[] {
                a, b, c, d, e, f, "Add"
            });
        } else if(whichTable.equals("tableCollage"))
        {
            rs = Sweetdream.db.query("SELECT * FROM collage ORDER BY Name");
            Sweetdream.collageTableModel.setRowCount(0);
            Sweetdream.collageTableModel.insertRow(0, new Object[] {
                a, b, c, d, e, "Add"
            });
        }
        int columnsPlaylists = 1;
        for(; rs.next(); num++)
        {
        	if (rs.getObject(1) != null) a = rs.getObject(1).toString();   
             if (!whichTable.equals("tablePlaylists"))  {
        	if (rs.getObject(2) != null) b = rs.getObject(2).toString();
        	if (rs.getObject(3) != null) c = rs.getObject(3).toString();
        	if (rs.getObject(4) != null) d = rs.getObject(4).toString();
        	if (rs.getObject(5) != null) e = rs.getObject(5).toString();
        	if (rs.getObject(6) != null) f = rs.getObject(6).toString();
        	if (whichTable.equals("tableAlbums")) g = rs.getObject(7).toString();
             }
            
            if(whichTable.equals("tableArtists"))
            {
                Sweetdream.artistTableModel.insertRow(num, new Object[] {
                    a, c, b, e, "Delete"
                });
                continue;
            }
            if(whichTable.equals("tableAlbums"))
            {
                Sweetdream.albumTableModel.insertRow(num, new Object[] {
                    a, b, c, g, d, e, f, "Delete"
                });
                continue;
            }
            if(whichTable.equals("tablePlaylists"))
            {
                Sweetdream.playlistTableModel.insertRow(num, new Object[] {
                    a, "Delete"
                });
                continue;
            }
            if(whichTable.equals("tableEvents"))
            {
                Sweetdream.eventTableModel.insertRow(num, new Object[] {
                    a, b, c, d, "", e, "Delete"
                });
                continue;
            }
            if(whichTable.equals("tableCollage"))
            {
                Sweetdream.collageTableModel.insertRow(num, new Object[] {
                    a, b, c, d, e, "Delete"
                });
            }
        }
    }

    
    
    
    public static void reloadPlaylists() throws SQLException {
        ResultSet rs = null;
        Object o = null;
        Sweetdream.allPlaylists.clear();
        Sweetdream.allPlaylists.add("-- playlist --");
        rs = Sweetdream.db.query("SELECT * FROM playlists");
        do
        {
            if(!rs.next())
            {
                break;
            }
            o = rs.getObject(2);
            if(o != null)
                Sweetdream.allPlaylists.add(o);
            
        } while(true);
        String temp = Sweetdream.playlistSelectBox.getSelectedItem().toString();
        Sweetdream.playlistSelectBox.setModel(new DefaultComboBoxModel(Sweetdream.allPlaylists));
        if(!temp.equals("-- playlist --"))
        {
            Sweetdream.playlistSelectBox.setSelectedItem(temp);
        }
        Sweetdream.viewPlaylistSelectBox.setModel(new DefaultComboBoxModel(Sweetdream.allPlaylists));
        Sweetdream.addToPlaylistBtn.addMouseListener(new Sweetdream.AddToPlaylistPopup());
        
        
       /* Populate RIGHT CLICK Playlists submenu */
       Sweetdream.submenuPlaylists.removeAll();
       Vector temp1 = new Vector();
       JMenuItem item0;
            try {
                temp1 = getPlaylistsArray();
            } catch(SQLException sq) { sq.printStackTrace(); }
	    
            for(int i = 0; i < temp1.size(); i++) {
                item0 = new JMenuItem(temp1.get(i).toString());
                item0.addActionListener(new ActionListener() {
                		public void actionPerformed(ActionEvent e)
            {
            	    
            JMenuItem source = (JMenuItem)(e.getSource());
            String s = source.getText().trim();
            String thePlaylistString = s;
                    
		    Vector thePlaylistVector = new Vector();
		    eFunctions.printActivity("Selected playlist: " + thePlaylistString);
		    eFunctions.printActivity("Track to Add: " + Sweetdream.tree.getSelectionPath().getLastPathComponent().toString());
		    if(Sweetdream.tree.getMinSelectionRow() < 0 || Sweetdream.tree.getSelectionPath().getLastPathComponent().toString().contains(File.separator))
		    {
			    JOptionPane.showMessageDialog(Sweetdream.frame, "Select a track to add to a Playlist.");
			    return;
		    }
		    System.out.println("thePlaylistString: " + thePlaylistString);
		    try
		    {
			    thePlaylistVector = getPlaylistVector(thePlaylistString);      
			    savePlaylist(thePlaylistVector, thePlaylistString);
			    displayPlaylist(thePlaylistString);
		    }
		    catch(SQLException sq) { sq.printStackTrace(); }		   
                }		
                });
                Sweetdream.submenuPlaylists.add(item0);
            }
            Sweetdream.submenuPlaylists.addSeparator();
            
            item0 = new JMenuItem("- New Playlist -");
            item0.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
            {
            	    JMenuItem source = (JMenuItem)(e.getSource());
	            String s = source.getText().trim();
                    Vector thePlaylistVector = new Vector();
                    eFunctions.savePlaylist(thePlaylistVector, "");
            }
            });
            Sweetdream.submenuPlaylists.add(item0);
            Sweetdream.fileMusicMenu.revalidate();
    }

    public static Vector getPlaylistsArray() throws SQLException
    {
        ResultSet rs = null;
        Vector playlistArray = new Vector();
	
        rs = Sweetdream.db.query("SELECT * FROM playlists");
        for(int i = 0; rs.next(); i++)
        {
	    if(rs.getObject(2) != null) playlistArray.add(rs.getObject(2).toString());
        }
        return playlistArray;
    }
    
    public static Vector getMoodsArray() throws SQLException {
        ResultSet rs = null;
        String z = null;
        Vector moodsArray = new Vector();
	
        rs = Sweetdream.db.query("SELECT * FROM moods WHERE Path = ''");
        for(int i = 0; rs.next(); i++)
        {
            z = rs.getObject(2).toString();
            if(z != null)
            {
                moodsArray.add(z);
           //     printActivity(moodsArray.get(i) + "\n");
            }
        }
        return moodsArray;
    }

    public static void retrieveVariables() throws SQLException
    {
        ResultSet rs = null;
        String one = null;
        String two = null;
        String tempMusicPath = "",tempAudioPath = "",tempVideoPath = "",tempMP3Path = "",tempPhotoPath = "";
	
        rs = Sweetdream.db.query("SELECT * FROM variables");
        if(System.getProperty("os.name").startsWith("Win"))
        {
            tempMusicPath = "MusicDirectoryPath";
            tempAudioPath = "AudioDirectoryPath";
            tempVideoPath = "VideoDirectoryPath";
            tempPhotoPath = "PhotoDirectoryPath";
            tempMP3Path = "MP3PlayerDirectoryPath";
        } else
        {
            tempMusicPath = "MusicLinuxPath";
            tempAudioPath = "AudioLinuxPath";
            tempVideoPath = "VideoLinuxPath";
            tempPhotoPath = "PhotoLinuxPath";
            tempMP3Path = "MP3LinuxPath";
        }
        printActivity("Retrieving variables from database.");
        do
        {
            if(!rs.next()) break;
            
            one = rs.getObject(1).toString();
            two = rs.getObject(2).toString();
            //printActivity("retrieving variable name: " + one + " | value: " + two);
            if(two != null)
            {
                     if(one.equals(tempMusicPath)) 	Sweetdream.MusicDirectoryPath = two;
                else if(one.equals(tempAudioPath)) 	Sweetdream.AudioDirectoryPath = two;
                else if(one.equals(tempVideoPath)) 	Sweetdream.VideoDirectoryPath = two;
                else if(one.equals(tempPhotoPath)) 	Sweetdream.PhotoDirectoryPath = two;
                else if(one.equals(tempMP3Path)) 	Sweetdream.MP3PlayerDirectoryPath = two;
                else if(one.equals("tracksPlayedVectorSize")) Sweetdream.tracksPlayedVectorSize = two;
                
                else if(one.equals("themeColorRed")) 	Sweetdream.themeColorRed = Integer.parseInt(two);
                else if(one.equals("themeColorGreen")) 	Sweetdream.themeColorGreen = Integer.parseInt(two);
                else if(one.equals("themeColorBlue")) 	Sweetdream.themeColorBlue = Integer.parseInt(two);
                
                else if(one.equals("libraryFont")) 	Sweetdream.libraryFont = two;
                else if(one.equals("libraryFontSize")) 	Sweetdream.libraryFontSize = Integer.parseInt(two);
                else if(one.equals("libraryRows")) 	Sweetdream.libraryVisibleRows = Integer.parseInt(two);
                else if(one.equals("artistImageType"))  Sweetdream.ArtistImageType = two;
                else if(one.equals("collageVideoRows"))  eCollage.collageVideoRows = Integer.parseInt(two);
                else if(one.equals("collageVideoCols"))  eCollage.collageVideoCols = Integer.parseInt(two);
                else if(one.equals("collageMusicRows"))  eCollage.collageMusicRows = Integer.parseInt(two);
                else if(one.equals("collageMusicCols"))  eCollage.collageMusicCols = Integer.parseInt(two);
                else if(one.equals("collageRandomStart"))eCollage.collageRandomStart = Boolean.parseBoolean(two);   //{ if (Integer.parseInt(two).equals("Yes")) }
                else if(one.equals("collageItemSize"))  eCollage.collageItemSize = two;
             //   else if(one.equals("artistImageType"))  	Sweetdream.artistImageType = two;
                
                else if(one.equals("lastLibraryTab"))  Sweetdream.lastLibraryTab = Integer.parseInt(two);
                else if(one.equals("computerVoice"))
                {
                    if(two.equals("On")) Sweetdream.computerVoice = true;
                    else Sweetdream.computerVoice = false;
                }
            }
        } while(true);
        if(!Sweetdream.AudioDirectoryPath.equals("") && new File(Sweetdream.AudioDirectoryPath).exists())  
		Sweetdream.tabbedPaneLibrary.addTab("<html><br>Audio", null, Sweetdream.scrollViewAudioLibrary, "All Audio");
        if(!Sweetdream.VideoDirectoryPath.equals("") && new File(Sweetdream.VideoDirectoryPath).exists()) 
		Sweetdream.tabbedPaneLibrary.addTab("<html><br>Video", null, Sweetdream.scrollViewVideoLibrary, "All Video");
		if(!Sweetdream.PhotoDirectoryPath.equals("") && new File(Sweetdream.PhotoDirectoryPath).exists()) 
		Sweetdream.tabbedPaneLibrary.addTab("<html><br>Photo", null, Sweetdream.scrollViewPhotoLibrary, "All Photos");

        updateOptionsTextBoxes();
    }

    public static void retrieveAlbumInfo() throws SQLException
    {
        ResultSet rs = null;
        Object o = null;
        String albumID = null;
        String albumName = null;
        String artistName = null;
        String albumCover = null;
        String albumYear = null;
        String albumFrequency = null;
        String albumVoiceCommand = null;
	
        rs = Sweetdream.db.query("SELECT * FROM albums WHERE albumName = '" + Sweetdream.editID3AlbumTextBox.getText() + "'");
        ResultSetMetaData meta = rs.getMetaData();
        int colmax = meta.getColumnCount();
        do
        {
            if(!rs.next()) break;

            for(int i = 0; i < colmax; i++)
            {
                o = rs.getObject(i + 1);
                if(i == 0) albumID = o.toString(); 
                if(i == 1) artistName = o.toString();
                if(i == 2) albumName = o.toString();
                if(i == 3) albumCover = o.toString();
                if(i == 4) albumYear = o.toString();
                if(i == 5) albumFrequency = o.toString();
                if(i == 6) albumVoiceCommand = o.toString();
                
                printActivity(i + ") " + o.toString() + " ");
            }

            if(albumID != null) Sweetdream.albumID = albumID;
         //   if(artistName != null) Sweetdream.editAlbumArtistNameBox.setText(artistName);
         //   if(albumName != null) Sweetdream.editAlbumAlbumNameBox.setText(albumName);
            if(albumCover != null) Sweetdream.editAlbumCoverBox.setText(albumCover);
            if(albumYear != null) Sweetdream.editAlbumReleaseYearBox.setText(albumYear);
            if(albumFrequency != null) Sweetdream.editAlbumFrequencyBox.setText(albumFrequency);
            if(albumVoiceCommand != null) Sweetdream.editAlbumVoiceCommandBox.setText(albumVoiceCommand);
        } while(true);
        if(albumName == null)
		printActivity("retrievingAlbumInfo didn't find anything.");
    }
    
    

    public static void displayArtistImage(String theArtist) throws SQLException
    {
		printActivity("displayArtistImage(): " + theArtist);
		String imagePath = "", dir = "";
		int temp = 1;
		
		if (theArtist.equals("eTrain"))
			dir = "images" + File.separator + "etrain";
		else
			dir = "images" + File.separator + "artists" + File.separator + theArtist;
		
	
			File imageDir = new File(dir);
			
			/* If artist directory does not exist, create it */
				if(!imageDir.exists()) {  // && !imageDir.contains(".")
				printActivity("Creating new artist image folder: " + dir);
				imageDir.mkdir();
			}
			
			String tmp[] = imageDir.list();
			if (imageDir.list() == null || tmp.length < 1) { // No artist images, so display default eTrain image
			dir = "images" + File.separator + "etrain";
			imageDir = new File(dir);
			tmp = imageDir.list();
			Sweetdream.imageFrameLabel.setIcon(createImageIcon("images/system/eTrain.gif"));
		}
		
				do
				{
					int randomNum = Sweetdream.generator.nextInt(tmp.length);
					imagePath = dir + File.separator + tmp[randomNum].toString();
					if(imagePath.contains("humbs.db"))
				temp = 0;
					else
					{
						temp = 1;
						Sweetdream.showPicture("coverLabel", imagePath, Sweetdream.currentAlbumName, "");
					}
				} while(temp == 0);
    }

  
    

    
    public static void themeSearch(String searchingIn, String searchingFor) throws SQLException
    {
        String one = "";
        for(int i = 1; i < Sweetdream.musicDirVector.size() && !one.startsWith(searchingFor); one = Sweetdream.musicDirVector.get(i - 1).toString())
        {
            i++;
        }

        printActivity("searchingFor: " + searchingFor + "  one: " + one);
        if(one.startsWith(searchingFor))
        {
            Sweetdream.playTypeSelectBox.setSelectedIndex(0);
            if(one != null)
            {
                Sweetdream.usingSearch = searchingFor;
            }
            Sweetdream.themeDisplay.setText(searchingFor);
        } else
        {
            printActivity("themeSearch Query returned no matches.");
            Sweetdream.themeDisplay.setText("Random");
        }
    }

    
    public static void adjustVolume(int percentage) {  
    	   printActivity("Adjusting volume to " + percentage + "%");
    	    try {  
    	    	    if (percentage > 0) { Sweetdream.mainSoundPlayer.setGain((double)percentage / 100); Sweetdream.currentVolumeLevel = percentage; }
    	    	    else { Sweetdream.isPaused = true; Sweetdream.mainSoundPlayer.pause(); Sweetdream.pause_timer.stop(); Sweetdream.mainSoundPlayer.setGain(0.5D); Sweetdream.currentVolumeLevel = 50; }
    	    } catch(BasicPlayerException ex) { ex.printStackTrace(); }
    }
    
    
    public static void oldthemeSearch(String searchingIn) throws SQLException
    {
        ResultSet rs = null;
        String one = null;
        if(searchingIn.equals("artists"))
		rs = Sweetdream.db.query("SELECT * FROM artists WHERE artistName LIKE '%" + Sweetdream.themeTextBox.getText() + "%'");
        else if(searchingIn.equals("albums"))
		rs = Sweetdream.db.query("SELECT * FROM albums WHERE albumName LIKE '%" + Sweetdream.themeTextBox.getText() + "%'");
        
        for(; rs.next(); Sweetdream.themeDisplay.setText(Sweetdream.themeTextBox.getText()))
        {
            one = rs.getObject(2).toString();
            Sweetdream.playTypeSelectBox.setSelectedIndex(0);
            if(one != null)
            {
                Sweetdream.usingSearch = Sweetdream.themeTextBox.getText();
            }
        }

        if(one == null)
        {
            printActivity("themeSearch Query returned no matches.");
            Sweetdream.themeDisplay.setText("Random");
        }
    }

    public static void displayAlbumImage(String currentAlbumName, String overrideLoop) throws SQLException
    {
        ResultSet rs = null;
        Object o = null;
        String artist = "";
        String album = "";
        String coverImage = "";
        String releaseyear = "";
        String frequency = "";
	
        if(currentAlbumName != null && currentAlbumName.contains("'"))
		currentAlbumName = currentAlbumName.replace("'", "&quot;");

        rs = Sweetdream.db.query("SELECT * FROM albums WHERE albumName = '" + currentAlbumName + "'");
        ResultSetMetaData meta = rs.getMetaData();
        int colmax = meta.getColumnCount();
        while(rs.next()) 
        {
            int i = 0;
            while(i < colmax) 
            {
                o = rs.getObject(i + 1);
                if(i == 1) artist = o.toString();
                if(i == 2) album = o.toString();
                if(i == 3) coverImage = "images/covers/" + o.toString();
                if(i == 4) releaseyear = o.toString();
                if(i == 5) frequency = o.toString();
                if(i == 0) printActivity("----Album Info------------");
                else printActivity(i + ") " + o.toString() + " ");
                
                i++;
            }
        }
        printActivity("getArtistImage artist: " + artist + " coverImage: " + coverImage);
        File theCover = new File(coverImage);
        if(coverImage == null || coverImage.equals("") || !theCover.exists())
        {
            if(Sweetdream.actionsListVector.elementAt(1) != "Add Album Cover")
		    Sweetdream.actionsListVector.add(1, "Add Album Cover");
            
            Sweetdream.ActionSelectBox.setModel(new DefaultComboBoxModel(Sweetdream.actionsListVector));
	    displayArtistImage(Sweetdream.currentPlayingArtist);
            return;
        } else
        {
            Sweetdream.actionsListVector.remove("Add Album Cover");
            Sweetdream.ActionSelectBox.setModel(new DefaultComboBoxModel(Sweetdream.actionsListVector));
        }
        if(coverImage.equals("") || Sweetdream.ArtistImageType.equals("Saved Images Only") || album == null)
        {
            if(!Sweetdream.currentPlayingArtist.equals("") && !overrideLoop.equals("override"))
            {
                displayArtistImage(Sweetdream.currentPlayingArtist);
                return;
            }
        } else
        if(!Sweetdream.ArtistImageType.equals("Saved Images Only") || Sweetdream.ArtistImageType.equals("No Image"))
        {
            Sweetdream.showPicture("coverLabel", coverImage, album, releaseyear);
            return;
        }
        if(album == null)
        {
            printActivity("displayAlbumImage Query returned no matches.");
            Sweetdream.addAlbumBtn.setVisible(true);
            Sweetdream.imagePane.setVisible(false);
            Sweetdream.showPicture("coverLabel", "images/etrain/green-train.gif", "", "");
            Sweetdream.imageFrameLabel.setIcon(createImageIcon("images/system/eTrain.gif"));
            Sweetdream.imageFrame.pack();
        }
    }

    public static String displayNow(String dateFormat)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());
    }

    public static String parseSongPath(String songPath, String whatToReturn)
    {
        if(songPath.length() < 5) return "";
        
        if(whatToReturn.equals("song"))
        {
            String temp = "";
            String temp2 = "";
            String temp3 = "";
            if(songPath.contains(File.separator))
		    temp = songPath.substring(songPath.lastIndexOf(File.separator)).replace(File.separator, "");
            
            if(temp.contains(" - "))
		    temp2 = temp.substring(temp.indexOf(" - ") + 3);
            else temp2 = temp;
            
            if(temp2.lastIndexOf(".") == temp2.length() - 4)
		    temp3 = temp2.substring(0, temp2.length() - 4);
            else temp3 = temp2;
            
            return temp3;
        }
        if(whatToReturn.equals("album"))
        {
            String albumFolderName = null;
            String tempA = "";
            try
            {
                int numberOfSlashes = songPath.length() - songPath.replaceAll(File.separator, "").length();
                if(numberOfSlashes == 2)
                {
                    albumFolderName = songPath.substring(songPath.lastIndexOf(File.separator)).substring(1);
                } else
                if(numberOfSlashes > 2)
                {
                    tempA = songPath.substring(songPath.indexOf(File.separator, 2));
                    int songStart = tempA.indexOf(File.separator, 2);
                    albumFolderName = tempA.substring(0, songStart).substring(1);
                }
            }
            catch(IllegalArgumentException yo) { printActivity("ILL argument"); }
            catch(NullPointerException yoq) { printActivity("null POINTER"); }
            return albumFolderName;
        }
        if(whatToReturn.equals("artist"))
        {
            if(songPath.lastIndexOf(File.separator) == songPath.indexOf(File.separator, 2))
		    return songPath;
            
            if(songPath.contains(File.separator))
		    return songPath.substring(1, songPath.indexOf(File.separator, 2));
            else return songPath;                                
            
        } else return "";
    }

    
    /* Calculate total tracks played for calculateFrequencyFont() to calculate a percentage 
       This way we can get this variable once, and not have to call it repeatedly from 
       calculateFrequencyFont() which runs with each row of every JTree       */
    public static void getTotalTimesPlayed() throws SQLException
    {
    	ResultSet rs = null;
        int totalPlays = 1;
        
        for(rs = Sweetdream.db.query("SELECT TimesPlayed FROM artists"); rs.next();)
		totalPlays = totalPlays + Integer.parseInt(rs.getObject(1).toString());
	
	Sweetdream.totalPlays = totalPlays;
    }    
                                     
    /* Give: number of times an artist has played */
    public static int calculateFrequencyFont(int times) throws SQLException
    {
        double percentage = 0;
        int fontSize = 12;
        
        if (times > 0) percentage = ((double) times / (double) Sweetdream.totalPlays) * 100;
        
             if (percentage < 0.24)      fontSize = 12;
        else if (percentage < 0.37)      fontSize = 13;
        else if (percentage < 0.54)      fontSize = 14;
        else if (percentage < 0.81)      fontSize = 15;                 
        else if (percentage < 1)         fontSize = 16;
        else if (percentage < 1.25)      fontSize = 17;
        else if (percentage < 1.5)       fontSize = 18;
        else if (percentage < 1.75)      fontSize = 19;
        else if (percentage < 2)         fontSize = 20;
        else if (percentage < 2.25)      fontSize = 21;
        else if (percentage < 2.5)       fontSize = 22;
        else if (percentage < 3)         fontSize = 23;
        else if (percentage < 3.5)       fontSize = 24;
        else if (percentage < 4)         fontSize = 25;
        else if (percentage < 4.5)       fontSize = 26;
        else if (percentage < 5)         fontSize = 27;
        else if (percentage < 5.5)       fontSize = 28;
        else if (percentage < 6   && Sweetdream.totalPlays > 20)      fontSize = 29;
        else if (percentage < 6.5 && Sweetdream.totalPlays > 20)      fontSize = 30;
        else if (percentage < 7   && Sweetdream.totalPlays > 20)      fontSize = 31;
        else if (percentage < 7.5 && Sweetdream.totalPlays > 20)      fontSize = 32;
        else if (percentage < 8   && Sweetdream.totalPlays > 20)      fontSize = 33;
        else if (percentage < 8.5 && Sweetdream.totalPlays > 20)      fontSize = 34;
        else if (percentage < 9   && Sweetdream.totalPlays > 20)      fontSize = 35;
        else if (percentage < 9.5 && Sweetdream.totalPlays > 20)      fontSize = 36;
        else if (percentage < 10  && Sweetdream.totalPlays > 20)      fontSize = 37;
        else fontSize = 38;

	//System.out.println("totalPlays: " + Sweetdream.totalPlays + "  ...  " + "times: " + times + " percentage: " + percentage);
	return fontSize;
    }
    
    
    
    public static void addArtistToDatabase(String theArtist) throws SQLException
    {
    	    Sweetdream.db.update("INSERT INTO artists(ArtistName) VALUES('" + StringEscapeUtils.escapeSql(theArtist) + "')");
    }

    
    public static int checkForNewArtist(String theArtist) throws SQLException
    {
        ResultSet rs = null;
        int times = 0;
        String one = "";
        boolean exists = false;
        
        if (theArtist == null) return -1;
        
        for(rs = Sweetdream.db.query("SELECT TimesPlayed FROM artists WHERE artistName = '" + StringEscapeUtils.escapeSql(theArtist) + "' AND artistid < 800000"); rs.next();)
        	{ exists = true; times = Integer.parseInt(rs.getObject(1).toString()); }
	
        if(!exists) times = -1;
        
        return times;
    }

    public static String voiceCommandSearch(String voiceCommand, String searchTable) throws SQLException
    {
        ResultSet rs = null;
        String one = null, two = null;
	
        rs = Sweetdream.db.query("SELECT * FROM " + searchTable + " WHERE voicecommand = '" + voiceCommand + "'");
        do
        {
            if(!rs.next())
            {
                break;
            }
            one = rs.getObject(3).toString().toLowerCase();
	    if(searchTable == "playlists") { two = rs.getObject(2).toString().toLowerCase(); }
            if(one != null && searchTable != "playlists")
            {
                //Sweetdream.usingSearch = one;
                Sweetdream.found = "yes";
                one = rs.getObject(3).toString();
                printActivity("voiceCommandSearch: usingSearch: " + one);
            } else if(searchTable == "playlists") {
		    Sweetdream.found = "yes";
		    Sweetdream.usingSearch2 = rs.getObject(2).toString();
		    printActivity("Sweetdream.usingSearch2: " + Sweetdream.usingSearch2);
	      }
        } while(true);
        if(one == null)
        {
            printActivity("voiceCommandSearch Query returned no matches.");
            Sweetdream.found = "no";
	    return "";
        }
	return one;
    }

    public static void closeMood()
    {
	    	Sweetdream.playCurrentMood = false;
		Sweetdream.usingSearch = "";
		Sweetdream.themeDisplay.setText("");
                Sweetdream.themeTextBox.setText("");
                Sweetdream.playTypeSelectBox.setSelectedIndex(0);
		Sweetdream.controlsPane.setBorder(BorderFactory.createTitledBorder(""));
    }
    
    public static void runEvents()
    {
        ResultSet rs = null;
        Object o = null;
        String time = null;
        String type = null;
        String value = null;
	
        try {
            rs = Sweetdream.db.query("SELECT * FROM events WHERE time = '" + displayNow("h:mm a") + "' AND Active = 'Y'");
            do
            {
                if(!rs.next())
                {
                    break;
                }
                time = rs.getObject(2).toString();
                type = rs.getObject(3).toString();
                value = rs.getObject(4).toString();
                if(displayNow("h:mm a").equals(time))
                {
                    if(Sweetdream.playBtn.getIcon() == Sweetdream.playButtonIcon)
                    {
                        if(type.equals("Song"))
                        {
                            try
                            {
                                Sweetdream.mainSoundPlayer.open(Sweetdream.openFile(Sweetdream.MusicDirectoryPath + value));
                                Sweetdream.mainSoundPlayer.play();
                            }
                            catch(BasicPlayerException e1) { }
                        } else
                        if(type.equals("Playlist"))
                        {
                            displayPlaylist(value);
                            Sweetdream.nextTrack("Timed event: playlist");
                        }
                    }
                         if(type.equals("Webpage")) openBrowser(value);
                    else if(type.equals("Media"))   launchMovie(value,"");
                    
                    SysTray.trayIcon.displayMessage(value, time, java.awt.TrayIcon.MessageType.NONE);
                }
            } while(true);
        }
        catch(SQLException sq) { sq.printStackTrace(); }
    }

    public static void packFrame()
    {
        if(Sweetdream.frame.getExtendedState() < 5)
		Sweetdream.frame.pack();
    }

    public static void startTimer()
    {
        int setTimerSeconds = 0;
        String setTimerSecondsString = "";
        setTimerSecondsString = (String)JOptionPane.showInputDialog(new JFrame(), "Enter Number of Seconds:", "Begin Timer", -1, null, null, "120");
        if(!setTimerSecondsString.equals(""))
		setTimerSeconds = Integer.parseInt(setTimerSecondsString);
        
        Sweetdream.totalSeconds = setTimerSeconds;
        if(setTimerSeconds > 0)
		Sweetdream.countdown_timer.start();
        
        Sweetdream.countdownFrame.setVisible(true);
    }

    public static void updateCountdown()
    {
        Sweetdream.totalSeconds--;
        String currentSeconds = Sweetdream.formatter.format(Sweetdream.totalSeconds % 60);
        Sweetdream.countdownLabel.setText(Math.round(Sweetdream.totalSeconds / 60) + ":" + currentSeconds);
        if(Sweetdream.totalSeconds == 2)
		countdownFinished();
        
        if(Sweetdream.totalSeconds == 0)
        {
            Sweetdream.countdown_timer.stop();
            Sweetdream.countdownLabel.setVisible(false);
            Sweetdream.countdownFrame.setBackground(Color.green);
        }
    }

    public static void countdownFinished()
    {
        String dir = "sounds" + File.separator + "timer";
        String tmpSoundsList[] = (new File(dir)).list();
        String theSound = null;
        if(tmpSoundsList != null && tmpSoundsList.length > 0)
        {
            int temp = 1;
            do
            {
                int randomNum = Sweetdream.generator.nextInt(tmpSoundsList.length);
                theSound = dir + File.separator + tmpSoundsList[randomNum].toString();
            } while(temp == 0);
            try
            {
                Sweetdream.mainSoundPlayer.open(Sweetdream.openFile(theSound));
                Sweetdream.mainSoundPlayer.play();
            }
            catch(BasicPlayerException ew) { ew.printStackTrace(System.out); }
        } else
        {
            try
            {
                Sweetdream.mainSoundPlayer.open(Sweetdream.ackSound);
                Sweetdream.mainSoundPlayer.play();
            }
            catch(BasicPlayerException ew) { ew.printStackTrace(System.out); }
        }
	
        javax.swing.Timer temp_timer = new javax.swing.Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.countdownFrame.setVisible(false);
            }

        });
        temp_timer.setRepeats(false);
        temp_timer.setDelay(2000);
        temp_timer.start();
    }

}

