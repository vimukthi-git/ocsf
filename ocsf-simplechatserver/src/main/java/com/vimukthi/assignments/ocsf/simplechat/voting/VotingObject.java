/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vimukthi.assignments.ocsf.simplechat.voting;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 *
 * @author firzanm
 */
public class VotingObject {


    public String question;
    //List<String> answersList = new ArrayList<String>();
    //List<Integer> answersCounter = new ArrayList<Integer>();

    Hashtable<Integer, VotingAnswerObject> tableAnswersCounter = new Hashtable<Integer, VotingAnswerObject>();
    
    public VotingObject(){

        question = "";
    }

    public void AddQuestionsAndAnswers(List<String> answers){

        int count = answers.size();

        for(int i = 0; i < count; ++i ){

            String value = answers.get(i);

            if(i == 0){
                question = value;
            }
            else{
                VotingAnswerObject answerObj = new VotingAnswerObject();
                answerObj.answer = value;
                answerObj.answerID = i-1;

                tableAnswersCounter.put(i-1, answerObj);
            }
        }
    }


    public boolean AddVoting(String vote){

        boolean bAdded = false;
        Integer ivote = 0;
        try{
            ivote = Integer.parseInt(vote);
        }
        catch(Exception ex){
            ivote = 0;
        }

        VotingAnswerObject answerObj = tableAnswersCounter.get(ivote);

        if(answerObj != null){

             answerObj.incrementCount();
             tableAnswersCounter.put(ivote, answerObj);
             bAdded = true;
        }

        return bAdded;
    }

   
    public Hashtable<Integer, VotingAnswerObject> getAnswersObject(){
        return tableAnswersCounter;
    }
}
