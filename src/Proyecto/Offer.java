package examples.protocols;

import java.util.*;
import jade.proto.ContractNetResponder;

public class Offer {
    String name;
    float price;
    ContractNetResponder responder;

    public Offer(String name, float price, ContractNetResponder responder){
        this.name = name;
        this.price = price;
        this.responder = responder;
    }

    public String getName(){
        return name;
    }

    public float getPrice(){
        return price;
    }

    public ContractNetResponder getResponder(){
        return responder;
    }

    public String toString(){
        return this.name;
    }
    
}
