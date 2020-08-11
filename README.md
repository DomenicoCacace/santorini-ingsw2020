![Java CI with Maven](https://github.com/DomenicoCacace/ingsw2020-Albanese-Boldini-Cacace/workflows/Java%20CI%20with%20Maven/badge.svg)

![](https://www.despelvogel.com/wp-content/uploads/2018/06/img_5b2c2aeaf05ad.png)

# Overview
Santorini is an abstract strategy board game for 2-4 players (2-3 in this specific implementation) designed and released in 2004 by Gordon Hamilton. Ispired by the architecture of cliffside villages on Santorini Island in Greece, the game is played on a grid where each turn players build a town by placing building pieces up to three levels high. To win the game, players must move one of their two characters to the third level of the town.  
The game can be expanded using god powers (introduced by Roxley Games), which give each player a unique way to break the rules.

## Functionalities
The implemented functionalities are: 

- Basic Rules
- Complete Rules
- Socket
- CLI
- GUI
- Multiple games
- Persistence


## Authors
Scaglione Margara - Gruppo <b>AM37</b>
- <b>Luca Albanese</b> (luca1.albanese@mail.polimi.it)
- <b>Filippo Boldini</b> (filippo.boldini@mail.polimi.it)
- <b>Domenico Cacace</b> (domenico.cacace@mail.polimi.it)

## License
The project has been developed for the Software Engineering final examination at Politecnico di Milano; the graphical resources copyright is held by Roxley Games, which provided the mentioned resources for educational purposes only.
  
# Starting the game
The game is split into two different JAR files, which can be downloaded from the Releases section of the repository or built using the  <code>mvn clean install</code> command.  

## Server
The machine running the server must be reachable from the clients in order to play the game. To start the server, use the command  

<code> java -jar Server.jar </code>  
 
 By default, when launching this command, the server starts to listen for incoming message on port 4321, and will accept no more than 15 clients in the waiting room; custom values can be set using command line argument: for example, to launch a server instance greeting a maximum of 30 clients on port 15122, you can use the command  

 <code> java -jar Server.jar --port 15122 --maxClients 30</code>  

 Once launched, the server will print the events log on the standard output.

 ## Client
 There are three possible options for the client graphics:
 - GUI: <code>java -jar Client.jar</code>
 - Base CLI: <code>java -jar Client.jar --CLI</code>
 - Advanced CLI: <code>java -jar Client.jar --CLI --beta</code>
  
  Both the base and advanced CLI require a terminal emulator supporting ANSI color codes; the advanced mode also requires the terminal emulator to support non-canonical input and to be run on a Unix-like OS. The advanced CLI mode is in an experimental phase, so it might contain some bugs.

  ### Login
  Upon launching the client application, the user is asked to insert its username and the server address: the server address can be a symbolic URL (e.g. <i>santoriniserver.com</i>) or the public IP of the machine hosting the server. By default, the port the client tries to connect to is 4321; it can be changed appending <code>:portNumber</code> to the server address (e.g. <code>10.0.0.5:2541</code>). 

   After the first start, the game saves a list of the last 5 server-username combos used to log in.

  ### Lobbies

  If the login is successful, the user is asked to create or join a lobby. Lobby names are unique, so the user cannot choose the name of an already existing lobby; at the end of a match, the lobby is deleted. The lobby creator must also provide the number of players for the game to start (2 or 3).  
  In the waiting room, two or more users can have the same username; when joining a lobby, the usernames of the players must be unique: in this case, the player is asked to change its username before entering the lobby.  

  If an user leaves the lobby in this phase, the lobby ownership is passed to the second player which joined if present, otherwise the lobby is deleted.  

  ### Before the game starts

  When the lobby reaches its maximum capacity, the lobby owner is asked to choose a number of god powers equal to the number of players for the game.  
  Once all the gods have been selected, the players are asked to choose their god, starting from the second player who joined the lobby; upon choosing a god, it is removed from the list of available gods for the following players, so that each player has an unique god power for the game.  
  When all the players chose their god, the lobby owner decides which player makes the first move.

  ### During the game

  During their turn, each player has to choose one of their worker, move it in an anjacent cell and place a building block; when a player moves one of their workers on a level 3 building, the game ends. God powers can alter this procedure.  


  ### After the game ends

  When the game ends, all players are moved to the waiting room, making them ready to start a new match. If the number of players in the waiting room exceeds the maximum capacity, they are disconnected from the server.

   ### Game interruptions
  The game might not end with a winner: as asked in the project specifications, if a player disconnects during the game, all players are disconnected as well, leaving the game with no winner.  
  The game can be resumed if the players reconnect to a lobby with the same name of the original one with the same usernames they used in the first place. Saved games are held on the server for a week.
