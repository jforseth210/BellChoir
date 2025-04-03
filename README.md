## Bell Choir

This is a project that plays songs using a different thread for each note. This is Lab 2 of CS-410 Operating Systems at Carroll College

### Requirements

- [x] Project must be committed and pushed up to GitHub: If you're reading this, that's working
- [x] Must use ANT to build/run: build.xml is included. Should be able to run "ant run"
- [x] Each Member must play each assigned note in a separate thread: We create a Thread for each note in `playSong()`
- [x] The assignment must be able to play the instructor provided song ‘Mary Had a Little Lamb’ with the sound output being properly recognizable with appropriate timing: The song is played as long as the file is specified in build.xml
- [x] Student provided songs may be provided as additional song files to other students for testing/validation: Joy.txt works as well.
- [x] Improper song files will be provided during the final instructor demonstration to determine how well the program behaves when given invalid data: BadSong.txt errors out with detailed error messages

#### Extra Credit

- [x] Creating a custom song to play: Joy.txt plays Ode to Joy/Joyful Joyful We Adore Thee
- [ ] Adding support for Flat notes
- [ ] Allowing the players to play multiple Notes at the same time (harmony)

### Challenges

#### Input Validation

Input validation was challenging to get right. Accounting for the various ways to write an invalid song and providing a helpful error message was difficult.

#### Threads

Creating the threads was a little tricky. At first, I was just calling `member.run()`, which doesn't create a thread.

Then I created a thread for every note in the song `new Thread(member).start()`. Which works, but isn't very efficient.

Finally I stored the threads in the map, which allowed me to reuse them.
