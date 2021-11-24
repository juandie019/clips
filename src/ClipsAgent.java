package test;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

import net.sf.clipsrules.jni.*;

public class ClipsAgent extends Agent {

    Environment clips;

    protected void setup(){
        try {
           clips = new Environment();
        } catch (Exception e) {
            System.out.println(e);
        }

        addBehaviour(new TellBehaviour());
        addBehaviour(new AskBehaviour());
    } 

    private class TellBehaviour extends Behaviour {

        boolean tellDone = false;

        public void action() {
            try {
                clips.eval("(clear)");
                
                clips.load("/Users/diego/Documents/CLIPSJNI/work/clps/ClipsAgent/load-templates.clp");
                clips.load("/Users/diego/Documents/CLIPSJNI/work/clps/ClipsAgent/load-facts.clp");
                clips.load("/Users/diego/Documents/CLIPSJNI/work/clps/ClipsAgent/load-rules.clp");

                clips.eval("(reset)");
                
            } catch (Exception e) {
                System.out.println(e);
            }

            tellDone =  true;
        } 
    
        public boolean done() {
            if (tellDone)
                return true;
            else
        return false;
        }
   
    }// END of inner class TellBehaviour


    private class AskBehaviour extends Behaviour {

        boolean askDone = false;

        public void action() {
            try {
                // clips.eval("(facts)");
                // clips.eval("(rules)");
                clips.run();
            } catch (Exception e) {
                System.out.println(e);
            }
            
            askDone = true;
        } 
    
        public boolean done() {
            if (askDone)
                return true;
            else
                return false;
        }

        public int onEnd(){
            myAgent.doDelete();
            return super.onEnd();
        }
    }// END of inner class AskBehaviour
}
