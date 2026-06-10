# Nether Reactor: Revisited

A Fabric mod that re-adds the Nether Reactor from Minecraft: Pocket Edition to modern versions of Minecraft: Java Edition for the Fabric mod loader.

##  Features
* You can reactivate a deactivated Nether reactor core by mimicking the structure of activating a normal Nether reactor, but with obsidian instead of cobblestone.
* When activated, a Nether reactor core has two states: "stable" and "unstable". A Nether reactor core will be stable once it finishes activating, and will be unstable and explode after a set amount of time if the reactor structure is destroyed, or immediately if the unstable core itself is destroyed.
  * You can change the value of the `nether-reactor-revisited:stable_core_lifetime` game rule (default value: `900`) to configure how long a stable Nether reactor will remain active.
  * You can change the value of the `nether-reactor-revisited:unstable_core_lifetime` game rule (default value: `60`) to configure how long an unstable Nether reactor will remain until it explodes.