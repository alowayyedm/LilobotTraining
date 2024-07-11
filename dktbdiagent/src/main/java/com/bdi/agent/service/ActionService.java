package com.bdi.agent.service;

import com.bdi.agent.model.Action;
import com.bdi.agent.model.Desire;
import com.bdi.agent.repository.ActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ActionService {

    private final ActionRepository actionRepository;

    @Autowired
    public ActionService(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    public Action getActionById(Long id) {
        return actionRepository.getById(id);
    }

    public void addAction(Action action) {
        actionRepository.save(action);
    }

    List<Action> getActionsByDesireId(Long desireId) {
        return actionRepository.findByDesireId(desireId);
    }

    public Action getUncompletedAction(Long desireId) {
        List<Action> actions = getActionsByDesireId(desireId);
        for (Action action : actions) {
<<<<<<< HEAD
            if (!action.getIsCompleted()) {
=======
            if (!action.getCompleted()) {
>>>>>>> origin/updatedLilo
                return action;
            }
        }
        return null;
    }

    /**
<<<<<<< HEAD
=======
     * Sets all actions of the given desires to be uncompleted.
     *
     * @param desires The desires for which to set the activity of the actions.
     */
    public void setActionsUncompleted(List<Desire> desires) {
        for (Desire desire : desires) {
            List<Action> actions = getActionsByDesireId(desire.getId());

            for (Action action : actions) {
                action.setCompleted(false);
                actionRepository.save(action);
            }
        }
    }

    /**
>>>>>>> origin/updatedLilo
    * Each desire has an associated set of actions. This method creates actions for each desire.
    *
    * */
    public void addActionsToDesire(Desire desire) {
        if (desire.getName().equals("D1")) {
            addAction(new Action(desire, "inform", "A1","bullying", "what", false));
            addAction(new Action(desire, "inform", "A2","bullying", "details", false));
            addAction(new Action(desire, "inform", "A3","bullying", "feeling", false));
        }

        if (desire.getName().equals("D2")) {
            addAction(new Action(desire, "inform", "A4","chitchat", "goodbye", false));
        }

        if (desire.getName().equals("D3")) {
            addAction(new Action(desire, "request", "A5","goal", "howkt", false));
        }

        if (desire.getName().equals("D4")) {
            addAction(new Action(desire, "request", "A6","help", "how", false));
//            addAction(new Action(desire, "request", "A7","help", "severity", false));
            addAction(new Action(desire, "request", "A7","help", "say", false));
        }

    }

}
