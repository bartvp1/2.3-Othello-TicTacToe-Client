package nl.hanze.game.client.server;

public interface Observer {

    default void update(String s){
        System.out.println("Observer: "+s);
        //TODO: implement reponse handling mechanism
    }
}
