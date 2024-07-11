# Meeting Minutes - Meeting with the TA Week 9
## Meeting Information
**Date/Time:** 20/06/2023, 13:00-13:30 <br>
**Purpose:** Weekly meeting with the TA <br>
**Location:** Building 28, TU Delft <br>
**Note Takers:** Irene Aldabaldetrecu Alberdi, Kayleigh Jones <br>

## Attendees
- Irene Aldabaldetrecu Alberdi
- Fanni Fiedrich 
- Kayleigh Jones
- Francisco Ruas Vaz
- Sten van Wolfswinkel
- Ruben Backx *(Teaching Assistant)*

## Agenda Points
- Demonstration of progress:
  - Show working user settings page
  - Show trainer page with own session
  - Show phase transitions
- Update on user testing:
  - Discuss how the user testing went
  - Discuss issue with the client being opposed to doing a usability test with collected data
- Receive and discuss draft report feedback

## Draft Report Feedback
- Front cover: title should be capitalized and author names should be in alphabetical order.
- References should not be used consecutively, i.e. the same reference should not be repeated more than once in a span of a few paragraphs.
- Double check styling (commas, some capital letters are missing).
- Use backticks for opening quotes and normal quotes for closing quotes in LATEX.
- Non-functional requirements should not do MoSCoW. They are still more important than the functional requirements.
- We can leave the docker container requirement in the list of non-functional requirements because it is done.
- Have full sentences for the conclusion in Chapter 3. 
- List sources when listing advantages and disadvantages for the back-end architecture section. 
- End section talking about other technologies that we could have potentially used instead of Spring or Rasa and why we did not use them
  (it was a hard requirement from the client; we thought these other sources/frameworks were better but using them would have meant sacrificing time...).
- Also explain why we decided to use Vue.js instead of other frameworks. The advantages/reasoning that we mention in the report right now can also be applied
  to React. Explain why we did not decide to use React instead.
- Our explanations for authentication and implementation sections is too detailed. We can talk about architecture and design patterns,
  but specific components such as the Authorization Filter should not be mentioned. 
  - Leave out individual Spring Security details (Authentication Architecture); the explanation now is too high-level.
  - The implementation section for the path algorithm is good, but we should now write about endpoints.
  - Chat History implementation section is too detailed when it comes to individual entries.
- Difference between implementation and design choices: design choices section should describe our initial ideas or draft before actually implementing them,
  implementation section should describe the changes made throughout the process.

## User Testing
What Mo said about the restrictions concerning user testing is not true. We can still include our data for the user testing and call it a 'user test' in our report.
We are able to carry out proper User Testing; the necessary forms can be found online. In the future, we should double check information.
It would have been nice to send a consent form to the testers.

## Next Meeting (Final Presentation)
**Date/Time:** Wednesday 28/06/2023, 16:00 <br>
**Location:** EEMCS, TU Delft