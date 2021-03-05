## Suggestions Tooltip Showcase

This is an example Bukkit plugin for demonstration purposes on how to add custom tooltips (on-hover) over commands completion suggestions.
Made for Paper 1.16.5

This presents two different approaches: one using ProtocolLib by listening to the `TAB_COMPLETE` packet and the other using Paper's experimental Mojang API `AsyncPlayerSendSuggestionsEvent`.

#### Both of these cases are <b>baked</b> suggestions, a bit of a more complex (expensive) mapping system can be implemented with very little effort for dynamic suggestions (e.g. online player names, factions/towns listing, permission nodes, etc.).

![image](https://user-images.githubusercontent.com/35617540/110066644-b60b9480-7d50-11eb-9381-24a7b0578208.png)
![image](https://user-images.githubusercontent.com/35617540/110066648-b73cc180-7d50-11eb-9ece-60d882ff453e.png)
![image](https://user-images.githubusercontent.com/35617540/110066649-b7d55800-7d50-11eb-8bd6-87b771febbd6.png)

