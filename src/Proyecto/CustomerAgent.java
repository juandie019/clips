/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package examples.protocols;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.domain.FIPANames;

import java.util.*;

// import java.util.Date;
// import java.util.Vector;
// import java.util.Enumeration;
// import java.util.Arrays;

/**
   This example shows how to implement the initiator role in 
   a FIPA-contract-net interaction protocol. In this case in particular 
   we use a <code>ContractNetInitiator</code>  
   to assign a dummy task to the agent that provides the best offer
   among a set of agents (whose local
   names must be specified as arguments).
   @author Giovanni Caire - TILAB
 */
public class CustomerAgent extends Agent {
	private int nProductos;
	private int nResponders = 1;
	// private String [] sellers = {"amazon1"};
	private String [] sellers = {"amazon1", "amazon2", "alibaba"};

	protected void setup() { 
	Object[] args = getArguments();
	Object payment = ""; 
	Object[] products = getArguments() != null ? new Object[getArguments().length - 1] : null;
	String order = "";

	if(args != null && args.length > 1){
		payment = args[args.length - 1]; //we get the payment method

		for(int i = 0; i < products.length; i++){
			products[i] = args[i];
		}

		order = payment.toString() + ":" + Arrays.toString(products);
		System.out.println(order);
	}
		
  	if (products != null && products.length > 0) {
  		nProductos = products.length;
  		System.out.println("Trying buy "+ nProductos +" products.");
  		
  		// Fill the CFP message
  		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		  
  		for (String seller :sellers) {
	  		msg.addReceiver(new AID(seller, AID.ISLOCALNAME));
  		}
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		// We want to receive a reply in 10 secs
		msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
		msg.setContent(order);
			
			addBehaviour(new ContractNetInitiator(this, msg) {
				
				protected void handlePropose(ACLMessage propose, Vector v) {
					String msg = propose.getContent();
					String[] productsList = msg.split(",");

					System.out.println("Aaaagent "+propose.getSender().getName()+" proposed "+propose.getContent());
					
					
					List<Offer> offers = new LinkedList<Offer>();
					String[] productAux = null;
					
					for(String pl: productsList){
						productAux = pl.replace("(", "").replace(")", "").split(":");
						offers.add(new Offer(productAux[0], Float.parseFloat(productAux[1]), propose));
					}

					for(Offer o: offers){
						System.out.println("offer " + o.toString());
					}

				}
				
				protected void handleRefuse(ACLMessage refuse) {
					System.out.println("Agent "+refuse.getSender().getName()+" refused");
				}
				
				protected void handleFailure(ACLMessage failure) {
					if (failure.getSender().equals(myAgent.getAMS())) {
						// FAILURE notification from the JADE runtime: the receiver
						// does not exist
						System.out.println("Responder does not exist");
					}
					else {
						System.out.println("Agent "+failure.getSender().getName()+" failed");
					}
					// Immediate failure --> we will not receive a response from this agent
					nResponders--;
				}
				
				protected void handleAllResponses(Vector responses, Vector acceptances) {
					List<Offer>offers = new LinkedList<Offer>();
					List<Offer>bestOffers = new LinkedList<Offer>();

					if (responses.size() < nResponders) {
						// Some responder didn't reply within the specified timeout
						System.out.println("Timeout expired: missing "+(nResponders - responses.size())+" responses");
					}

					Enumeration e = responses.elements();

					while(e.hasMoreElements()) {
						ACLMessage msg = (ACLMessage) e.nextElement();
						if (msg.getPerformative() == ACLMessage.PROPOSE)
							for (Offer o :getReponderOffers(msg)){
								offers.add(o);
							}
					}

					for(Object productName: products){
						Offer bestOffer = getBestbestOffer(offers, productName);
						if(bestOffer != null)
							bestOffers.add(bestOffer);
					}
					

					for(String seller :sellers){
						List<Offer> acceptedOffersSeller = getSellerOffers(bestOffers, seller);
						if(acceptedOffersSeller.size() > 0){
							String content = "";
							ACLMessage msg = acceptedOffersSeller.get(0).getMsg();

							for(Offer offer :acceptedOffersSeller){
								content += offer.getName() + ',';
							}

							ACLMessage reply = msg.createReply();
							acceptances.addElement(reply);
							System.out.println("Comprando " + content);
							reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							reply.setContent(content);
						}
					}
				}
				
				protected void handleInform(ACLMessage inform) {
					System.out.println("Agent "+inform.getSender().getName()+ "dice: " + inform.getContent());
				}
			} );
  	}
  	else {
  		System.out.println("No products specified.");
  	}
  	} 

	protected List<Offer> getReponderOffers(ACLMessage msg){
		List<Offer> offers = new LinkedList<Offer>();
		String message = msg.getContent();
		String[] productsList = message.split(",");
		String[] product = null;
		
		for(String pl: productsList){
			product = pl.replace("(", "").replace(")", "").split(":");
			offers.add(new Offer(product[0], Float.parseFloat(product[1]), msg));
		}

		return offers;
	}

	protected List<Offer> getSellerOffers(List<Offer> offers, String sellerName){
		List<Offer> sellerOffers = new LinkedList<Offer>();
		
		for(Offer sellerOf: offers){
			if(sellerOf.getSellerName().equals(sellerName))
			sellerOffers.add(sellerOf);
		}

		return sellerOffers;
	}

	protected Offer getBestbestOffer(List<Offer>offers, Object producName){
		Offer bestOffer = null;

		for(Offer o: offers){
			if(o.getName().equals(producName)){
				if(bestOffer == null)
					bestOffer = o;
				else{
					if(o.getPrice() < bestOffer.getPrice())  
						bestOffer = o;
				}
			}
		}

		return bestOffer;
	}

	public class Offer {
		String name;
		float price;
		ACLMessage msg;

		public Offer(String name, float price, ACLMessage msg){
			this.name = name;
			this.price = price;
			this.msg = msg;
		}

		public String getName(){
			return name;
		}

		public float getPrice(){
			return price;
		}

		public ACLMessage getMsg(){
			return msg;
		}

		public String getSellerName(){
			return msg.getSender().getLocalName();
		}

		public String toString(){
			return "Comprando " + this.name + " a " + msg.getSender().getLocalName() + "por " + String.valueOf(price);
		}
		
	}
}



