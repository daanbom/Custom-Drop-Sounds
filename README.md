# Custom Drop Sounds
A Plugin which plays custom sounds when you receive certain drops.

## Configuration
Currently, there are a few options for custom sounds.

You can have a custom sound for 5 tiers of drops. 
You can customize how low/high the drop has to be for each tier.
For example, you can pick to have the "lowest" tier be between 50k and 250k.

There are custom sounds for all clue's available, currently they are all the same sound
But you can replace them to have different individual sounds.

In combination with the standard Runelite Ground Items plugin you can highlight items and use a custom sound for those drops.

You can also use custom pet sounds.

Master volume is the global volume of the sound effects (0-100%)


## Choose your own custom sounds
This is actually quite easy, you just have to replace the correct file.
Go to your `/.runelite/custom-drops-sounds/` directory
and replace the corresponding .wav file with a .wav file of your own.
*Note* that the filename has to stay the exact same.
All .wav files are conveniently named, so you know which to replace.
For example, if you would like to replace the sound for when a pet drops, 
you would replace `pet_sound.wav` with a different .wav file *of the same name*

![Readme Image.png](src%2Fmain%2Fresources%2FReadme%20Image.png)

## Future Development
Currently, the sound plays whenever it sees a new item on the floor.
This means that if you drop an item from your inventory it will also play.
I might improve on that in the future if people are interested.

Also planning to add a Pokemon theme soundpack.

If you find any bugs please let me know either through GitHub or by messaging me on discord at el diab#9825

