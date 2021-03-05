## Suggestions Tooltip Showcase

This is an example Bukkit plugin for demonstration purposes on how to add custom tooltips (on-hover) over commands completion suggestions.
Made for Paper 1.16.5

This presents two different approaches: one using ProtocolLib by listening to the `TAB_COMPLETE` packet and the other using Paper's experimental Mojang API `AsyncPlayerSendSuggestionsEvent`.

#### Both of these cases are <b>baked</b> suggestions, a bit of a more complex (expensive) mapping system can be implemented with very little effort for dynamic suggestions (e.g. online player names, factions/towns listing, permission nodes, etc.).
