/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vimukthi.assignments.ocsf.simplechat.voting;

import java.util.Hashtable;

/**
 *
 * @author firzanm
 */
public class VotingManager {

    private Hashtable<String, VotingObject> votingTables = new Hashtable<String, VotingObject>();

    public VotingManager(){
        
    }

    public boolean IsEligibleForVote(String loginID){

         VotingObject voteObject = votingTables.get(loginID);

         if(voteObject != null)
             return false;
         
         return true;
    }

    public VotingObject getVotingObject(String loginID){

         VotingObject voteObject = votingTables.get(loginID);

         if(voteObject != null)
             return voteObject;

         return null;
    }

    public void addVotingObject(VotingObject voteObj, String loginID){

        votingTables.put(loginID, voteObj);
    }

}
