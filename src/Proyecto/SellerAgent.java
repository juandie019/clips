package examples.protocols;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.core.behaviours.Behaviour;

import java.util.*;

import net.sf.clipsrules.jni.*;

/**
   This example shows how to implement the responder role in 
   a FIPA-contract-net interaction protocol. In this case in particular 
   we use a <code>ContractNetResponder</code>  
   to participate into a negotiation where an initiator needs to assign
   a task to an agent among a set of candidates.
   @author Giovanni Caire - TILAB
 */
public class SellerAgent extends Agent {
    Environment clips;

	protected void setup() {
		try {
			clips = new Environment();
		}catch (Exception e) {
			System.out.println(e);
		}

		addBehaviour(new TellBehaviour());
		addBehaviour(new AskBehaviour());

		System.out.println("Agent "+getLocalName()+" waiting for CFP...");

		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
				MessageTemplate.MatchPerformative(ACLMessage.CFP) );

		addBehaviour(new ContractNetResponder(this, template) {
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				String products = cfp.getContent();
				String[] productsList = products.replace("[", "").replace("]", "").split(",");
				
				for(int i = 0; i<productsList.length; i++){
					System.out.println(cfp.getSender().getLocalName() + " wants to get " + productsList[i]);
				}
				
				int proposal = evaluateAction();
				String message = "";
				// if (proposal > 2) {
				if (true) {
					try {
						String assertProduct = null;
						String assertOrder = "(order (seller-id " + getLocalName() + ") (order-id " + cfp.getSender().getLocalName() + "))";
						clips.assertString(assertOrder);
							
						for(int i = 0; i<productsList.length; i++){
							assertProduct = "(order-description (order-id " + cfp.getSender().getLocalName() + ") (product " + productsList[i] + "))";
							clips.assertString(assertProduct);
						}
						
						clips.run();

						List<FactAddressValue> offers = clips.findAllFacts("offer");
						List<FactAddressValue> agentOffers = new LinkedList<FactAddressValue>();

						for(FactAddressValue o: offers) {
							if(cfp.getSender().getLocalName().equals(o.getSlotValue("order-id").toString())){
								System.out.println("A "+getLocalName()+": Proposing "+ o.getSlotValue("price") + " for " + o.getSlotValue("product").toString());
								agentOffers.add(o);
							}
						}

						System.out.print("debugg" + agentOffers.toString());

						if(agentOffers.size() > 0){
							for(FactAddressValue o: agentOffers) {
								message += o.getSlotValue("product") + ":" + o.getSlotValue("price") + ",";
							}
							

						}else{
							throw new RefuseException("evaluation-failed");
						}


						
						// clips.eval("(facts)");
					} catch (Exception e) {
						System.out.println(e);
					}

					// We provide a proposal
					System.out.println("Agent "+getLocalName()+": Proposing "+proposal);
					ACLMessage propose = cfp.createReply();
					propose.setPerformative(ACLMessage.PROPOSE);
					propose.setContent(message);
					return propose;
				}
				else {
					// We refuse to provide a proposal
					System.out.println("Agent "+getLocalName()+": Refuse");
					throw new RefuseException("evaluation-failed");
				}
			}

			@Override
			protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
				System.out.println("Agent "+getLocalName()+": Proposal accepted");
				if (performAction()) {
					System.out.println("Agent "+getLocalName()+": Action successfully performed");
					ACLMessage inform = accept.createReply();
					inform.setPerformative(ACLMessage.INFORM);
					return inform;
				}
				else {
					System.out.println("Agent "+getLocalName()+": Action execution failed");
					throw new FailureException("unexpected-error");
				}	
			}

			protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
				System.out.println("Agent "+getLocalName()+": Proposal rejected");
			}
		} );
		
	}

	private int evaluateAction() {
		// Simulate an evaluation by generating a random number
		return (int) (Math.random() * 10);
	}

	private boolean performAction() {
		// Simulate action execution by generating a random number
		return (Math.random() > 0.2);
	}

	private class TellBehaviour extends Behaviour {

        boolean tellDone = false;
		
		
        public void action() {

            try {
                clips.eval("(clear)");
                
                clips.load("/Users/diego/Documents/CLIPSJNI/work/src/Proyecto/clps/load-templates.clp");
                clips.load("/Users/diego/Documents/CLIPSJNI/work/src/Proyecto/clps/load-facts.clp");
                clips.load("/Users/diego/Documents/CLIPSJNI/work/src/Proyecto/clps/load-rules.clp");

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
   
    }

	private class AskBehaviour extends Behaviour {

        boolean askDone = false;

        public void action() {
            try {
                // System.out.println(clips.eval("(facts)"));
                // clips.eval("(facts)");
                // clips.eval("(rules)");
                // clips.run();
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

        // public int onEnd(){
        //     // myAgent.doDelete();
        //     // return super.onEnd();
        // }
    }// END of inner class AskBehaviour
}

