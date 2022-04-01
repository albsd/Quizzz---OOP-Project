Notes week 8

next week: fix most remaining issues before friday (MVP)
deleting game too early: should be deleted when final player has left

singleplayer leaderboard:
    - still shows "you" player (should be removed)
    - highlights all players with same name (should highlight only the same MAC address)
    - change GameResult class to store MAC address

we don't need to unzip the activities programmatically
run gametimer on server side, send message to client at next question 
normalise question structure
escape strings in questions (otherwise weird characters get shown)
filter activities that take too much energy
open questions should disable text input after 'Enter' press

using halve timer makes other players wait uncertain amount
    - implement negative time amount when waiting to show animation
    - display "Waiting for other players" when waiting

don't use Gaussian function? answer is the mean most of the time.
