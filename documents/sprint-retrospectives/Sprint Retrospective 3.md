# Sprint Retrospective, Iteration 3
Group 16a

| **Issue**                                              | **Assigned To** | **Estimated Effort** | **Actual Effort** | **Done**                                                      | **Notes**                                                                                                                                                                                                                                                         |
|--------------------------------------------------------|-----------------|----------------------|-------------------|---------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Optimal path algorithm backend                         | Fanni           | 15h (this sprint)    | 12h (this sprint) | No                                                            | There are many things to consider in the algorithm, and it is difficult to analyze the impact of changing certain values on the overall path. This is taking quite a bit of time. Some restrictions on the graph generation had to be made (see problem section). |
| Chat typing icon                                       | Sten            | 2h                   | 4h                | yes                                                           | Not merged. Uses apache 2.0 licensed CSS                                                                                                                                                                                                                          |
| Backend and connecting of different trainer chat modes | Sten            | 5h                   | 7h                | No, only testing left                                         | I still need to write some frontend and backend tests and then it can be merged                                                                                                                                                                                   |
| Connecting belief value input to backend               | Kayleigh        | -                    | 10h               | Yes                                                           | No estimation because it should have been done last sprint, but it took longer because of switching to websockets and being dependent on other branches as a result.                                                                                              |
| Chat history page                                      | Kayleigh        | 9h                   | 9h                | No                                                            | Frontend exists and has clickable buttons, but is in no way connected to backend or functional since we need authentication to do so.                                                                                                                             |
| Interface for chat mode of trainer                     | Francisco       | 11h                  | 10h               | MR active but still need to address some merge review changes | Small modifications need to be done from merge review, such as add more comments and fix small bugs                                                                                                                                                               |
| Interface for chat mode of trainer                     | Sten            | 1h                   | 1h                | See above                                                     | Applied some fixes so that it could be used in my own issue                                                                                                                                                                                                       |
| Hosting system on server                               | Francisco       | -                    | 5h (this sprint)  | Fully accessible website of an old version of the dev branch  | Will need to be updated to a newer version of dev at some point and still need to allow rasa and rasa action server to run at the same time                                                                                                                       |

## Main Problems Encountered

### Problem 1
**Description:** Creating the path algorithm requires an analysis of impact of intents on the overall path taken, and belief values. A general algorithm was finished this week, however some restrictions on the graph generation had to be made. The main one is that not the overall optimal path is considered, since the size of the decision tree would be too large otherwise. Right now, the optimal path is found per phase. A more detailed description will be given in the documentation of the algorithm.

**Reaction:** Some restrictions on the path algorithm were made (described above).

### Problem 2
**Description:** Due to exams multiple group members had less time to work on the sprint, however this was announced beforehand in all cases.

**Reaction:** Task division of following sprint(s) adjusted.

## Adjustments for the next Sprint Plan
- More testing on frontend (and backend), since this was not done before.
- Start working on the report during sprints.