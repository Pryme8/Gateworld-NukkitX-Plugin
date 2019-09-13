# Gateworld-NukkitX-Plugin
This will crash until you set up mySQL connection in the config. The databases should be easy to figure out from the classes that call any database communication.

- DONE : Alpha Player Class Types (Ranger, Warrior, Craftsman)
- DONE : Basic Guilds with Access Right Hierarchy
- DONE : Migration of Portals Plugin, to internal functions and change the loop function to be more performant.
- DONE : Score Containers that automatically update to the Database on a set interval.
- DONE : Basic Geofences
- DONE : Block Protections by Geofences.
- DONE : Blast protection by block by Geofence.  Blocks on the outside of a Protected geofence still get destroyed the event is not cancled, so damage will still happen.
- SOME : Basic Maps, this is very buggy right now and is just a copy and paste of the maps plugin for nukkit currently.  I have plans to make it way more robust.
- SOME : PHP Sister website that shows player stats and will have player log-in section for more specific details and access to guild information and a geofence map. http://Pryme8.com/Gateworld
- SOME : Building elements on the main world to start creating Instances for.

- WORKING ON : Particles for each of the in game Gates to specifiy their location.

- TODO : Migrate Gates from a yml to a Database table.
- TODO : Make Gates use Geofences instead of metadata.
- TODO : Finish Arena, this includes the scripting for starting/finishing the event and inviting all close players to watch.
- TODO : Finish Guild Access Rights, and create the 3 other guild Types besides RealmGuard: Peaceful, Normal, Hostile
- TODO : Update StartZone and remap the gates.
- TODO : For Peaceful Guilds or the RealmGuards Geofences, all Damage needs to be negated.
- TODO : End the Endermans capabilities inside specific geofences.
- TODO : First MySQL Health Checks.  Make sure that the Server repairs tables or creates them if they do not exsist.
- TODO : Player Charicter Stats and XP System.
- TODO : Player Class Specific build restrictions.
- TODO : Player Skill Trees.
- TODO : Physical Currency System (PCS), not just metadata but an actuall stack that you can carry like a money bag.  Half will drop on death.
- TODO : Banks for PCS
- TODO : Mining Instances.
- TODO : Serilaize Method for Player Inventory
- TODO : Seperate FreeZone Inventory from the rest of the Zones.
- TODO : Link PHP Sites Geofence Map to Players inventory and only build graphical representations of the world for maps the player currently has in their inventory.
- TODO : Create "MapBook" Item, where player can stack known maps for the Geofence Map system.
- TODO : Figure out the bug that pops up in log about a null event randomly... Usually late at night when one of the testers logs on and is doing stuff.


