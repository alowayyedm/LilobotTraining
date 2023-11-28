# Meeting Minutes - Meeting Kindertelefoon and TU Delft
## Meeting Information
**Date/Time:** 31/05/2023, 15:00-15:18 <br>
**Purpose:** Second meeting with KT and master students involved with Lilobot <br>
**Location:** Microsoft Teams <br>
**Note Takers:** Irene Aldabaldetrecu Alberdi, Kayleigh Jones <br>

## Attendees
- Irene Aldabaldetrecu Alberdi *(Bachelor team)*
- Fanni Fiedrich *(Bachelor team)*
- Kayleigh Jones *(Bachelor team)*
- Francisco Ruas Vaz *(Bachelor team)*
- Sten van Wolfswinkel *(Bachelor team)*
- Ayrton Braam *(Master student)*
- Dongxu Lu *(Master student)*
- *(KT Representative A)*
- *(KT Representative B)*

## Agenda points 
- Demo: show changes in the design of the website compared to the prototype
- Ask what kind of power hierarchy is needed for KT's trainer/learner system for better understanding of how we could implement user access rights
- Discuss the potential addition of a tutorial page on the website
- Discuss what feedback should be displayed on the chat history page

## Demo
The following features were highlighted in the demo: 
- Changed chat component to our own instead of Rasa. Still works as before
- Can join conversations using the sessionID of the learner
- Planning to replace sessionID with account system (input username to join)
- Graph shows example messages and values of nodes in (possible) optimal path
- Graph belief value list shows info on hover
- Added arrows to input boxes
- Message interception: accept, edit and reject conversations from Lilo

## Feedback
### *Balance between research and usage by Kindertelefoon*<br>
The people at Kindertelefoon had positive feedback on the interface of the application. However, it still feels too technical to the average target end-user of the application. We mentioned we are planning to mitigate this by adding an alternative way to do manual belief updates. In addition to the belief input list, we are working on preset buttons that will bring the conversation to the phases from the Five Phase Model, which the people at KT are experienced with.

### *Hierarchy of the Kindertelefoon trainers and learners*<br>
When proposed two solutions on how to implement access rights in the system:
1. One admin account exists, that can upgrade a learner to a trainer
2. Anyone can choose to be a trainer

The volunteers at Kindertelefoon stated that their organization works with a lot of trainers, so the second option would be more useful. 

This brought us to a follow-up question: When anyone can be a trainer, how should we ensure the learner's conversation with Lilobot is somewhat protected, i.e., people who are not recognized as real KT trainers do not invade the leaner's conversation? For this, we asked the people at Kindertelefoon which/how many trainers are normally assigned to a learner. The result is as follows: 
- A learner usually has one trainer but there are exceptions. 
- Additionally, there are other types of roles in the real-life hierarchy (e.g. Mentors) that would not differ in access rights in the application, but this should be taken into account when deciding on how many trainers a learner can have. 
- Finally, trainers can have many learners that they are the trainer of.

### *Chat history page*<br>
One of the Master's students suggested the feedback could be done through peer-reviewing: learners' conversations are stored in the system, so they could be assigned to review other people's chats. 

However, the implementation of this is likely too large given the time left in the project.

## Next Meeting
**Date/Time:** 14/06/2023, TBD <br>
