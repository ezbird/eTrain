package sweetdream;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import java.awt.TextField;
import java.io.*;
import java.sql.SQLException;
import javax.sound.sampled.TargetDataLine;
import javax.speech.recognition.*;
import javax.swing.*;
import javax.swing.tree.TreePath;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import java.net.*;

public class eVoice
{
    private Recognizer recognizer;
   // private JSGFGrammar jsgfGrammarManager;
    private Microphone microphone;

    public eVoice()
        throws IOException, PropertyException, InstantiationException, SQLException
    {
      //  java.net.URL url = (sweetdream.Sweetdream.class).getResource("Sweetdream.config.xml");
      
      // The config.xml is needed by sphinx4
      java.net.URL url = Sweetdream.class.getResource("src/Sweetdream.config.xml");
        ConfigurationManager cm = new ConfigurationManager(url);
        recognizer = (Recognizer)cm.lookup("recognizer");
   //      jsgfGrammarManager = (JSGFGrammar)cm.lookup("jsgfGrammar");
        microphone = (Microphone)cm.lookup("microphone");
    }

    public void execute()
        throws IOException, GrammarException
    {
        System.out.print(" Loading recognizer ...");
        recognizer.allocate();
        System.out.println(" Ready");
	Sweetdream.startupTimeEnd = System.currentTimeMillis();
	eFunctions.printActivity("Startup Time: " + ((Sweetdream.startupTimeEnd - Sweetdream.startupTimeStart)) + " ms");
	
        if(microphone.startRecording())
        {
            Sweetdream.statusDisplay.setText("Ready");
            Sweetdream.frame.setVisible(true);
            if(Sweetdream.computerVoice)
            {
                try
                {
                    Sweetdream.mainSoundPlayer.open(Sweetdream.startupSound);
                    Sweetdream.mainSoundPlayer.play();
                }
                catch(BasicPlayerException e1) { }
            }
            do
            {
                Result result = recognizer.recognize();
                if(result != null)
                {
                    String resultText = result.getBestFinalResultNoFiller().toLowerCase();
                    if(resultText != "") { eFunctions.updateLog(resultText); Sweetdream.voiceCommandDisplay.setText(resultText); }
                    
                    /* Control voice commands if audio is currently playing */
                    // WOuld checking the progress bar be better?  or use isPlaying variable? 
                    if((!Sweetdream.songDurationDisplay.getText().equals(" / 0:00") || Sweetdream.currentPlayingSong.contains("flac")) 
                    	    && !Sweetdream.songDurationDisplay.getText().equals(" / 0:01") 
                    	    && !Sweetdream.songDurationDisplay.getText().equals(" / 0:02"))
                    {
                       // Something is playing, act if its "computer stop please", otherwise ignore
                       if(resultText.equals("computer stop please")) { eFunctions.reset(); return; }
		    }
            
		    // PLAYING ARTIST
                    if(resultText.startsWith("play") && resultText.endsWith("please") && !resultText.contains("the album"))
                    {
                     //   if(Sweetdream.progressBar.getValue() != 0 || Sweetdream.isPaused)
                     //   {
                     //       return;
                     //   }
		     String tempUsingSearch = "";
                        try
                        {
                            tempUsingSearch = eFunctions.voiceCommandSearch(resultText, "artists");
                        }
                        catch(SQLException e) { System.out.println("Problem with SQL " + e); }
			
                        if(tempUsingSearch != "" && Sweetdream.found.equals("yes"))
                        {
			    eFunctions.closePlaylist();
			    eFunctions.closeMood();
			    eFunctions.toggleShowLibrary("hide");
			    TreePath path2 = Sweetdream.tree.getPathForRow(0);
                            CheckNode node = (CheckNode)path2.getLastPathComponent();
                            if(node != null) node.setSelected(true);
                            
                            Sweetdream.playTypeSelectBox.setSelectedIndex(0);
			    Sweetdream.usingSearch = tempUsingSearch;
			    Sweetdream.controlsPane.setBorder(BorderFactory.createTitledBorder(tempUsingSearch));
			    Sweetdream.themeDisplay.setText(Sweetdream.usingSearch);
			    Sweetdream.usingSearch2 = "";
			    Sweetdream.tabbedPaneLibrary.setSelectedIndex(0);
                            Sweetdream.nextTrack("audible 'play <artist> please' command");
                        }
                    }
		    
		    // ACTIVATE PLAYLIST
                    if(resultText.startsWith("open") && resultText.contains("play list") && !resultText.contains("the album"))
                    {
                        try
                        {
                            eFunctions.voiceCommandSearch(resultText, "playlists");
                        }
                        catch(SQLException e) { System.out.println("Problem with SQL " + e); }
			
                        if(Sweetdream.found.equals("yes"))
                        {
                            Sweetdream.playTypeSelectBox.setSelectedIndex(0);
			    try { eFunctions.displayPlaylist(Sweetdream.usingSearch2); } catch(SQLException sq) { sq.printStackTrace(); }
                            Sweetdream.nextTrack("audible 'open <playlistname> playlist' command");
                        }
                        else System.out.println("Playlist with this voicecommand not found.");
                    }
                    
                    
                    // PLAY ALBUM ======================================================
		    else if(resultText.startsWith("play the album"))
                    {
                        String editedResultText = " ";
			String tempUsingSearch = "";
                        String tempUsingSearch2 = "";
                        try
                        {
                            Sweetdream.mainSoundPlayer.stop();
                        }
                        catch(BasicPlayerException ew) { ew.printStackTrace(System.out); }
			
                        Sweetdream.getLastSpace = resultText.lastIndexOf(" ");
                        String lastWord = resultText.substring(Sweetdream.getLastSpace, resultText.length());
                        System.out.println("look here: " + lastWord);
			
                             if(lastWord.equals(" one"))   tempUsingSearch2 = "01";
			else if(lastWord.equals(" two"))   tempUsingSearch2 = "02";
			else if(lastWord.equals(" three")) tempUsingSearch2 = "03";
			else if(lastWord.equals(" four"))  tempUsingSearch2 = "04";
                        else if(lastWord.equals(" five"))  tempUsingSearch2 = "05";
                        else if(lastWord.equals(" six"))   tempUsingSearch2 = "06";
                        else if(lastWord.equals(" seven")) tempUsingSearch2 = "07";
                        else if(lastWord.equals(" eight")) tempUsingSearch2 = "08";
                        else if(lastWord.equals(" nine"))  tempUsingSearch2 = "09";
                        else if(lastWord.equals(" ten"))   tempUsingSearch2 = "10";
                        else if(lastWord.equals(" eleven"))tempUsingSearch2 = "11";
                        else if(lastWord.equals(" twelve")) tempUsingSearch2 = "12";
                        else if(lastWord.equals(" thirteen")) tempUsingSearch2 = "13";
                        else if(lastWord.equals(" fourteen")) tempUsingSearch2 = "14";
                        else
                        if(lastWord.equals(" fifteen"))
                        {
                            tempUsingSearch2 = "15";
                        } else
                        if(lastWord.equals(" sixteen"))
                        {
                            tempUsingSearch2 = "16";
                        } else
                        if(lastWord.equals(" seventeen"))
                        {
                            tempUsingSearch2 = "17";
                        } else
                        if(lastWord.equals(" eighteen"))
                        {
                            tempUsingSearch2 = "18";
                        } else
                        if(lastWord.equals(" nineteen"))
                        {
                            tempUsingSearch2 = "19";
                        } else
                        if(lastWord.equals(" twenty"))
                        {
                            tempUsingSearch2 = "20";
                        }
                        Sweetdream.usingSearch2 = tempUsingSearch2;
                        if(resultText.contains("track"))
                        {
                            editedResultText = resultText.substring(0, resultText.lastIndexOf("track") - 1);
                            Sweetdream.playTypeSelectBox.setSelectedIndex(0);
                        } else
                        if(lastWord.equals(" please"))
                        {
                            editedResultText = resultText.substring(0, resultText.lastIndexOf("please") - 1);
                            Sweetdream.playTypeSelectBox.setSelectedIndex(0);
                            Sweetdream.usingSearch2 = "01";
                            Sweetdream.voiceCommandSequential = true;
                            System.out.println("Play album please. --  editedResultText: " + editedResultText);
                        } else
                        if(lastWord.equals(" random"))
                        {
                            editedResultText = resultText.substring(0, resultText.lastIndexOf("random") - 1);
                            Sweetdream.usingSearch2 = "";
                            Sweetdream.playTypeSelectBox.setSelectedIndex(0);
                            System.out.println("Play album randomly. --  editedResultText: " + editedResultText);
                        }
                        System.out.println("editedResultText: " + editedResultText);
                        if(editedResultText != " ")
                        {
                            try
                            {
                                tempUsingSearch = eFunctions.voiceCommandSearch(editedResultText, "albums");
				Sweetdream.usingSearch = tempUsingSearch;
				Sweetdream.controlsPane.setBorder(BorderFactory.createTitledBorder(tempUsingSearch));
                            }
                            catch(SQLException e) { System.out.println("Problem with SQL " + e); }
                        }
                        if(Sweetdream.usingSearch != "")
                        {
                            //eFunctions.Acknowledged();
                         //   eFunctions.refreshTree("library");
			 
                            Sweetdream.themeDisplay.setText(Sweetdream.usingSearch);
                            Sweetdream.controlsPane.setBorder(BorderFactory.createTitledBorder(Sweetdream.usingSearch));
                            Sweetdream.albumCoverOverride = true;
                            eFunctions.closePlaylist();
                            Sweetdream.tabbedPaneLibrary.setSelectedIndex(0);
                            Sweetdream.nextTrack("audible 'play the album' command");
                        }
                    }
		    /* else if(resultText.startsWith("open") && resultText.endsWith("play list"))
                    {
                        String spokenPlaylist = resultText.replace("open ", "").replace(" play list", "");
                        System.out.println("spokenPlaylist: " + spokenPlaylist);
                        try
                        {
                            eFunctions.displayPlaylist(resultText.replace("open ", "").replace(" play list", ""));
                        }
                        catch(SQLException e) { System.out.println("Problem with SQL " + e); }
                    } 
		    */
		    else if(resultText.startsWith("open") && resultText.endsWith("collage please"))
                    {
                    	    
                    	    eCollage.collageVideoFrame.setVisible(true);
                    	    Sweetdream.frame.setVisible(false);
                    	    eCollage.collageVideoFrame.setExtendedState(6);
                    }   
                   else if(resultText.startsWith("transfer") && resultText.endsWith("songs please"))
                    {
                        eFunctions.Acknowledged();
                        if(resultText.substring(9).equals("seven songs please"))
                        {
                            Sweetdream.transferTracks(7);
                        } else
                        if(resultText.substring(9).equals("twenty songs please"))
                        {
                            Sweetdream.transferTracks(20);
                        } else
                        if(resultText.substring(9).equals("fifty songs please"))
                        {
                            Sweetdream.transferTracks(52);
                        }
                    } 
		    /*
		    else if(resultText.equals("deactivate random mode"))
                    {
                        eFunctions.Acknowledged();
                        Sweetdream.playTypeSelectBox.setSelectedIndex(1);
                    } 
		    else if(resultText.equals("activate random mode"))
                    {
                        eFunctions.Acknowledged();
                        Sweetdream.playTypeSelectBox.setSelectedIndex(0);
                    } 
		    */
		    else if(resultText.equals("computer stop please"))
                    {
                        eFunctions.reset();
                    }
		    else if(resultText.equals("open he may uhl please"))
                    {
                        eFunctions.Acknowledged();
                        eFunctions.openBrowser("http://www.gmail.com/");
                    } else
                    if(resultText.equals("computer next track please"))
                    {
                        Sweetdream.nextTrack("Audible 'computer next track please' command");
                    } 
		    /*
		    else if(resultText.equals("computer down volume"))
                    {
                        eFunctions.Acknowledged();
                    } else if(resultText.equals("computer normal volume"))
                    {
                        eFunctions.Acknowledged();
                    } else if(resultText.equals("computer up volume"))
                    {
                        eFunctions.Acknowledged();
                    } 
		    */
		    else if(resultText.equals("computer open channel"))
                    {
                        Sweetdream.captureAudio();
                    } else if(resultText.equals("close channel please"))
                    {
                        Sweetdream.targetDataLine.stop();
                        Sweetdream.targetDataLine.close();
                        Sweetdream.statusDisplay.setText("Channel recorded successfully.");
                        try
                        {
                            Sweetdream.mainSoundPlayer.open(Sweetdream.ackSound);
                            Sweetdream.mainSoundPlayer.play();
                            Sweetdream.mainSoundPlayer.play();
                        }
                        catch(BasicPlayerException ew) { ew.printStackTrace(System.out); }
                    } 
		    else if(resultText.equals("computer initiate shut down"))
                    {
                        eFunctions.shutdownComputer("go");
                    } else
                    if(resultText.equals("computer over ride shut down"))
                    {
                        eFunctions.shutdownComputer("stop");
                    } else
                    if(resultText.equals("computer stop music"))
                    {
                        eFunctions.Acknowledged();
                        eFunctions.reset();
                    } else
                    if(resultText.equals("computer collapse tree"))
                    {
                        eFunctions.Acknowledged();
                        eFunctions.collapseTree("Music");
                    } else
                    if(resultText.equals("computer expand tree"))
                    {
                        eFunctions.Acknowledged();
                        eFunctions.expandTree("Music");
                    } else
                    if(resultText.equals("open random music program") || resultText.equals("activate random music program"))
                    {
                        eFunctions.Acknowledged();
                        Sweetdream.usingSearch = "";
                        Sweetdream.controlsPane.setBorder(BorderFactory.createTitledBorder("Random"));
                        Sweetdream.themeDisplay.setText("");
                        Sweetdream.themeTextBox.setText("");
                        Sweetdream.nextTrack("audible 'activate random music program' command");
                    } else
                    if(resultText.equals("play three tracks random"))
                    {
                        eFunctions.Acknowledged();
                        Sweetdream.usingSearch = "";
                        Sweetdream.controlsPane.setBorder(BorderFactory.createTitledBorder("Random"));
                        Sweetdream.themeDisplay.setText("");
                        Sweetdream.themeTextBox.setText("");
                        Sweetdream.tracksLeftActionSelectBox.setVisible(true);
                        Sweetdream.tracksLeft = 3;
                        Sweetdream.tracksLeftDisplay.setText("3 left ");
                        Sweetdream.nextTrack("audible 'play three tracks random' command");
                    } else
                    if(resultText.startsWith("play") && resultText.endsWith("more"))
                    {
                        eFunctions.Acknowledged();
                        System.out.println(resultText.substring(5));
                        Sweetdream.tracksLeftActionSelectBox.setVisible(true);
                        if(resultText.substring(5).equals("to more"))
                        {
                            Sweetdream.tracksLeft = 2;
                            Sweetdream.tracksLeftDisplay.setText("2 left ");
                        } else
                        if(resultText.substring(5).equals("three more"))
                        {
                            Sweetdream.tracksLeft = 3;
                            Sweetdream.tracksLeftDisplay.setText("3 left ");
                        } else
                        if(resultText.substring(5).equals("seven more"))
                        {
                            Sweetdream.tracksLeft = 7;
                            Sweetdream.tracksLeftDisplay.setText("7 left ");
                        }
                    } else
                    if(resultText.startsWith("transfer") && resultText.endsWith("please"))
                    {
                        String editedVoiceCommand = "play " + resultText.substring(9);
                        try
                        {
                            eFunctions.voiceCommandSearch(editedVoiceCommand, "artists");
                        }
                        catch(SQLException e) { System.out.println("Problem with SQL " + e); }
			
                        if(Sweetdream.usingSearch != "" && Sweetdream.found.equals("yes"))
                        {
                            Sweetdream.getSecondLevelCommand = true;
                        }
                    }
                } else
                {
                    System.out.println("I can't hear what you said.\n");
                }
            } while(true);
        } else
        {
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
            System.exit(1);
            System.out.print("\nDone. Cleaning up ...");
            recognizer.deallocate();
            System.out.println(" Goodbye.\n");
            System.exit(0);
            return;
        }
    }

    /*
    
    private void loadAndRecognize(String grammarName)
        throws IOException, GrammarException
    {
        jsgfGrammarManager.loadJSGF(grammarName);
        dumpSampleSentences(grammarName);
        recognizeLoadedGrammar();
    }

    private void recognizeLoadedGrammar()
        throws GrammarException
    {
        boolean done = false;
        do
        {
            if(done)
            {
                break;
            }
            Result result = recognizer.recognize();
            String bestResult = result.getBestFinalResultNoFiller();
            RuleGrammar ruleGrammar = jsgfGrammarManager.getRuleGrammar();
            RuleParse ruleParse = ruleGrammar.parse(bestResult, null);
            if(ruleParse != null)
            {
                System.out.println("\n  " + bestResult + "\n");
                done = isExit(ruleParse);
            }
        } while(true);
    }

    private boolean isExit(RuleParse ruleParse)
    {
        String tags[] = ruleParse.getTags();
        for(int i = 0; tags != null && i < tags.length; i++)
        {
            if(tags[i].trim().equals("exit"))
            {
                return true;
            }
        }

        return false;
    }

    private void loadAndRecognizeMusic()
        throws IOException, GrammarException
    {
        jsgfGrammarManager.loadJSGF("music");
        RuleGrammar ruleGrammar = jsgfGrammarManager.getRuleGrammar();
        addRule(ruleGrammar, "song1", "listen to over the rainbow");
        addRule(ruleGrammar, "song2", "listen to as time goes by");
        addRule(ruleGrammar, "song3", "listen to singing in the rain");
        addRule(ruleGrammar, "song4", "listen to moon river");
        addRule(ruleGrammar, "song5", "listen to white christmas");
        addRule(ruleGrammar, "song6", "listen to mrs robinson");
        addRule(ruleGrammar, "song7", "listen to when you wish upon a star");
        addRule(ruleGrammar, "song8", "listen to the way we were");
        addRule(ruleGrammar, "song9", "listen to staying alive");
        addRule(ruleGrammar, "song10", "listen to the sound of music");
        addRule(ruleGrammar, "song11", "listen to the man that got away");
        addRule(ruleGrammar, "song12", "listen to diamonds are a girl's best friend");
        jsgfGrammarManager.commitChanges();
        dumpSampleSentences("music");
        recognizeLoadedGrammar();
    }

    private void addRule(RuleGrammar ruleGrammar, String ruleName, String jsgf)
        throws GrammarException
    {
        javax.speech.recognition.Rule newRule = ruleGrammar.ruleForJSGF(jsgf);
        ruleGrammar.setRule(ruleName, newRule, true);
        ruleGrammar.setEnabled(ruleName, true);
    }

    private void dumpSampleSentences(String title)
    {
        System.out.println(" ====== " + title + " ======");
        System.out.println("Speak one of: \n");
        jsgfGrammarManager.dumpRandomSentences(200);
        System.out.println(" ============================");
    }
    */
    
}

