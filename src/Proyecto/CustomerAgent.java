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
	// private String [] sellers = {"amazon1"};
	// private String [] sellers = {"amazon1", "alibaba"};
	private String [] sellers = {"amazon1", "amazon2", "alibaba"};
	private int nResponders = sellers.length;

	protected void setup() { 
		Object[] args = getArguments();
		Object[] products = getArguments() != null ? new Object[getArguments().length - 1] : null;
		Object payment = ""; 
		String order = "";

		if(args != null && args.length > 1){
			payment = args[args.length - 1]; //we get the payment method

			for(int i = 0; i < products.length; i++){
				products[i] = args[i];
			}

			order = payment.toString() + ":" + Arrays.toString(products); //we get ready the order
		}
			
		if (products != null && products.length > 0) {
			System.out.println(getLocalName() + " trying get "+ products.length +" products.");
			
			// Fill the CFP message
			ACLMessage msg = new ACLMessage(ACLMessage.CFP);
			
			for (String seller :sellers) { //adding the recivers
				msg.addReceiver(new AID(seller, AID.ISLOCALNAME));
			}

			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			// We want to receive a reply in 15 secs
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 15000));
			msg.setContent(order);
				
			addBehaviour(new ContractNetInitiator(this, msg) {
				
				protected void handlePropose(ACLMessage propose, Vector v) {
					System.out.println("Seller " + propose.getSender().getLocalName() + " has some of the productos ");
				}
				
				protected void handleRefuse(ACLMessage refuse) {
					System.out.println("Seller " + refuse.getSender().getLocalName() + " does not have the products");
				}
				
				protected void handleFailure(ACLMessage failure) {
					if (failure.getSender().equals(myAgent.getAMS())) {
						// FAILURE notification from the JADE runtime: the receiver
						// does not exist
						System.out.println("Seller does not exist");
					}
					else {
						System.out.println("Seller "+failure.getSender().getLocalName()+" says product run out of stock");
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
							for (Offer o :getReponderOffers(msg)){ //we get the offer for each product sold by a seller
								offers.add(o);
							}
					}

					for(Object productName: products){ // we get the list fo the best offer for each product
						Offer bestOffer = getBestbestOffer(offers, productName);
						if(bestOffer != null)
							bestOffers.add(bestOffer);
					}
					
					for(String seller :sellers){ //get the seller offer to send them an accept proposal
						List<Offer> acceptedOffersSeller = getSellerOffers(bestOffers, seller);
						if(acceptedOffersSeller.size() > 0){//sending accept proposal
							String content = "";
							ACLMessage msg = acceptedOffersSeller.get(0).getMsg();

							System.out.println("Accepting propose to " + msg.getSender().getLocalName() + " of the next products: ");

							for(Offer offer :acceptedOffersSeller){ //building the msg
								content += offer.getName() + ',';
								System.out.println(offer.toString());
							}

							ACLMessage reply = msg.createReply();
							acceptances.addElement(reply);
							reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							reply.setContent(content);
						}else{ //sending a reject proposal
							List<Offer> offersRejected = getSellerOffers(offers, seller);
							
							if(offersRejected.size() > 0){ //it makes a proposal so we have to reject
								ACLMessage reply = offersRejected.get(0).getMsg().createReply();
								reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
								acceptances.addElement(reply);
							}
						}
					}
				}
				
				protected void handleInform(ACLMessage inform) {
					System.out.println("Agent "+inform.getSender().getLocalName()+ " dice: " + inform.getContent());
				}
			} );
		}
		else {
			System.out.println("No products o payment method specified.");
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
			return this.name + " en " + String.valueOf(price) + " dollars";
		}
		
	}
}



