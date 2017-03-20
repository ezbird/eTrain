#!/bin/sh

# path to etrain folder
cd ~/eTrain

# run etrain   -   echo "asdf" | sudo -S 
java -mx750m -splash:images/etrain/chugging-train.gif -Dswing.defaultlaf=com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel -jar bin/etrain.jar $1 $2 $3

# use this instead to utilize advanced features that require sudo privileges
# echo "sudo-password-here" | sudo -S java -jar -mx400m bin/etrain.jar off iff mon1

