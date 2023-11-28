# Meeting Minutes - Meeting with Client Week 2
## Meeting Information
**Date/Time:** 04/05/2023, 14:00-14:30 <br>
**Purpose:** Weekly meeting with the client <br>
**Location:** Building 28, TU Delft <br>
**Note Takers:** Irene Aldabaldetrecu Alberdi, Kayleigh Jones <br>

## Attendees
- Mohammed Al Owayyed *(Client, PhD candidate, project leader)*
- Irene Aldabaldetrecu Alberdi *(Bachelor team)*
- Fanni Fiedrich *(Bachelor team)*
- Kayleigh Jones *(Bachelor team)*
- Francisco Ruas Vaz *(Bachelor team)*
- Sten van Wolfswinkel *(Bachelor team)*
- Ayrton Braam *(Master student)*
- Dongxu Lu *(Master student)*

## About the KT meeting
Frontend is more focused on Kindertelefoon, not research. Have something working for the research. Could be used to make someone understand the model more. Having it for the teacher in the current stage makes sense. Mo wants an interface for himself to play around with

## New Business

### *Reason for having two login pages*<br>
Teacher and learner - fine to only have 1 login page.

### *How multiple chats at the same time should work*<br>
The idea was to have multiple side by side, but it is not important. Scrap the requirement.

### *Webpage unavailability without ssh to student-linux.tudelft.nl*<br>
We get 4 ports: One for postgres, 2 for rasa. If it doesn't work in the end, we can change to a different (less secure, not hosted at TU Dekft) server.

### *Bugs in the existing chatbot (asking Ayrton Braam)*<br>
About B2 not being checked for D2.

No. | Desire description |
---- | ---- |
D1 | Lilobot wil het over zijn probleem hebben |
D2 | Lilobot wil het gesprek beëindigen |
D3 | Lilobot wil dat de Kindertelefoon de pestkoppen van school haalt |
D4 | Lilobot wil met zijn leraar praten over situatie |
D5 | Lilobot wil samen met de Kindertelefoon een oplossing zoeken |

Goal of the algorithm:
```
   D2 (bad)
  /        \
D1 -> D5 -> D3 -> D4 -> D2 (with b2 == 1)
```

The shortest path should be defined on user intents. D2 should only happen at the end. Certain intents don’t parse correctly. Also, Lilo's beliefs are quite low at the beginning so you need to keep asking questions. 

Conversation should end when B2 is reached. Identify what the last state of the optimal path is and then add a check (e.g. B2 reaches 1). The child can end the conversation on its own? - D2 can end the conversation.  

For optimal path, maybe get started by having a smaller version of the BDI model and run a small simulation that we can then extend based on the model.

### ~~*Conflicts between separate projects working on Lilobot*<br>~~

## Next Meeting
**Date/Time:** 11/05/2023, 14:00-14:30 <br>
**Location:** Building 28, TU Delft
