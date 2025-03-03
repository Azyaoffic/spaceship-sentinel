# Iti0301 2024 Game

Project plan - https://gitlab.cs.ttu.ee/datjul/iti0301-2024-game/-/wikis/Project-plan

## Description
This project aims to develop a multiplayer game using the LibGDX framework in Java. The game will feature multiplayer functionality, allowing players to connect over the internet and engage in gameplay together.

A single- or a multiplayer game where the shared goal of players is to defend the mothership located in the center of the map from waves of monsters. The players are spawned into the world at random places. After the wave ends, the mothership will regenerate a bit and each living player will be awarded some rare thing.
The main goal of players is to defend a mothership. If it is destroyed, all players instantly lose. If the player dies, they lose the “connection to the land” and will become spectators - unable to interact with the world. If all players “lose connection” - the game is over as well. Players are revived after each wave.
In order to defend the base, players can upgrade their weapons and armor or construct different buildings/constructions, such as walls, turrets etc, which require certain resources. Players will be able to find them in gameworld or get those resources after success waves. 
There are three tiers of trades available:

- First tier upgrades can be purchased from the Mothership and are fairly cheap - walls, turrets, basic armor etc

~~Second tier requires players to purchase the “the PT upgrade” for Mothership and consists of upgraded structures, which have more health, deal more damage etc~~ Not yet implemented
- Third tier of upgrades is only accessible through a “wandering trader” who sells the absolute best items in the game which not only are quite expensive, but also require the player to find treasures which are scattered all across the map.

## Installation
- Clone the repository to your local machine.
- Install Java Development Kit (JDK) if not already installed.
- Open the project in your preferred Integrated Development Environment (IDE).

- Firstly run the server file. To run the server, you must open "server" folder as a separate project in your preferred IDE (IntelliJ IDEA is preferred) - **This is Not needed unless you want to host the server yourself (or Taltech server is down)**

- Secondly run the client to launch the game.
  - Open "client" folder as a separate project and launch DesktopLauncher.java from the "Desktop" folder

**Now you can connect to Taltech server using the "Connect to Taltech" button in the menu bypassing the local server.**
It is still possible to connect to local server though - just launch it on the same host as the client.

## Usage
- Upon launching the game, players will be presented with the main menu.
- Players can choose to host a game or join an existing game.
- Once connected, players can move their characters using the arrow keys or WASD and shoot using the mouse left click.
- Inventory opens with TAB button.
- When trading hub is nearbly next to inventory opens trading hub.


### Client-Server interraction
<details><summary>Click here to see details</summary>

## Data sent between client and server:
Now that some things are processed by the server itself, we shall divide the list into “**Server used as liaise between clients**” and “**Server calculates and sends to clients**”.

#### Used as a liaise / for synchronization purposes

- Player information (coordinates, nickname, held weapon)
-	Mothership HP
-	Bullet information

#### Produced by server and sent to clients at the same time

-	Enemies (coordinates, texture, id, target)
-	Resource generators (locations and type)
-	Time (when the game has started and when will it end)
-	Bot players (coordinates, id, health, damage, target etc)

#### Other (unsure)
-	Turrets (in developement)


## How it’s sent:
**99% of data transfers use UDP.**

Every data package has a special Packet class made specifically for it (for example, PacketBullets, PacketEnemies etc) which defines a method used to send data to server.

The data is sent from a client using a Map, containing a “type” field for distinguishing different packets, and info fields, containing other relevant information (coordinates, health, rotation etc.).

The server is listening for these packets and if it sees one, the packet is “repacked” into a larger map, containing source connection id (as given out by kryonet) and packet type through a packet with connection id == 0. 

As a result, the same information looks like this:

From client: Map<”type”: “bulletcoords”, “x”: “500”, “y”: “600”>
From server: Map<0: Map<”type”: “bullet”>, 1: Map<“x”: “500”, “y”: “600”>>

_the time data is sent a bit differently – clientId is -10 and it’s only ever sent from server to clients_


Player position data is updated every time player moves, 

Bullet and enemy data - whenever at least one of those exists.



## Data handling (may be a bit outdated)

**Server**:

- Optionally prints the received data into the console.
- Repacks the data and broadcasts it to the connected clients usually using UDP (players’ nicknames are sent through TCP)


**Client**:

- Saves the received data into variables according to the type.
- Processes each variable from above:
  - receivedPlayers: renders all the players whose clientId differs from the client’s own clientId
  - receivedBullets: for every non-null bullet, it creates a bullet with ownerId == 0 (unlike client’s own bullets which have its id and unlike enemy bullets which use -10) and adds to the list of player’s own bullets
  - receivedNicknames: renders nicknames according to clientId
  - receivedEnemies: creates Enemy object each frame with all the characteristics of the received one (position, target, health etc). This code is not ideal and enemies do have a tendency to malfunction. This will be fixed. 
  - receivedTime: used for calculating how much time is left until the end of the game, also used for spawning waves on timer.


## Connectivity to Taltech servers
Now that we have also installed our server onto Taltech server, it is also an option for game to connect to.

The first connection happens on the client start – first it checks the localhost (to be specific, localhost:8080/8081) and if it fails – it tries to connect to hardcoded Taltech IP (and if it fails – it hungs, we know).

It is also possible to connect to Taltech server even if the local server is running. 

The connection gets reset after every gameover which is not ideal but this was easier to implement and it is more fail-proof (less things that can go wrong).


</details>


## Authors and acknowledgment
Developers: Alexander Smirnov, Daniel Tjulinov, Viktor Dovbnia

## License
This project is licensed under the wtfpl License. See the [LICENSE](https://choosealicense.com/licenses/wtfpl/) file for details.

## Project status
Not In development anymore
