# Android-Real-Time-Voice-Encryption

This is the project containing the implementation of the Android Real-Time Voice Encryption, by Aitor Martinicorena.

The contents of the project are stored in Downloads/Project.

The folder VoipCryptoCall stores the files of the Android project, implemented in Android Studio.

The VoipCryptoCall.apk is an installation file for the application. It has to be transferred to the mobile phones and selected. This will start the installation process.

The folder server contains the files index.php and methods.php, which form the working part of the server. In order to put the server to work, those two files have to be dumped into the htdocs folder in a XAMPP server with MySQL and phpMyAdmin support. 

The table with the user information can be created using the script in the /Downloads/Project/Database.md file in phpMyAdmin. It has to be created in a database called DataUsers. 

Using the application:

-- When first opening the application after installation, an activity for registering will open. The user has to fill in two parameters: mobile phone and password. This will be the users' credentials. 

-- The next times the application is opened, another activity will open. This one requires the password to open.

-- After signing in or registering, the user will be taken to another activity. This one has three options: listen to calls ("Listen calls"), select user from list ("Choose contact") or inserting a user's number phone ("Introduce number"). The first opens another activity that listens to incoming calls and, as of this version, has to be opened in at least one of the users for the communication to take place. The second one opens a list with the devices' stored contacts so the user can select one. The last one allows the user to insert the number of the user he wants to call to. The last two result in calling a calling activity that establishes a communication with that user (if information is correct).
