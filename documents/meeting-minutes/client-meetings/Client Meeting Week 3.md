# Meeting Minutes - Meeting with Client Week 2
## Meeting Information
**Date/Time:** 11/05/2023, 14:00-14:30 <br>
**Purpose:** Weekly meeting with the client <br>
**Location:** Building 28, TU Delft <br>
**Note Takers:** Irene Aldabaldetrecu Alberdi, Kayleigh Jones <br>

## Attendees
- Mohammed Al Owayyed *(Client, PhD candidate, project leader)*
- Fanni Fiedrich *(Bachelor team)*
- Francisco Ruas Vaz *(Bachelor team)*
- Irene Aldabaldetrecu Alberdi *(Bachelor team)*
- Kayleigh Jones *(Bachelor team)*
- Sten van Wolfswinkel *(Bachelor team)*
- Ayrton Braam *(Master student)*
- Dongxu Lu *(Master student)*

## Perception
- A perception is what the Java code gets from Rasa. It has the 'context' related to the intent.
- The perception has a bit more information than the intent, such as the active desire attribute and the subject. 
- “Dat” from the previous sentence is still kept. 

## Issues with triggers
- Ignoring the trigger works until it does not ask you to call school. We cannot locate “Can you call my school” in the code. If we move the trigger time up by a lot, Lilo will somehow ask you to call school(“actionsetreminder”).
- There is a certain 'randomness' to when Lilobot receives the trigger. Ayrton needs to make the if statement more 'narrow' to activate the trigger faster. He wants the trigger to be a response that does NOT change the belief values. 
- Randomness may be in line 73 in actions.py: 'time = random.randint(0, 20)'

## Optimal Path Algorithm
- We want a way to suggest the best things the user can say to make the child arrive at the optimal solution.
- If not possible, enough to know what to do next to go to the next desire.
- The goal of the algorithm is to know what the user should say in the next few steps. It does not necessarily have to be a 'realistic' simulation; the goal is for people to practice the 5-step-model. It is a training system; it is not meant to simulate real life.

## Next Meeting
**Date/Time:** 16/05/2023, 14:30-14:50 <br>
**Location:** Building 28, TU Delft
