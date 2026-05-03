# ByteClans

<p align="center">
  <a href="https://www.java.com/">
    <img src="https://img.shields.io/badge/Java-21+-blue" alt="Java"/>
  </a>
  <a href="https://papermc.io/">
    <img src="https://img.shields.io/badge/PaperMC-1.19%2B-green" alt="PaperMC"/>
  </a>
  <a href="license">
    <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License"/>
  </a>
  <a href="https://discord.com/invite/3K9yrZQRmS">
    <img src="https://img.shields.io/discord/1350369915521204276?label=Discord&color=7289DA&logo=discord&logoColor=white" alt="Discord"/>
  </a>
</p>

**ByteClans** is a lightweight, extensible clan plugin for **PaperMC** servers.  
It provides a clean, minimal clan system out of the box — designed to be extended via addons and built with developers in mind.

---

## Overview

ByteClans focuses on doing one thing well: providing a solid clan foundation that server owners can configure and developers can build on top of.  
The plugin ships with the essentials — clan creation, member management, invite system, chat modes, PvP settings and statistics — while staying lightweight and easy to extend.

---

<p align="center">
  <a href="https://discord.com/invite/3K9yrZQRmS">
    <img src="https://imgur.com/DvyC4jL.png" width="600" alt="ByteSessionRestore preview">
  </a>
  <br/>
  <i>If you need help, join the Discord server.</i>
</p>

---

## Features

- Create, disband, leave and manage clans.
- Fully configurable role system via `roles.yml` — add as many roles as you want.
- Invite system with configurable expiration time.
- PvP mode per clan: No Damage, Safe Damage, Friendly Fire.
- Clan chat mode with per-player toggle.
- Kill, death and streak statistics tracked per clan.
- PlaceholderAPI support out of the box.
- No external database required — works out of the box.
- Performant and server-friendly by design.
- Clean developer API with events, managers and lookups — ready for addons.

---

## Installation

1. Download the latest **ByteClans** release from the [releases page](https://github.com/Bytephoria/byte-clans/releases).
2. Place the JAR inside your server's `plugins/` folder.
3. Start your Paper server — config files will be generated automatically.
4. Configure `config.yml`, `messages.yml` and `roles.yml` to your liking.
5. Restart.

---

## Contributing

1. Fork the repository.
2. Create a branch: `git checkout -b feature/my-feature` or `git checkout -b fix/my-fix`
3. Commit your changes and open a Pull Request.

Please follow the existing code style:
- Use `this.` for all instance field references.
- Use `final` on parameters, local variables and fields wherever applicable.
- Respect the module architecture — keep each layer responsible for its own concerns.
- No breaking changes to existing API interfaces without prior discussion.

### Plugin Philosophy

ByteClans is designed to be a **general-purpose**  clan plugin that works on any server, regardless of game mode.
Pull requests that push the plugin toward a specific game mode — such as clan homes, territory claiming, economy integration, or similar — will be automatically rejected.
If you need those kinds of features, you have two supported paths:

- **Create an addon** that depends on ByteClans. The plugin provides a stable API specifically for extending functionality without bloating the core.
- **Fork the project** and adapt it to your needs if your use case requires tighter integration or custom behavior.
---

## License

This project is released under the [MIT License](LICENSE).  
You are free to use, modify, and distribute it with attribution.
