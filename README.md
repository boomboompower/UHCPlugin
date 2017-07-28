# UHC
This is my take on UHC. None of this is done for a specific server, it
has been created for fun out of my free time. It may be used in the
future

### Feature list
Any supported features are listed here, any planned features have a ~~strikethrough~~
- Golden heads
- Apple/Flint dropping rates
- Apple famine
- Cutclean
- Timebomb (70% done)
- Skull/Golden head dropping
- Golden head crafting
- Enderpearl damage toggler
- ~~Backpacks~~
- ~~Special world generations~~
- ~~Custom settings/Config file~~

### Commands & Subcommands
A list of all available commands with their subcommands
- Game _(Permission: **uhc.game**)_
  - Min - Change the minumum player count
  - Max - Change the maximum player count
  - Forcestart - Force start the game
- UHC _(Permission: **uhc.command**)_
  - Heal - Heals the current player
  - Info - Displays current setting info
  - Perls - Toggles pearl damage on & off
  - Rates - Changes apple/flint rates | Toggles apple famine
  - Skulls - Toggles dropping of player heads
  - Cutclean - Toggles cutclean gamemode
  - Timebomb - Toggles timebomb gamemode
  - Broadcast - Broadcasts a message to everyone online
  - Projectile - Toggles entity health on projectile hit setting **\[BROKEN]**

### Useful utils
Things you may find useful to use in your own project
- [AES File](https://github.com/boomboompower/UHCPlugin/blob/master/src/main/java/me/boomboompower/uhcplugin/utils/AES.java)
  \- Encrypts strings based on the given key
- [Permissions File](https://github.com/boomboompower/UHCPlugin/blob/master/src/main/java/me/boomboompower/uhcplugin/utils/Permissions.java)
  \- Better way of handling permissions (Similar to the AES file)
- [CommandBase File](https://github.com/boomboompower/UHCPlugin/blob/master/src/main/java/me/boomboompower/uhcplugin/commands/CommandBase.java)
  \- A neater and more efficient interface for commands. Based on Forge's
  CommandBase class (But created entirely by me)


