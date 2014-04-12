/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vimukthi.assignments.ocsf.simplechat.voting;

/**
 *
 * @author firzanm
 */
public class VotingAnswerObject {

    public String answer = "";
    public int answerID = -1;
    public int count = 0;

    public VotingAnswerObject(){
        
    }

    public void incrementCount(){
        ++count;
    }


}
