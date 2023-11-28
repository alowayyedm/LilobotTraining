# Meeting Minutes - Meeting with the Client Week 4
## Meeting Information
**Date/Time:** 16/05/2023, 14:30-14:50 <br>
**Purpose:** Weekly meeting with Mo<br>
**Location:** Building 28, TU Delft <br>
**Note Takers:** Irene Aldabaldetrecu Alberdi, Kayleigh Jones <br>

## Attendees
- Mohammed Al Owayyed *(Client, PhD. Candidate)*
- Irene Aldabaldetrecu Alberdi
- Fanni Fiedrich 
- Kayleigh Jones
- Francisco Ruas Vaz
- Sten van Wolfswinkel
- Ayrton Braam *(Master Student involved with Mohammed Al Owayyed's research)*

## Agenda Items

### Creating our own Chat Widget instead of using Rasa's
We asked if it's alright to create our own version of the Rasa webchat widget since using the existing one is very bad design in our code. Mo thinks it's alright, and suggests we add it to the project requirements.

### Optimal Path Algorithm - Code vs Thesis Inconsistencies
There are inconsistencies in the code vs thesis on e.g. which beliefs are/aren't allowed. We asked the client which one to follow. We should follow what is in the **code** rather than the thesis.

### Optimal Path Algorithm - Generalization and Extendibility 
The current path algorithm is very specific to the way the belief changing is implemented. Changing this belief changing code would ruin the algorithm. We asked the client if this is a problem. The client says it's ok **not to generalize** it. If more problems about the algorithm occur due to the way the BDI model was implemented, we should discuss this with Mo,and could end up doing some hardcoding if necessary in the end.

### Allowing all Belief Value Changes in the TrainingPortal
Since not all forced belief value combinations would result in a reachable desired end of the conversation, we asked if we should disable some belief value changing on the frontend. Mo says we shouldn't limit the trainer's control here: all belief values should be changeable in the TrainerPortal. However, we could highlight relevant beliefs of the current phase if we have time left. 

## Next Meeting
**Date/Time:** Monday 22/05/2023, 13:00-13:30 <br>
**Location:** Building 28, TU Delft