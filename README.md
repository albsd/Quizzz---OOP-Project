# The QUIZZZZ app

## Description of project
This project was developed by a group of 6 students from the 1st year BSc. Computer Science and Engineering at TU Delft in 2022. As the university was celebrating its 180th anniversary, this project manifests a quiz application to raise the energy consumption awareness.

## Architecture
The app consists of three subprojects.
1. **Client** - a javafx application
2. **Commons** - set of classes common to the client and server
3. **Server** - java spring boot server

## Group members

| Profile Picture | Name | Email |
|---|---|---|
| <a href="https://imgbb.com/"><img src="https://i.ibb.co/7VSxGDk/Capture.png" alt="Philip" border="0" height=60></a> | Pil Kyu Cho | p.k.cho@student.tudelft.nl|
| <a href="https://imgbb.com/"><img src="https://i.ibb.co/RCYJ2zR/Lohithsai.jpg" alt="Lohithsai" border="0" height=60></a> | Lohithsai Yadala Chanchu | L.S.YadalaChanchu@student.tudelft.nl|
| <a href="https://tinyurl.com/jakubpatrik"><img src="https://tinyurl.com/jakubpatrik" alt="Jakub" border="0" height=60></a> | Jakub Patrik | J.Patrik@student.tudelft.nl|
| <a href="https://i.imgur.com/wqNjFX6.jpg"><img src="https://i.imgur.com/wqNjFX6.jpg" alt="Albert" border="0" height=60></a> | Albert-Alexandru Sandu | A.A.Sandu@student.tudelft.nl|
| <a href="https://ibb.co/1bzqskD"><img src="https://i.ibb.co/1bzqskD/kare.jpg" alt="Kerem" border="0" height=60></a> | Kerem Yoner | K.Yoner@student.tudelft.nl|
| <a href="https://imgbb.com"><img src="https://i.ibb.co/9T45dgz/D487-F8-F3-4-ED9-41-F2-AFA0-B1-CBF04-BF58-C.jpg" alt="Elias" border="0" height=60></a> | Elias Hoste | E.R.Hoste@student.tudelft.nl|

## How to run it
1. Make sure the `/server/quizzzz.mv.db` is a valid export of the H2 db. You can alter port by changing the `server.port` value in `application.properties` file of the `server` subproject.
2. Start the server with
```sh
./gradlew bootRun
```

3. Start the client
```sh
./gradlew run
```
4. Specify the host and port of the server you want to connect to, in the UI.

## For any developers who contributes to it
To the developers, as of now the server and client Main classes can only be run as 
specified in the "How to run it" section. The build will fail if you run the classes 
from IDE like Intellij as the Intellij configs to run the classes is very different 
from Gradle configs

## License
MIT License

Copyright (c) 2022 TU Delft

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
