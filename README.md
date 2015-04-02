![alt tag](http://dev.bukkit.org/media/images/82/383/ArenaOneShot.png)

A minigame that allows you to customize Bow & Arrows along with all other weapons. Players start with one highly damaging arrow (customizable) and receive another only once (customizable) they have killed someone else. The game can be a giant free for all, or you can have players be on specific teams to fight it out.

You can modify almost all aspects of the game by editing the OneShotConfig.yml. Including what items they get, whether they get prizes, etc.

## Features

 - Customizable damage settings for bow/arrow and any other minecraft item
 - Can make arrows instashot (no need to pull back on the bow)
 - Can make arrows break when they miss
 - Customize items to give after player kill another player
 - On death blast like on mineplex
 - Killstreaks with customizable messages
 - Any of the default BattleArena options (disguises, prizes, join requirements, etc) 

## Installing

Put the ArenaOneShot.jar in your plugins folder, along with BattleArena.jar. 

## Creating an arena

 - 1. /os create <arena name> : Example /oitc create myFirstArena
 - 2. /os alter <arena name> spawn 2 <- setup a second spawn, you can keep adding them if you want more than 2,
 like /os alter < arena name> spawn <team number>. Example : /os alter myFirstArena spawn 3 
 - 3. Get the item you want to use as kill reward in hand (default 1 arrow) and type /os setItem
 - 4. Finaly you are done, type /os join and let's test it out ;)

## Joining
 
 - arena.join.oneshot : Permission node to join.
 - arena.leave : Permission to leave the game.
 - /os join : Command to join. Or you can click on a join sign
 - /os leave : Command to leave. Or you can click on a leave sign 

## Help

If you found any bug or want new feature to be added leave a comment or use tickets.

## Requirements

This plugin is made to hook into [BattleArena](http://dev.bukkit.org/bukkit-plugins/battlearena2/), so you also need to download it as well. 

## Official bukkit page
http://dev.bukkit.org/bukkit-plugins/arenaoneshot/
