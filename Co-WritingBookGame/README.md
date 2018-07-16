## Description
Co-Writing Book is a game in which each player writes one sentence of a ‘book’ in a turn-by-turn manner, without being able to see more than the previous sentence.

e.g: Player A starts, writing “If I had a world of my own”.

Player B sees “If I had a world of my own”, and writes “everything would be nonsense”.

Player C sees “everything would be nonsense”, and writes “Nothing would be what it is”

Player D sees “Nothing would be what it is”, and writes “because everything would be what it isn’t”

Player E sees “because everything would be what it isn’t”, and writes “Alice.”

When the book is ‘completed’, all of the contributing players can access and read it. Everyone gets 10 points for contributing to the book.

The aim of this exercise is to write an application server (in any language and framework running on the JVM) that lets you get a book, add a sentence to it, add 10 points to all participants when a book is completed, and return a leaderboard of the five ‘greatest’ writers.

## Compile

try to use java 8 or more version to compile please

4+ command to execute for compilation as given below in any OS are

> dir /b /s *.java >> files.txt
> javac @files.txt
> java com.application.Server
> java com.application.Client suraj
> java com.application.Client suraj2
> java com.application.Client surajz

```
## Run
java Server anyport

followed by 

default port is 9999

java Client suraj  usedaboveport
 
java Client suraj2  usedaboveport

Type HELP to display the server menu:

GET to retrieve the last sentence of a book.

WRITE to to write a sentence to a book.

STATUS to show the leaderboard of users.

EXIT to quit the game.

Once a book is complete , 10 points are added to all players.

## Others
The Program is based on Interface -implementation model

ie., model/application layer 

