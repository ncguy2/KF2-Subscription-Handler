# KF2 Server Workshop Tool

A tool for managing a "Killing Floor 2" Server; mainly the custom maps that are added through Steam.

## How to use
Download the released file(s) you want, and save it in the same folder as your KF2 Server (SteamCMD/KF2Server/).
Everything else should be self-explanatory. If problems occur, you're free to contact me or send in a bug report.
I suggest checking "todo.txt" first, though. Some of those problems will be logged in there.

## Changes from [sakuolin/KF2-Subscription-Handler](https://github.com/sakuolin/KF2-Subscription-Handler)

- UI uses JavaFX
- Steam workshop API support (Requires API key and SteamId)
- More subscription data visible to the user
- DragandDrop reordering of map cycle
- Selectable working directory (Application can be run from anywhere and pointed to the Killing Floor 2 server directory)
- Confirmation dialog for individual subscriptions
- Basic support for Workshop collections

## Plans

- Configuration for all major server settings
- Full SteamCMD/Steam Workshop integration
- Workshop collection support ([u/Daar375](https://www.reddit.com/r/killingfloor/comments/6vxvra/if_anyone_is_interested_i_wrote_a_small_program/dm45n2b/))
- Local cache to reduce delays caused by Http requests

## Development Limitations

- Steam API requests may be refused if too many are sent at once due to the "10 requests per 10 seconds" rule, and use the cache-aware functions where applicable