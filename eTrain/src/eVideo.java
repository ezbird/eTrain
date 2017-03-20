package sweetdream;

import java.util.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.Canvas;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.concurrent.*;

import uk.co.caprica.vlcj.binding.LibX11;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
//import uk.co.caprica.vlcj.player.events.VideoOutputEventListener;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Native;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;


import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.windows.WindowsCanvas;



public class eVideo {
	
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private final EmbeddedMediaPlayerComponent mediaPlayerComponent;

  MediaPlayerFactory mediaPlayerFactory;
  static EmbeddedMediaPlayer mediaPlayer;
  CanvasVideoSurface videoSurface;
  FullScreenStrategy fullScreenStrategy;
	
  private static final int SKIP_TIME_MS = 20 * 1000;
  private static JFileChooser fileChooser;
  
  public static JFrame videoFrame;
  public static JWindow controlsFrame;
  public JPanel contentPane;
  public Canvas canvas;
  JButton pauseBtn = new JButton("", Sweetdream.pauseButtonIcon);
  JButton stopBtn = new JButton("", Sweetdream.stopButtonIcon);
  JButton playBtn = new JButton("", Sweetdream.playButtonIcon);
  JButton nextBtn = new JButton("", Sweetdream.nextButtonIcon);
  JButton prevBtn = new JButton("", Sweetdream.prevButtonIcon);
  JButton fullScreenBtn = new JButton("", Sweetdream.collageIcon);
  JButton screenshotBtn = new JButton("", Sweetdream.viewIcon);
  JButton closeBtn = new JButton("", Sweetdream.closeIcon);
  static javax.swing.Timer controls_timer;
  static String[] mediaOptions = {"video-filter=logo", "logo-opacity=25"}; // "logo-file=train.gif",
  private JPanel label;
  public JSlider positionSlider;
  public JLabel timeLabel;
  public boolean decorated = true;

  
  
  // Guard to prevent the position slider firing spurious change events when
  // the position changes during play-back - events are only needed when the 
  // user actually drags the slider and without the guard the play-back 
  // position will jump around
  private boolean setPositionValue;
  public boolean playAnotherVideo = false;
  
     public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
     }
  
  public eVideo( String videoFile ) {
  	 
   /* This is supposed to improve things, but doesn't seem to work in Windows */
    LibX11.INSTANCE.XInitThreads();
    Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
    
    positionSlider = new JSlider();
    positionSlider.setMinimum(0);
    positionSlider.setMaximum(100);
    positionSlider.setValue(0);
    positionSlider.setToolTipText("Position");
    timeLabel = new JLabel("hh:mm:ss");
    
    label = new JPanel();
    label.setBackground(Color.lightGray);
    label.setOpaque(true);
    label.setBorder(new LineBorder(Color.darkGray));
    
    controlsFrame = new JWindow();
    controlsFrame.setBackground(Color.white);
    controlsFrame.setPreferredSize(new Dimension(450, 90));
    controlsFrame.setSize(new Dimension(450, 80));
    controlsFrame.setMinimumSize(new Dimension(450, 90));
    controlsFrame.setLocation(0,30);
    controlsFrame.setVisible(false);

    	// AWTUtilities is phased out for JDK7
    	// JDK7 currently fucks shits up (libswo.so not found)
	// com.sun.awt.AWTUtilities.setWindowShape(controlsFrame, new RoundRectangle2D.Float(0, 0, controlsFrame.getWidth(), controlsFrame.getHeight(), 35, 35));
	// com.sun.awt.AWTUtilities.setWindowOpacity(controlsFrame, .7f);
    
    canvas = new Canvas();
    canvas.setVisible(true);
   canvas.setBackground(Color.black);
   
    contentPane = new JPanel();
    contentPane.setBackground(Color.black);
    contentPane.setLayout(new BorderLayout());
    
    
   // videoFrame.setContentPane(contentPane);
    videoFrame = new JFrame("eTrain Video");
    videoFrame.add(canvas);
    videoFrame.setSize(1680, 1050);
    //videoFrame.setExtendedState(videoFrame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
    videoFrame.setUndecorated(true);
    videoFrame.setIconImage((new ImageIcon("images/system/eTrain.gif")).getImage());
    decorated = !decorated;
toggleDecoration(decorated);


    
    canvas.addMouseMotionListener(new MouseMotionListener()
    {          
    	   public void mouseDragged(MouseEvent e) { 
                 Point new_location = new Point(MouseInfo.getPointerInfo().getLocation().x,MouseInfo.getPointerInfo().getLocation().y);
    	   	 videoFrame.setLocation(new_location);
    	   }
    	   public void mouseMoved(MouseEvent e) {
    	    	 controls_timer.start();
    	    	 System.out.print(".");
    	    	 controlsFrame.setVisible(true);
    	   }
    });

    label.add(prevBtn);
    	prevBtn.setToolTipText("Skip 20 seconds backward");
    label.add(stopBtn);
    	stopBtn.setToolTipText("Stop playback");
    label.add(pauseBtn);
    	nextBtn.setToolTipText("Pause playback");
    label.add(playBtn);
    	playBtn.setToolTipText("Resume playback");
    	playBtn.setVisible(false);
    label.add(nextBtn);
    	nextBtn.setToolTipText("Skip 20 seconds forward");
    label.add(fullScreenBtn);
    	fullScreenBtn.setToolTipText("Toggle fullscreen");
    label.add(screenshotBtn);
    	screenshotBtn.setToolTipText("Snap screenshot");
    label.add(closeBtn);
    	closeBtn.setToolTipText("Close");
    label.add(positionSlider);
    label.add(timeLabel);
    label.setBackground(Color.white);

    controlsFrame.getContentPane().add(label);
    
    

    videoFrame.setVisible(true);
 
    controls_timer = new javax.swing.Timer(3500, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
              if (MouseInfo.getPointerInfo().getLocation().x > 450 && MouseInfo.getPointerInfo().getLocation().y > 80)
              	      { controlsFrame.setVisible(false); controls_timer.stop(); }
              else controls_timer.restart();
            }
        });

          mediaPlayerFactory = new MediaPlayerFactory(new String[] {"--no-video-title-show", "--no-overlay"}); // video output: , "--vout=xvideo"
      	  mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
      	  mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

  //  fullScreenStrategy = new DefaultFullScreenStrategy(videoFrame);
 //   mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
    
    
    videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
    mediaPlayer.setVideoSurface(videoSurface);
    
    
    
    mediaPlayer.addMediaPlayerEventListener(new TestPlayerMediaPlayerEventListener());
    
    canvas.addMouseListener(new VideoPopupMenu());
    
    pauseBtn.setSize(new Dimension(50, 25));
    pauseBtn.setMnemonic(KeyEvent.VK_Q);
        pauseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	mediaPlayer.pause();
            	playBtn.setVisible(true);
            	pauseBtn.setVisible(false);
            }
        });
    stopBtn.setSize(new Dimension(50, 25));
    stopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	mediaPlayer.stop(); 
            	videoFrame.setVisible(false);
            	controlsFrame.setVisible(false);
            }
        });
    playBtn.setSize(new Dimension(50, 25));
        playBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	mediaPlayer.play();
            	playBtn.setVisible(false);
            	pauseBtn.setVisible(true);
            }
        });
    nextBtn.setSize(new Dimension(50, 25));
    nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	mediaPlayer.skip(SKIP_TIME_MS);
            }
        });
    prevBtn.setSize(new Dimension(50, 25));
    prevBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	mediaPlayer.skip(-SKIP_TIME_MS);
            }
        });
    fullScreenBtn.setSize(new Dimension(50, 25));
    fullScreenBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mediaPlayer.toggleFullScreen();
      }
    });
    screenshotBtn.setSize(new Dimension(50, 25));
    screenshotBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {    
        mediaPlayer.saveSnapshot(new File("images/screen" + getDateTime() + ".png"));
      }
    });
    
    closeBtn.setSize(new Dimension(50, 25));
    closeBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	    
			
        mediaPlayer.release();
       // mediaPlayerFactory.release();
        controlsFrame.setVisible(false);
        videoFrame.setVisible(false);
          }
    });
   /* 
    mediaPlayer.addVideoOutputEventListener(new VideoOutputEventListener() {  
      public void videoOutputAvailable(MediaPlayer mediaPlayer, boolean videoOutput) {
        if(videoOutput) {
          Dimension size = mediaPlayer.getVideoDimension();
          if(size != null) {
            canvas.setSize(size.width, size.height);
           videoFrame.pack();
           fullScreenStrategy.enterFullScreenMode();
          }
        }
      }
    });
*/
    String mediaPath = videoFile;   
    
    //mediaPlayer.playMedia(mediaPath, mediaOptions);
    mediaPlayer.playMedia(mediaPath, mediaOptions);
    
    
    positionSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if(!positionSlider.getValueIsAdjusting() && !setPositionValue) {
          float positionValue = (float)positionSlider.getValue() / 100.0f;
          mediaPlayer.setPosition(positionValue);
        }
      }
    });
       
    videoFrame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
      //  mediaPlayer.release();
      //  mediaPlayerFactory.release();
        controlsFrame.setVisible(false);
        videoFrame.setVisible(false);
      }
    });
    
    executorService.scheduleAtFixedRate(new UpdateRunnable(mediaPlayer), 0L, 1L, TimeUnit.SECONDS);
  }
  
  
 
  /*
  public void start(String videoFile) {
    String[] mediaOptions = {"video-filter=logo", "logo-file=train.gif", "logo-opacity=25"};;
    mediaPlayer.playMedia(videoFile, mediaOptions);
  }
  */
  
  	
    int lastState = 0;
Rectangle lastBounds = null;

private void toggleDecoration(boolean decorated) {
    videoFrame.dispose();
    if (decorated) {
        //save last bounds and its extended state
        lastState = videoFrame.getExtendedState();
        lastBounds = videoFrame.getBounds();
        try{
            videoFrame.setExtendedState(videoFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }
        catch(Exception ev){
            videoFrame.setBounds(videoFrame.getGraphicsConfiguration().getDevice().getDefaultConfiguration().getBounds());
            ev.printStackTrace();
        }
    }
    else {
        //restore last bounds and its extended state
     //   videoFrame.setBounds(lastBounds);
        videoFrame.setExtendedState(lastState);
    }
    videoFrame.setUndecorated(decorated);
    videoFrame.setVisible(true);
}
      
  	  
  
  public static void pause() {
    mediaPlayer.pause();
  }
  
    private final class UpdateRunnable implements Runnable {

    private final MediaPlayer mediaPlayer;
    
    private UpdateRunnable(MediaPlayer mediaPlayer) {
      this.mediaPlayer = mediaPlayer;
    }
    
    public void run() {
      final long time = mediaPlayer.getTime();
      final long duration = mediaPlayer.getLength();
      final int position = duration > 0 ? (int)Math.round(100.0 * (double)time / (double)duration) : 0;
      
      // Updates to user interface components must be executed on the Event
      // Dispatch Thread
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          updateTime(time);
          updatePosition(position);
     //     updateChapter(chapter, chapterCount);
        }
      });
    }
  }
  
   private void updateTime(long millis) {
   	   DecimalFormat myFormatter = new DecimalFormat("##00");
   	   String hours = String.valueOf(myFormatter.format(TimeUnit.MILLISECONDS.toHours(millis)));
   	   String minutes = String.valueOf(myFormatter.format(TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))));
   	   String seconds = String.valueOf(myFormatter.format(TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
   	   String s = hours + ":" + minutes + ":" + seconds;
   	   timeLabel.setText(s);
  }
  
  private void updatePosition(int value) {
    // Set the guard to stop the update from firing a change event
    setPositionValue = true;
    positionSlider.setValue(value);
    setPositionValue = false;
  }
  
  static class VideoPopupMenu extends MouseAdapter implements ActionListener
    {
        private JPopupMenu fileMenu;

        public void mousePressed(MouseEvent e)
        {
        	if(SwingUtilities.isRightMouseButton(e))	
        		fileMenu.show(e.getComponent(), e.getX(), e.getY());
        	
        	else if(e.getClickCount() == 2) {
        		mediaPlayer.toggleFullScreen();
        		//com.sun.awt.AWTUtilities.setWindowShape(videoFrame, new RoundRectangle2D.Float(0, 0, videoFrame.getWidth(), videoFrame.getHeight(), 35, 35));
        	}
        }

        public void actionPerformed(ActionEvent e) {
	    JMenuItem source = (JMenuItem)(e.getSource());
	    String s = source.getText().trim();
	    
	    if(s.equals("Play")) mediaPlayer.play();
            else if(s.equals("Stop")) mediaPlayer.stop();
            else if(s.equals("Pause")) mediaPlayer.pause(); 
            else if(s.equals("Forward 10s")) mediaPlayer.skip(SKIP_TIME_MS); 
            else if(s.equals("Back 10s")) mediaPlayer.skip(-SKIP_TIME_MS);
            else if(s.equals("Always on Top")) { if (videoFrame.isAlwaysOnTop()) videoFrame.setAlwaysOnTop(false); else videoFrame.setAlwaysOnTop(true); }
            else {
            	if(JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(eVideo.videoFrame)) {
            		//mediaPlayer.playMedia(fileChooser.getSelectedFile().getAbsolutePath(),mediaOptions);
            	}
            }
        }

        public VideoPopupMenu()
        {
            super();
	    fileMenu = new JPopupMenu();
            JMenuItem item1 = new JMenuItem("Play");
            JMenuItem item2 = new JMenuItem("Stop");
            JMenuItem item3 = new JMenuItem("Pause");
            JMenuItem item4 = new JMenuItem("Forward 20s");
            JMenuItem item5 = new JMenuItem("Back 20s");
            JCheckBoxMenuItem item6 = new JCheckBoxMenuItem("Always on Top");
            item1.addActionListener(this);
            item2.addActionListener(this);
            item3.addActionListener(this);
            item4.addActionListener(this);
            item5.addActionListener(this);
            item6.addActionListener(this);
	    fileMenu.add(item1);
	    fileMenu.add(item2);
	    fileMenu.add(item3);
	    fileMenu.add(item4);
	    fileMenu.add(item5);
	    fileMenu.add(item6);
        }
    }
    
  private final class TestPlayerMediaPlayerEventListener extends MediaPlayerEventAdapter {
    
  	  
    public void finished(MediaPlayer mediaPlayer) {
        System.out.println("Video is finished!");
        
        if (playAnotherVideo) {
        	
        }
        else {
          mediaPlayer.release();
          mediaPlayerFactory.release();
          controlsFrame.setVisible(false);
          videoFrame.setVisible(false);
        }
    
    }
    /*
    public void mediaChanged(MediaPlayer mediaPlayer) {
      System.out.println("mediaChanged(mediaPlayer={})", mediaPlayer);
    }
   
    public void paused(MediaPlayer mediaPlayer) {
      System.out.println("paused(mediaPlayer={})", mediaPlayer);
    }

    
    public void playing(MediaPlayer mediaPlayer) {
      System.out.println("playing(mediaPlayer={})", mediaPlayer);
    }

    
    public void stopped(MediaPlayer mediaPlayer) {
      System.out.println("stopped(mediaPlayer={})", mediaPlayer);
    }
*/
   /* 
    public void metaDataAvailable(MediaPlayer mediaPlayer, VideoMetaData videoMetaData) {
      System.out.println("metaDataAvailable(mediaPlayer={},videoMetaData={})", mediaPlayer, videoMetaData);
      
      Dimension dimension = videoMetaData.getVideoDimension();
      System.out.println("dimension={}", dimension);
      if(dimension != null) {
        // FIXME with some videos this sometimes causes lots of errors and corrupted playback until the canvas is resized _again_ or movie is paused and played
        videoSurface.setSize(videoMetaData.getVideoDimension());
        mainFrame.pack();
      }
      else {
        Logger.warn("Video size not available");
      }
      
      // You can set a logo like this if you like...
      File logoFile = new File("./etc/vlcj-logo.png");
      if(logoFile.exists()) {
        mediaPlayer.setLogoFile(logoFile.getAbsolutePath());
        mediaPlayer.setLogoOpacity(0.5f);
        mediaPlayer.setLogoLocation(10, 10);
        mediaPlayer.enableLogo(true);
      }

      // Demo the marquee      
      mediaPlayer.setMarqueeText("VLCJ Test");
      mediaPlayer.setMarqueeSize(40);
      mediaPlayer.setMarqueeOpacity(95);
      mediaPlayer.setMarqueeColour(Color.white);
      mediaPlayer.setMarqueeTimeout(3000);
      mediaPlayer.setMarqueeLocation(50, 100);
      mediaPlayer.enableMarquee(true);

      // Not quite sure how crop geometry is supposed to work...
      //
      // Assertions in libvlc code:
      //
      // top + height must be less than visible height
      // left + width must be less than visible width
      //
      // With DVD source material:
      //
      // Reported size is 1024x576 - this is what libvlc reports when you call 
      // get video size
      //
      // mpeg size is 720x576 - this is what is reported in the native log
      //
      // The crop geometry relates to the mpeg size, not the size reported 
      // through the API
      //
      // For 720x576, attempting to set geometry to anything bigger than 
      // 719x575 results in the assertion failures above (seems like it should
      // allow 720x576) to me
      
//      mediaPlayer.setCropGeometry("4:3");
    }

    
    public void error(MediaPlayer mediaPlayer) {
      System.out.println("error(mediaPlayer={})", mediaPlayer);
    }
    */
  }
}
