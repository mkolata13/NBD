package model;


public class ClientTypeGold extends ClientType {

    public ClientTypeGold() {
        super(0.5);
    }

    @Override
    public String getTypeName() {
        return "gold";
    }
}
