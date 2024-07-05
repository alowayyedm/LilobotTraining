package com.bdi.agent.service;

import com.bdi.agent.model.Action;
import com.bdi.agent.model.Desire;
import com.bdi.agent.repository.ActionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionService {

    private final ActionRepository actionRepository;

    @Autowired
    public ActionService(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    /**
     * Get am action by its id.
     *
     * @param id The id of the action
     * @return The action
     */
    public Action getActionById(Long id) {
        Optional<Action> optionalAction = actionRepository.findById(id);
        if (optionalAction.isEmpty()) {
            return null;
        }
        return optionalAction.get();
    }

    public void addAction(Action action) {
        actionRepository.save(action);
    }

    List<Action> getActionsByDesireId(Long desireId) {
        return actionRepository.findByDesireId(desireId);
    }

    /**
     *Finds and returns an action by a given id.
     */
    public Action getUncompletedAction(Desire desire) {
        Set<Action> actions = desire.getActions();
        if (actions.size() == 0) {
            return null;
        }
        if (!hasUncompletedAction(desire)) {
            setActionsUncompleted(List.of(desire));
        }
        for (Action action : actions) {
            if (!action.getCompleted()) {
                return action;
            }
        }

        throw new IllegalArgumentException("this should not be possible");
    }

    /**
     * Checks if the desire has an uncompleted.
     *
     * @param desire The desire to check
     * @return If the action is completed
     */
    public boolean hasUncompletedAction(Desire desire) {
        Set<Action> actions = desire.getActions();
        for (Action action : actions) {
            if (!action.getCompleted()) {
                return true;
            }
        }
        return false;
    }

    /**
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
     * finds all actions corresponding to a list desires
     * method can be used to find all actions for a scenario.
     *
     * @param desires list of desires that you will search actions for
     * @return list off all actions for all desires
     */
    public List<Action> getActionsByDesires(List<Desire> desires) {
        List<Action> allActions = new ArrayList<>();
        desires.forEach(desire ->  {
            allActions.addAll(actionRepository.findByDesireId(desire.getId()));
        });
        return allActions;
    }

    /**
    * Each desire has an associated set of actions. This method creates actions for each desire.
    *
    * */
    public void addActionsToDesire(Desire desire) {
        if (desire.getName().equals("D1")) {
            addAction(new Action(desire, "inform", "A1", "bullying", "what", false));
            addAction(new Action(desire, "inform", "A2", "bullying", "details", false));
            addAction(new Action(desire, "inform", "A3", "bullying", "feeling", false));
        }

        if (desire.getName().equals("D2")) {
            addAction(new Action(desire, "inform", "A4", "chitchat", "goodbye", false));
        }

        if (desire.getName().equals("D3")) {
            addAction(new Action(desire, "request", "A5", "goal", "howkt", false));
        }

        if (desire.getName().equals("D4")) {
            addAction(new Action(desire, "request", "A6", "help", "how", false));
            //addAction(new Action(desire, "request", "A7","help", "severity", false));
            addAction(new Action(desire, "request", "A7", "help", "say", false));
        }

    }

}
