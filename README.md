Mastermind
==========

Csci 582 Android: project 2, Mastermind game.

Design Choices

I designed this app with the goal of simple, clean design. I had a lot of fun choosing the colors and working on the feel of the app. The gameboard of this app is comprised of nested layouts which were created dynamically to allow for different game difficulties. The game relies on DialogFragments to guide the user through the application. The MasterMind class is the container for almost all of the application’s data, and user scores are stored inside a database.

This app has been tested on two emulators, both on API level 17. One emulator mimics a Nexus 4, and the other features a very high density display. The minimum API level supported is 14.

Known bugs: None.

Missing Functionality: None. I would, however, love to make this app adjustable to any screen size.

How to Play: Choose a color to the right of the screen. The color will be enlarged when selected. Then, tap the peg slot where you would like to place the color. Hit the “Check” button when you are done. The first number represents the “red pegs” and the second number represents the “white pegs” (I chose to keep them both white). 

