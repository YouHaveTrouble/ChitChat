# ChitChat
## A simple chat formatting plugin

## Why?

ChitChat was created to handle basic chat functionality while preserving message signatures and using its features to
the fullest extent.

***This plugin requires chat signatures to be enabled to function correctly.***

## Features

### Freely customizable chat format

#### Format

Chat format field in the config uses [MiniMessage](https://docs.advntr.dev/minimessage/format.html) to take full
advantage of minecraft's text component system. 

#### PAPI Placeholders

Plugin requires you to wrap placeholders in a `<placeholder:papi_placeholder>` format, so for example
`%player_displayname%` would be `<placeholder:player_displayname>`.

### Ability for moderators to delete messages

Players with `chitchat.deletemessage` permission will see a red [x] next to every player message in chat. Clicking it
will delete the message using that messages associated chat signature.

### Using colors and formatting in messages

You can allow players to use colors and formatting in their messages by giving them `chitchat.formatting` permission.
If you want finer control over which minimessage tags are allowed, here's a full list of available options in a
`permissiion: tag` format:
```yaml
chitchat.color: <color> tags
chitchat.rainbow: <rainbow> tags
chitchat.gradient: <gradient> tags
chitchat.font: <font> tags
chitchat.format: All tags mentioned below
chitchat.format.bold: <bold> tags
chitchat.format.italic: <italic> tags
chitchat.format.obfuscated: <obfuscated> tags
chitchat.format.strikethrough: <strikethrough> tags
chitchat.format.underline: <underline> tags
```
