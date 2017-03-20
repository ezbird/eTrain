/* eTrain Media Player

Sweetdream.java is the primary source file.

*/
package sweetdream;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.*;
import java.sql.SQLException;                                                                                                                               
import java.text.*;
import java.util.*;                                                                                                                                                  
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javazoom.jlgui.basicplayer.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.blinkenlights.jid3.*;
import org.blinkenlights.jid3.v1.*;
import org.blinkenlights.jid3.v2.*;
import static org.imgscalr.Scalr.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;

public class Sweetdream implements BasicPlayerListener
{
	static JPopupMenu fileMusicMenu;
	static int numFolders=0;
    static int numFiles = 0;
    static JMenu submenuMoods, submenuPlaylists;
        
    static JFrame frame = new JFrame("eTrain");
    static Frame optionsFrame = new Frame("Options");
    static Frame addAlbumFrame = new Frame("Add New Album");
    static Frame editID3Frame = new Frame("Edit ID3 Tag");
    static Frame logViewFrame = new Frame("eTrain Log");
    static Frame newArtistFrame = new Frame("Add New Artist");
    static Frame imageFrame = new Frame("eTrain");
    static Frame touchFrame = new Frame("Welcome to eTrain Touch");
    static Frame countdownFrame = new Frame("");
    
    public static BasicController theSoundPlayer = null;
    public static int musicDirIndex = -1, musicFileIndex = -1, audioDirIndex = -1, audioFileIndex = -1, videoFileIndex = -1;
    public static String currentSongPath = "";
    public static String tracksPlayedVectorSize = "25";
    
    public static long startupTimeStart = 0;
    public static long startupTimeEnd = 0;
    
    //DATABASE VARIABLES
    public static int themeColorRed = 200;
    public static int themeColorGreen = 200;
    public static int themeColorBlue = 200;
    public static String ArtistImageType = "";
    public static String libraryFont = "Vera";
    public static int libraryFontSize = 11;
    public static int libraryVisibleRows = 27;
    public static String showLibraryOnStartup = "Yes";
    public static String MusicDirectoryPath = "",VideoDirectoryPath = "",AudioDirectoryPath = "",PhotoDirectoryPath = "",MP3PlayerDirectoryPath = "";
    public static boolean collapseAfterPlayFinish = true;
    public static boolean playCurrentPlaylist = false;
    public static boolean playCurrentMood = false;
    public static boolean computerVoice = true;
    public static boolean voiceCommandSequential = false;
    public static boolean isSystemSound = false;
    public static boolean enableFrequencyFont = true;
    
    public static int errorsInARow = 0;
    static int playlistIndexToPlay = -1;
    static int playlistIndexToCopy = 0;
    static int lastLibraryTab = 0;
    static javax.swing.Timer event_timer, countdown_timer,startup_timer,pause_timer;
    static int totalSeconds = 145;
    static JFileChooser m_fileChooser = new JFileChooser("MultiSelectionEnabledChangedProperty");
    static JFileChooser getImageFileChooser = new JFileChooser("MultiSelectionEnabledChangedProperty");
    static JFileChooser browseFolderChooser = new JFileChooser();
    static eDatabase db = null;
    static JTree tree,tree_touch,tree_audio,tree_audiocategory,tree_playlist,tree_last25,tree_video,tree_options,tree_touchmusic,tree_photo,tree_mp3player = new JTree();
    static CheckNode root_music,root_touch,root_audio,root_audiocategory,root_video,root_mp3player,root_photo;
    static DefaultTreeModel mp3playerModel,librarytreeModel,touchtreeModel,videotreeModel,categorytreeModel,playlisttreeModel,last25treeModel,audiotreeModel,phototreeModel;
    static AudioFormat audioFormat;
    static TargetDataLine targetDataLine;
    static InputStream source;
    static OutputStream copy;
    public String fileToCopy;
    public static int numToBeTransferred;
    public static int nextMinutePushes;
    public static String copyName = null;
    public static final int INIT = 0, OPEN = 1, PLAY = 2, PAUSE = 3, STOP = 4;
    public static int playerState = 0;
    public static long secondsAmount = 0L;
    static String albumID = "1";
    int byteslength;
    long total;
    float progress;
    static int InfiniteLoopPreventor = 0;
    static int tempFrequency = 1;
    static int prevBtnClicks = 0;
    static int folderRowNum = 0;
    static int getLastSpace = -1;
    static int numTransferred = 0;
    static int tracksLeft = -1;
    static int currentRowNum = 0;
    static int totalPlays = 1;
    static int currentVolumeLevel = 50; // Percentage from 1 to 100%

    int secondsLength;
    
    /* Images and Sounds which are saved inside the etrain.jar file */
    	public static Icon addIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/Add24.gif"));
    	public static Icon scrollUpIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/Export24.gif"));
    	public static Icon scrollDownIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/Import24.gif"));
        public static Icon playButtonIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/media/Play24.gif"));
        public static Icon stopButtonIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/media/Stop24.gif"));
        public static Icon pauseButtonIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/media/Pause24.gif"));
        public static Icon nextButtonIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/media/StepForward24.gif"));
        public static Icon prevButtonIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/media/StepBack24.gif"));
        public static Icon refreshIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/Refresh24.gif"));
        public static Icon saveIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/Save24.gif"));
        public static Icon plusIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/ZoomIn24.gif"));
        public static Icon minusIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/ZoomOut24.gif"));
        public static Icon optionsButtonIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/Help24.gif"));
        public static Icon logButtonIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/History24.gif"));
        public static Icon eventsButtonIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/Search24.gif"));
        public static Icon deleteIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/delete.gif"));
        public static Icon uploadIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/upload.gif"));
        public static Icon viewIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/view.gif"));
        public static Icon hideLibraryIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/navigation/Back16.gif"));
        public static Icon collageIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/development/Host24.gif"));
        public static Icon preferencesIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/Preferences24.gif"));
        public static Icon touchIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/RowInsertBefore24.gif"));
        public static Icon textIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/text.gif"));
        public static Icon musicNotesIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/musicnotes4.png"));
        public static Icon closePlaylistIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/icon_viewright.gif"));
        public static Icon videoIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/icon_video.gif"));
        public static Icon closeIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/close.gif"));
        public static Icon speakerIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/icon_speaker.gif"));
        public static Icon cdIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/system/icon_cd.gif"));
        public static Icon toggleFontIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/general/Replace24.gif"));
        public static Icon muteIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/media/Volume24.gif"));
        public static Icon unmuteIcon = new ImageIcon((sweetdream.Sweetdream.class).getResource("images/toolbarButtonGraphics/media/Volume16.gif"));
        
        public static URL ackSound = (sweetdream.Sweetdream.class).getResource("sounds/acknowledged.mp3");
        public static URL transferSound = (sweetdream.Sweetdream.class).getResource("sounds/transfer_of_data_complete.wav");
        public static URL startupSound = (sweetdream.Sweetdream.class).getResource("sounds/program_ready.wav");
        public static URL deletedSound = (sweetdream.Sweetdream.class).getResource("sounds/file_deleted.mp3");
        public static URL shutdownSound = (sweetdream.Sweetdream.class).getResource("sounds/shutdown.mp3");
        public static URL channelopenSound = (sweetdream.Sweetdream.class).getResource("sounds/channel_open.mp3");
        public static URL affirmSound = (sweetdream.Sweetdream.class).getResource("sounds/affirmative.wav");
        
        public static URL niceFont = (sweetdream.Sweetdream.class).getResource("fonts/Lato-Regular.ttf");
    /* end getresources inside jar */
    


    
    public static Font font10,font12,font15;
    Dimension dimension100x20;
    public static String currentAlbumName = "";
    public static String currentLoadedPlaylist = "";
    public static String usingSearch = "", usingSearch2 = "";
    public static String found = "no", bypassFrequency = "no";
    static String currentPlayingArtist = "",currentPlayingAlbum = "",currentPlayingSong = "", currentPlayingTree = "";
    static String htmlBtnString = "<html><center><font color='navy'>";
	
    static final JButton nextPageBtn = new JButton("",nextButtonIcon);
    static final JButton prevPageBtn = new JButton("",prevButtonIcon);
    static final JButton stopBtn = new JButton("", stopButtonIcon);
    static final JButton playBtn = new JButton("", playButtonIcon);
    static final JButton nextBtn = new JButton("", nextButtonIcon);
    static final JButton prevBtn = new JButton("", prevButtonIcon);
    static final JButton optionsBtn = new JButton("", optionsButtonIcon);
    static final JButton expandCollapseBtn = new JButton("", plusIcon);
    static final JButton refreshBtn = new JButton("", refreshIcon);
    static final JButton muteBtn = new JButton("", muteIcon);
    static final JButton saveMoodBtn = new JButton("", preferencesIcon);
    static final JButton toggleFrequencyFontBtn = new JButton("", toggleFontIcon);
    static final JButton showLibraryBtn = new JButton("", prevButtonIcon);
    static final JButton lyricsSubmitBtn = new JButton(htmlBtnString + "Update Lyrics", saveIcon);
    static final JButton addAlbumBtn = new JButton(htmlBtnString + "Add Album...");
    static final JButton addAlbumSubmitBtn = new JButton(htmlBtnString + "Create Album");
    static final JButton updateID3Btn = new JButton(htmlBtnString + "Update Info");
    static final JButton mp3playerDeleteBtn = new JButton("", deleteIcon);
    static final JButton mp3playerAddTracksBtn = new JButton("", uploadIcon);
    static final JButton getMusicDirectoryBtn = new JButton(htmlBtnString + "...");
    static final JButton getAudioDirectoryBtn = new JButton(htmlBtnString + "...");
    static final JButton getVideoDirectoryBtn = new JButton(htmlBtnString + "...");
    static final JButton getPhotoDirectoryBtn = new JButton(htmlBtnString + "...");
    static final JButton getMP3PlayerDirectoryBtn = new JButton(htmlBtnString + "...");
    static final JButton addAlbumCoverBtn = new JButton(htmlBtnString + "Browse...");
    static final JButton editAlbumCoverBtn = new JButton(htmlBtnString + "Browse...");
    static final JButton newArtistSubmitBtn = new JButton(htmlBtnString + "Create Artist");
    static final JButton viewPlaylistBtn = new JButton("", viewIcon);
    static final JButton logBtn = new JButton("", logButtonIcon);
    static final JButton eventsBtn = new JButton("", eventsButtonIcon);
    static final JButton collageDisplayBtn = new JButton("", collageIcon);
    static final JButton touchDisplayBtn = new JButton("", touchIcon);
    static final JButton nextMinuteBtn = new JButton(">");
    static final JButton addToPlaylistBtn = new JButton("", addIcon);
    static final JButton ScrollUpBtn = new JButton("", scrollUpIcon);
    static final JButton ScrollDownBtn = new JButton("", scrollDownIcon);
    static final JButton viewCommandsTxtBtn = new JButton("Voice Commands");
    static final JButton downloadUpdateBtn = new JButton("Check for Update");
    static final JButton playlistDeleteBtn = new JButton("Delete Playlist");
    static final JButton playlistSetVoiceCommandBtn = new JButton("Set");
    static final JButton playlistCleanBtn = new JButton("Clean Up");
    static final JButton saveLogBtn = new JButton("Save Log");
    static final JButton cancelDownloadBtn = new JButton("Cancel");
    static final JButton setColorBtn = new JButton("Choose...");
    
    static Box boxOptionsB,boxOptionsC,boxOptionsD,boxOptionsE,boxOptionsF,boxOptionsG;
    static Box boxOptions_Updates;
    static Box boxRightside;
    static Box boxLeftside = new Box(1);
    static JPanel boxLibraryButtons,boxOptionsA;
    static JPanel statusPane = new JPanel();
    static JPanel voiceCommandPane = new JPanel();
    static JPanel controlsPane;
    static JPanel optionsPane = new JPanel();
    static JPanel imagePane = new JPanel();
    static JPanel logPanel = new JPanel();
    static JPanel currentPlaylistPane = new JPanel();
    static JPanel downloadPanel = new JPanel();
    static JPanel panelOfEverything = new JPanel();
    static JLabel currentColorLabel = new JLabel(" ");
    static JLabel noMP3PlayerDisplay = new JLabel("\n\nMP3 player not detected.");
    static JLabel saveLogDisplay = new JLabel("Good day.");
    static JLabel statusDisplay = new JLabel("Loading...");
    static JLabel voiceCommandDisplay = new JLabel("");
    static JLabel themeDisplay = new JLabel("", null, 2);
    static JLabel tracksLeftDisplay = new JLabel("", null, 4);
    static JLabel songTimeDisplay = new JLabel("");
    static JLabel songDurationDisplay = new JLabel("");
    static JLabel tracksTransferredDisplay = new JLabel("");
    static JLabel newArtistLabel = new JLabel();
    static JLabel coverLabel = new JLabel();
    static JLabel photoLabel = new JLabel();
    static JLabel imageFrameLabel = new JLabel("");
    static JLabel downloadLabel = new JLabel();
    static JLabel displayCurrentVersionLabel = new JLabel();
    static JLabel countdownLabel = new JLabel("", null, 0);
   
    static JPanel touchMainPanel = new JPanel(new GridBagLayout());
    
    static Vector allPlaylists = new Vector();
    static Vector currentPlaylistVector = new Vector();
    static Vector currentMoodVector = new Vector();
    static Vector tracksPlayedVector = new Vector(Integer.parseInt(tracksPlayedVectorSize));
    static Vector logVector = new Vector();
    static Vector activityVector = new Vector();
    static Vector musicDirVector = new Vector();
    static Vector musicCompleteVector = new Vector();
    static Vector audioDirVector = new Vector();
    static Vector audioCompleteVector = new Vector();
    static Vector videoDirVector = new Vector();
    static Vector videoCompleteVector = new Vector();
    static Vector photoDirVector = new Vector();
    static Vector photoCompleteVector = new Vector();
    static Vector actionsListVector = new Vector();
    
    static String playlistActions[] = { "-- playlist options --", "View", "Simple Mode", "Copy to MP3 Player", "Close" };
    static JComboBox artistImageTypeSelectBox = new JComboBox(new String[] { "Album Covers", "Saved Images", "Both", "No Image" });
    static JComboBox tracksLeftSelectBox = new JComboBox(new String[] { "\u221E", "0", "1", "2", "3", "4", "7", "10", "15", "20", "30" });
    static JComboBox playTypeSelectBox = new JComboBox(new String[] { "Random", "Sequential", "Loop Track" });
    static JComboBox tracksLeftActionSelectBox = new JComboBox(new String[] { "Just Stop", "Close eTrain", "Shutdown", "Back to Random" });
    static JComboBox setThemeSelectBox = new JComboBox(new String[] { "Nimbus", "Metal",  "GTK", "Motif" });
    static JComboBox playlistSelectBox = new JComboBox(new String[] { "" });
    static JComboBox viewPlaylistSelectBox = new JComboBox(new String[] { "" });
    static JComboBox computerVoiceSelectBox = new JComboBox(new String[] { "On", "Off" });
    static JComboBox libraryFontSizeSelectBox = new JComboBox(new String[] { "8", "9", "10", "11", "12", "13", "14", "15", "17", "20", "22", "24", "28", "35", "40", "50" });
    static JComboBox libraryFontSelectBox = new JComboBox(new String[] { "Navilu","Serif", "SansSerif", "Monospaced", "Dialog", "DialogInput" });
    static JComboBox libraryVisibleRowsSelectBox = new JComboBox(new String[] { "14", "17", "20", "25", "30", "35", "40", "45" });
    static JComboBox showLibraryOnStartupSelectBox = new JComboBox(new String[] { "Show", "Hide" });
    static JComboBox fullScreenSelectBox = new JComboBox(new String[] { "Normal", "Full Screen" });

    static JComboBox timeTravelSelectBox = new JComboBox(new String[] { "%", "10", "20", "30", "40", "50", "60", "70", "80", "90" });
    static JComboBox playlistActionSelectBox = new JComboBox(new String[] { "-- options --", "View", "Simple Mode", "Copy to MP3 Player", "Close" });
    static JComboBox ActionSelectBox = new JComboBox(actionsListVector);
    
    static JSlider progressBar = new JSlider(0, 200, 0);
    public static Random generator = new Random();
    static JProgressBar percentageBar = new JProgressBar(0, 100);
    static JSplitPane splitOptionsPane = new JSplitPane(1);
    public Map audioInfo;
    public static BasicController mainSoundPlayer = null;
    public static BasicController secondarySoundPlayer = null;
    long microseconds;
    static NumberFormat formatter = new DecimalFormat("00");
    
    static TextField searchBox = new TextField("", 10);
    static TextField themeTextBox = new TextField("", 7);
    static TextField MusicDirectoryTextBox = new TextField("", 20);
    static TextField AudioDirectoryTextBox = new TextField("", 20);
    static TextField VideoDirectoryTextBox = new TextField("", 20);
    static TextField PhotoDirectoryTextBox = new TextField("", 20);
    static TextField MP3PlayerDirectoryTextBox = new TextField("", 20);
    static TextField playlistVoiceCommandBox = new TextField("", 20);
    static TextField RepeatPlayNumTextBox;

    static final TextField addAlbumArtistNameBox = new TextField("", 12);
    static final TextField addAlbumCoverBox = new TextField("", 12);
    static final TextField addAlbumAlbumNameBox = new TextField("", 12);
    static final TextField addAlbumFrequencyBox = new TextField("2", 5);
    static final TextField addAlbumReleaseYearBox = new TextField("", 5);
    static final TextField addAlbumVoiceCommandBox = new TextField("play the album ", 20);
    static final TextField editID3TitleTextBox = new TextField("", 20);
    static final TextField editID3ArtistTextBox = new TextField("", 20);
    static final TextField editAlbumCoverBox = new TextField("", 12);
    static final TextField editID3AlbumTextBox = new TextField("", 20);
    static final TextField editAlbumFrequencyBox = new TextField("2", 5);
    static final TextField editAlbumReleaseYearBox = new TextField("", 5);
    static final TextField editAlbumVoiceCommandBox = new TextField("play the album ", 28);
    static final TextField newArtistVoiceCommandBox = new TextField("play  please", 20);
    static final TextField newArtistNameBox = new TextField("", 20);
    
    static JTextArea touchArtistsPanel = new JTextArea("ARTISTS");
    static JTextArea touchSongsPanel = new JTextArea("SONGS");
    static JTextArea lyricsDisplay;
    static JTextArea commandsInput;
    static JTextArea viewLogHistory = new JTextArea();
    static JTextArea currentPlaylistTextArea = new JTextArea();
    static JScrollPane lyricsPane;
    static TextArea lyricsInput = new TextArea(" ", 15, 40, 1);
    static String shortcutsText = "\n\n" +
                "Next Track = ALT + N\n" +
                "Stop = ALT + S\n" +
                "Options = ALT + O\n" +
                "Play/Pause = ALT + P\n" +
                "Expand Library = ALT + E\n" +
                "Collapse Library = ALT + C\n" +
                "\n";
    static String commandsText = "\n\n" +
                "open dvd please\n" +
                "open <name> playlist\n" +
                "open random music program\n" +
                "Play/Pause = ALT + P\n" +
                "Expand Library = ALT + E\n" +
                "Collapse Library = ALT + C\n" +
                "\n";
    static JTextArea shortcutsLabel = new JTextArea(shortcutsText);
    static JTextArea viewActivityTextArea = new JTextArea();
    
    static JScrollPane activityViewPane = new JScrollPane(viewActivityTextArea);
    static JScrollPane commandsInputPane;
    static JScrollPane logViewPane = new JScrollPane(viewLogHistory);
    
    static boolean getSecondLevelCommand = false;
    static boolean playDirectly = false;
    static boolean isPaused = false;
    static boolean isMute = false;
    static boolean checkSelected = false;
    static boolean albumCoverOverride = false;
    static JTabbedPane tabbedPaneLibrary = new JTabbedPane();
    static JTabbedPane tabbedFrontPanel = new JTabbedPane();
    static JTabbedPane tabbedPaneViewDatabase = new JTabbedPane();
    static JTable tableArtists, tableAlbums, tablePlaylists, tableEvents, tableCollage;
    static DefaultTableModel artistTableModel = new DefaultTableModel();
    static DefaultTableModel albumTableModel = new DefaultTableModel();
    static DefaultTableModel playlistTableModel = new DefaultTableModel();
    static DefaultTableModel eventTableModel = new DefaultTableModel();
    static DefaultTableModel collageTableModel = new DefaultTableModel();
    static JScrollPane scrollViewMusicLibrary,scrollViewTouchLibrary,scrollViewMusicCategory,scrollViewPlaylist,scrollViewLast25,scrollViewAudioLibrary,scrollViewVideoLibrary,scrollViewPhotoLibrary,scrollViewArtistDatabase,scrollViewAlbumDatabase,
    			scrollViewPlaylistDatabase,scrollViewEvents,scrollViewCollageDatabase,scrollViewMP3Player,scrollTouchMusic,scrollSearch;
  
    static JLayeredPane layeredPane = new JLayeredPane();
    static Box horizPlaylistVoiceRow = new Box(0);
    static WindowAdapter frameCloser = null;
    
    //Volume Slider stuff
    static final int FPS_MIN = 1;
    static final int FPS_MAX = 100;
    static final int FPS_INIT = 50;    //initial value
    

    /* Handles JTree clicking algorithms (single, double, and right) */
    class NodeSelectionListener extends MouseAdapter
    {
        JTree tree;

        public void mouseClicked(MouseEvent e)
        {
            int x = e.getX();
            int y = e.getY();
            int getrow = tree.getRowForLocation(x, y);
            TreePath path = tree.getPathForRow(getrow);
            if(path == null) return;
            
            CheckNode node = (CheckNode)path.getLastPathComponent();
            if(node.getSelectionMode() == 4)
            {
		    if(e.getButton() == 3) { eFunctions.printActivity("Right Click"); return; }
  
		// Click on Photo JTree
		if(e.getClickCount() == 1 && (tree.getSelectionPath().getLastPathComponent().toString().contains(".jpg") || tree.getSelectionPath().getLastPathComponent().toString().contains(".gif")))
                {	if (!tree.getSelectionPath().getLastPathComponent().toString().contains(File.separator)) {
			System.out.println("photo: " + tree.getSelectionPath().getParentPath().toString().replace("[", "").replace("]", "").replace(", ", "") + File.separator + tree.getSelectionPath().getLastPathComponent().toString());
			Sweetdream.showPicture("Photo Tree", tree.getSelectionPath().getParentPath().toString().replace("[", "").replace("]", "").replace(", ", "") + File.separator + tree.getSelectionPath().getLastPathComponent().toString(), "", "");
			boxLeftside.setVisible(false);
			photoLabel.setVisible(true);
			}
		}
		
               else if(e.getClickCount() == 1 && !Sweetdream.frame.isVisible() && Sweetdream.tree_touch.isCollapsed(Sweetdream.tree_touch.getMinSelectionRow()))
			Sweetdream.tree_touch.expandRow(Sweetdream.tree_touch.getMinSelectionRow());
                
                if(e.getClickCount() == 2 && !tree.getSelectionPath().getLastPathComponent().toString().contains(File.separator))
                {
                    if(Sweetdream.tabbedPaneLibrary.getTitleAt(Sweetdream.tabbedPaneLibrary.getSelectedIndex()).contains("Music"))
                    {
                        eFunctions.reset();
                        Sweetdream.bypassFrequency = "yes";
                        Sweetdream.playDirectly = true;
                        Sweetdream.nextTrack("Double-clicked row in Library");
                    } 
                    else if(Sweetdream.tabbedPaneLibrary.getTitleAt(Sweetdream.tabbedPaneLibrary.getSelectedIndex()).contains("Video"))
                    {
                        eFunctions.reset();
                        String videoToPlay1 = tree.getSelectionPath().getParentPath().toString().replace("[", "").replace("]", "").replace(", ", "");
                        String videoToPlay0 = VideoDirectoryPath;
                        String videoToPlay2 = tree.getSelectionPath().getLastPathComponent().toString();
                        String videoToPlay = videoToPlay1 + File.separator + videoToPlay2;
                        eFunctions.printActivity("Video to play: " + videoToPlay);
                        if(videoToPlay.lastIndexOf(".") == videoToPlay.length() - 4)
                        {
                            eFunctions.launchMovie(videoToPlay,"override random starting position");
                        }
                    } else
                    if(Sweetdream.tabbedPaneLibrary.getTitleAt(Sweetdream.tabbedPaneLibrary.getSelectedIndex()).contains("Audio"))
                    {
                        eFunctions.reset();
                        Sweetdream.bypassFrequency = "yes";
                        Sweetdream.playDirectly = true;
                        Sweetdream.nextTrack("Double-clicked row in Audio Library");
                    }
                } 

		else if(e.getClickCount() == 1 && getrow != 0)
                {
                    if(tree.isCollapsed(tree.getMinSelectionRow()))
			    tree.expandRow(tree.getMinSelectionRow());
		    else {
			boolean isSelected = !node.isSelected();
			node.setSelected(isSelected);
		    }
                } else if(e.getClickCount() == 1)
                {
                    eFunctions.printActivity("node.getParent(): " + node.getParent());
                    boolean isSelected = !node.isSelected();
                    node.setSelected(isSelected);
                }
            }
            ((DefaultTreeModel)tree.getModel()).nodeChanged(node);
            if(getrow == 0)
            {
                tree.revalidate();
                tree.repaint();
            }
        }

        NodeSelectionListener(JTree tree)
        {
            super();
            this.tree = tree;
        }
    }

    
    static class LibraryPopup extends MouseAdapter implements ActionListener
    {
        JTree theTree;
        private JPopupMenu folderMusicMenu,fileMenu,folderMenu,lyricsMenu;
        private String whichTree = "";
        JMenu submenuMoods;

	    public void mouseEntered(MouseEvent e)
            {
            	collapseAfterPlayFinish = false;
                scrollViewMusicLibrary.setBorder(new LineBorder(Color.white, 2));      
            }
            public void mouseExited(MouseEvent e)
            {
            	collapseAfterPlayFinish = true;
                scrollViewMusicLibrary.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        public void mousePressed(MouseEvent e)
        {
            theTree = eFunctions.getSelectedTree();
            if(theTree.getPathForLocation(e.getX(), e.getY()) == null) return;
	    
			if(SwingUtilities.isRightMouseButton(e)) {
				if(theTree.getPathForLocation(e.getX(), e.getY()).getLastPathComponent().toString().contains(File.separator))
					displayFolderMenu(e,theTree);
				else
					displayFileMenu(e,theTree);
			}
        }

        private void displayFileMenu(MouseEvent e, JTree theTree)
        {
       //     if(SwingUtilities.isRightMouseButton(e))
       //     {
		    if (tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Music"))
			    fileMusicMenu.show(e.getComponent(), e.getX(), e.getY());
		    else if (tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Audio"))
			    fileMusicMenu.show(e.getComponent(), e.getX(), e.getY());
		    else
			    fileMenu.show(e.getComponent(), e.getX(), e.getY());

                theTree.setSelectionRow(theTree.getRowForPath(theTree.getPathForLocation(e.getX(), e.getY())));
         //   }
        }

        private void displayFolderMenu(MouseEvent e, JTree theTree)
        {
           //  if(SwingUtilities.isRightMouseButton(e))
           //  {
		    if (tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Music"))
			    folderMusicMenu.show(e.getComponent(), e.getX(), e.getY());
		    else 
			    folderMenu.show(e.getComponent(), e.getX(), e.getY());
		    
                theTree.setSelectionRow(theTree.getRowForPath(theTree.getPathForLocation(e.getX(), e.getY())));
            // }
        }

        public void actionPerformed(ActionEvent e)
        {
    
	    JMenuItem source = (JMenuItem)(e.getSource());
	    String s = source.getText().trim();
	    
	  //  itemLibraryFile1.addMouseListener(new AddToPlaylistPopup());
            JTree tempTree = eFunctions.getSelectedTree();
            
	   if(s.equals("Open Folder")) {
                
		String folderPath1 = "",folderPath2 = "";
		
		if(tempTree.getMinSelectionRow() != 0)
			folderPath1 = tempTree.getSelectionPath().getParentPath().toString().replace("[", "").replace("]", "").replace(", ", "");
                folderPath2 = tempTree.getSelectionPath().getLastPathComponent().toString();
                eFunctions.printActivity("folderPath: " + folderPath1 + folderPath2);
                eFunctions.openBrowsingWindow(folderPath1 + folderPath2);
             //   itemLibraryFolder2.setSelected(false);
            }
		    
	   else if(s.equals("Get Lyrics")) {
                String url = "http://www.google.com/search?tab=iw&hl=en&q=lyrics " + Sweetdream.tree.getSelectionPath().getLastPathComponent().toString().substring(4);
                eFunctions.openBrowser(url);
               // itemLibraryDeleteFile.setSelected(false);
            } 
	    else if(s.equals("Edit Information")) {

		    try {
				    
		String filenameEdited = tempTree.getSelectionPath().getParentPath().toString().replace("]", "").replace("[", "").replace("Music, ","Music").replace(", /","/").replace(", \\","\\") + File.separator + tempTree.getSelectionPath().getLastPathComponent().toString();
		
		
		if(!filenameEdited.contains("ogg") && !filenameEdited.contains("oga") && !filenameEdited.contains("wav") && !filenameEdited.contains("flac"))
			filenameEdited = filenameEdited + ".mp3";
			System.out.println("file whose ID3 is edited: " + filenameEdited);

		    editID3TitleTextBox.setText(eFunctions.getID3Info(filenameEdited,"Title"));
		    editID3ArtistTextBox.setText(eFunctions.getID3Info(filenameEdited,"Artist"));
		    editID3AlbumTextBox.setText(eFunctions.getID3Info(filenameEdited,"Album"));
		  } catch(ID3Exception ioe) { ioe.printStackTrace(System.out); }
		    
		    editID3Frame.setVisible(true); 
		//    itemLibraryEditInfo.setSelected(false);
	    }
	    else if(s.equals("Delete File")) {
                int answer = JOptionPane.showConfirmDialog(new JFrame(), "This will delete the file from the hard drive. Continue?");
                if(answer == 0)
                {
			String extension = "";
			String thefilename = tempTree.getSelectionPath().getLastPathComponent().toString(); 
			if(thefilename.lastIndexOf(".") == thefilename.length() - 4) extension = "";  // NON-mp3 file, so extension is already attached in the JTree
			else extension = ".mp3"; // No extension found, so we're playing an mp3
			
                    eFunctions.deleteFile(tempTree.getSelectionPath().getParentPath().toString().replace("[", "").replace("]", "").replace(", ", "") + File.separator + thefilename + extension, "Music");
		//    itemLibraryDeleteFile.setSelected(false);
                }
            }
	    else if(s.equals("Add Artist Image")) {
                String url = "http://www.google.com/images?hl=en&q=" + Sweetdream.tree.getSelectionPath().getLastPathComponent().toString().substring(1);
                eFunctions.openBrowser(url);
              //  itemLibraryFolder3.setSelected(false);
		
            } 
            
            
            else if(s.equals("Add this artist to Database") && tree.getSelectionPath().getLastPathComponent().toString().contains(File.separator)) {
            	newArtistNameBox.setText(tree.getSelectionPath().getLastPathComponent().toString().substring(1));
            	newArtistVoiceCommandBox.setText("play " + tree.getSelectionPath().getLastPathComponent().toString().substring(1).toLowerCase() + " please");
                newArtistFrame.setVisible(true);
		
            } else if(tree.getSelectionPath().getLastPathComponent().toString().contains(File.separator)) {   // Must be assigning a Mood to an Artist   
		    // Add to Playlist menu has its own class 
		    
		    String theMoodString = "";
		    Vector theMoodVector = new Vector();
		    eFunctions.printActivity("Selected mood: " + s);
		    eFunctions.printActivity("Artist to Add: " + Sweetdream.tree.getSelectionPath().getLastPathComponent().toString());
		    theMoodString = s;
		    if(Sweetdream.tree.getMinSelectionRow() < 0 || !Sweetdream.tree.getSelectionPath().getLastPathComponent().toString().contains(File.separator))
		    {
			    JOptionPane.showMessageDialog(Sweetdream.frame, "Select a folder to add to a Mood.");
			    return;
		    }
		    try {
			    theMoodVector = eFunctions.getMoodVector(theMoodString);
		    }
		    catch(SQLException sq) { sq.printStackTrace(); }
	    
		    if(theMoodString.equals("- New Mood -"))
			    eFunctions.saveMood(Sweetdream.tree.getSelectionPath().getLastPathComponent().toString(), "");
		    else
			    eFunctions.saveMood(Sweetdream.tree.getSelectionPath().getLastPathComponent().toString(), theMoodString);
            
		    eFunctions.refreshTree("Moods");   
	    }
	    else {   // Must be adding a track to a playlist
		    String thePlaylistString = "";
		    Vector thePlaylistVector = new Vector();
		    eFunctions.printActivity("Selected playlist: " + s);
		    eFunctions.printActivity("Track to Add: " + Sweetdream.tree.getSelectionPath().getLastPathComponent().toString());
		    thePlaylistString = s;
		    if(Sweetdream.tree.getMinSelectionRow() < 0 || Sweetdream.tree.getSelectionPath().getLastPathComponent().toString().contains(File.separator))
		    {
			    JOptionPane.showMessageDialog(Sweetdream.frame, "Select a track to add to a Playlist.");
			    return;
		    }
		    try
		    {
			    thePlaylistVector = eFunctions.getPlaylistVector(thePlaylistString);
		    
			    if(thePlaylistString.equals("- New Playlist -"))
			    	    eFunctions.savePlaylist(thePlaylistVector, "");
			    else
			    	    eFunctions.savePlaylist(thePlaylistVector, thePlaylistString);
			    eFunctions.displayPlaylist(thePlaylistString);
		    }
		    catch(SQLException sq) { sq.printStackTrace(); }
	    }
        }

        public LibraryPopup()
        {
            super();
            JTree theTree = null;
            fileMusicMenu = new JPopupMenu();
            folderMusicMenu = new JPopupMenu();
            fileMenu = new JPopupMenu();
            folderMenu = new JPopupMenu();
            lyricsMenu = new JPopupMenu();
            JMenuItem item0;

            JMenuItem addArtist = new JMenuItem("Add this artist to Database");
            JMenuItem itemLibraryEditInfo = new JMenuItem("Edit Information ");
            JMenuItem itemLibraryDeleteFile = new JMenuItem("Delete File ");
            JMenuItem itemLibraryFolder2 = new JMenuItem("Open Folder ");
            JMenuItem itemLibraryFolder4 = new JMenuItem("Open Folder ");
            JMenuItem itemLibraryFolder3 = new JMenuItem("Add Artist Image ");
		 
            submenuMoods = new JMenu("Assign Mood: ");
            submenuPlaylists = new JMenu("Add to Playlist: ");
		 
            itemLibraryEditInfo.addActionListener(this);
            itemLibraryDeleteFile.addActionListener(this);
	    
            itemLibraryFolder2.addActionListener(this);
            itemLibraryFolder3.addActionListener(this);
            addArtist.addActionListener(this);

			fileMenu.add(itemLibraryDeleteFile);
			folderMenu.add(itemLibraryFolder4);
			
			fileMusicMenu.add(submenuPlaylists);
			fileMusicMenu.add(itemLibraryEditInfo);
            fileMusicMenu.add(itemLibraryDeleteFile);

            folderMusicMenu.add(addArtist);
            folderMusicMenu.add(submenuMoods);
            folderMusicMenu.add(itemLibraryFolder2);
            folderMusicMenu.add(itemLibraryFolder3);
	   
            /* Populate Moods submenu */
            Vector temp = new Vector();
            try {
                temp = eFunctions.getMoodsArray();
            } catch(SQLException sq) { sq.printStackTrace(); }
	    
            for(int i = 0; i < temp.size(); i++) {
                item0 = new JMenuItem(temp.get(i).toString());
                item0.addActionListener(this);
                submenuMoods.add(item0);
            }
            submenuMoods.addSeparator();
            item0 = new JMenuItem("- New Mood -");
            item0.addActionListener(this);
            submenuMoods.add(item0);
        }
    }
   
  
      static class AddToPlaylistPopup extends MouseAdapter implements ActionListener {

        static JMenuItem item0;
        private JPopupMenu theMenu;

        public void mousePressed(MouseEvent e)
        {
            theMenu.show(e.getComponent(), e.getX(), e.getY());
        }

        public void actionPerformed(ActionEvent e)
        {
            String thePlaylistString = "";
            Vector thePlaylistVector = new Vector();
            eFunctions.printActivity("Selected playlist: " + e.getActionCommand());
            thePlaylistString = e.getActionCommand();
            if((Sweetdream.tree.getMinSelectionRow() < 0 || Sweetdream.tree.getSelectionPath().getLastPathComponent().toString().contains(File.separator)) && Sweetdream.currentSongPath == "")
            {
                JOptionPane.showMessageDialog(Sweetdream.frame, "Select an audio file in the library to add to a playlist.");
                return;
            }
            try
            {
                thePlaylistVector = eFunctions.getPlaylistVector(thePlaylistString);    
	    
            if(thePlaylistString.equals("- New Playlist -"))
            	eFunctions.savePlaylist(thePlaylistVector, "");
            else {
               int answer = JOptionPane.showConfirmDialog(new JFrame(), "Add track to playlist \"" + thePlaylistString + "\"?", "Update Playlist", JOptionPane.YES_NO_OPTION);
               if(answer == 0) {
		   eFunctions.savePlaylist(thePlaylistVector, thePlaylistString);
		   try { Sweetdream.currentPlaylistVector = eFunctions.getPlaylistVector(thePlaylistString); } catch(SQLException sq) { sq.printStackTrace(); }
	       }
	       
	       
	    }
	    }
            catch(SQLException sq) { sq.printStackTrace(); }
        }

        public AddToPlaylistPopup()
        {
            theMenu = new JPopupMenu();
            Vector temp = new Vector();
            try
            {
                temp = eFunctions.getPlaylistsArray();
            }
            catch(SQLException sq) { sq.printStackTrace(); }
	    
            for(int i = 0; i < temp.size(); i++)
            {
                item0 = new JMenuItem(temp.get(i).toString());
                item0.addActionListener(this);
                theMenu.add(item0);
            }

            theMenu.addSeparator();
            item0 = new JMenuItem("- New Playlist -");
            item0.addActionListener(this);
            theMenu.add(item0);
        }
    }
    


    static class ImagePopup extends MouseAdapter implements ActionListener
    {
        static JCheckBoxMenuItem item1,item2,item3;
        private JPopupMenu theMenu;

        public void mousePressed(MouseEvent e)
        {
            if(SwingUtilities.isRightMouseButton(e))
            {
                theMenu.show(e.getComponent(), e.getX(), e.getY());
                item1.setSelected(false);
                item2.setSelected(false);
                item3.setSelected(false);
            }
        }

        public void actionPerformed(ActionEvent e)
        {
            javax.swing.MenuElement elements[] = theMenu.getSubElements();
            JCheckBoxMenuItem item1 = (JCheckBoxMenuItem)elements[0];
            JCheckBoxMenuItem item2 = (JCheckBoxMenuItem)elements[1];
            JCheckBoxMenuItem item3 = (JCheckBoxMenuItem)elements[2];

            if(item1.isSelected())
            {
                Sweetdream.imageFrame.setVisible(true);
                Sweetdream.frame.setVisible(false);
                Sweetdream.imageFrame.pack();
            }
	    else if(item2.isSelected())
            {
                String url = "http://www.google.com/images?hl=en&q=" + Sweetdream.currentPlayingArtist.replace(" ", "+");
                eFunctions.openBrowser(url);
                item2.setSelected(false);
            }
	    else if(item3.isSelected())
            {
                eFunctions.openBrowsingWindow("images/artists/" + Sweetdream.currentPlayingArtist);
            }
        }

        public ImagePopup()
        {
            theMenu = new JPopupMenu();
            item1 = new JCheckBoxMenuItem("Floating Image ", false);
            item2 = new JCheckBoxMenuItem("Add new artist image ", false);
            item3 = new JCheckBoxMenuItem("Edit artist images ", false);
            item1.addActionListener(this);
            item2.addActionListener(this);
            item3.addActionListener(this);
            theMenu.add(item1);
            theMenu.add(item2);
            theMenu.add(item3);
        }
    }
    
    public Sweetdream()
    {
        fileToCopy = null;
        byteslength = -1;
        total = -1L;
        progress = -1F;
        //font10 = new Font("Lato Regular", 0, 11);
        font12 = new Font("Dialog", 0, 12);
        font15 = new Font("Dialog", 0, 15);
        dimension100x20 = new Dimension(100, 20);
        audioInfo = null;
        m_fileChooser.setMultiSelectionEnabled(true);
        
        playBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if(Sweetdream.playBtn.getIcon() == Sweetdream.playButtonIcon)
                {
                    Sweetdream.playBtn.setIcon(Sweetdream.pauseButtonIcon);
                    if(Sweetdream.progressBar.getValue() > 2)
                    {
                        try
                        {
                            Sweetdream.isPaused = false;
                            Sweetdream.mainSoundPlayer.resume();
                        }
                        catch(BasicPlayerException ex) { ex.printStackTrace(); }
                    }
                else if(Sweetdream.tree.getSelectionPath() == null)
                    	Sweetdream.nextTrack("Play Button: no song was selected");
                else
                    {
                        eFunctions.reset();
                        Sweetdream.playDirectly = true;
                        Sweetdream.nextTrack("Play button");
                    }
                }
            else if(Sweetdream.playBtn.getIcon() == Sweetdream.pauseButtonIcon)
                {
                    Sweetdream.playBtn.setIcon(Sweetdream.playButtonIcon);
		    //Pause eTrain playback
		    pause_timer.start();
                }
            }
        });

	ScrollDownBtn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {	    
		    JScrollBar verticalScrollBar = scrollViewMusicLibrary.getVerticalScrollBar();
		    verticalScrollBar.setValue(verticalScrollBar.getValue()+150);
		}
        });
	ScrollUpBtn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {	    
		    JScrollBar verticalScrollBar = scrollViewMusicLibrary.getVerticalScrollBar();
		    verticalScrollBar.setValue(verticalScrollBar.getValue()-150);
		}
        });
	
        stopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	   
                eFunctions.reset();
                statusDisplay.setText( numFolders + " artists, " + numFiles + " songs");
                
                int w = 100;
      int h = 100;
      int pix[] = new int[w * h];
      int index = 0;
      for (int y = 0; y < h; y++) {
          int red = (y * 255) / (h - 1);
          for (int x = 0; x < w; x++) {
              int blue = (x * 255) / (w - 1);
              pix[index++] = (255 << 24) | (red << 16) | blue;  // shifts the bits to the left? so multiply by 24, by 16!
          }
      }
      /* DEBUG: Display all available mixers
           javax.sound.sampled.Mixer.Info mixerInfo[] = AudioSystem.getMixerInfo();
           eFunctions.printActivity("Available mixers:");
           for(int cnt = 0; cnt < mixerInfo.length; cnt++)
				eFunctions.printActivity(mixerInfo[cnt].getName());
	  */
            }
            
        });
	 
        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                eFunctions.reset();
                Sweetdream.nextTrack("next button");
            }
        });
	
        prevBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                eFunctions.reset();
                eFunctions.printActivity("Prev Btn: prevBtnClicks: " + prevBtnClicks);
                eFunctions.printActivity("tracksPlayedVector.size(): " + tracksPlayedVector.size());
                eFunctions.printActivity("tracksPlayedVector.capacity(): " + tracksPlayedVector.capacity());
                Sweetdream.prevBtnClicks++;
                Sweetdream.prevBtnClicks %= Integer.parseInt(tracksPlayedVectorSize);
                String previousSongPath = "",filePath = "";
                if(tracksPlayedVector.size() > prevBtnClicks)
                {              
                    previousSongPath = tracksPlayedVector.get(tracksPlayedVector.size() - prevBtnClicks).toString();
                } else if(tracksPlayedVector.size() == prevBtnClicks) {
                	progressBar.setValue(0);
                	eFunctions.printActivity("asdf");
                	filePath = musicCompleteVector.get(musicFileIndex).toString();
                	try { mainSoundPlayer.open(openFile(MusicDirectoryPath + filePath));
                	      mainSoundPlayer.play(); } catch(BasicPlayerException e1) { }
                }
                else return;
                
                eFunctions.printActivity("tracksPlayedVector.size(): " + tracksPlayedVector.size());
                eFunctions.printActivity("previousSongPath: " + previousSongPath);
                 
                try
                {
                    eFunctions.displayAlbumImage(Sweetdream.currentPlayingAlbum, "try artist image after");
                }
                catch(SQLException sq) { sq.printStackTrace(); }
                try
                {
                    Sweetdream.mainSoundPlayer.open(Sweetdream.openFile(Sweetdream.MusicDirectoryPath + previousSongPath));
                    Sweetdream.mainSoundPlayer.play();
                }
                catch(BasicPlayerException e1) { }
            }
        });
	
        optionsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.optionsFrame.setVisible(true);
		Sweetdream.optionsFrame.toFront();  // bring Options window to front (otherwise would stay behind if it was already open)
		Sweetdream.optionsFrame.repaint();
		if (tree_options.getLastSelectedPathComponent().toString().contains("Save / Exit")) Sweetdream.tree_options.setSelectionRow(1);		
		// try { eFunctions.retrieveVariables(); } catch(SQLException ex2) { ex2.printStackTrace(); 
            }
        });
	
	showLibraryBtn.addMouseListener(new MouseListener() {
			public void mouseEntered(MouseEvent e) { eFunctions.toggleShowLibrary(""); }
			public void mouseExited(MouseEvent e) {  }
			public void mouseClicked(MouseEvent e) {  }
			public void mousePressed(MouseEvent e) {  }
			public void mouseReleased(MouseEvent e) {  }
	});
	/*
        showLibraryBtn.addActionListener(new ActionListener() {
	    
            public void actionPerformed(ActionEvent e)
            {
		    eFunctions.toggleShowLibrary(""); // leaving blank parameter will toggle show/hide
            }
        });
	*/
        logBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.saveLogDisplay.setText("Good day to you.");
                if(Sweetdream.logViewFrame.isVisible())
			Sweetdream.logViewFrame.setVisible(false);
                else
			Sweetdream.logViewFrame.setVisible(true);
            }
	});
        eventsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.optionsFrame.setVisible(true);
                Sweetdream.tree_options.setSelectionRow(4);
                Sweetdream.tabbedPaneViewDatabase.setSelectedIndex(3);
            }
        });
        saveLogBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                File logDir = new File("logs");
                if(!logDir.exists())
                {
                    eFunctions.printActivity("Creating logs folder.");
                    logDir.mkdir();
                }
                Sweetdream.saveLogDisplay.setText("Thank you, log saved.");
                String s = DateFormat.getDateInstance().format(new Date());
                try
                {
                    try
                    {
                        eFunctions.saveTxtFile(new File("logs" + File.separator + "log" + s + ".txt"), Sweetdream.viewLogHistory.getText());
                    }
                    catch(FileNotFoundException eqq) { eqq.printStackTrace(); }
                }
                catch(IOException eqq) { eqq.printStackTrace(); }
            }
        });
        collageDisplayBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if(!eCollage.collageVideoFrame.isVisible())
                {
                    eCollage.collageVideoFrame.setVisible(true);
                    Sweetdream.frame.setVisible(false);
                    eCollage.collageVideoPanel.setVisible(true);
                    eCollage.collageVideoFrame.setExtendedState(6);
                }
            }
        });
        touchDisplayBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if(!Sweetdream.touchFrame.isVisible())
                {
                    Sweetdream.touchFrame.setVisible(true);
                    Sweetdream.frame.setVisible(false);
                    Sweetdream.touchFrame.setExtendedState(6);
                }
            }
        });
        nextMinuteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.nextMinutePushes++;
                int totalBytes = ((Integer)audioInfo.get("audio.length.bytes")).intValue();
                int incrementBytes = totalBytes / 10;
                try
                {
                    Sweetdream.mainSoundPlayer.seek(incrementBytes * Sweetdream.nextMinutePushes);
                }
                catch(BasicPlayerException eqq) { eqq.printStackTrace(); }
            }
        });
	
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                eFunctions.refreshTree("All");
            }
        });
        
        muteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	    
            	if(Sweetdream.muteBtn.getIcon() == Sweetdream.muteIcon)
                {
                    Sweetdream.muteBtn.setIcon(unmuteIcon);
		    Sweetdream.isMute = true;
                    try
                    {
                        Sweetdream.mainSoundPlayer.setGain(0.0D);
                    }
                    catch(BasicPlayerException ex) { ex.printStackTrace(); }
                }
                else {
                	Sweetdream.muteBtn.setIcon(muteIcon);
		    Sweetdream.isMute = false;
                    try
                    {
                        Sweetdream.mainSoundPlayer.setGain(0.5D);
                    }
                    catch(BasicPlayerException ex) { ex.printStackTrace(); }
                }
            	    

            }
        });

        
	 saveMoodBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
             eFunctions.saveMood("",""); // Save to a new mood
            }
         });
         
         
        toggleFrequencyFontBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
             if (enableFrequencyFont) enableFrequencyFont = false; 
             else { enableFrequencyFont = true; }
             eFunctions.refreshTree("Music");
            }
         });
	
        expandCollapseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                String whichTab = "";
                if(tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Music")) whichTab = "Music";
                if(tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Moods")) whichTab = "Moods";
                if(tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Audio")) whichTab = "Audio";
                if(tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Video")) whichTab = "Video";
                if(tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Photo")) whichTab = "Photo";
                
                if(Sweetdream.expandCollapseBtn.getIcon() == Sweetdream.plusIcon)
			eFunctions.expandTree(whichTab);
                else
                	eFunctions.collapseTree(whichTab);
            }
        });
	
        mp3playerAddTracksBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                File defaultDir = new File(Sweetdream.MusicDirectoryPath);
                Sweetdream.m_fileChooser.setCurrentDirectory(defaultDir);
                int retval = Sweetdream.m_fileChooser.showOpenDialog(Sweetdream.frame);
                if(retval == 0)
                {
                    File file[] = Sweetdream.m_fileChooser.getSelectedFiles();
                    if(file != null)
                    {
                        for(int i = 0; i < file.length; i++)
                        {
                            Sweetdream.statusDisplay.setText("Transferring to MUVO...");
                            eFunctions.printActivity(file[i].toString());
                            CopyFile asdf;
                            try
                            {
                                asdf = new CopyFile(file[i].toString(), Sweetdream.MP3PlayerDirectoryPath, 1);
                            }
                            catch(IOException ioe) { ioe.printStackTrace(System.out); }
                        }
                        Sweetdream.tree_mp3player.setSelectionRow(0);
                    }
                }
            }
        });
	

        getMusicDirectoryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.MusicDirectoryTextBox.setText(eFunctions.browseDirectory());
            }
        });
        getAudioDirectoryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.AudioDirectoryTextBox.setText(eFunctions.browseDirectory());
            }
        });
        getVideoDirectoryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.VideoDirectoryTextBox.setText(eFunctions.browseDirectory());
            }
        });
        getPhotoDirectoryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.PhotoDirectoryTextBox.setText(eFunctions.browseDirectory());
            }
        });
        getMP3PlayerDirectoryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.MP3PlayerDirectoryTextBox.setText(eFunctions.browseDirectory());
            }
        });
	
        addAlbumCoverBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                File defaultDir = new File("images/covers");
                Sweetdream.m_fileChooser.setCurrentDirectory(defaultDir);
                int retval = Sweetdream.m_fileChooser.showOpenDialog(Sweetdream.frame);
                if(retval == 0) {
                    File file = Sweetdream.m_fileChooser.getSelectedFile();
                    if(file != null)
                    {
                        String filename = file.toString();
                        Sweetdream.addAlbumCoverBox.setText(filename.substring(filename.lastIndexOf(File.separator)));
                        Sweetdream.editAlbumCoverBox.setText(filename.substring(filename.lastIndexOf(File.separator)));
                        Sweetdream.addAlbumFrame.setVisible(true);
                    }
                }
            }
        });
	
        editAlbumCoverBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                File defaultDir = new File("images/covers");
                Sweetdream.m_fileChooser.setCurrentDirectory(defaultDir);
                int retval = Sweetdream.m_fileChooser.showOpenDialog(Sweetdream.frame);
                if(retval == 0)
                {
                    File file = Sweetdream.m_fileChooser.getSelectedFile();
                    if(file != null)
                    {
                        Sweetdream.editAlbumCoverBox.setText(file.toString());
                        Sweetdream.editID3Frame.setVisible(true);
                    }
                }
            }
        });
	
        addAlbumSubmitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    Sweetdream.db.update("INSERT INTO albums(artistName,albumName,coverImage,releaseYear,frequencyRating,voiceCommand) VALUES('"
			    	+ Sweetdream.addAlbumArtistNameBox.getText() + "','" + Sweetdream.addAlbumAlbumNameBox.getText() + "','" + Sweetdream.addAlbumCoverBox.getText() + "','" + Sweetdream.addAlbumReleaseYearBox.getText() + "','" + Sweetdream.addAlbumFrequencyBox.getText() + "','" + Sweetdream.addAlbumVoiceCommandBox.getText() + "')");
                }
                catch(SQLException ex2) { ex2.printStackTrace(); }
                Sweetdream.addAlbumFrame.setVisible(false);
                Sweetdream.showPicture("coverLabel", Sweetdream.addAlbumCoverBox.getText(), Sweetdream.addAlbumAlbumNameBox.getText(), Sweetdream.addAlbumReleaseYearBox.getText());

                if(!Sweetdream.newArtistLabel.getText().equals(""))
                {
                    File qFile = new File("commands.gram");
                    if(qFile.exists())
                    {
                        Sweetdream.commandsInput.setText(eFunctions.openTxtFile(qFile));
                    }
                }
            }
        });
	
        updateID3Btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
		try {
		    String filenameEdited = tree.getSelectionPath().getParentPath().toString().replace("]", "").replace("[", "").replace("Music, ","Music").replace(", /","/").replace(", \\","\\") + File.separator + tree.getSelectionPath().getLastPathComponent().toString();
		    System.out.println("filenameEdited: " + filenameEdited);
		    if(!filenameEdited.contains("ogg") && !filenameEdited.contains("oga") && !filenameEdited.contains("wav") && !filenameEdited.contains("flac"))
			  filenameEdited = filenameEdited + ".mp3";
		
		    eFunctions.editID3(filenameEdited,Sweetdream.editID3ArtistTextBox.getText(),Sweetdream.editID3TitleTextBox.getText(),Sweetdream.editID3AlbumTextBox.getText());
			    }
                            catch(ID3Exception ioe) { ioe.printStackTrace(System.out); }
                Sweetdream.editID3Frame.setVisible(false);
            }
        });
	
        addAlbumBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Sweetdream.addAlbumAlbumNameBox.setText("");
                Sweetdream.addAlbumVoiceCommandBox.setText("play the album ");
                Sweetdream.addAlbumReleaseYearBox.setText("");
                Sweetdream.addAlbumCoverBox.setText("");
                Sweetdream.addAlbumFrame.setVisible(true);
            }
        });
	
        playlistSetVoiceCommandBtn.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e)
             {
	      try {
                  Sweetdream.db.update("UPDATE playlists SET voicecommand = '" + playlistVoiceCommandBox.getText() + "' WHERE playlistname = '" + Sweetdream.viewPlaylistSelectBox.getSelectedItem().toString() + "'");
              } catch(SQLException ex2) { ex2.printStackTrace(); }
             }
	  	});
	  
        viewCommandsTxtBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if(System.getProperty("os.name").startsWith("Win"))
                {
                    try {
                        Runtime.getRuntime().exec(new String[] {
                            "C:\\WINDOWS\\notepad.exe", "commands.gram"
                        });
                    }
                    catch(IOException exc) { exc.printStackTrace(); }
                    
                } else
                {
                    try {
                        Runtime.getRuntime().exec("sudo gedit commands.gram");
                    }
                    catch(IOException exc) { exc.printStackTrace(); }
                }
            }
	});

	downloadUpdateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
               int answer = JOptionPane.showConfirmDialog(new JFrame(), "This will download the latest version of eTrain. Continue?");
               if(answer == 0)
		   eFunctions.downloadFile("www/etrain/latest", "etrain.jar", "bin/etrain.jar");
            }
        });

        mp3playerDeleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if(Sweetdream.tree_mp3player.getSelectionPath() == null)
			JOptionPane.showMessageDialog(new JFrame(), "Please select a file on the mp3 player to delete.");
                else
                {
                    int file[] = Sweetdream.tree_mp3player.getSelectionRows();
                    if(file != null)
                    {
                        for(int i = 0; i < file.length; i++)
				eFunctions.deleteFile(Sweetdream.MP3PlayerDirectoryPath + Sweetdream.tree_mp3player.getPathForRow(file[i]).getLastPathComponent().toString(), null);
                    }
                    eFunctions.refreshTree("mp3player");
                }
            }
        });
	
        newArtistSubmitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    Sweetdream.db.update("INSERT INTO artists(artistName,voiceCommand,TIMESPLAYED) VALUES('" + StringEscapeUtils.escapeSql(Sweetdream.newArtistNameBox.getText()) + "','" + newArtistVoiceCommandBox.getText().toLowerCase() + "','0')");
                    Sweetdream.newArtistFrame.setVisible(false);
                }
                catch(SQLException sq) { sq.printStackTrace(); }
            }
        });
	
        lyricsSubmitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try {
                    eFunctions.expandTree("Music");
                    File newTxtFile = new File("lyrics/" + Sweetdream.tree.getPathForRow(Sweetdream.musicFileIndex).getLastPathComponent().toString() + ".txt");
                    eFunctions.printActivity("Lyric Text File Created. = " + newTxtFile.toString());
                    eFunctions.saveTxtFile(newTxtFile, Sweetdream.lyricsInput.getText());
                    Sweetdream.optionsFrame.setVisible(false);
                }
                catch(FileNotFoundException ee) { }
                catch(IOException ee) { }
                File qFile = new File("lyrics/" + Sweetdream.tree.getPathForRow(Sweetdream.musicFileIndex).getLastPathComponent().toString() + ".txt");
                if(qFile.exists())
                {
                    Sweetdream.lyricsPane.setVisible(true);
                    String record = null;
                    StringBuffer buf = new StringBuffer();
                    try
                    {
                        FileReader fr = new FileReader(qFile);
                        BufferedReader br = new BufferedReader(fr);
                        record = new String();
                    }
                    catch(IOException eq) { eq.printStackTrace(); }
                    Sweetdream.lyricsDisplay.setText(buf.toString());
                    Sweetdream.lyricsDisplay.setCaretPosition(0);
                }
            }
        });
	
        viewPlaylistBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if(!Sweetdream.currentLoadedPlaylist.equals(""))
                {
                    Sweetdream.optionsFrame.setVisible(true);
                    Sweetdream.tabbedPaneViewDatabase.setSelectedIndex(2);
                    Sweetdream.tree_options.setSelectionRow(4);
                    Sweetdream.viewPlaylistSelectBox.setSelectedItem(Sweetdream.currentLoadedPlaylist);
                    Sweetdream.splitOptionsPane.setDividerLocation(130);
                }
            }
        });
        
        /* Check every minute for a scheduled event, and if one exists for the current time, run it */
        event_timer = new javax.swing.Timer(60000, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                eFunctions.runEvents();
            }
        });
        /* Adjust the volume gradually to "fade out" when pausing */
        pause_timer = new javax.swing.Timer(75, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	currentVolumeLevel = currentVolumeLevel - 3;
                eFunctions.adjustVolume(currentVolumeLevel); // give it a percentage
            }
        });
        event_timer.setInitialDelay(12000);
        event_timer.start();
	
        countdown_timer = new javax.swing.Timer(1000, new ActionListener() {
           public void actionPerformed(ActionEvent e)
            {
                eFunctions.updateCountdown();
            }
        });
	
	
        playlistCleanBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                int answer = JOptionPane.showConfirmDialog(new JFrame(), "Delete unfound files from this playlist?");
                if(answer == 0)
                {
                    String playlistName = Sweetdream.viewPlaylistSelectBox.getSelectedItem().toString();
                    Vector temp = new Vector();
                    int q = 0;
                    try
                    {
                        temp = eFunctions.getPlaylistVector(playlistName);
                    }
                    catch(SQLException ex2) { ex2.printStackTrace(); }
                    for(int i = 0; i < temp.size(); i++)
                    {
                        File fileToCheck = new File(Sweetdream.MusicDirectoryPath + temp.get(i).toString());
                        if(fileToCheck.exists()) continue;
                        
                        q++;
                        try {
                            Sweetdream.db.update("DELETE FROM playlist_contents WHERE PlaylistName = '" + playlistName + "' AND TrackPath = '" + temp.get(i) + "'");
                        }
                        catch(SQLException ex2) { ex2.printStackTrace(); }
                        eFunctions.printActivity("Not Found: " + Sweetdream.MusicDirectoryPath + temp.get(i) + "\n");
                    }

                    try
                    {
                        eFunctions.displayTable(playlistName, "tablePlaylists");
                    }
                    catch(SQLException ex2) { ex2.printStackTrace(); }
                    JOptionPane.showMessageDialog(Sweetdream.frame, "Clean up complete. " + q + " items were deleted.");
                }
            }
        });
	
        playlistDeleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                int answer = JOptionPane.showConfirmDialog(new JFrame(), "Delete this entire playlist?");
                if(answer == 0)
                {
                    String playlistName = Sweetdream.viewPlaylistSelectBox.getSelectedItem().toString();
                    try {
                        Sweetdream.db.update("DELETE FROM playlist_contents WHERE PlaylistName = '" + playlistName + "'");
			Sweetdream.db.update("DELETE FROM playlists WHERE PlaylistName = '" + playlistName + "'");
			Sweetdream.playlistTableModel.setRowCount(0);
			eFunctions.reloadPlaylists();
                    }
                    catch(SQLException ex2) { ex2.printStackTrace(); }
                    
                    Sweetdream.addToPlaylistBtn.addMouseListener(new AddToPlaylistPopup());
                }
            }
        });
        WindowAdapter frameCloser = new WindowAdapter() {
            public void windowClosing(WindowEvent evt)
            {
                Frame frame = (Frame)evt.getSource();
                frame.setVisible(false);
                frame.dispose();
            }
        };  
                WindowAdapter frameCloser2 = new WindowAdapter() {
            public void windowClosing(WindowEvent evt)
            {
                Frame frame = (Frame)evt.getSource();
                frame.setVisible(false);
                frame.dispose();
                try { 
				Sweetdream.db.update("UPDATE variables SET value = '" + Sweetdream.tabbedPaneLibrary.getSelectedIndex() + "' WHERE variablename = 'lastLibraryTab'"); 
				
			} catch(SQLException ex2) { ex2.printStackTrace(); }
                System.exit(0);
            }
        };  
        addAlbumFrame.addWindowListener(frameCloser);
        editID3Frame.addWindowListener(frameCloser);
	
        logViewFrame.addWindowListener(frameCloser);
        optionsFrame.addWindowListener(frameCloser);
        newArtistFrame.addWindowListener(frameCloser);
        touchFrame.addWindowListener(frameCloser);
        countdownFrame.addWindowListener(frameCloser);
        frame.addWindowListener(frameCloser);
	
        tracksLeftSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                if(Sweetdream.tracksLeftSelectBox.getSelectedIndex() == 0)
                {
                    Sweetdream.tracksLeft = -1;
                    Sweetdream.tracksLeftDisplay.setText("");
                    Sweetdream.tracksLeftActionSelectBox.setVisible(false);
                } else
                if(Sweetdream.tracksLeftSelectBox.getSelectedIndex() > 0)
                {
                    Sweetdream.tracksLeft = Integer.parseInt(selectBoxOption);
                    Sweetdream.tracksLeftDisplay.setText(selectBoxOption + " left ");
                    Sweetdream.tracksLeftActionSelectBox.setVisible(true);
                }
            }
        });
        timeTravelSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                if(Sweetdream.timeTravelSelectBox.getSelectedIndex() == 0) return;
                
                if(audioInfo.get("audio.type").equals("OGG"))
                {
                    int totalBytes = ((Integer)audioInfo.get("audio.length.bytes")).intValue();
                    int incrementBytes = (totalBytes / 10) * (Integer.parseInt(selectBoxOption) / 10);
                    int remainder = incrementBytes % 4;
                    try
                    {
                        Sweetdream.mainSoundPlayer.seek(incrementBytes - remainder);
                    }
                    catch(BasicPlayerException eqq) { eqq.printStackTrace(); }
                }
		else if(audioInfo.get("audio.type").equals("MP3"))
                {
                    int totalBytes = ((Integer)audioInfo.get("audio.length.bytes")).intValue();
                    int incrementBytes = totalBytes / 10;
                    try
                    {
                        Sweetdream.mainSoundPlayer.seek(incrementBytes * (Integer.parseInt(selectBoxOption) / 10));
                    }
                    catch(BasicPlayerException eqq) { eqq.printStackTrace(); }
		    
                    int newProgressBarPos = Math.round(Sweetdream.progressBar.getMaximum() / 10) * (Integer.parseInt(selectBoxOption) / 10);
                    Sweetdream.progressBar.setValue(newProgressBarPos);
                    eFunctions.printActivity("progressBar.getMaximum(): " + Sweetdream.progressBar.getMaximum() + ", newProgressBarPos: " + newProgressBarPos);
                }
                
                eFunctions.adjustVolume(currentVolumeLevel);
            }
        });
	
        setColorBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
		 Color newColor = JColorChooser.showDialog(
                     new JFrame(),
                     "Choose Background Color",
                     panelOfEverything.getBackground());
		
                 themeColorRed = newColor.getRed();
                 themeColorGreen = newColor.getGreen();
                 themeColorBlue = newColor.getBlue();
                 
		eFunctions.updateThemeColor(themeColorRed,themeColorGreen,themeColorBlue);
            }
        });

	
        playTypeSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
            }
        });
	
        playlistActionSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();                         
                String selectBoxOption = (String)cb.getSelectedItem();
                if(selectBoxOption.equals("View"))
                {
                   if(!Sweetdream.currentLoadedPlaylist.equals(""))
                    {
                        Sweetdream.optionsFrame.setVisible(true);
                        Sweetdream.tabbedPaneViewDatabase.setSelectedIndex(2);
                        Sweetdream.tree_options.setSelectionRow(4);
                        Sweetdream.viewPlaylistSelectBox.setSelectedItem(Sweetdream.currentLoadedPlaylist);
                        Sweetdream.splitOptionsPane.setDividerLocation(130);
                        Sweetdream.playlistActionSelectBox.setSelectedIndex(0);
                    }
                }
		else if(selectBoxOption.equals("Close"))
                {
                    eFunctions.togglePlaylistView("Normal");
                    eFunctions.closePlaylist();
                    Sweetdream.imagePane.setVisible(false);
                }
		else if(selectBoxOption.equals("Simple Mode")) eFunctions.togglePlaylistView("Simple");
                else if(selectBoxOption.equals("Normal Mode")) eFunctions.togglePlaylistView("Normal");
                else if(selectBoxOption.equals("Copy to MP3 Player")) Sweetdream.transferPlaylist(Sweetdream.currentPlaylistVector.size());
              //  else if(!selectBoxOption.equals("Add Playlist Image"))
            }
        });
	
        libraryFontSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                Sweetdream.libraryFont = selectBoxOption;
                eFunctions.updateTreeFont();
            }
        });
	
	libraryFontSizeSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                Sweetdream.libraryFontSize = Integer.parseInt(selectBoxOption);
                
                eFunctions.updateTreeFont();
             //   int height = Sweetdream.libraryVisibleRows * Sweetdream.libraryFontSize;
             //   Sweetdream.scrollViewMusicLibrary.setPreferredSize(new Dimension(400, height));
	     //	Sweetdream.scrollViewMusicLibrary.setMinimumSize(new Dimension(400, height));
            }
        });
	
        libraryVisibleRowsSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                Sweetdream.libraryVisibleRows = Integer.parseInt(selectBoxOption);
                int height = Sweetdream.libraryVisibleRows * 20;
		tree.setVisibleRowCount(Sweetdream.libraryVisibleRows);
                scrollViewMusicLibrary.setPreferredSize(new Dimension(400, height));
		scrollViewMusicLibrary.setMinimumSize(new Dimension(400, height));
               // eFunctions.refreshTree("Music");
               // eFunctions.packFrame();
            }
        });
	
        fullScreenSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                if(selectBoxOption.equals("Full Screen")) Sweetdream.frame.setExtendedState(6);
                
                eFunctions.packFrame();
            }
        });
	
        setThemeSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                if(selectBoxOption.equals("GTK"))
                {
                    try
                    {
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                    }
                    catch(Exception eqq) { }
                }
		else if(selectBoxOption.equals("Nimbus"))
                {
                    try
                    {
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                    }
                    catch(Exception eqq) { }
		    // tree.setBackground(new Color(255,255,255));
                }
		else if(selectBoxOption.equals("Motif"))
                {
                    try
                    {
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    }
                    catch(Exception eqq) { }
                    
                }
		else if(selectBoxOption.equals("Metal"))
                {
                    try
                    {
                        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    }
                    catch(Exception eqq) { }
                }
                SwingUtilities.updateComponentTreeUI(Sweetdream.frame.getContentPane());
                SwingUtilities.updateComponentTreeUI(Sweetdream.optionsFrame);
                SwingUtilities.updateComponentTreeUI(Sweetdream.logViewFrame);
		SwingUtilities.updateComponentTreeUI(eCollage.collageVideoFrame);
            }
        });
	
        ActionSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                Sweetdream.ActionSelectBox.setSelectedIndex(0);
		
                if(selectBoxOption.equals("Add Album Cover"))
                {
                    String url = "http://www.google.com/images?tab=iw&hl=en&q=" + Sweetdream.currentPlayingArtist + " " + Sweetdream.currentPlayingAlbum;
                    eFunctions.openBrowser(url);
                    Sweetdream.addAlbumAlbumNameBox.setText(Sweetdream.currentPlayingAlbum);
                    Sweetdream.editAlbumCoverBox.setText(Sweetdream.currentPlayingAlbum);
                    File defaultDir = new File("images/covers");
                    Sweetdream.m_fileChooser.setCurrentDirectory(defaultDir);
                    int retval = Sweetdream.m_fileChooser.showOpenDialog(Sweetdream.frame);
                    if(retval == 0)
                    {
                        File file = Sweetdream.m_fileChooser.getSelectedFile();
                        if(file != null)
                        {
                            String filename = file.toString();
                            Sweetdream.addAlbumCoverBox.setText(filename.substring(filename.lastIndexOf(File.separator) + 1));
                            Sweetdream.editAlbumCoverBox.setText(filename.substring(filename.lastIndexOf(File.separator) + 1));
                            Sweetdream.addAlbumFrame.setVisible(true);
                        }
                    }
                }
		else if(selectBoxOption.equals("Mute"))
                {
                    Sweetdream.actionsListVector.remove("Mute");
                    Sweetdream.actionsListVector.add(1, "Unmute");
                    Sweetdream.ActionSelectBox.setModel(new DefaultComboBoxModel(Sweetdream.actionsListVector));
                    Sweetdream.isMute = true;
                    try
                    {
                        Sweetdream.mainSoundPlayer.setGain(0.0D);
                    }
                    catch(BasicPlayerException ex) { ex.printStackTrace(); }
                }
		else if(selectBoxOption.equals("Unmute"))
                {
                    Sweetdream.actionsListVector.remove("Unmute");
                    Sweetdream.actionsListVector.add(1, "Mute");
                    Sweetdream.ActionSelectBox.setModel(new DefaultComboBoxModel(Sweetdream.actionsListVector));
                    Sweetdream.isMute = false;
                    try
                    {
                        Sweetdream.mainSoundPlayer.setGain(1.0D);
                    }
                    catch(BasicPlayerException ex) { ex.printStackTrace(); }
                }
		else if(selectBoxOption.equals("Floating Image Mode"))
                {
                    Sweetdream.imageFrame.setVisible(true);
                    Sweetdream.frame.setVisible(false);
                    Sweetdream.imageFrame.pack();
                }
		
		else if(selectBoxOption.equals("Close Mood"))
                {
			eFunctions.closeMood();
		}
		else if(selectBoxOption.equals("Transfer to MP3 Player"))
                {
                    String setNumTracksString = "";
                    setNumTracksString = (String)JOptionPane.showInputDialog(new JFrame(), "Enter Number of Tracks:", "Copy to MP3 Player", -1, null, null, "40");
                    eFunctions.printActivity("user requests transferring " + Integer.parseInt(setNumTracksString) + "tracks.");
                    if(!setNumTracksString.equals(""))
			    Sweetdream.transferTracks(Integer.parseInt(setNumTracksString));
                    
                } else if(selectBoxOption.equals("Start Timer"))
                	eFunctions.startTimer();
                else if(selectBoxOption.equals("Open Channel"))
                	Sweetdream.captureAudio();
                else if(selectBoxOption.equals("Show/Hide Song Display"))
                {
                    if(Sweetdream.statusPane.isVisible())
                      Sweetdream.statusPane.setVisible(false);
                    else
                      Sweetdream.statusPane.setVisible(true);
                    
                    eFunctions.packFrame();
                }
		else if(selectBoxOption.equals("Show/Hide Library"))
                	eFunctions.toggleShowLibrary(""); //will toggle when "hide/show" is left blank
		else if(selectBoxOption.equals("Semi-Collapse Library"))
                	eFunctions.semiCollapseAll("Music");
                else if(selectBoxOption.equals("Override Shutdown"))
                	eFunctions.shutdownComputer("stop");
                else if(selectBoxOption.equals("Close Channel"))
                {
                    Sweetdream.targetDataLine.stop();
                    Sweetdream.targetDataLine.close();
                    Sweetdream.statusDisplay.setText("Channel recorded successfully.");
                } else if(selectBoxOption.equals("View"))
                {
                    if(!Sweetdream.currentLoadedPlaylist.equals(""))
                    {
                        Sweetdream.optionsFrame.setVisible(true);
                        Sweetdream.tabbedPaneViewDatabase.setSelectedIndex(2);
                        Sweetdream.tree_options.setSelectionRow(4);
                        Sweetdream.viewPlaylistSelectBox.setSelectedItem(Sweetdream.currentLoadedPlaylist);
                        Sweetdream.splitOptionsPane.setDividerLocation(130);
                        Sweetdream.playlistActionSelectBox.setSelectedIndex(0);
                    }
                } else if(selectBoxOption.equals("Close"))
                {
                    eFunctions.togglePlaylistView("Normal");
                    eFunctions.closePlaylist();
                    Sweetdream.imagePane.setVisible(false);
                }
		else if(selectBoxOption.equals("Simple Mode"))
                	eFunctions.togglePlaylistView("Simple");
                else if(selectBoxOption.equals("Normal Mode"))
                	eFunctions.togglePlaylistView("Normal");
                else if(selectBoxOption.equals("Copy to MP3 Player"))
                	Sweetdream.transferPlaylist(Sweetdream.currentPlaylistVector.size());
                
            }
        });
	
        playlistSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                if(!selectBoxOption.equals("-- playlist --"))
                {
                    try
                    {
                        eFunctions.displayPlaylist(selectBoxOption);
                        tabbedPaneLibrary.setSelectedIndex(2); // Show Playlist tab
                    }
                    catch(SQLException sq) { sq.printStackTrace(); }
                    try
                    {
                        eFunctions.displayTable(selectBoxOption, "tablePlaylists");
                    }
                    catch(SQLException sq) { sq.printStackTrace(); }
                } else
                {
                    eFunctions.togglePlaylistView("Normal");
                    eFunctions.closePlaylist();
                }
            }
        });
	
        artistImageTypeSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                Sweetdream.ArtistImageType = selectBoxOption;
            }
        });
	
        viewPlaylistSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
		//playlistVoiceCommandBox.setText()
                try
                {
                    eFunctions.displayTable(selectBoxOption, "tablePlaylists");
                }
                catch(SQLException sq) { sq.printStackTrace(); }
            }
        });
	
        computerVoiceSelectBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                String selectBoxOption = (String)cb.getSelectedItem();
                if(selectBoxOption.equals("On"))
			Sweetdream.computerVoice = true;
                else
			Sweetdream.computerVoice = false;
            }
        });
	
        MouseListener imageClick = new MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {
            	      if(e.getClickCount()==2){
            	    Sweetdream.imageFrame.setVisible(true);
                    Sweetdream.frame.setVisible(false);
                    Sweetdream.imageFrame.pack();  
            } else {
                if(SwingUtilities.isLeftMouseButton(e))
                	// If clicked, get next image (of that artist, playlist, etc.)
			Sweetdream.getArtistImage(Sweetdream.currentPlayingArtist.trim(), Sweetdream.currentAlbumName);
            
               }
            }
        };
	
	MouseListener photoClick = new MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {
                if(SwingUtilities.isLeftMouseButton(e))
			photoLabel.setVisible(false);
			boxLeftside.setVisible(true);
			eFunctions.packFrame();
            }
        };
	
        MouseListener toggleImageFrameClick = new MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {
                if(SwingUtilities.isLeftMouseButton(e))
                {
                    Sweetdream.frame.setVisible(true);
                    ImagePopup.item1.setSelected(false);
                    Sweetdream.imageFrame.setVisible(false);
                }
            }
        };

        imageFrame.addMouseListener(new MouseAdapter()
        {    
    	   public void mouseClicked(MouseEvent e)
            {
    	    Sweetdream.frame.setVisible(true);
                    ImagePopup.item1.setSelected(false);
                    Sweetdream.imageFrame.setVisible(false);
            }
         });
        imageFrame.addMouseMotionListener(new MouseMotionAdapter()
        {     
    	   public void mouseDragged(MouseEvent e) { 
                 Point new_location = new Point(MouseInfo.getPointerInfo().getLocation().x,MouseInfo.getPointerInfo().getLocation().y);
    	   	 imageFrame.setLocation(new_location);
    	   }
        });
        
        MouseListener last25treeClick = new MouseAdapter() {
        	   public void mousePressed(MouseEvent e)
            {
            	    String selectedElement = tree_last25.getSelectionPath().getLastPathComponent().toString();
            	    String vectorElement = "";
            	    int i = 0;
            	    
            	    do {
                        if(i >= musicCompleteVector.size()) break;
                        
                        vectorElement = musicCompleteVector.get(i).toString();
			
                        // PLAY SELECTED LAST 25 TRACK
                        if(vectorElement.contains(selectedElement) && vectorElement.contains("."))
                        {
			    eFunctions.printActivity("Found clicked Last 25 in musicCompleteVector! " + " selectedElement: " + selectedElement + " vectorElement: " + vectorElement);
                            eFunctions.reset();
                            try{
                              mainSoundPlayer.open(openFile(MusicDirectoryPath + vectorElement));
                              mainSoundPlayer.play();
                            }
                            catch(BasicPlayerException eqq) { eqq.printStackTrace(); }
                            break;
                        }
                        i++;
                    } while(true);
            }
        };
        
                MouseListener playlistTreeClick = new MouseAdapter() {
        	   public void mousePressed(MouseEvent e)
            {
            	    String selectedElement = tree_playlist.getSelectionPath().getLastPathComponent().toString();
            	  //  String vectorElement = "";
            	  //  int i = 0;
            	    
            	   // do {
                    //    if(i >= musicCompleteVector.size()) break;
                        
                     //   vectorElement = musicCompleteVector.get(i).toString();
			
                        // PLAY SELECTED LAST 25 TRACK
                     //   if(vectorElement.contains(selectedElement) && vectorElement.contains("."))
                     //   {
			 //   eFunctions.printActivity("Found clicked Last 25 in musicCompleteVector! " + " selectedElement: " + selectedElement + " vectorElement: " + vectorElement);
                            eFunctions.reset();
                            try{
                              mainSoundPlayer.open(openFile(MusicDirectoryPath + selectedElement));
                              mainSoundPlayer.play();
                            }
                            catch(BasicPlayerException eqq) { eqq.printStackTrace(); }
                          //  break;
                    //    }
                   //     i++;
                   // } while(true);
            }
        };
               
        /* Activate Mood when Moods JTree is (left) clicked */
        MouseListener categorytreeClick = new MouseAdapter() {
        	   public void mousePressed(MouseEvent e)
            {
        	JPopupMenu fileMenu = new JPopupMenu();
        	JPopupMenu folderMenu = new JPopupMenu();
        	JMenuItem item0 = new JMenuItem("Delete Mood");
        	JMenuItem item1 = new JMenuItem("Remove from Mood");
        	item0.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                   int answer = JOptionPane.showConfirmDialog(new JFrame(), "Delete this mood?");
                    if(answer == 0)
                    {
                        try {
                            Sweetdream.db.update("DELETE FROM moods WHERE MoodName = '" + tree_audiocategory.getSelectionPath().getLastPathComponent().toString() + "'");
                        }
                        catch(SQLException ex2) { ex2.printStackTrace(); }
                        eFunctions.refreshTree("Moods");  
                    }
            }
        });
        folderMenu.add(item0);
               item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                   int answer = JOptionPane.showConfirmDialog(new JFrame(), "Remove this item ?");
                    if(answer == 0)
                    {
                    	    System.out.println();
                        try {
                            Sweetdream.db.update("DELETE FROM moods WHERE MoodName = '" + tree_audiocategory.getSelectionPath().getPathComponent(1) + "' AND Path = '" + tree_audiocategory.getSelectionPath().getLastPathComponent().toString() + "'");
                        }
                        catch(SQLException ex2) { ex2.printStackTrace(); }
                        eFunctions.refreshTree("Moods");  
                    }
            }
        });
        fileMenu.add(item1);
         
	    /* Prevent starting mood if tree node was opened */
            TreePath path = tree.getPathForRow(tree.getRowForLocation(e.getX(), e.getY()));
            if(path == null) return;
		    
		String selectedStr;
                if(SwingUtilities.isLeftMouseButton(e)) // && !Sweetdream.tree_audiocategory.getSelectionPath().getLastPathComponent().toString().contains("::"))
                {
			if(tree_audiocategory.getSelectionPath() != null) {
				
				 selectedStr = tree_audiocategory.getSelectionPath().getLastPathComponent().toString();
				
				 /* SELECTED ARTIST DIRECTLY FROM MOODS TREE */
				 if (selectedStr.contains(File.separator)) {
					eFunctions.closeMood();
					usingSearch = selectedStr.substring(1);
					themeDisplay.setText(usingSearch);
					controlsPane.setBorder(BorderFactory.createTitledBorder("Mood: " + selectedStr));
					nextTrack("Setting the Mood: " + Sweetdream.usingSearch);
					// eFunctions.toggleShowLibrary("hide");
				}
				else if (selectedStr.startsWith("Moods ")) {
					eFunctions.expandTree("Moods");					
				}
				else {  /* SETTING THE MOOD */
					eFunctions.closeMood();
					try {  // Populate currentMoodVector
					   Sweetdream.currentMoodVector = eFunctions.getMoodVector(selectedStr);
					}
					catch(SQLException sq) { sq.printStackTrace(); }
					playCurrentMood = true;
					Border compound = BorderFactory.createEtchedBorder();
					
					/* Close Playlist if open */
					eFunctions.togglePlaylistView("Normal");
					eFunctions.closePlaylist();
					
					/* Reset theme search textbox, etc */
					Sweetdream.themeDisplay.setText("");
					//controlsPane.setBorder(BorderFactory.createTitledBorder(""));
					Sweetdream.themeTextBox.setText("");
					Sweetdream.usingSearch = "";
					Sweetdream.controlsPane.setBorder(BorderFactory.createTitledBorder("Mood: " + selectedStr));
					
					playTypeSelectBox.setSelectedIndex(0);
					nextTrack("Playing a new mood.");
				}
			}
                }
                /*Right clicked - display menu */
                else {
                	if(tree_audiocategory.getPathForLocation(e.getX(), e.getY()).getLastPathComponent().toString().contains(File.separator))
			  fileMenu.show(e.getComponent(), e.getX(), e.getY());
                	else
			  folderMenu.show(e.getComponent(), e.getX(), e.getY());
                	
                	tree_audiocategory.setSelectionRow(tree_audiocategory.getRowForPath(tree_audiocategory.getPathForLocation(e.getX(), e.getY())));
                }
            }
            
          /*  
              public void actionPerformed(ActionEvent e)
        {
	    JMenuItem source = (JMenuItem)(e.getSource());
	    String s = source.getText().trim();
 
	   if(s.equals("Delete Mood")) {

            }

        }
           */ 
        };
	
        MouseListener lyricsClick = new MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {
                Sweetdream.optionsFrame.setVisible(true);
                Sweetdream.tree_options.setSelectionRow(4);
                try {
                    eFunctions.reloadPlaylists();
                }
                catch(SQLException sq) { sq.printStackTrace(); }
            }
        };
	
        java.awt.event.MouseMotionListener sliderDrag = new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e)
            {
                try {
                    int totalBytes = ((Integer)audioInfo.get("audio.length.bytes")).intValue();
                    eFunctions.printActivity("totalBytes: " + totalBytes);
                    eFunctions.printActivity("getDurationSeconds(audioInfo): " + getDurationSeconds(audioInfo));
                    eFunctions.printActivity("progressBar.getValue(): " + Sweetdream.progressBar.getValue());
                    double newPositionPercent = (double)Sweetdream.progressBar.getValue() / (double)getDurationSeconds(audioInfo);
                    eFunctions.printActivity("percentage(%): " + newPositionPercent);
                    long skipBytes = Math.round(newPositionPercent * (double)totalBytes);
                    Sweetdream.mainSoundPlayer.seek(skipBytes);
                }
                catch(BasicPlayerException eqq) { eqq.printStackTrace(); }
            }
        };
	
        themeTextBox.addTextListener(new TextListener() {
            public void textValueChanged(TextEvent e)
            {
                if(Sweetdream.themeTextBox.getText().equals("Music")) Sweetdream.themeTextBox.setText("");
                else if(Sweetdream.themeTextBox.getText().equals("")) Sweetdream.themeDisplay.setText("All");
                
                   try
                   {
                      eFunctions.themeSearch("artists", Sweetdream.themeTextBox.getText());
                   }
                   catch(SQLException ex2) { ex2.printStackTrace(); }
		   
                if(Sweetdream.themeDisplay.getText().equals("Random"))
                {
                    try
                    {
                        eFunctions.themeSearch("albums", Sweetdream.themeTextBox.getText());
                    }
                    catch(SQLException ex2) { ex2.printStackTrace(); }
                }
            }
        });
        
        
        searchBox.addTextListener(new TextListener() {
            public void textValueChanged(TextEvent e)
            {
            	   System.out.println(searchBox.getText());
              
            }
        });
	
        themeTextBox.addMouseListener(new MouseListener() {
            public void mouseExited(MouseEvent e) { }
            public void mouseEntered(MouseEvent e) { }
            public void mousePressed(MouseEvent e) { }
            public void mouseReleased(MouseEvent e) { }
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2)
                {
                    eFunctions.closeMood();
                    eFunctions.closePlaylist();
                }
            }
        });
	
	//themeTextBox.setToolTipText("Enter letters or words to filter music");
        //boxOptionsA = new Box(1);
	boxOptionsA = new JPanel(new GridLayout(7, 1, 5, 5));
        Box boxMusicDirectory = new Box(0);
           boxMusicDirectory.add(new JLabel("Music Directory: "));
           boxMusicDirectory.add(Box.createHorizontalStrut(7));
           boxMusicDirectory.add(MusicDirectoryTextBox);
        MusicDirectoryTextBox.setPreferredSize(new Dimension(180, 20));
        MusicDirectoryTextBox.setMaximumSize(new Dimension(180, 20));
        MusicDirectoryTextBox.setText(MusicDirectoryPath);
        boxMusicDirectory.add(getMusicDirectoryBtn);
		getMusicDirectoryBtn.setPreferredSize(new Dimension(40, 20));
		getMusicDirectoryBtn.setMaximumSize(new Dimension(40, 20));
        boxOptionsA.add(boxMusicDirectory);
        Box boxAudioDirectory = new Box(0);
           boxAudioDirectory.add(new JLabel("Audio Directory: "));
           boxAudioDirectory.add(Box.createHorizontalStrut(7));
           boxAudioDirectory.add(AudioDirectoryTextBox);
              AudioDirectoryTextBox.setPreferredSize(new Dimension(180, 20));
              AudioDirectoryTextBox.setMaximumSize(new Dimension(180, 20));
              AudioDirectoryTextBox.setText(AudioDirectoryPath);
        boxAudioDirectory.add(getAudioDirectoryBtn);
		getAudioDirectoryBtn.setPreferredSize(new Dimension(40, 20));
		getAudioDirectoryBtn.setMaximumSize(new Dimension(40, 20));
        boxOptionsA.add(boxAudioDirectory);
        Box boxVideoDirectory = new Box(0);
           boxVideoDirectory.add(new JLabel("Video Directory: "));
           boxVideoDirectory.add(Box.createHorizontalStrut(7));
           boxVideoDirectory.add(VideoDirectoryTextBox);
              VideoDirectoryTextBox.setPreferredSize(new Dimension(180, 20));
              VideoDirectoryTextBox.setMaximumSize(new Dimension(180, 20));
              VideoDirectoryTextBox.setText(VideoDirectoryPath);
           boxVideoDirectory.add(getVideoDirectoryBtn);
	   	getVideoDirectoryBtn.setPreferredSize(new Dimension(40, 20));
		getVideoDirectoryBtn.setMaximumSize(new Dimension(40, 20));
        boxOptionsA.add(boxVideoDirectory);
	Box boxPhotoDirectory = new Box(0);
           boxPhotoDirectory.add(new JLabel("Photo Directory: "));
           boxPhotoDirectory.add(Box.createHorizontalStrut(7));
           boxPhotoDirectory.add(PhotoDirectoryTextBox);
              PhotoDirectoryTextBox.setPreferredSize(new Dimension(180, 20));
              PhotoDirectoryTextBox.setMaximumSize(new Dimension(180, 20));
              PhotoDirectoryTextBox.setText(PhotoDirectoryPath);
           boxPhotoDirectory.add(getPhotoDirectoryBtn);
	   	getPhotoDirectoryBtn.setPreferredSize(new Dimension(40, 20));
		getPhotoDirectoryBtn.setMaximumSize(new Dimension(40, 20));
        boxOptionsA.add(boxPhotoDirectory);
        Box boxRepeatPlay = new Box(0);
        	boxRepeatPlay.add(new JLabel("Repeat Play Prevention: "));
		boxRepeatPlay.add(Box.createHorizontalStrut(7));
		boxRepeatPlay.add(RepeatPlayNumTextBox);
        		RepeatPlayNumTextBox.setPreferredSize(dimension100x20);
			RepeatPlayNumTextBox.setMaximumSize(dimension100x20);
        Box boxMp3Directory = new Box(0);
        	boxMp3Directory.add(new JLabel("MP3 Player:        "));
		boxMp3Directory.add(Box.createHorizontalStrut(7));
		boxMp3Directory.add(MP3PlayerDirectoryTextBox);
        		MP3PlayerDirectoryTextBox.setPreferredSize(new Dimension(180, 20));
			MP3PlayerDirectoryTextBox.setMaximumSize(new Dimension(180, 20));
			MP3PlayerDirectoryTextBox.setText(MP3PlayerDirectoryPath);
        boxMp3Directory.add(getMP3PlayerDirectoryBtn);
		getMP3PlayerDirectoryBtn.setPreferredSize(new Dimension(40, 20));
		getMP3PlayerDirectoryBtn.setMaximumSize(new Dimension(40, 20));
        boxOptionsA.add(boxMp3Directory);
        Box boxCompy = new Box(0);
        	boxCompy.add(new JLabel("Computer Voice: "));
		boxCompy.add(Box.createHorizontalStrut(7));
		boxCompy.add(computerVoiceSelectBox);
        computerVoiceSelectBox.setPreferredSize(new Dimension(220, 30));
	computerVoiceSelectBox.setMaximumSize(new Dimension(220, 30));
        boxOptionsA.add(boxCompy);
        Box boxOtherStuff = new Box(0);
        boxOtherStuff.add(viewCommandsTxtBtn);
        viewCommandsTxtBtn.setPreferredSize(new Dimension(350, 40));
        boxOptionsA.add(boxOtherStuff);
        //boxOptionsA.add(Box.createRigidArea(new Dimension(1, 3)));
        //boxOptionsA.add(addAlbumBtn);
        	addAlbumBtn.setPreferredSize(new Dimension(350, 40));
		addAlbumBtn.setVisible(false);
		addAlbumBtn.setMnemonic(65);
        optionsPane.add(boxOptionsA);
        boxOptionsB = new Box(1);
        boxOptionsB.add(new JLabel(" "));
        boxOptionsB.setPreferredSize(new Dimension(350, 200));

        Box boxSetTheme = new Box(0);
		boxSetTheme.add(Box.createHorizontalGlue());
       		boxSetTheme.add(new JLabel("Theme: "));
		boxSetTheme.add(Box.createHorizontalStrut(7));
		boxSetTheme.add(setThemeSelectBox);
			setThemeSelectBox.setPreferredSize(new Dimension(150, 25));
			setThemeSelectBox.setMaximumSize(new Dimension(150, 25));
        boxOptionsB.add(boxSetTheme);
        Box boxArtistImageType = new Box(0);
		boxArtistImageType.add(Box.createHorizontalGlue());
        	boxArtistImageType.add(new JLabel("Display Image: "));
		boxArtistImageType.add(Box.createHorizontalStrut(7));
		boxArtistImageType.add(artistImageTypeSelectBox);
			artistImageTypeSelectBox.setPreferredSize(new Dimension(150, 25));
			artistImageTypeSelectBox.setMaximumSize(new Dimension(150, 25));
			artistImageTypeSelectBox.setFont(font10);
        boxOptionsB.add(boxArtistImageType);
        Box boxLibraryFont = new Box(0);
		boxLibraryFont.add(Box.createHorizontalGlue());
        	boxLibraryFont.add(new JLabel("Library font: "));
		boxLibraryFont.add(Box.createHorizontalStrut(7));
		boxLibraryFont.add(libraryFontSizeSelectBox);
			libraryFontSizeSelectBox.setPreferredSize(new Dimension(50, 25));
			libraryFontSizeSelectBox.setMaximumSize(new Dimension(50, 25));
		boxLibraryFont.add(Box.createHorizontalStrut(7));
		boxLibraryFont.add(libraryFontSelectBox);
			libraryFontSelectBox.setPreferredSize(new Dimension(93, 25));
			libraryFontSelectBox.setMaximumSize(new Dimension(93, 25));
        boxOptionsB.add(boxLibraryFont);
        Box boxLibraryVisibleRows = new Box(0);
		boxLibraryVisibleRows.add(Box.createHorizontalGlue());
        	boxLibraryVisibleRows.add(new JLabel("Library visible rows: "));
		boxLibraryVisibleRows.add(Box.createHorizontalStrut(7));
		boxLibraryVisibleRows.add(libraryVisibleRowsSelectBox);
			libraryVisibleRowsSelectBox.setPreferredSize(new Dimension(150, 25));
			libraryVisibleRowsSelectBox.setMaximumSize(new Dimension(150, 25));
        boxOptionsB.add(boxLibraryVisibleRows);
	Box boxShowLibraryOnStartup = new Box(0);
		boxShowLibraryOnStartup.add(Box.createHorizontalGlue());
        	boxShowLibraryOnStartup.add(new JLabel("Show Library on startup: "));
		boxShowLibraryOnStartup.add(Box.createHorizontalStrut(7));
		boxShowLibraryOnStartup.add(showLibraryOnStartupSelectBox);
			showLibraryOnStartupSelectBox.setPreferredSize(new Dimension(150, 25));
			showLibraryOnStartupSelectBox.setMaximumSize(new Dimension(150, 25));
        boxOptionsB.add(boxShowLibraryOnStartup);
        Box boxFullScreen = new Box(0);
		boxFullScreen.add(Box.createHorizontalGlue());
		boxFullScreen.add(new JLabel("Display size: "));
		boxFullScreen.add(Box.createHorizontalStrut(7));
		boxFullScreen.add(fullScreenSelectBox);
			fullScreenSelectBox.setPreferredSize(new Dimension(150, 25));
			fullScreenSelectBox.setMaximumSize(new Dimension(150, 25));
        boxOptionsB.add(boxFullScreen);
	Box boxColor = new Box(0);
		boxColor.add(Box.createHorizontalGlue());
	        boxColor.add(new JLabel("Color: "));
		boxColor.add(Box.createHorizontalStrut(7));
		boxColor.add(currentColorLabel);
			currentColorLabel.setBackground(new Color(250, 250, 250));
			currentColorLabel.setPreferredSize(new Dimension(25, 20));
			currentColorLabel.setMaximumSize(new Dimension(25, 20));
			currentColorLabel.setMinimumSize(new Dimension(25, 20));
		boxColor.add(setColorBtn);
			setColorBtn.setPreferredSize(new Dimension(150, 25));
			setColorBtn.setMaximumSize(new Dimension(150, 25));
        boxOptionsB.add(boxColor);
        boxOptionsB.setVisible(false);
        optionsPane.add(boxOptionsB);
	
        boxOptionsC = new Box(1);
	boxOptionsC.add(noMP3PlayerDisplay);
		noMP3PlayerDisplay.setVisible(false);
	root_mp3player = new CheckNode("root_mp3player", true, false);
        tree_mp3player = new JTree(mp3playerModel);
        tree_mp3player.setFont(font10);
        tree_mp3player.getSelectionModel().setSelectionMode(4);
        tree_mp3player.setVisibleRowCount(20);
		scrollViewMP3Player = new JScrollPane(tree_mp3player);
		scrollViewMP3Player.setPreferredSize(new Dimension(310, 370));
	boxOptionsC.add(scrollViewMP3Player);
		
       
        DefaultTreeCellRenderer renderer3 = new DefaultTreeCellRenderer();
        	renderer3.setLeafIcon(musicNotesIcon);
		renderer3.setClosedIcon(cdIcon);
        tree_mp3player.setCellRenderer(renderer3);
        
        Box boxForButtons = new Box(0);
        	boxForButtons.add(mp3playerAddTracksBtn);
        		mp3playerAddTracksBtn.setToolTipText("Upload Track(s) to Mp3 Player");
		boxForButtons.add(Box.createHorizontalStrut(10));
		boxForButtons.add(mp3playerDeleteBtn);
        		mp3playerDeleteBtn.setToolTipText("Delete Track(s) on Mp3 Player");
        boxOptionsC.add(boxForButtons);
        optionsPane.add(boxOptionsC);
        boxOptionsC.setVisible(false);
        boxOptionsD = new Box(1);
       
       // boxOptionsD.add(tabbedPaneViewDatabase);
        boxOptionsD.add(tabbedPaneViewDatabase);
    //    boxOptionsD.setPreferredSize(new Dimension(620, 500));
      //  boxOptionsD.setMinimumSize(new Dimension(620, 500));
        boxOptionsD.setVisible(false);
        optionsPane.add(boxOptionsD); //, BorderLayout.CENTER
        boxOptionsE = new Box(1);
	boxOptionsF = new Box(1);
	boxOptionsG = new Box(1);
	
        Box lyricsBox = new Box(1);
        	lyricsBox.add(lyricsInput);
		lyricsBox.add(Box.createRigidArea(new Dimension(1, 35)));
		lyricsBox.add(lyricsSubmitBtn);
			lyricsSubmitBtn.setSize(new Dimension(50, 25));
		lyricsBox.add(Box.createRigidArea(new Dimension(1, 35)));
        boxOptionsE.add(lyricsBox);
        boxOptionsE.setPreferredSize(new Dimension(650, 500));
        boxOptionsE.setMinimumSize(new Dimension(650, 500));
        boxOptionsE.setVisible(false);
        optionsPane.add(boxOptionsE);
	boxOptionsF.add(shortcutsLabel);
		shortcutsLabel.setFont(new Font("Serif", Font.ITALIC, 16));
		shortcutsLabel.setEditable(false);
		shortcutsLabel.setBackground(new Color(245, 245, 245));
	optionsPane.add(boxOptionsF);
	boxOptionsG.add(activityViewPane);
		activityViewPane.setPreferredSize(new Dimension(500, 400));
		activityViewPane.setMinimumSize(new Dimension(500, 400));
		activityViewPane.setFont(new Font("Serif", Font.ITALIC, 16));
		//activityViewPane.setEditable(false);
		activityViewPane.setBackground(new Color(245, 245, 245));
		//shortcutsLabel.setLineWrap(true);
		//shortcutsLabel.setWrapStyleWord(true);
	optionsPane.add(boxOptionsG);
	
	boxOptions_Updates = new Box(1);
		boxOptions_Updates.add(displayCurrentVersionLabel);
			displayCurrentVersionLabel.setText("\n\nCurrent version:" + Sweetdream.class.getPackage().getImplementationVersion() + "\n\n");

		boxOptions_Updates.add(downloadUpdateBtn);
	optionsPane.add(boxOptions_Updates);
	
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("eTrain Options");
        DefaultMutableTreeNode category = null;
        category = new DefaultMutableTreeNode("Locations");
        top.add(category);
        category = new DefaultMutableTreeNode("Display");
        top.add(category);
        category = new DefaultMutableTreeNode("MP3 Player");
        top.add(category);
        category = new DefaultMutableTreeNode("Database");
        top.add(category);
     //   category = new DefaultMutableTreeNode("Lyrics");
     //   top.add(category);
        category = new DefaultMutableTreeNode("Updates");
        top.add(category);
        category = new DefaultMutableTreeNode("Shortcuts");
        top.add(category);
	category = new DefaultMutableTreeNode("Activity");
        top.add(category);
        category = new DefaultMutableTreeNode("Save / Exit");
        top.add(category);
	
        TreeSelectionListener optionsTreeListener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)Sweetdream.tree_options.getLastSelectedPathComponent();
                if(node == null) return;
                else {
                    Object nodeInfo = node.getUserObject();
                    eFunctions.optionsSwitcher(nodeInfo.toString());
                    Sweetdream.optionsFrame.pack();
                    return;
                }
            }
        };
	
        tree_options = new JTree(top);
        tree_options.setMinimumSize(new Dimension(120, 50));
        tree_options.setPreferredSize(new Dimension(120, 50));
        tree_options.getSelectionModel().setSelectionMode(1);
        tree_options.addTreeSelectionListener(optionsTreeListener);
        tree_options.setSelectionRow(1);
        splitOptionsPane.setDividerLocation(130);
        splitOptionsPane.setLeftComponent(tree_options);
        splitOptionsPane.setRightComponent(optionsPane);
       // optionsFrame.setLocationRelativeTo(null);
       // optionsFrame.setAlwaysOnTop(true);
        optionsFrame.setMinimumSize(new Dimension(650, 300));
        optionsFrame.setSize(new Dimension(650, 300));
        optionsFrame.add(splitOptionsPane);
        optionsFrame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
        optionsFrame.pack();
	
        JPanel newArtistPane = new JPanel(new GridLayout(3, 2, 25, 25));
        	newArtistPane.add(new JLabel("Artist: "));
		newArtistPane.add(newArtistNameBox);
		newArtistPane.add(new JLabel("Voice Command: "));
		newArtistPane.add(newArtistVoiceCommandBox);
			newArtistVoiceCommandBox.setCaretPosition(5);
			newArtistVoiceCommandBox.setMaximumSize(new Dimension(300, 35));
		newArtistPane.add(new JLabel(""));
		newArtistPane.add(newArtistSubmitBtn);
        newArtistFrame.add(newArtistPane);
        newArtistFrame.setSize(new Dimension(550, 200));
        JPanel addAlbumPane = new JPanel(new GridLayout(9, 2, 3, 7));
        addAlbumPane.add(new JLabel("  Artist Name: "));
        addAlbumPane.add(addAlbumArtistNameBox);
        addAlbumPane.add(new JLabel("  Album Name: "));
        addAlbumPane.add(addAlbumAlbumNameBox);
        addAlbumPane.add(new JLabel("  Voice Command: "));
        addAlbumPane.add(addAlbumVoiceCommandBox);
        addAlbumPane.add(new JLabel("  Frequency (1-3): "));
        addAlbumPane.add(addAlbumFrequencyBox);
        addAlbumPane.add(new JLabel("  Release Year: "));
        addAlbumPane.add(addAlbumReleaseYearBox);
        addAlbumPane.add(new JLabel("  Album Cover: "));
        addAlbumPane.add(addAlbumCoverBox);
        addAlbumPane.add(new JLabel());
        addAlbumPane.add(addAlbumCoverBtn);
        addAlbumPane.add(new JLabel());
        addAlbumPane.add(addAlbumSubmitBtn);
        addAlbumFrame.add(addAlbumPane);
        addAlbumFrame.pack();
        addAlbumFrame.setAlwaysOnTop(true);
        JPanel editID3Panel = new JPanel(new GridLayout(5, 2, 3, 7));
		editID3Panel.add(new JLabel("  Title: "));
		editID3Panel.add(editID3TitleTextBox);
		editID3Panel.add(new JLabel("  Artist Name: "));
		editID3Panel.add(editID3ArtistTextBox);
		editID3Panel.add(new JLabel("  Album Name: "));
		editID3Panel.add(editID3AlbumTextBox);
		editID3Panel.add(new JLabel("  Year: "));  // GENRE also?
		editID3Panel.add(editAlbumReleaseYearBox);
		editID3Panel.add(new JLabel());
		editID3Panel.add(updateID3Btn);
        editID3Frame.add(editID3Panel);
        editID3Frame.setVisible(false);
	editID3Frame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
        editID3Frame.pack();
	
        TableModelListener tableUpdateListener = new TableModelListener() {
            public void tableChanged(TableModelEvent e)
            {
                TableModel model = (TableModel)e.getSource();
                if(e.getColumn() >= 0)
                {
                    String columnName = model.getColumnName(e.getColumn());
                    Object editedCellData = model.getValueAt(e.getFirstRow(), e.getColumn());
                    Object getID = model.getValueAt(e.getFirstRow(), 0);
                    Object getCellHeader = model.getColumnName(e.getColumn());
                    if(getID != null)
                    {
                      try {
                    	    
   
                        if(Sweetdream.tabbedPaneViewDatabase.getTitleAt(Sweetdream.tabbedPaneViewDatabase.getSelectedIndex()).equals("Artists") && e.getFirstRow() != 0)
                        {
                            eFunctions.printActivity("UPDATE <table> SET <field> = '" + editedCellData + "' WHERE <id> ='" + getID + "'");
                               	
                            if(getCellHeader.equals("Artist"))
                            	    Sweetdream.db.update("UPDATE artists SET artistname = '" + StringEscapeUtils.escapeSql(editedCellData.toString())                                                                                                  + "' WHERE artistid = '" + getID + "'");
                                
                            else if(getCellHeader.equals("Voice Command"))
                            	    Sweetdream.db.update("UPDATE artists SET voicecommand = '" + StringEscapeUtils.escapeSql(editedCellData.toString()) + "' WHERE artistid = '" + getID + "'");
                           
                            else if(getCellHeader.equals("Times Played"))
                            	    Sweetdream.db.update("UPDATE artists SET TimesPlayed = '" + StringEscapeUtils.escapeSql(editedCellData.toString()) + "' WHERE artistid = '" + getID + "'");

                        } else if(Sweetdream.tabbedPaneViewDatabase.getTitleAt(Sweetdream.tabbedPaneViewDatabase.getSelectedIndex()).equals("Albums") && e.getFirstRow() != 0)
                        {
                            eFunctions.printActivity("UPDATE albums SET <field> = '" + editedCellData + "' WHERE id ='" + getID + "'");
                            if(getCellHeader.equals("Artist"))
                            	    Sweetdream.db.update("UPDATE albums SET artistname = '" + StringEscapeUtils.escapeSql(editedCellData.toString()) + "' WHERE id = '" + getID + "'");
                               
                            else if(getCellHeader.equals("Album"))
                            	    Sweetdream.db.update("UPDATE albums SET albumname = '" + StringEscapeUtils.escapeSql(editedCellData.toString()) + "' WHERE id = '" + getID + "'");
                                
                            else if(getCellHeader.equals("Voice Command"))
                            	    Sweetdream.db.update("UPDATE albums SET voicecommand = '" + editedCellData + "' WHERE id = '" + getID + "'");
                               
                            else if(getCellHeader.equals("Album Cover"))
                            	    Sweetdream.db.update("UPDATE albums SET coverimage = '" + editedCellData + "' WHERE id = '" + getID + "'");
                                
                            else if(getCellHeader.equals("Year"))
                            	    Sweetdream.db.update("UPDATE albums SET releaseyear = '" + editedCellData + "' WHERE id = '" + getID + "'");
                                
                            else if(getCellHeader.equals("Frequency"))
                            	    Sweetdream.db.update("UPDATE albums SET frequencyrating = '" + editedCellData + "' WHERE id = '" + getID + "'");
                            
                        } else if(Sweetdream.tabbedPaneViewDatabase.getTitleAt(Sweetdream.tabbedPaneViewDatabase.getSelectedIndex()).equals("Playlists"))
                        {
                            if(getCellHeader.equals(" ") || getCellHeader.equals(""))
                            	    eFunctions.printActivity("UPDATE playlists SET playlistimage = '" + editedCellData.toString() + "' WHERE playlistname = '" + Sweetdream.viewPlaylistSelectBox.getSelectedItem().toString() + "'");
                            
                            Sweetdream.db.update("UPDATE playlists SET playlistimage = '" + editedCellData.toString() + "' WHERE playlistname = '" + Sweetdream.viewPlaylistSelectBox.getSelectedItem() + "'");
                          
                        } else if(Sweetdream.tabbedPaneViewDatabase.getTitleAt(Sweetdream.tabbedPaneViewDatabase.getSelectedIndex()).equals("Events"))
                        {
                            if(getCellHeader.equals("Time"))
                            	Sweetdream.db.update("UPDATE events SET Time = '" + editedCellData.toString() + "' WHERE EventID = '" + getID + "'");
                                
                            else if(getCellHeader.equals("Type"))
                            	Sweetdream.db.update("UPDATE events SET Type = '" + editedCellData.toString() + "' WHERE EventID = '" + getID + "'");
                               
                            else if(getCellHeader.equals("Value"))
                            	Sweetdream.db.update("UPDATE events SET Value = '" + editedCellData.toString() + "' WHERE EventID = '" + getID + "'");
                               
                            else if(getCellHeader.equals("Active"))
                            {
                                String asdf = "";
                                if(editedCellData.toString().equals("true"))
                                	asdf = "Y";
                                else if(editedCellData.toString().equals("false"))
                                	asdf = "N";
                                
                                    Sweetdream.db.update("UPDATE events SET Active = '" + asdf + "' WHERE EventID = '" + getID + "'");
                               
                            }
                        } else if(Sweetdream.tabbedPaneViewDatabase.getTitleAt(Sweetdream.tabbedPaneViewDatabase.getSelectedIndex()).equals("Collage"))
                        {
                            if(getCellHeader.equals("Type"))
				    Sweetdream.db.update("UPDATE collage SET Type = '" + editedCellData.toString() + "' WHERE CollageID = '" + getID + "'");
                                
                            else if(getCellHeader.equals("Name"))
                            	    Sweetdream.db.update("UPDATE collage SET Name = '" + StringEscapeUtils.escapeSql(editedCellData.toString()) + "' WHERE CollageID = '" + getID + "'");
                               
                            else if(getCellHeader.equals("Folder or File Path"))
                            	    Sweetdream.db.update("UPDATE collage SET FilePath = '" + StringEscapeUtils.escapeSql(editedCellData.toString()) + "' WHERE CollageID = '" + getID + "'");
                               
                            else if(getCellHeader.equals("Category"))
                            	    Sweetdream.db.update("UPDATE collage SET Category = '" + editedCellData.toString() + "' WHERE CollageID = '" + getID + "'");
                        }
                    
                    } catch(SQLException ex2) { ex2.printStackTrace(); }
                }
            }
            }
        };
        artistTableModel.addColumn(" ");
        artistTableModel.addColumn("Artist");
        artistTableModel.addColumn("Voice Command");
        artistTableModel.addColumn("Times Played");
        artistTableModel.addColumn(" ");
        albumTableModel.addColumn(" ");
        albumTableModel.addColumn("Artist");
        albumTableModel.addColumn("Album");
        albumTableModel.addColumn("Voice Command");
        albumTableModel.addColumn("Album Cover");
        albumTableModel.addColumn("Year");
        albumTableModel.addColumn("Frequency");
        albumTableModel.addColumn(" ");
        playlistTableModel.addColumn("Tracks");
        playlistTableModel.addColumn(" ");
        eventTableModel.addColumn(" ");
        eventTableModel.addColumn("Time");
        eventTableModel.addColumn("Type");
        eventTableModel.addColumn("Value");
        eventTableModel.addColumn("!");
        eventTableModel.addColumn("Active");
        eventTableModel.addColumn(" ");
        collageTableModel.addColumn(" ");
        collageTableModel.addColumn("Type");
	collageTableModel.addColumn("Name");
        collageTableModel.addColumn("Folder or File Path");
        collageTableModel.addColumn("Category");
        collageTableModel.addColumn(" ");
        tableArtists = new JTable(artistTableModel);
        tableArtists.setFont(font12);
        tableArtists.getModel().addTableModelListener(tableUpdateListener);
        tableAlbums = new JTable(albumTableModel);
        tableAlbums.setFont(font10);
        tableAlbums.getModel().addTableModelListener(tableUpdateListener);
        tablePlaylists = new JTable(playlistTableModel);
        tablePlaylists.setFont(font10);
        tablePlaylists.getModel().addTableModelListener(tableUpdateListener);
        tableEvents = new JTable(eventTableModel);
        tableEvents.setFont(font10);
        tableEvents.getModel().addTableModelListener(tableUpdateListener);
        tableCollage = new JTable(collageTableModel);
        tableCollage.setFont(font10);
        tableCollage.getModel().addTableModelListener(tableUpdateListener);
        DefaultTableCellRenderer rendererSetWhiteFont = new DefaultTableCellRenderer();
        //rendererSetWhiteFont.setForeground(Color.white);
        TableColumn columnsArtistsTable = null;
        for(int i = 0; i < 4; i++)
        {
            columnsArtistsTable = tableArtists.getColumnModel().getColumn(i);
            if(i == 0)
            {
                columnsArtistsTable.setPreferredWidth(20);
                columnsArtistsTable.setCellRenderer(rendererSetWhiteFont);
                continue;
            }
            if(i == 1)
            {
                columnsArtistsTable.setPreferredWidth(270);
                continue;
            }
            if(i == 2)
            {
                columnsArtistsTable.setPreferredWidth(430);
            }
            if(i == 3)
            {
                columnsArtistsTable.setPreferredWidth(70);
            }
        }

        TableColumn columnsAlbumsTable = null;
        for(int i = 0; i < 7; i++)
        {
            columnsAlbumsTable = tableAlbums.getColumnModel().getColumn(i);
            if(i == 0)
            {
                columnsAlbumsTable.setPreferredWidth(20);
                columnsAlbumsTable.setCellRenderer(rendererSetWhiteFont);
                continue;
            }
            if(i == 1)
            {
                columnsAlbumsTable.setPreferredWidth(180);
                continue;
            }
            if(i == 2)
            {
                columnsAlbumsTable.setPreferredWidth(200);
                continue;
            }
            if(i == 3)
            {
                columnsAlbumsTable.setPreferredWidth(270);
                continue;
            }
            if(i == 4)
              columnsAlbumsTable.setPreferredWidth(270);
            else
              columnsAlbumsTable.setPreferredWidth(50);
            
        }

        tablePlaylists.getColumnModel().getColumn(0).setPreferredWidth(480);
        tablePlaylists.getColumnModel().getColumn(1).setPreferredWidth(50);
        TableColumn eventTypeColumn = tableEvents.getColumnModel().getColumn(2);
        JComboBox comboBox = new JComboBox();
          comboBox.addItem("Song");
          comboBox.addItem("Media");
          comboBox.addItem("Playlist");
          comboBox.addItem("Webpage");
        eventTypeColumn.setCellEditor(new DefaultCellEditor(comboBox));
        tableEvents.getColumnModel().getColumn(0).setPreferredWidth(20);
        tableEvents.getColumnModel().getColumn(0).setCellRenderer(rendererSetWhiteFont);
        tableEvents.getColumnModel().getColumn(1).setPreferredWidth(80);
        tableEvents.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableEvents.getColumnModel().getColumn(3).setPreferredWidth(300);
        tableEvents.getColumnModel().getColumn(4).setPreferredWidth(30);
        tableEvents.getColumnModel().getColumn(5).setPreferredWidth(40);
        tableEvents.getColumnModel().getColumn(6).setPreferredWidth(50);
        TableColumn columnsCollageTable = null;
        for(int i = 0; i < 5; i++)
        {
            columnsCollageTable = tableCollage.getColumnModel().getColumn(i);
            if(i == 0)
            {
                columnsCollageTable.setPreferredWidth(10);
                columnsCollageTable.setCellRenderer(rendererSetWhiteFont);
            }
            if(i == 1)
            {
                columnsCollageTable.setPreferredWidth(100);
                continue;
            }
            if(i == 2)
            {
                columnsCollageTable.setPreferredWidth(200);
                continue;
            }
            if(i == 3)
            {
                columnsCollageTable.setPreferredWidth(250);
                continue;
            }
            if(i == 4)
		    columnsCollageTable.setPreferredWidth(100);
            else
		    columnsCollageTable.setPreferredWidth(50);
        }

	
        tableArtists.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e)
            {
                int columnIndex = Sweetdream.tableArtists.columnAtPoint(e.getPoint());
                int rowIndex = Sweetdream.tableArtists.rowAtPoint(e.getPoint());
                Object getID = Sweetdream.artistTableModel.getValueAt(rowIndex, 0);
                Object rightmostColumnFunction = Sweetdream.artistTableModel.getValueAt(rowIndex, 4);
                String action = rightmostColumnFunction.toString();
                if(columnIndex == 4 && action.equals("Add"))
                {
                    eFunctions.addRowToDatabase(rowIndex, "artists");
                } else if(columnIndex == 4)
                {
                    int answer = JOptionPane.showConfirmDialog(new JFrame(), "Delete this artist?");
                    if(answer == 0)
                    {
                        Sweetdream.artistTableModel.removeRow(rowIndex);
                        try
                        {
                            Sweetdream.db.update("DELETE FROM artists WHERE artistid = '" + getID + "'");
                        }
                        catch(SQLException ex2) { ex2.printStackTrace(); }
                    }
                }
            }
            public void mousePressed(MouseEvent mouseevent) { }
            public void mouseReleased(MouseEvent mouseevent) { }
            public void mouseExited(MouseEvent mouseevent) { }
            public void mouseEntered(MouseEvent mouseevent) { }
        });
	

        tableAlbums.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e)
            {
                int columnIndex = Sweetdream.tableAlbums.columnAtPoint(e.getPoint());
                int rowIndex = Sweetdream.tableAlbums.rowAtPoint(e.getPoint());
                Object getID = Sweetdream.albumTableModel.getValueAt(rowIndex, 0);
                Object rightmostColumnFunction = Sweetdream.albumTableModel.getValueAt(rowIndex, 7);
                String action = rightmostColumnFunction.toString();
		
                if(columnIndex == 7 && action.equals("Add"))
			eFunctions.addRowToDatabase(rowIndex, "albums");
                else if(columnIndex == 7 && action.equals("Delete"))
                {
                    int answer = JOptionPane.showConfirmDialog(new JFrame(), "Delete this album?");
                    if(answer == 0)
                    {
                        Sweetdream.albumTableModel.removeRow(rowIndex);
                        try
                        {
                            Sweetdream.db.update("DELETE FROM albums WHERE id = '" + getID + "'");
                        }
                        catch(SQLException ex2) { ex2.printStackTrace(); }
                    }
                }
            }

            public void mousePressed(MouseEvent mouseevent) { }
            public void mouseReleased(MouseEvent mouseevent) { }
            public void mouseExited(MouseEvent mouseevent) { }
            public void mouseEntered(MouseEvent mouseevent) { }
        });
	
        tablePlaylists.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e)
            {
                int columnIndex = Sweetdream.tablePlaylists.columnAtPoint(e.getPoint());
                int rowIndex = Sweetdream.tablePlaylists.rowAtPoint(e.getPoint());
                Object getTrackPath = Sweetdream.playlistTableModel.getValueAt(rowIndex, 0);
                if(columnIndex == 1 && !getTrackPath.toString().equals("playlist image: ") && !getTrackPath.toString().equals(""))
                {
                    int answer = JOptionPane.showConfirmDialog(new JFrame(), "Delete this track from the playlist?");
                    if(answer == 0)
                    {
                        Sweetdream.playlistTableModel.removeRow(rowIndex);
                        try
                        {
                            Sweetdream.db.update("DELETE FROM playlist_contents WHERE PlaylistName = '" + Sweetdream.viewPlaylistSelectBox.getSelectedItem().toString() + "' AND TrackPath = '" + getTrackPath.toString() + "'");
                        }
                        catch(SQLException ex2) { ex2.printStackTrace(); }
                    }
                }
            }
            public void mousePressed(MouseEvent mouseevent) { }
            public void mouseReleased(MouseEvent mouseevent) { }
            public void mouseExited(MouseEvent mouseevent) { }
            public void mouseEntered(MouseEvent mouseevent) { }
        });
	
        tableEvents.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e)
            {
                int columnIndex = Sweetdream.tableEvents.columnAtPoint(e.getPoint());
                int rowIndex = Sweetdream.tableEvents.rowAtPoint(e.getPoint());
                Object getID = Sweetdream.eventTableModel.getValueAt(rowIndex, 0);
                Object getTime = Sweetdream.eventTableModel.getValueAt(rowIndex, 1);
                Object getType = Sweetdream.eventTableModel.getValueAt(rowIndex, 2);
                Object getValue = Sweetdream.eventTableModel.getValueAt(rowIndex, 3);
                String action = Sweetdream.eventTableModel.getValueAt(rowIndex, 6).toString();
                String active = Sweetdream.eventTableModel.getValueAt(rowIndex, 5).toString();
                if(columnIndex == 6 && action.equals("Add"))
			eFunctions.addRowToDatabase(rowIndex, "events");
                else if(columnIndex == 6 && action.equals("Delete"))
                {
                    int answer = JOptionPane.showConfirmDialog(new JFrame(), "Delete this scheduled event?");
                    if(answer == 0)
                    {
                        Sweetdream.eventTableModel.removeRow(rowIndex);
                        try { Sweetdream.db.update("DELETE FROM events WHERE EventID = '" + getID.toString() + "'"); }
                        	catch(SQLException ex2) { ex2.printStackTrace(); }
                    }
                } else if(active.equals("Y"))
                {
                    try { Sweetdream.db.update("UPDATE events SET Active = 'Y' WHERE EventID = '" + getID.toString() + "'"); }
                    	catch(SQLException ex2) { ex2.printStackTrace(); }
                } else if(active.equals("N"))
                {
                    try {  Sweetdream.db.update("UPDATE events SET Active = 'N' WHERE EventID = '" + getID.toString() + "'"); }
                    	catch(SQLException ex2) { ex2.printStackTrace(); }
                }
            }
            public void mousePressed(MouseEvent mouseevent) { }
            public void mouseReleased(MouseEvent mouseevent) { }
            public void mouseExited(MouseEvent mouseevent) { }
            public void mouseEntered(MouseEvent mouseevent) { }
        });
	
        tableCollage.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e)
            {
                int columnIndex = Sweetdream.tableCollage.columnAtPoint(e.getPoint());
                int rowIndex = Sweetdream.tableCollage.rowAtPoint(e.getPoint());
                Object getID = Sweetdream.collageTableModel.getValueAt(rowIndex, 0);
		Object getType = Sweetdream.collageTableModel.getValueAt(rowIndex, 1);
                Object getName = Sweetdream.collageTableModel.getValueAt(rowIndex, 2);
                Object getFilePath = Sweetdream.collageTableModel.getValueAt(rowIndex, 3);
                Object getCategory = Sweetdream.collageTableModel.getValueAt(rowIndex, 4);
                String action = "";
                if(rowIndex != 0 || columnIndex == 5)
			action = Sweetdream.collageTableModel.getValueAt(rowIndex, 5).toString();
                else return;
                
                if(columnIndex == 5 && action.equals("Add"))
			eFunctions.addRowToDatabase(rowIndex, "collage");
                else if(columnIndex == 5 && action.equals("Delete"))
                {
                    int answer = JOptionPane.showConfirmDialog(new JFrame(), "Delete this collage item?");
                    if(answer == 0)
                    {
                        Sweetdream.collageTableModel.removeRow(rowIndex);
                        try
                        {
                            Sweetdream.db.update("DELETE FROM collage WHERE CollageID = '" + getID.toString() + "'");
                        }
                        catch(SQLException ex2) { ex2.printStackTrace(); }
                    }
                }
            }
	    public void mousePressed(MouseEvent mouseevent) { }
            public void mouseReleased(MouseEvent mouseevent) { }
            public void mouseExited(MouseEvent mouseevent) { }
            public void mouseEntered(MouseEvent mouseevent) { }

        });
        Box panelArtists = new Box(1);
        tabbedPaneViewDatabase.addTab("Artists", null, panelArtists, "Artists");
        tabbedPaneViewDatabase.setMnemonicAt(0, 49);
        Box viewVoiceDatabasePane = new Box(1);
        scrollViewArtistDatabase = new JScrollPane(tableArtists);
        viewVoiceDatabasePane.add(scrollViewArtistDatabase);
        panelArtists.add(viewVoiceDatabasePane);
        Box panelAlbums = new Box(1);
        tabbedPaneViewDatabase.addTab("Albums", null, panelAlbums, "Albums");
        tabbedPaneViewDatabase.setMnemonicAt(1, 50);
        Box viewAlbumDatabasePane = new Box(1);
        scrollViewAlbumDatabase = new JScrollPane(tableAlbums);
        viewAlbumDatabasePane.add(scrollViewAlbumDatabase);
        panelAlbums.add(viewAlbumDatabasePane);
        Box panelPlaylists = new Box(1);
        tabbedPaneViewDatabase.addTab("Playlists", null, panelPlaylists, "Playlists");
        tabbedPaneViewDatabase.setMnemonicAt(2, 51);
        Box viewPlaylistDatabasePane = new Box(1);
        
        	horizPlaylistVoiceRow.add(new JLabel("Voice command:"));
		horizPlaylistVoiceRow.add(playlistVoiceCommandBox);
		horizPlaylistVoiceRow.add(playlistSetVoiceCommandBtn);
        viewPlaylistDatabasePane.add(horizPlaylistVoiceRow);
        scrollViewPlaylistDatabase = new JScrollPane(tablePlaylists);
        viewPlaylistDatabasePane.add(scrollViewPlaylistDatabase);
        Box horizRow2 = new Box(0);
        	horizRow2.add(playlistCleanBtn);
		horizRow2.add(playlistDeleteBtn);
        viewPlaylistDatabasePane.add(horizRow2);
        panelPlaylists.add(viewPlaylistSelectBox);
        viewPlaylistSelectBox.setPreferredSize(new Dimension(250, 20));
        panelPlaylists.add(viewPlaylistDatabasePane);
        Box panelEvents = new Box(1);
        tabbedPaneViewDatabase.addTab("Events", null, panelEvents, "Events");
        tabbedPaneViewDatabase.setMnemonicAt(3, 52);
        Box viewEventDatabasePane = new Box(1);
        scrollViewEvents = new JScrollPane(tableEvents);
        viewEventDatabasePane.add(scrollViewEvents);
        panelEvents.add(viewEventDatabasePane);
        Box panelCollage = new Box(1);
        tabbedPaneViewDatabase.addTab("Collage", null, panelCollage, "Collage");
        tabbedPaneViewDatabase.setMnemonicAt(4, 53);
        Box viewCollageDatabasePane = new Box(1);
        scrollViewCollageDatabase = new JScrollPane(tableCollage);
           scrollViewCollageDatabase.setMaximumSize(new Dimension(650, 450));
           scrollViewCollageDatabase.setPreferredSize(new Dimension(650, 400));
        viewCollageDatabasePane.add(scrollViewCollageDatabase);
        panelCollage.add(viewCollageDatabasePane);
        
        logPanel.add(saveLogBtn);
        logPanel.add(saveLogDisplay);
	
        saveLogDisplay.setPreferredSize(new Dimension(200, 25));
        logPanel.add(logViewPane);
        logViewPane.setPreferredSize(new Dimension(380, 250));
        logViewPane.setMaximumSize(new Dimension(380, 250));
        logPanel.setPreferredSize(new Dimension(500, 300));
        logViewFrame.setPreferredSize(new Dimension(500, 320));
        logViewFrame.setMaximumSize(new Dimension(500, 320));
        logViewFrame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
	logViewFrame.add(logPanel);
        logViewFrame.pack();
	
        Box boxOfEverything = new Box(0);
        boxRightside = new Box(1);
        root_music = new CheckNode("root_music", true, false);
        root_music.add(new CheckNode(MusicDirectoryPath));
        root_touch = new CheckNode("root_touch", true, false);
        root_touch.add(new CheckNode(MusicDirectoryPath));
        try
        {
            librarytreeModel = null;
        }
        catch(NullPointerException npe)
        {
            librarytreeModel = null;
            npe.printStackTrace();
        }
        tree = new JTree(librarytreeModel);
        tree.setCellRenderer(new CheckRenderer());
        tree.getSelectionModel().setSelectionMode(1);
        tree.addMouseListener(new NodeSelectionListener(tree));
        tree.setRootVisible(true);
        tree.setFont(font10);
        tree.setRowHeight(0);
        tree.setVisibleRowCount(27);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setMaximumSize(new Dimension(400, 1200));
        tree.setBackground(new Color(195, 150, 220));
        
	scrollViewMusicLibrary = new JScrollPane(tree);
	
	//layeredPane.add(scrollViewMusicLibrary, 1);
	
	
	
	//scrollViewMusicLibrary.setBackground(new Color(255,255,255));
	scrollViewMusicLibrary.setPreferredSize(new Dimension(350, 620));
	scrollViewMusicLibrary.setMaximumSize(new Dimension(400, 1200));
	
        root_touch = new CheckNode("root_touch", true, false);
        root_touch.add(new CheckNode(MusicDirectoryPath));
        try {
            touchtreeModel = null;
        }
        catch(NullPointerException npe)
        {
            touchtreeModel = null;
            npe.printStackTrace();
        }
        tree_touch = new JTree(touchtreeModel);
        tree_touch.setCellRenderer(new CheckRenderer());
        tree_touch.getSelectionModel().setSelectionMode(1);
        tree_touch.addMouseListener(new NodeSelectionListener(tree_touch));
        tree_touch.setRootVisible(true);
        tree_touch.setFont(new Font("Monospaced", 0, 50));
	
	
        root_video = new CheckNode("root_video", true, false);
        root_video.add(new CheckNode(VideoDirectoryPath));
        try {
            videotreeModel = null;
        }
        catch(NullPointerException npeq)
        {
            videotreeModel = null;
            npeq.printStackTrace();
        }
        tree_video = new JTree(videotreeModel);
        tree_video.setCellRenderer(new CheckRenderer());
        tree_video.getSelectionModel().setSelectionMode(1);
        tree_video.addMouseListener(new NodeSelectionListener(tree_video));
        tree_video.setRootVisible(true);
        tree_video.setFont(font10);
        tree_video.setRowHeight(17);
        tree_video.putClientProperty("JTree.lineStyle", "Angled");
        tree_video.setBackground(new Color(255, 204, 153));
        
	scrollViewVideoLibrary = new JScrollPane(tree_video);
	scrollViewVideoLibrary.setBackground(new Color(255,255,255));
	scrollViewVideoLibrary.setPreferredSize(new Dimension(340, 450));
	scrollViewVideoLibrary.setMaximumSize(new Dimension(500, 1200));
	
        root_audio = new CheckNode("root_audio", true, false);
        root_audio.add(new CheckNode(AudioDirectoryPath));
          
        try
        {
            audiotreeModel = null;
        }
        catch(NullPointerException npeq) { audiotreeModel = null; npeq.printStackTrace(); }
        
        tree_audio = new JTree(audiotreeModel);
        tree_audio.setCellRenderer(new CheckRenderer());
        tree_audio.getSelectionModel().setSelectionMode(1);
        tree_audio.addMouseListener(new NodeSelectionListener(tree_audio));
        tree_audio.setRootVisible(true);
        tree_audio.setFont(font10);
        tree_audio.setRowHeight(17);
        tree_audio.putClientProperty("JTree.lineStyle", "Angled");
        tree_audio.setBackground(new Color(204, 229, 255));
        
	scrollViewAudioLibrary = new JScrollPane(tree_audio);
	scrollViewAudioLibrary.setBackground(new Color(255,255,255));
	scrollViewAudioLibrary.setPreferredSize(new Dimension(340, 450));
	scrollViewAudioLibrary.setMaximumSize(new Dimension(500, 1200));
	
	root_photo = new CheckNode("tree_photo", true, false);
        root_photo.add(new CheckNode(PhotoDirectoryPath));
        try
        {
            phototreeModel = null;
        }
        catch(NullPointerException npeq) { phototreeModel = null; npeq.printStackTrace(); 
        }
        tree_photo = new JTree(phototreeModel);
        tree_photo.setCellRenderer(new CheckRenderer());
        tree_photo.getSelectionModel().setSelectionMode(1);
        tree_photo.addMouseListener(new NodeSelectionListener(tree_photo));
        tree_photo.setRootVisible(true);
        tree_photo.setFont(font10);
        tree_photo.setRowHeight(17);
        tree_photo.putClientProperty("JTree.lineStyle", "Angled");
        tree_photo.setBackground(new Color(255, 229, 204));
	scrollViewPhotoLibrary = new JScrollPane(tree_photo);
	scrollViewPhotoLibrary.setMaximumSize(new Dimension(500, 1200));
	scrollViewPhotoLibrary.setPreferredSize(new Dimension(340, 450));
	
        tree_audiocategory = new JTree(categorytreeModel);
        tree_audiocategory.addMouseListener(categorytreeClick);
        tree_audiocategory.setFont(font15); 
        
        tree_playlist = new JTree(playlisttreeModel);
        tree_playlist.addMouseListener(playlistTreeClick);
        tree_playlist.setBackground(new Color(255, 255, 204));
       
        tree_last25 = new JTree(last25treeModel);
        tree_last25.addMouseListener(last25treeClick);
        tree_last25.setBackground(new Color(224, 224, 224));

	tabbedPaneLibrary.setTabPlacement(JTabbedPane.RIGHT);
        tabbedPaneLibrary.addTab("<html><br>Music<br></html>", null, scrollViewMusicLibrary, "All Music");
        scrollViewMusicCategory = new JScrollPane(tree_audiocategory);
        scrollViewMusicCategory.setPreferredSize(new Dimension(340, 450));
        
        tabbedPaneLibrary.addTab("<html><br>Moods<br></html>", null, scrollViewMusicCategory, "Music by Category");
        
        
        scrollViewPlaylist = new JScrollPane(tree_playlist);
        scrollViewPlaylist.setPreferredSize(new Dimension(340, 450));
        tabbedPaneLibrary.addTab("<html><br>Playlist<br></html>", null, scrollViewPlaylist, "Playlist");
        
        scrollViewLast25 = new JScrollPane(tree_last25);
        scrollViewLast25.setMaximumSize(new Dimension(500, 1200));
        scrollViewLast25.setPreferredSize(new Dimension(340, 450));
        tabbedPaneLibrary.addTab("<html><br>Last 25<br></html>", null, scrollViewLast25, "Last 25 Played");
        
        // tabbedPaneLibrary.addTab("<html><br>Search<br></html>", null, scrollSearch, "Search");
        // boxRightside.add(searchBox);
       	// searchBox.setMaximumSize(new Dimension(300, 20));  
       	
        boxRightside.add(tabbedPaneLibrary);

        

	
    boxLibraryButtons = new JPanel(); //new GridLayout(16, 1, 2, 2)
    
    boxLibraryButtons.setLayout(new BoxLayout(boxLibraryButtons, BoxLayout.PAGE_AXIS));                                                                 
    
	//boxLibraryButtons.setAlignmentY(Component.BOTTOM_ALIGNMENT); //right
//	boxLibraryButtons.setPreferredSize(new Dimension(42, 510));
	boxLibraryButtons.setMaximumSize(new Dimension(28, 410));                          
        boxLibraryButtons.add(Box.createRigidArea(new Dimension(2, 5)));                 
        boxLibraryButtons.setAlignmentX(Component.CENTER_ALIGNMENT);

        	        //Create the slider
final JSlider volumeSlider = new JSlider(JSlider.VERTICAL,
                                      FPS_MIN, FPS_MAX, FPS_INIT);


class volumeSliderListener implements ChangeListener {  
        public void stateChanged(ChangeEvent event) {  
            final int gg = volumeSlider.getValue();  
            eFunctions.adjustVolume(gg);
            currentVolumeLevel = gg;
        }  
    }  
       
volumeSlider.addChangeListener(new volumeSliderListener());


	boxLibraryButtons.add(volumeSlider);
		volumeSlider.setPreferredSize(new Dimension(20, 70));
		volumeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxLibraryButtons.add(Box.createRigidArea(new Dimension(2, 4)));  
                                    
        boxLibraryButtons.add(muteBtn);
        	boxLibraryButtons.setBackground(Color.white);
        	muteBtn.setToolTipText("Mute / Unmute audio");
		//muteBtn.setContentAreaFilled(false);
		muteBtn.setMnemonic(KeyEvent.VK_M);
		muteBtn.setPreferredSize(new Dimension(42, 28));    
		muteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
	boxLibraryButtons.add(Box.createRigidArea(new Dimension(2, 4)));
	boxLibraryButtons.add(refreshBtn);
        	refreshBtn.setToolTipText("Refresh library");
		//refreshBtn.setContentAreaFilled(false);
		refreshBtn.setMnemonic(82);
		refreshBtn.setPreferredSize(new Dimension(42, 28));
		refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
	boxLibraryButtons.add(Box.createRigidArea(new Dimension(2, 4)));	
        boxLibraryButtons.add(expandCollapseBtn);
        	expandCollapseBtn.setToolTipText("Expand library");
		//expandCollapseBtn.setContentAreaFilled(false);
		expandCollapseBtn.setMnemonic(69);
		expandCollapseBtn.setPreferredSize(new Dimension(42, 28));
		expandCollapseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
	boxLibraryButtons.add(Box.createRigidArea(new Dimension(2, 4)));
        boxLibraryButtons.add(addToPlaylistBtn);
        	addToPlaylistBtn.setToolTipText("Add selected song to playlist, otherwise, add song that is playing ");
		//addToPlaylistBtn.setContentAreaFilled(false);
		addToPlaylistBtn.setMnemonic(76);
		addToPlaylistBtn.setPreferredSize(new Dimension(42, 28));
		addToPlaylistBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
	boxLibraryButtons.add(Box.createRigidArea(new Dimension(2, 4)));
	//boxLibraryButtons.add(ScrollDownBtn);
        	ScrollDownBtn.setToolTipText("Scroll down 5 lines");
		//ScrollDownBtn.setContentAreaFilled(false);
		ScrollDownBtn.setMnemonic(76);
		ScrollDownBtn.setPreferredSize(new Dimension(42, 28));
		ScrollDownBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
	//boxLibraryButtons.add(Box.createRigidArea(new Dimension(2, 5)));
	//boxLibraryButtons.add(ScrollUpBtn);
        	ScrollUpBtn.setToolTipText("Scroll up 5 lines");
		//ScrollUpBtn.setContentAreaFilled(false);
		ScrollUpBtn.setMnemonic(76);
		ScrollUpBtn.setPreferredSize(new Dimension(42, 28));
		ScrollUpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
	//boxLibraryButtons.add(Box.createRigidArea(new Dimension(2, 5)));
	boxLibraryButtons.add(saveMoodBtn);
		saveMoodBtn.setToolTipText("Save checked artists as new Mood");
		saveMoodBtn.setPreferredSize(new Dimension(42, 28));
		saveMoodBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		//saveMoodBtn.setContentAreaFilled(false);
	boxLibraryButtons.add(Box.createRigidArea(new Dimension(2, 4)));
	boxLibraryButtons.add(toggleFrequencyFontBtn);
		toggleFrequencyFontBtn.setToolTipText("Toggle Library Frequency Font");
		toggleFrequencyFontBtn.setPreferredSize(new Dimension(42, 28));
		toggleFrequencyFontBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		//toggleFrequencyFontBtn.setContentAreaFilled(false);
	                                                                                                               
       	
       /* This is what came together to try and keep this set of buttons floating underneath the tabs of the main library JTabbedPane
       It is not quite perfect, but still nice. */
      Rectangle tabBounds = tabbedPaneLibrary.getBoundsAt(0);
      Container glassPane = (Container) frame.getRootPane().getGlassPane();
      glassPane.setVisible(true);
      glassPane.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      gbc.fill = GridBagConstraints.NONE;
    //  int margin = tabbedPaneLibrary.getWidth() - (tabBounds.x + tabBounds.width);  // - (tabBounds.x + tabBounds.width);  
    //     margin += (tabBounds.width - boxLibraryButtons.getPreferredSize().width) / 2;  
    //  System.out.println("margin: " + margin);
      gbc.insets = new Insets(0, 10, 10, 10);
      gbc.anchor = GridBagConstraints.SOUTHEAST;
      glassPane.add(boxLibraryButtons, gbc);
       
       
       
       
        boxLeftside.add(Box.createRigidArea(new Dimension(2, 8)));

	JPanel boxButtons = new JPanel(new GridLayout(1, 3, 2, 2));
        	boxButtons.setPreferredSize(new Dimension(340, 50));
		boxButtons.setMaximumSize(new Dimension(340, 50));
		boxButtons.setBackground(Color.white);
		boxButtons.add(prevBtn);
        prevBtn.setMnemonic(66);
        prevBtn.setToolTipText("Previous");
        //prevBtn.setContentAreaFilled(false);
        boxButtons.add(playBtn);
        	playBtn.setMnemonic(80);
		playBtn.setToolTipText("Play");
	//	playBtn.setContentAreaFilled(false);
        boxButtons.add(stopBtn);
        	stopBtn.setMnemonic(83);
		stopBtn.setToolTipText("Stop");
	//	stopBtn.setContentAreaFilled(false);
        boxButtons.add(nextBtn);
        	nextBtn.setMnemonic(78);  // ALT + N
		nextBtn.setToolTipText("Next");
	//	nextBtn.setContentAreaFilled(false);
        
     //  statusPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	//statusPane.setBorder(BorderFactory.createTitledBorder(""));
        statusPane.setPreferredSize(new Dimension(340, 75));
        statusPane.setMaximumSize(new Dimension(340, 75));
       // statusPane.setMinimumSize(new Dimension(340, 75));
      //  statusPane.setBackground(Color.getHSBColor(299, 5, 92) );
     //   statusPane.setBackground(new Color(255,255,255));
	//statusPane.add(Box.createRigidArea(new Dimension(2, 18)));
        statusPane.add(statusDisplay);
        	statusDisplay.setFont(new Font("SansSerif", 0, 15));
        
		statusPane.setBackground(Color.white);
		//statusDisplay.setPreferredSize(new Dimension(340, 75));
		//statusDisplay.setMaximumSize(new Dimension(340, 75));
		statusDisplay.setMinimumSize(new Dimension(340, 50));
		statusDisplay.setHorizontalTextPosition(JLabel.CENTER);
		statusDisplay.setVerticalTextPosition(JLabel.BOTTOM);
        statusPane.add(tracksTransferredDisplay);
        statusPane.add(percentageBar, "Center");
        
        voiceCommandPane.add(voiceCommandDisplay);
        	voiceCommandDisplay.setFont(new Font("SansSerif", 0, 15));
        	voiceCommandDisplay.setMinimumSize(new Dimension(340, 40));
        	voiceCommandDisplay.setHorizontalTextPosition(JLabel.CENTER);
        	
        percentageBar.setVisible(false);
        percentageBar.setStringPainted(true);
        percentageBar.setValue(0);
        
        Box boxProgressBar = new Box(0);
		boxProgressBar.setPreferredSize(new Dimension(340, 30));
		boxProgressBar.setMaximumSize(new Dimension(340, 30));
		boxProgressBar.setBackground(Color.white);
		boxProgressBar.add(timeTravelSelectBox);
			timeTravelSelectBox.setFont(font10);
			timeTravelSelectBox.setToolTipText("Skip to this percentage of track currently playing");
			timeTravelSelectBox.setMaximumSize(new Dimension(70, 30));
			timeTravelSelectBox.setPreferredSize(new Dimension(40, 30));
		boxProgressBar.add(Box.createHorizontalStrut(3));
		boxProgressBar.add(progressBar);
			progressBar.addMouseMotionListener(sliderDrag);
			progressBar.setPreferredSize(new Dimension(270, 20));
		boxProgressBar.add(Box.createHorizontalStrut(3));
		boxProgressBar.add(songTimeDisplay);
		boxProgressBar.add(songDurationDisplay);
        
        imagePane.setMinimumSize(new Dimension(350, 140));
        imagePane.setMaximumSize(new Dimension(350, 400));
        imagePane.addMouseListener(imageClick);
        imagePane.addMouseListener(new ImagePopup());
        imagePane.add(coverLabel);
	
        controlsPane = new JPanel(new GridLayout(2, 3, 2, 2));
        controlsPane.setPreferredSize(new Dimension(250, 100));
	controlsPane.setMaximumSize(new Dimension(340, 100));
        controlsPane.add(themeTextBox);
        	themeTextBox.setPreferredSize(new Dimension(115, 25));
		themeTextBox.setFont(font10);
		themeDisplay.setToolTipText("Theme to search for");
        controlsPane.add(themeDisplay, "After");
        	themeDisplay.setFont(font10);
		themeDisplay.setMaximumSize(new Dimension(115, 30));
        controlsPane.add(tracksLeftSelectBox);
		tracksLeftSelectBox.setFont(new Font("Dialog", 0, 15));
		tracksLeftSelectBox.setPreferredSize(new Dimension(190, 25));
		tracksLeftSelectBox.setMaximumSize(new Dimension(190, 30));
		tracksLeftSelectBox.setToolTipText("Select number of tracks until action occurs");
        controlsPane.add(playTypeSelectBox);
		playTypeSelectBox.setPreferredSize(new Dimension(115, 20));
        	playTypeSelectBox.setFont(new Font("Dialog", 0, 14));
		playTypeSelectBox.setToolTipText("Select play mode");


        controlsPane.add(tracksLeftDisplay, "After");
        tracksLeftDisplay.setFont(font12);
        controlsPane.add(tracksLeftActionSelectBox);
        	tracksLeftActionSelectBox.setPreferredSize(new Dimension(190, 20));
		tracksLeftActionSelectBox.setMaximumSize(new Dimension(190, 30));
		tracksLeftActionSelectBox.setToolTipText("Select action to take when tracks left reaches 0");
		tracksLeftActionSelectBox.setVisible(false);
		
	
      //  boxLeftside.add(Box.createRigidArea(new Dimension(2, 2)));
        Box boxSelectboxes = new Box(0);
	boxSelectboxes.setPreferredSize(new Dimension(340, 40));
        boxSelectboxes.setMaximumSize(new Dimension(340, 40));
	
        boxSelectboxes.add(playlistSelectBox);
		playlistSelectBox.setToolTipText("Select saved playlist");
		playlistSelectBox.setFont(font12);
		playlistSelectBox.setMaximumSize(new Dimension(170, 40));
        boxSelectboxes.add(Box.createHorizontalStrut(7));
        boxSelectboxes.add(ActionSelectBox);
       		 ActionSelectBox.setToolTipText("Perform general tasks/actions");
		 ActionSelectBox.setFont(font12);
		 ActionSelectBox.setMaximumSize(new Dimension(170, 40));
        
        JPanel boxMoreButtons = new JPanel(new GridLayout(1, 5, 2, 2));
        boxMoreButtons.setPreferredSize(new Dimension(340, 40));
        boxMoreButtons.setMaximumSize(new Dimension(340, 40));
	boxMoreButtons.setBackground(Color.white);
        boxMoreButtons.add(optionsBtn);
        	optionsBtn.setMnemonic(79);
		optionsBtn.setToolTipText("Options");
		//optionsBtn.setSize(new Dimension(92, 40));
        boxMoreButtons.add(logBtn);
        	logBtn.setMnemonic(76);
		logBtn.setToolTipText("View Log");
		//logBtn.setSize(new Dimension(92, 40));
        boxMoreButtons.add(eventsBtn);
        	eventsBtn.setMnemonic(69);
		eventsBtn.setToolTipText("Events");
        boxMoreButtons.add(collageDisplayBtn);
		collageDisplayBtn.setToolTipText("Display Media Collage");
		collageDisplayBtn.setMnemonic(67);
     //   boxMoreButtons.add(touchDisplayBtn);   UNCOMMENT FOR LIBRARY TOUCH DISPLAY
        boxMoreButtons.add(showLibraryBtn);
		showLibraryBtn.setToolTipText("Show/Hide Library");
		showLibraryBtn.setMnemonic(72);
        
	boxLeftside.add(boxButtons);
      //boxLeftside.add(Box.createRigidArea(new Dimension(2, 10)));
	boxLeftside.add(statusPane);
      //boxLeftside.add(Box.createRigidArea(new Dimension(2, 10)));
	boxLeftside.add(Box.createRigidArea(new Dimension(2, 5)));
	boxLeftside.add(boxProgressBar);
        boxLeftside.add(Box.createRigidArea(new Dimension(2, 5)));
	boxLeftside.add(Box.createVerticalGlue());
        boxLeftside.add(imagePane);
	//boxLeftside.add(Box.createVerticalGlue());
        boxLeftside.add(Box.createRigidArea(new Dimension(2, 10)));
	boxLeftside.add(Box.createVerticalGlue());
	boxLeftside.add(boxSelectboxes);
	//boxLeftside.add(Box.createRigidArea(new Dimension(2, 5)));
        boxLeftside.add(controlsPane);
	boxLeftside.add(boxMoreButtons);
	
	//boxLeftside.add(Box.createVerticalGlue());
        boxLeftside.add(Box.createRigidArea(new Dimension(2, 5)));
	
        tabbedFrontPanel.addTab("Playlist", null, currentPlaylistPane, "Playlist");
        currentPlaylistPane.setBorder(BorderFactory.createTitledBorder("Current Playlist"));
        currentPlaylistPane.setVisible(false);
        currentPlaylistPane.setMinimumSize(new Dimension(270, 70));
        currentPlaylistPane.setPreferredSize(new Dimension(270, 75));
        currentPlaylistPane.add(playlistActionSelectBox);
        	playlistActionSelectBox.setPreferredSize(new Dimension(130, 20));
        tabbedFrontPanel.addTab("Lyrics", null, lyricsPane, "Lyrics");
        lyricsPane.setBorder(BorderFactory.createTitledBorder("Lyrics"));
        lyricsPane.setVisible(false);
        lyricsDisplay.addMouseListener(lyricsClick);
        lyricsDisplay.setBackground(new Color(238, 238, 238));
        lyricsDisplay.setFont(font10);

        boxOfEverything.add(Box.createRigidArea(new Dimension(10, 1)));
        boxOfEverything.add(boxLeftside);
	boxOfEverything.add(photoLabel);
		photoLabel.addMouseListener(photoClick);
        boxOfEverything.add(Box.createRigidArea(new Dimension(10, 1)));
        boxOfEverything.add(boxRightside);

	
	
	
        touchFrame.add(touchMainPanel);
        scrollTouchMusic = new JScrollPane(tree_touch);
        touchMainPanel.add(scrollTouchMusic);
        touchMainPanel.setBackground(Color.yellow);
        scrollTouchMusic.setPreferredSize(new Dimension(510, 700));
        touchArtistsPanel.setMinimumSize(new Dimension(200, 785));
        touchSongsPanel.setMinimumSize(new Dimension(200, 785));
        touchMainPanel.setMinimumSize(new Dimension(300, 85));
        touchMainPanel.setPreferredSize(new Dimension(300, 85));
        touchMainPanel.setMaximumSize(new Dimension(800, 1200));
        touchMainPanel.setBackground(Color.yellow);
        touchMainPanel.setBorder(new LineBorder(Color.white, 4));
        touchMainPanel.setVisible(true);
        touchFrame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
        touchFrame.setUndecorated(true);
        imageFrame.add(imageFrameLabel);
        imageFrameLabel.setBorder(new LineBorder(Color.white));
      //  imageFrameLabel.addMouseListener(toggleImageFrameClick);
        imageFrame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
        imageFrame.setUndecorated(true);
        imageFrame.setSize(400, 400);
        
	frame.add(panelOfEverything);
	//frame.setMinimumSize(new Dimension(800, 400));
	frame.pack();
	//com.sun.awt.AWTUtilities.setWindowShape(frame, new RoundRectangle2D.Float(0, 0, frame.getWidth(), frame.getHeight(), 35, 35));
	 
	
        panelOfEverything.add(boxOfEverything);
        panelOfEverything.setLayout(new BoxLayout(panelOfEverything, BoxLayout.PAGE_AXIS));
        panelOfEverything.setBackground(Color.white);
        boxOfEverything.setMinimumSize(new Dimension(900, 785));
        controlsPane.setBackground(Color.white);
        imagePane.setBackground(Color.white);
        progressBar.setBackground(Color.white);
        statusDisplay.setBackground(Color.white);
    //    frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(1);
        frame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
       // panelOfEverything.setBorder(new LineBorder(Color.gray, 5));
        countdownFrame.add(countdownLabel);
        countdownLabel.setFont(new Font("Dialog", 0, 100));
        countdownFrame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
        countdownFrame.setSize(400, 400);
        frame.setVisible(false);
        
        
    }

    public void opened(Object stream, Map properties)
    {
        if(stream.toString().contains("sweetdream") && stream.toString().contains("sounds"))
		isSystemSound = true;
        else
        {
            isSystemSound = false;
            secondsAmount = 0L;
            audioInfo = properties;
            String filepath = stream.toString();
            secondsLength = getDurationSeconds(properties);
            progressBar.setMaximum(secondsLength);
            int durationMinutes = secondsLength / 60;
            String durationSeconds = formatter.format(secondsLength % 60 - 1);
            
	    if(durationSeconds.equals("-01"))
		    durationSeconds = formatter.format(0L);
            
            songDurationDisplay.setText(" / " + durationMinutes + ":" + durationSeconds);
            if (properties.containsKey("author") && !properties.get("author").toString().contains("Unknown"))
		    currentPlayingArtist = properties.get("author").toString().replace("ORT=", "");
            else
		    currentPlayingArtist = eFunctions.parseSongPath(filepath, "artist").replace("ORT=", "");
            
            if (properties.containsKey("title"))
		    currentPlayingSong = properties.get("title").toString();
            else
		    currentPlayingSong = eFunctions.parseSongPath(filepath, "song");
            
            if (properties.containsKey("album"))
		    currentPlayingAlbum = properties.get("album").toString();
            else
		    currentPlayingAlbum = eFunctions.parseSongPath(filepath, "album");
            
            if (currentPlayingArtist == null || currentPlayingArtist.equals("") || currentPlayingArtist.equals(":"))
		    currentPlayingArtist = filepath.substring(filepath.lastIndexOf(File.separator));
            
            if(!playCurrentPlaylist) getArtistImage(currentPlayingArtist, currentPlayingAlbum);
            
            eFunctions.playingStartsStuffToDo();
        }
    }

    public void progress(int bytesread, long microseconds, byte pcmdata[], Map properties)
    {
        if(properties.containsKey("audio.length.bytes"))
		byteslength = ((Integer)audioInfo.get("audio.length.bytes")).intValue();
        
        if(properties.containsKey("mp3.position.microseconds"))
		secondsAmount = Math.round(((Long)properties.get("mp3.position.microseconds")).longValue() / 0xf4240L);
        else secondsAmount = Math.round(microseconds / 0xf4240L);
        
        String str = Long.toString(secondsAmount);
        int secondsAmountInt = Integer.parseInt(str);
        int currentMinutes = Math.round(secondsAmount / 60L);
        String currentSeconds = formatter.format(secondsAmount % 60L);
        songTimeDisplay.setText(currentMinutes + ":" + currentSeconds);
        String audioformat = (String)properties.get("audio.type");
        if((long)progressBar.getValue() - secondsAmount <= 4L && (long)progressBar.getValue() - secondsAmount >= -4L)
		progressBar.setValue(secondsAmountInt);
        
        if(total <= 0L) total = Math.round(getDurationSeconds(audioInfo));
        if(total <= 0L) total = -1L;
        if(bytesread > 0 && byteslength > 0) progress = (((float)bytesread * 1.0F) / (float)byteslength) * 1.0F;
        
        if(properties.containsKey("audio.type"))
        {
            if(audioformat.equalsIgnoreCase("mp3"))
            {
                if(total > 0L)
		     secondsAmount = (long)((float)total * progress);
                else secondsAmount = -1L;
                
            } else
            if(audioformat.equalsIgnoreCase("wave"))
		    secondsAmount = (long)((float)total * progress);
            else secondsAmount = Math.round(microseconds / 0xf4240L);
            
        } else secondsAmount = Math.round(microseconds / 0xf4240L);
        
        if(secondsAmount < 0L)
		secondsAmount = Math.round(microseconds / 0xf4240L);
    }

    public void stateUpdated(BasicPlayerEvent event)
    {
        int state = event.getCode();
        Object obj = event.getDescription();
        if(state == 8 && !isSystemSound)
        {
            if(tracksLeft != 0)
            {
                if(tracksLeft > 0 && progressBar.getValue() > 5)
                {
                    tracksLeft--;
                    tracksLeftDisplay.setText(tracksLeft + " left ");
                }
                if((!songDurationDisplay.getText().equals(" / 0:00") || currentPlayingSong.contains("flac")) && !songDurationDisplay.getText().equals(" / 0:01") && !songDurationDisplay.getText().equals(" / 0:02"))
                {
                    errorsInARow = 0;
                    nextTrack("automatic next song (end of media)");
                    eFunctions.updateTimesPlayed(currentPlayingArtist);
                }
            } else
            {
                tracksLeft = -1;
                tracksLeftDisplay.setText("");
                eFunctions.reset();
                tracksLeftSelectBox.setSelectedIndex(0);
                tracksLeftActionSelectBox.setVisible(false);
                if(tracksLeftActionSelectBox.getSelectedIndex() == 0)
			eFunctions.collapseTree("Music");
                else if(tracksLeftActionSelectBox.getSelectedIndex() == 1) {
                	try { 
				Sweetdream.db.update("UPDATE variables SET value = '" + Sweetdream.tabbedPaneLibrary.getSelectedIndex() + "' WHERE variablename = 'lastLibraryTab'"); 
				
			} catch(SQLException ex2) { ex2.printStackTrace(); }
		System.exit(0); }
                else if(tracksLeftActionSelectBox.getSelectedIndex() == 2)
			eFunctions.shutdownComputer("go");
                else {
                    playTypeSelectBox.setSelectedIndex(0);
                    errorsInARow = 0;
                    nextTrack("automatic next song (end of media)");
                    eFunctions.updateTimesPlayed(currentPlayingArtist);
                }
            }
        }
	else if(state == 2)
        	eFunctions.printActivity("playing now.");
        else if(state == 6)
		eFunctions.printActivity("seeking");
        else if(state == 0)
		eFunctions.printActivity("opening...");
        else if(state == 3)
		eFunctions.printActivity("is stopped.");
    }

    public void setController(BasicController controller)
    {
        mainSoundPlayer = controller;
        secondarySoundPlayer = controller;
    }

    public void display(String msg)
    {
        eFunctions.printActivity(msg);
    }

   public static void transferTracks(int numToTransfer) {
		numTransferred = 0;
		String searchForMatch = "asdfasdf";
			try { CopyFile asdf = new CopyFile(null,null,numToTransfer); }
				  catch ( IOException ioe ) { ioe.printStackTrace( System.out ); }
  }
    public static void transferPlaylist(int numToTransfer)
    {
        numTransferred = 0;
        playlistIndexToCopy = 0;
        CopyFile asdf;
        try
        {
            asdf = new CopyFile("playlist", null, currentPlaylistVector.size());
        }
        catch(IOException ioe) { ioe.printStackTrace(System.out); }
    }

    public int getDurationSeconds(Map properties)
    {
        long milliseconds = -1L;
        int bytesLength = -1;
        if(properties != null)
        {
            if(properties.containsKey("bitrate") && properties.containsKey("audio.length.bytes"))
            {
                bytesLength = Math.round(((Integer)properties.get("audio.length.bytes")).intValue());
                int bitrate = Math.round(((Integer)properties.get("bitrate")).intValue());
                milliseconds = Math.round(Math.abs(bytesLength / (bitrate / 8))) * 1000;
            } else
            if(properties.containsKey("duration"))
            {
                milliseconds = (int)((Long)properties.get("duration")).longValue() / 1000;
                if(properties.containsKey("audio.length.bytes"))
                {
                    bytesLength = ((Integer)properties.get("audio.length.bytes")).intValue();
                }
            } else
            {
                int bitspersample = -1;
                int channels = -1;
                float samplerate = -1F;
                int framesize = -1;
                if(properties.containsKey("audio.samplesize.bits")) bitspersample = ((Integer)properties.get("audio.samplesize.bits")).intValue();
                if(properties.containsKey("audio.channels"))channels = ((Integer)properties.get("audio.channels")).intValue();
                if(properties.containsKey("audio.samplerate.hz"))samplerate = ((Float)properties.get("audio.samplerate.hz")).floatValue();
                if(properties.containsKey("audio.framesize.bytes"))framesize = ((Integer)properties.get("audio.framesize.bytes")).intValue();
		
                if(bitspersample > 0) milliseconds = (int)((1000F * (float)bytesLength) / (samplerate * (float)channels * (float)(bitspersample / 8)));
                else milliseconds = (int)((1000F * (float)bytesLength) / (samplerate * (float)framesize));
                
            }
        }
        return Math.round(Math.abs(milliseconds / 1000L));
    }

    public static void getArtistImage(String theArtist, String theAlbum)
    {
        if(theArtist.indexOf(" ") == 0) theArtist = theArtist.substring(1);
        
        eFunctions.printActivity("getArtistImage...  Artist: " + theArtist + " Album: " + theAlbum +  " ArtistImageType: " + ArtistImageType );
   
        try {
        
        if(albumCoverOverride || ArtistImageType.equals("Album Covers"))
        {
            albumCoverOverride = false;
            eFunctions.displayAlbumImage(theAlbum, "try artist image after");
            eFunctions.packFrame();
            return;
        }
        if(ArtistImageType.equals("Both"))
        {
            int randomNum = generator.nextInt(4);
            imagePane.setVisible(true);
            eFunctions.printActivity("Album Name: " + theAlbum);
            if(randomNum < 2)
            	    eFunctions.displayArtistImage(theArtist);
                
            else eFunctions.displayAlbumImage(theAlbum, "try artist image after");
                
            
        } else if(ArtistImageType.equals("Saved Images"))
        	eFunctions.displayArtistImage(theArtist);
            
          eFunctions.packFrame();
        
        }
        catch(SQLException sq) { sq.printStackTrace(); }
    }

    public static void getLyrics()
    {
        String tempTrackName = null;
        if(tree.getSelectionPath() != null)
        {
            tempTrackName = tree.getSelectionPath().getLastPathComponent().toString();
        }
        File qFile = new File("lyrics/" + tempTrackName + ".txt");
        if(qFile.exists())
        {
            lyricsPane.setVisible(true);
            String record = null;
            StringBuffer buf = new StringBuffer();
            try
            {
                FileReader fr = new FileReader(qFile);
                BufferedReader br = new BufferedReader(fr);
                record = new String();
                int preventEndless = 0;
            }
            catch(IOException eq)
            {
                eFunctions.printActivity("Uh oh, got an IOException error!");
                eq.printStackTrace();
            }
            lyricsDisplay.setText(buf.toString());
            lyricsInput.setText(buf.toString());
            tabbedFrontPanel.setVisible(true);
            tabbedFrontPanel.setSelectedIndex(1);
            tabbedFrontPanel.setPreferredSize(new Dimension(statusPane.getWidth(), 200));
        } else
        {
            lyricsInput.setText("");
            lyricsDisplay.setText("");
        }
    }

    public static void nextTrack(String whereFrom)
    {
    	    
    	    if (errorsInARow > 30) { eFunctions.reset(); errorsInARow = 0; return; }  // to try and alleviate sound card being occupied and any other infinite loop error
    	    
        eFunctions.printActivity("________\nNextTrack from: " + whereFrom + "\n___________");
        String TopFolder = "", NextTopFolder = "", whichTree = "";
        CheckNode node1 = null;
        int numTimes = 0,indexOfNextFolder = 1;
        prevBtnClicks = 0;
	
       // controlsPane.setBorder(BorderFactory.createTitledBorder(usingSearch));
	
	// ORIGINALLY PULLED CURRENT TREE BY TABBEDPANE.GETSELECTED INDEX... THIS IS BETTER 
                     if(currentPlayingTree.equals("Music")) whichTree = "Music";
                else if(currentPlayingTree.equals("Moods")) whichTree = "Moods";
                else if(currentPlayingTree.equals("Audio")) whichTree = "Audio";
                else if(currentPlayingTree.equals("Video")) whichTree = "Video";
                else if(currentPlayingTree.equals("Photo")) whichTree = "Photo";
                else if(currentPlayingTree.equals("")) {
                	if(tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Music")) whichTree = "Music";
                	if(tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Moods")) whichTree = "Moods";
                	if(tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Audio")) whichTree = "Audio";
                	if(tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Video")) whichTree = "Video";
                	if(tabbedPaneLibrary.getTitleAt(tabbedPaneLibrary.getSelectedIndex()).contains("Photo")) whichTree = "Photo";
                }
                else whichTree = "Music";
                
	
        if(touchFrame.isVisible())
	    whichTree = "Touch";

        if(playDirectly)
        {
            eFunctions.playNextDirectly(whichTree);
            return;
        }
        if(playCurrentPlaylist)
        {
            eFunctions.playNextPlaylist();
            return;
        }
	if(playCurrentMood)
        {
          //  eFunctions.playNextMood();
	    String thisElement = Sweetdream.currentMoodVector.get(generator.nextInt(currentMoodVector.size())).toString();  // ERROR:  not always positive
	    System.out.println("thisElement: " + thisElement);
	    Sweetdream.usingSearch = thisElement.substring(1);
        //    return;
        }
	
	// RANDOM Play
        if(playTypeSelectBox.getSelectedIndex() == 0)
        {
            eFunctions.printActivity("nextTrack, Option Three: Random is selected  - usingSearch is: " + usingSearch);
	    
            checkSelected = false;
	    
	    // Search by Artist or Album
            if(usingSearch.length() > 0)
            {
                int w = 0;
		// Look for Artist first
                do
                {
                    musicDirIndex = generator.nextInt(musicDirVector.size()); // This needs to include the first folder (-1?)
		    if (musicDirIndex == 0) musicDirIndex++;
		    
		   // eFunctions.printActivity(" Run through musicDirIndex at random up to 2000 times for an Artist containing usingSearch: " + usingSearch);
                    TopFolder = musicDirVector.get(musicDirIndex-1).toString();
		  //  eFunctions.printActivity(TopFolder);
                  if(musicDirVector.size() > (musicDirIndex+1)) 
		  	NextTopFolder = musicDirVector.get(musicDirIndex + 1).toString();

                    w++;
                } while(!TopFolder.toString().contains(usingSearch) && w < 2000);
		
		// Likely not in artists then, now search for album containing usingSearch
                if(w == 2000)
                {
                    int i = 0;
		    eFunctions.printActivity(" 2000 searches, no matches. Likely not artist then, run through musicCompleteVector for Album containing usingSearch: " + usingSearch);
                    do {
                        if(i >= musicCompleteVector.size()) break;
                        
                        TopFolder = musicCompleteVector.get(i).toString();
                        //System.out.println(" ./ " + TopFolder);
			
                        if(TopFolder.contains(usingSearch) && !TopFolder.contains("."))
                        {
			    musicDirIndex = i;
			    eFunctions.printActivity("Found album in musicDirVector: " + usingSearch + " musicDirIndex: " + musicDirIndex + " TopFolder: " + TopFolder);
                            break;
                        }
                        i++;
                    } while(true);
                    w = 1;
                    eFunctions.printActivity("musicDirVector.size(): " + musicDirVector.size());  // this is only artists, not album folders
		    eFunctions.printActivity("musicCompleteVector.size(): " + musicCompleteVector.size());  // this is every artist, album, and song
                    
		    do {
                        NextTopFolder = musicCompleteVector.get(musicDirIndex + w).toString();
			indexOfNextFolder = musicDirIndex + w;
                        w++;
                    } while(NextTopFolder.contains("."));      // This does not work when searching for ALBUMS    ---- still the case?
                }
            } else
            {
            	    
            	    
            	// Keep looping until we get to a Directory that is checked    
                do {
                    musicDirIndex = generator.nextInt(musicDirVector.size()+1);  // Generate random integer within size of music directory vector
		      if (musicDirIndex == 0) musicDirIndex++;		// prevents array index error?        
                    TopFolder = musicDirVector.get(musicDirIndex-1).toString();
                    node1 = (CheckNode)tree.getPathForRow(musicDirIndex).getLastPathComponent();
                   
                    
                    if(++numTimes > 4000) {
                        eFunctions.printActivity("4000 times");
                        JOptionPane.showMessageDialog(frame, "No top level folders are checked for random mode play.");
                        eFunctions.reset();
                        return;
                    }
                } while(node1 == null || !node1.isSelected() || !node1.toString().contains("/"));
            }
	    if (musicDirIndex > musicDirVector.size()) { 
		//    NextTopFolder = musicDirVector.get(musicDirVector.size()-1).toString();  // size always needs to be -1'ed in this case!!!
	    	    tree.setSelectionRow(musicDirIndex+1);
		    //eFunctions.printActivity("FIRST FIRST");
	    }
	    else {  
		    if(musicDirVector.size() > (musicDirIndex+1)) {
			    NextTopFolder = musicDirVector.get(musicDirIndex).toString();  // + 1 for Next folder cancels out Vector starting with 0
		    	    tree.setSelectionRow(musicDirIndex);
		    	    //eFunctions.printActivity("SECOND SECOND");
		    }
	    }
            if (!whichTree.equals("Moods") && collapseAfterPlayFinish)
            	    eFunctions.collapseTree(whichTree);
//            tree.scrollRowToVisible(tree.getRowForPath(tree.getSelectionPath()));

        }
        
        /* SEQUENTIAL */
        else
        {
            if(playTypeSelectBox.getSelectedIndex() == 1)
            {
                eFunctions.printActivity("playNextSequential: whichTree = " + whichTree);
                eFunctions.playNextSequential(whichTree);
                return;
            }
            if(playTypeSelectBox.getSelectedIndex() == 2)
            {
                progressBar.setValue(0);
                String filePath = musicCompleteVector.get(musicFileIndex).toString();
                tree.scrollRowToVisible(tree.getRowForPath(tree.getSelectionPath()) + 2);
                try
                {
                    mainSoundPlayer.open(openFile(MusicDirectoryPath + filePath));
                    mainSoundPlayer.play();
                }
                catch(BasicPlayerException e1)
                {
                    eFunctions.expandTree("Music");
                    nextTrack("ERROR: BasicPlayerException (Loop current track)");
                }
                return;
            }
        }
        int tempRandomNum = 0;
        String fullSongPath = "";
        String tempFolderName = "";
        String filenameEdited = "";
        if(usingSearch2.equals("01"))
        {
            musicFileIndex = musicDirIndex + 1;
            fullSongPath = musicCompleteVector.get(musicFileIndex).toString();
        } 
	else if(playTypeSelectBox.getSelectedIndex() == 0) {

            //eFunctions.printActivity("TopFolder@: " + TopFolder + "  NextTopFolder@: " + NextTopFolder);
            int indexOfCurrentFolder = musicCompleteVector.indexOf(TopFolder);
            if (NextTopFolder == "") indexOfNextFolder = indexOfCurrentFolder + 10;
            else indexOfNextFolder = musicCompleteVector.indexOf(NextTopFolder, indexOfCurrentFolder); // this is currently way off 3-4-10 for some artists
	    
            numTimes = 0;
            do
            {
		if (indexOfNextFolder != -1) tempRandomNum = generator.nextInt(Math.abs(indexOfNextFolder - indexOfCurrentFolder));
		else break; //tempRandomNum = 1;
		//if (tempRandomNum > 1) tempRandomNum--;
                fullSongPath = musicCompleteVector.get(indexOfCurrentFolder + tempRandomNum).toString();
		/*  // DEBUG
                eFunctions.printActivity("getting file to play... fullSongPath: " + fullSongPath);
		eFunctions.printActivity("tempRandomNum: " + tempRandomNum);
		eFunctions.printActivity("indexOfCurrentFolder: " + indexOfCurrentFolder + "indexOfNextFolder: " + indexOfNextFolder);
		*/

                if(++numTimes > 2000)
                {
                    eFunctions.printActivity("2000 times");
                    JOptionPane.showMessageDialog(frame, "No album level folders are checked for random mode play.");
                    eFunctions.reset();
                    return;
                }
                
            } while(!fullSongPath.contains(".")); //  || node1 == null || !node1.isSelected()
            musicFileIndex = indexOfCurrentFolder + 2;
        }
        if(fullSongPath == null || !fullSongPath.contains(usingSearch2))
        {
            eFunctions.expandTree("Music");
            eFunctions.printActivity("usingSearch2 did not match.\nusingSearch2: " + usingSearch2 + "  fullSongPath: " + fullSongPath);
            nextTrack("fullSongPath == null || !fullSongPath.contains(usingSearch2)");
            return;
        }

        updateTracksPlayedVector(fullSongPath);
	
        if(musicCompleteVector.size() >= musicFileIndex) filenameEdited = musicCompleteVector.get(musicFileIndex).toString();
        // tree.scrollRowToVisible(tree.getRowForPath(tree.getSelectionPath()) + 14);
        if(voiceCommandSequential)
        {
            voiceCommandSequential = false;
            playTypeSelectBox.setSelectedIndex(1);
            currentPlayingAlbum = usingSearch;
        }
        //getLyrics();
        if (!whichTree.equals("Moods") && collapseAfterPlayFinish) 
        	eFunctions.collapseTree(whichTree);
        try
        {
	    //eFunctions.printActivity("Now playing: " + MusicDirectoryPath + fullSongPath);
            mainSoundPlayer.open(openFile(MusicDirectoryPath + fullSongPath));
            mainSoundPlayer.play();
        }
        catch(BasicPlayerException e1) { errorsInARow++; System.out.println("errorsInARow: " + errorsInARow); nextTrack("ERROR: BasicPlayerException (final nextTrack) " + MusicDirectoryPath + fullSongPath + " " + errorsInARow); }
        catch(NullPointerException e1) { errorsInARow++; System.out.println("errorsInARow: " + errorsInARow); nextTrack("ERROR: NullPointerException (final nextTrack) " + MusicDirectoryPath + fullSongPath + " " + errorsInARow); }
        currentSongPath = fullSongPath;
        
        eFunctions.adjustVolume(currentVolumeLevel); // because BasicPlayer resets the volume
    }

    protected static File openFile(String file)
    {
        return new File(file);
    }

    public static void captureAudio()
    {
        try
        {
            mainSoundPlayer.open(channelopenSound);
            mainSoundPlayer.play();
        }
        catch(BasicPlayerException ew) { ew.printStackTrace(System.out); }
	
        try
        {
            statusDisplay.setText("Channel open.");
            audioFormat = getAudioFormat();
            javax.sound.sampled.DataLine.Info dataLineInfo = new javax.sound.sampled.DataLine.Info(javax.sound.sampled.TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
            (new CaptureThread()).start();
        }
        catch(Exception e) { e.printStackTrace(); System.exit(0); }
    }

    private static AudioFormat getAudioFormat()
    {
        float sampleRate = 8000F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public static void updateTracksPlayedVector(String fullSongPath) {
    	if(!tracksPlayedVector.isEmpty() && playTypeSelectBox.getSelectedIndex() == 0)
        {
            for(int i = 0; i < tracksPlayedVector.size(); i++)
            {
                if(tracksPlayedVector.get(i) == fullSongPath && tracksPlayedVector.get(i) != "")
                {
                    nextTrack("The selected song was equal to tracksPlayedVector position " + tracksPlayedVector.get(i) + ".");
                    return;
                }
            }
        }
        if(tracksPlayedVector.size() == Integer.parseInt(tracksPlayedVectorSize))
        {
            for(int i = 0; i < Integer.parseInt(tracksPlayedVectorSize) - 1; i++)
		    tracksPlayedVector.setElementAt(tracksPlayedVector.get(i + 1), i);

            tracksPlayedVector.setElementAt(fullSongPath, Integer.parseInt(tracksPlayedVectorSize) - 1);
        } else if(!fullSongPath.equals("")) tracksPlayedVector.add(fullSongPath);
        
       // eFunctions.printActivity("tracksPlayedVector (updated): " + tracksPlayedVector);
    }
    	    
    
    public static void showPicture(String labelToDisplay, String imagePath, String albumName, String releaseYear)
    {
        eFunctions.printActivity("showPicture(): " + labelToDisplay + " " + imagePath + " " +  albumName + " " + releaseYear);
        
        Icon icon;
        JLabel DisplayLabel;
        icon = null;
        DisplayLabel = null;
        String pictureType = "";
        if(labelToDisplay.equals("coverLabel")) {
			DisplayLabel = coverLabel;
			pictureType = "Artist Image";
		}
		else if(labelToDisplay.equals("Photo Tree")) {
			pictureType = "Photo Tree";	
			DisplayLabel = photoLabel;
		}
		
	    BufferedImage image = null;
  
      
		  try {
				  image = ImageIO.read(new File(imagePath));
		  } catch (IOException ex ) {}
		
        //icon = eFunctions.createImageIcon(imagePath);
        if(image == null)
        {
            DisplayLabel.setIcon(eFunctions.createImageIcon("images\\system\\eTrain.gif"));
            imageFrameLabel.setIcon(eFunctions.createImageIcon("images\\system\\eTrain.gif"));
            return;
        }
        try
        {
          //RESIZE ICON
           // icon = eCollage.resizeIcon(imagePath, null, pictureType);
            image = resize(image, Method.SPEED, 300, OP_ANTIALIAS); // Resize to width of 300 pixels
            
            DisplayLabel.setIcon(new ImageIcon(image));
            //DisplayLabel.setBorder(new LineBorder(Color.gray, 2));
            imageFrameLabel.setIcon(new ImageIcon(image));
            imageFrame.pack();
            imagePane.setVisible(true);
    //      String withHTML = "<html><br>&nbsp;" + albumName + "&nbsp;<br><br>&nbsp;" + releaseYear + "&nbsp;<br><br>";
    //      if(albumName != null && !albumName.equals("")) imagePane.setToolTipText(withHTML);

            eFunctions.packFrame();
        }
        catch(NullPointerException ex1)
        {
            ex1.printStackTrace();
            nextTrack("showPicture(): image not added to jar");
        }
        return;
    }

    
    
    /* 
    ---------------------------------------------------
    MAIN --  args:  load the voice recognition: on/off, load collage: on/off, display etrain by default: show/hide
    ------------------------------------------------------ 
    */
    public static void main(String args[])
    {
	startupTimeStart = System.currentTimeMillis();
	    
        try
        {
            eFunctions.printActivity("Voice recognition: " + args[0]);
            eFunctions.printActivity("Collage: " + args[1]);
            eFunctions.printActivity("Show Window: " + args[2]);
        }
        catch(ArrayIndexOutOfBoundsException ex1) { args[0] = "off"; args[1] = "off"; args[2] = "show"; }
        try
        {
            Sweetdream aSweetdream = new Sweetdream();
            db = new eDatabase("eTrainDatabase");
           
            if(args[1].equals("on")) {
            	    eCollage theCollage = new eCollage();
            }
            
            SysTray theSysTray = new SysTray();
            BasicPlayer bplayer = new BasicPlayer();
            bplayer.addBasicPlayerListener(aSweetdream);
            aSweetdream.setController(bplayer);
            
       //   Arduino arduino = new Arduino();
                     
	    tree.addMouseListener(new LibraryPopup());
	    tree_audio.addMouseListener(new LibraryPopup());
	    tree_video.addMouseListener(new LibraryPopup());
	    tree_photo.addMouseListener(new LibraryPopup());

	    eFunctions.startupStuffToDo(args[0], args[1], args[2]);
	    
	    if(args[0].equals("on"))
		    eFunctions.startMicrophone();
	    else horizPlaylistVoiceRow.setVisible(false);
	    
        }
        catch(Exception ex1) { ex1.printStackTrace(); return; }
	
	startupTimeEnd = System.currentTimeMillis();
	try
        {
	eFunctions.updateStartupLog(((startupTimeEnd - startupTimeStart)) + " ms, " + args[0] + " " + args[1] + " " + args[2]); }
	catch(IOException ex1) { ex1.printStackTrace(); return; }
    }

    static
    {
        RepeatPlayNumTextBox = new TextField(tracksPlayedVectorSize, 3);
        lyricsDisplay = new JTextArea();
        commandsInput = new JTextArea();
        lyricsPane = new JScrollPane(lyricsDisplay);
        commandsInputPane = new JScrollPane(commandsInput);
    }
        
    
    
    /*----- COPY FILE(s) TO MP3 PLAYER (or any hard drive, etc) -----*/
static class CopyFile extends Thread {
	 String fileToCopy,extension,dest,theSource;
	 
/** Fast & simple file copy. */
public CopyFile(String source, String thedest, int theNumber) throws IOException {
	 fileToCopy = source;
	 theSource = source;
	 dest = thedest;
	 numToBeTransferred = theNumber;
	 percentageBar.setValue(0);
	 percentageBar.setVisible(true);
	 percentageBar.setMaximum(numToBeTransferred);
	 eFunctions.expandTree("Music");
	 numTransferred = 0;
     start(); }
	    
  public void run()
    {
	int randomNum = 0;
	String fileToCopyString = "",destString = "",folderName = "frog.mp3",currentRow = "";
        CheckNode node;
	
	for (int i = 0; i < numToBeTransferred; i++) { // Loop through until numToBeTransferred is reached
	
	if (theSource == null) {  // Is not a specific file to copy, but a request for Random track copying
		eFunctions.printActivity("Copy files randomly");
	do {  // RANDOM Selection - Get file and make sure its checked
			//eFunctions.expandTree(tree);  //if (tree.getVisibleRowCount() < 100)  in case something outside collapses tree (nextTrack), expand it again
		randomNum = generator.nextInt(tree.getRowCount());
		currentRow = tree.getPathForRow(randomNum).getLastPathComponent().toString();
		node = (CheckNode)tree.getPathForRow(randomNum).getLastPathComponent();
		eFunctions.printActivity("______________________\nrandomNum: " + randomNum + " currentRow: " + currentRow + " node:" + node);
	} while (currentRow.contains(File.separator) || !node.isSelected()); // is a folder or the row's checkbox is unchecked
	
	String artistName = tree.getPathForRow(randomNum).getPathComponent(1).toString();
                eFunctions.printActivity("artistName: " + artistName);
                String artistalbumPath = "";
                int foldersInPath = Sweetdream.tree.getPathForRow(randomNum).getPathCount();
                
		     if(foldersInPath == 3) artistalbumPath = "";
                else if(foldersInPath == 4) artistalbumPath = Sweetdream.tree.getPathForRow(randomNum).getPathComponent(2).toString();
                else if(foldersInPath == 5) artistalbumPath = Sweetdream.tree.getPathForRow(randomNum).getPathComponent(2).toString() + Sweetdream.tree.getPathForRow(randomNum).getPathComponent(3).toString();
                else if(foldersInPath == 6) artistalbumPath = Sweetdream.tree.getPathForRow(randomNum).getPathComponent(2).toString() + Sweetdream.tree.getPathForRow(randomNum).getPathComponent(3).toString() + Sweetdream.tree.getPathForRow(randomNum).getPathComponent(4).toString();
                else if(foldersInPath == 7) artistalbumPath = Sweetdream.tree.getPathForRow(randomNum).getPathComponent(2).toString() + Sweetdream.tree.getPathForRow(randomNum).getPathComponent(3).toString() + Sweetdream.tree.getPathForRow(randomNum).getPathComponent(4).toString() + Sweetdream.tree.getPathForRow(randomNum).getPathComponent(5).toString();

                eFunctions.printActivity("artistalbumPath: " + artistalbumPath);
                eFunctions.printActivity("foldersInPath: " + foldersInPath);
                eFunctions.printActivity("currentRow: " + currentRow);
                String temp3 = "";
                if(currentRow.length() > 5 && currentRow.indexOf("-") == 3)
                    temp3 = currentRow.substring(5);
                else temp3 = currentRow;
                
	fileToCopyString = MusicDirectoryPath + artistName + artistalbumPath +  "/" + currentRow + ".mp3";
	destString = MP3PlayerDirectoryPath + File.separator + artistName + " - " + temp3 + ".mp3";
	
	statusDisplay.setText("copying " + temp3 + " by " + artistName);
	copyName = MP3PlayerDirectoryPath + File.separator + artistName + " - " + temp3 + ".mp3";
	eFunctions.printActivity("copyName: " + copyName);
	eFunctions.printActivity("fileToCopyString: " + fileToCopyString + "\n______________________");
	if (fileToCopyString.contains(".ogg") || fileToCopyString.contains(".jpg") || fileToCopyString.contains(".flac"))
		numTransferred--; // loop once more since ogg files won't play on mp3 player
	}
	else if (theSource.equals("playlist")) {  // Copy entire playlist
			eFunctions.printActivity("Copy entire playlist");
			fileToCopy = currentPlaylistVector.get(numTransferred).toString();
				if (fileToCopy.contains(".")) extension = ""; else extension = ".mp3"; // add file suffix if necessary
				fileToCopyString = MusicDirectoryPath + fileToCopy;
				
				
				if (fileToCopy.contains("\\")) fileToCopy = fileToCopy.substring(fileToCopy.lastIndexOf("\\"));  // allow for both file.separator types to go back and forth
				if (fileToCopy.contains("/")) fileToCopy = fileToCopy.substring(fileToCopy.lastIndexOf("/"));    // between windows and linux
				
			destString = MP3PlayerDirectoryPath + fileToCopy + extension;  
			//fileToCopyString = MusicDirectoryPath + fileToCopy;
			eFunctions.printActivity("fileToCopyString: " + fileToCopyString);
			eFunctions.printActivity("destString: " + destString);
			//fileToCopyString = fileToCopy;
	}
	else {  // Copy specific file (not random stuff)
			eFunctions.printActivity("Copy a specific file, not randomly generated");
			destString = MP3PlayerDirectoryPath + File.separator + fileToCopy + ".mp3";                                            
			fileToCopyString = fileToCopy;
	}
	
	eFunctions.printActivity("Copying file...");                                 
        FileChannel in = null, out = null;
     try {       
	  /* Create the input stream.  If an error occurs, end the program. */
	  try { in = new FileInputStream(new File(fileToCopyString)).getChannel(); }          
	  catch (FileNotFoundException e) {
			   statusDisplay.setText("File to copy not found.");
			   eFunctions.printActivity("File to copy not found.");
			   continue;
	  }                                                                                                                    
		  
	  /* Create the output stream.  If an error occurs, end the program. */
      try { out = new FileOutputStream(new File(destString)).getChannel(); }
      catch (IOException e) {
			  statusDisplay.setText("Is the MuVo plugged in?");
			  eFunctions.printActivity("Can't open output file \"" + copyName + "\".");
			  continue;
      }
	  try {
		  //in.transferTo( 0, in.size(), out);   //May be important! but oddly doubles file size
          long size = in.size();
          MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
	  	  out.write(buf); }
	  catch (IOException e) {
			  eFunctions.printActivity("IOException while copying file.");
	  		  continue; }

      } finally {
	try {
          if (in != null)      in.close();
          if (out != null)     out.close();
	  	  eFunctions.printActivity("File Copied.");
		  numTransferred++;
		  percentageBar.setValue(numTransferred);
		  tracksTransferredDisplay.setText("<html>" + numTransferred + " of " + numToBeTransferred + "<br>");
		  statusDisplay.setText("Transferring... ");
			  }
	catch (IOException e) {
		eFunctions.printActivity("IOException while copying file.");
	  	continue; }
     }
    } //end loop
	    // looped through the number of times (numToBeTransferred), so now finish up
	   if (collapseAfterPlayFinish) eFunctions.collapseTree("Music");
	   statusDisplay.setText("Tracks transferred successfully.");
	   percentageBar.setVisible(false);
	   eFunctions.refreshTree("mp3player");
	   tracksTransferredDisplay.setText("");
	   ActionSelectBox.setSelectedIndex(0);
	   SysTray.trayIcon.displayMessage("eTrain", 
                        "Tracks transferred successfully.",
                        TrayIcon.MessageType.NONE);  //INFO
	   fileToCopy = null;
	   if ((songTimeDisplay.getText().equals("0:00") || songTimeDisplay.getText().equals("0:01")) && computerVoice == true) { 
			   //if music is not currently playing, play the computer voice
	   try { theSoundPlayer.open(transferSound); theSoundPlayer.play(); } 
		    catch (BasicPlayerException eq) { eq.printStackTrace(); }
	   }
	}
}

    static class CaptureThread extends Thread
    {
        public void run()
        {
            javax.sound.sampled.AudioFileFormat.Type fileType = null;
            File audioFile = null;
            fileType = javax.sound.sampled.AudioFileFormat.Type.WAVE;
            Calendar cal = new GregorianCalendar();
            int nowmonth = cal.get(2);
            int nowhour = cal.get(10);
            int nowmin = cal.get(12);
            int nowsec = cal.get(13);
            Date now = new Date();
            DateFormat df = DateFormat.getDateInstance();
            String s = df.format(now);
            eFunctions.printActivity("Today is " + s);
            audioFile = new File("channels/" + s + ".wav");
	    
            if(audioFile.exists()) audioFile = new File("channels/" + s + " (2).wav");
            
            try
            {
                Sweetdream.targetDataLine.open(Sweetdream.audioFormat);
                Sweetdream.targetDataLine.start();
                AudioSystem.write(new AudioInputStream(Sweetdream.targetDataLine), fileType, audioFile);
            }
            catch(Exception e) { e.printStackTrace(); }
        }
        CaptureThread()
        { }
    }
    
}

