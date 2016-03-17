package ui.action;

public class UserAction {

    private String id;
    private int associated_value;

    public UserAction(String id, int associated_value){
        this.id = id;
        this.associated_value = associated_value;
    }

    public String getId(){
        return id;
    }

    public int getAssociatedValue(){
        return associated_value;
    }

    public boolean isIdEqualsTo(String other_id){
        return id.equals(other_id);
    }
}
