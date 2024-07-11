# Sprint Retrospective, Iteration 2
Group 16a

| **Issue**                                                  | **Assigned To**    | **Estimated Effort** | **Actual Effort** | **Done**                    | **Notes**                                                                                                                                                                                                                                                                                                                                                                                                                                    |
|------------------------------------------------------------|--------------------|----------------------|-------------------|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Optimal path algorithm backend                             | Fanni              | 20h                  | 14h30m (for now)  | No                          | We expected this issue to take longer than one week, since it involved creating a way to handle the algorithm. There were multiple problems along the way, and it is not finished yet. Right now, there is a prototype for generating the path from phase 1 to phase 3.                                                                                                                                                                      |
| Adding a button (frontend) to regenerate the optimal path  | Irene              | 30m                  | 1h                | Yes                         | Still needs to be reviewed, so the issue might not be completed if some fixes need to be done.                                                                                                                                                                                                                                                                                                                                               |
| Setting up backend for authentication                      | Irene              | 10h                  | 12h (for now)     | No                          | Spent at least 2 days doing research. Some of the functionality that I was familiar with has recently been deprecated so the learning curve was a bit bigger than expected. Moreover, I had to fix multiple errors when trying to run the application very near the deadline of the issue. Most of what is left to do is related to testing.                                                                                                 |
| Change in belief values interface                          | Francisco          | 10h                  | 8h (for now)      | Needs connecting to backend | Frontend aspect is complete but still need to now link it to the endpoint                                                                                                                                                                                                                                                                                                                                                                    |
| Incorporating webchat interface on top of the rasa webchat | Francisco          | 6h                   | 6h                | Finish MR changes           | This issue finishes the frontend setup for a webchat interface that will then be slightly refractored to work with the backend                                                                                                                                                                                                                                                                                                               |
| Calling the belief changing endpoint from the interface    | Kayleigh           | 15h                  | 20h (for now)     | No                          | Lots of time spent trying to get websockets to work. There are still some problems with fetching belief values at the start, that can’t be fixed before pulling Sten and Francisco’s changes. There are also some problems with float comparison that cause difficulty to manually test whether the updating of belief values from the trainer portal works. In the branch for this issue, I also did some fixes to the css inconsistencies. |
| Push and format meeting notes                              | Kayleigh & Irene   | -                    | 5h (Kayleigh)     | No                          | We had to format the notes from previous weeks into proper .md files as well.                                                                                                                                                                                                                                                                                                                                                                |
| Connecting chat on trainer page to another chat            | Sten               | 12h                  | 12h (so far)      | No                          | Was very close to being done, then we decided to create our own chat interface as using the rasa webchat was giving us lots of problems. Now the implementation for creating a rasa session and being able to send and receive messages is there, it just needs to be trivially implemented into the new chat interface                                                                                                                      |
| Migrating Rasa versions                                    | Sten and Francisco | 8h                   | 5h                | Yes                         | The actual migration was quite straight forward, only the model needed to be re-generated. Rasa X was not used so no migration had to be done regarding that. The action server also already works on the new version.                                                                                                                                                                                                                       |


## Main Problems Encountered

### Problem 1

**Description:** This is not necessarily a problem, but something we expected to happen with this sprint. There were a lot of big issues assigned this week, and almost none of them ended up being fully implemented at the end of the sprint. This however is nothing we can influence, apart from maybe splitting big issues up into smaller ones. For some tasks this is not possible though, since they are so closely tied together that they cannot be split up without having to refactor things at a later point.

**Reaction:** Considering splitting up big issues, if possible at all, into smaller one’s.

### Problem 2
**Description:** Connecting trainers to learners’ chat sessions, as well as fetching belief values, was a problem due to the rasa webchat not being designed for our kind of situation.

**Reaction:**
We proposed to our client to replace the rasa webchat interface we use with our own version. He agreed, and Francisco has started working on the new one.

### Problem 3
**Description:** The different merges of sprint 1 concerning the frontend of our application caused some unwanted side effects in the UI layout, since different people changed global css files.

**Reaction:**
Kayleigh fixed the inconsistencies in branch 22.


## Adjustments for the next Sprint Plan

If possible, we will split up big issues into smaller one’s, though we believe this to not be possible with issues of this sprint.
