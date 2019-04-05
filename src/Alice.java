import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

//implements ChangeableCondition, FieldSeller, SayPhraseSeller, DrinkableTea,

public class Alice extends Human implements Comparable<Alice>, Serializable {
    private Politeness politeness;
    private Condition condition = Condition.NORMAL;
    private CupOfTea cap;
    private String date;
    private int size;
    static final long serialVersionUID = 2;

    public String getDate() {
        return date;
    }

    public int getSize() {
        return size;
    }

    private static class CupOfTea implements Serializable{
        public CupOfTea(String s) {
            nameOfUser = s;
            //System.out.println(nameOfUser + ": Беру я чаёк, наливаю в стакан, щас мне будет легко");
        }
        public CupOfTea(String s,int par){
            nameOfUser=s;
            fullness = par;
        }

        private int getFullness(){
            return fullness;
        }
        private String nameOfUser;
        private int fullness = 100;
    }


    public Condition getCondition() {
        return this.condition;
    }

public int getfullness(){
        return cap.getFullness();
}
    @Override
    public void sayPhrase() {
        System.out.println(getName() + ": \"Ах ты, старая карга\"");
    }

    private boolean setPoliteness(Politeness politeness) {
        this.politeness = politeness;
        return true;
    }

    public Politeness getPoliteness() {
        return this.politeness;
    }

    public Alice(String name, Politeness politeness, int location,int fullness, String date, int size) {
        setName(name);
        setLocation(location);
        this.politeness = politeness;
        cap = new CupOfTea(name,fullness);
        this.date = date;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alice)) return false;
        if (!super.equals(o)) return false;
        Alice alice = (Alice) o;
        return getSize() == alice.getSize() &&
                getPoliteness() == alice.getPoliteness() &&
                getCondition() == alice.getCondition() &&
                Objects.equals(getfullness(), alice.getfullness()) &&
                Objects.equals(getDate(), alice.getDate()) &&
                Objects.equals(getName(), alice.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPoliteness(), getCondition(), getfullness(), getDate(), getSize(), getName());
    }

    @Override
    public String toString() {
        return "\nКласс: Alice\n" + "Имя: " + getName() + "\nВежливость: politeness = " + politeness + "\nDate: " + date+"\nSize: "+ size + "\nСостояние: condition = " + condition + "\nHashcode: " + Integer.toHexString(hashCode())+ "\nКоордината: "+getLocation()+"\ncap: \n\tNameOfUser: " +getName()+"\n\tFullness :"+ cap.getFullness()+'\n';
    }

    @Override
    public int compareTo(Alice alice) {
        return getLocation()-alice.getLocation();
    }

    public Alice() {
        setName("Алиса");
        setLocation(0);
        this.politeness = Politeness.POLITE;
        cap = new CupOfTea("Алиса");
    }

    public Alice(String name) {
        setName(name);
        setLocation(0);
        this.politeness = Politeness.POLITE;
        cap = new CupOfTea(name);
    }

    public Alice(String name, Politeness politeness) {
        setName(name);
        setLocation(0);
        this.politeness = politeness;
        cap = new CupOfTea(name);
    }

    public Alice(String name, Politeness politeness, int location) {
        setName(name);
        setLocation(location);
        this.politeness = politeness;
        cap = new CupOfTea(name);
    }

//    @Override
//    public void LetsDrinkTea() {
//        try {
//            cap.drink();
//        } catch (TeaNullException e) {
//            try {
//                setCondition(Condition.ANNOYED);
//            } catch (Throwable er) {
//                System.out.println(er.getMessage());
//                setPoliteness(Politeness.RUDE);
//            }
//            System.out.println(e.getMessage());
//        }
//    }

    //метод внутреннего класса
//        private void drink() throws TeaNullException {
//            if (fullness == 0) {
//                throw new TeaNullException("ГДЕ МОЙ ЧАЙ!?!?!?\n" + nameOfUser + ":состояние - ANNOYED", nameOfUser);
//            } else {
//                fullness -= 20;
//                System.out.println(nameOfUser + ": пью чаёк, флюп-флюп");
//            }
//        }

    //    public void setCondition(Condition condition) throws DoubleAnnoyedException {
//        if (this.condition == Condition.ANNOYED)
//            throw new DoubleAnnoyedException(getName() + ": Я В ЯРОСТИ!!!", getName());
//        else this.condition = condition;
//    }
    //
//    @Override
//    public String nameSeller() {
//        return getName();
//    }
//
//    @Override
//    public Condition conditionSeller() {
//        return getCondition();
//    }
//
//    @Override
//    public Politeness politenessSeller() {
//        return getPoliteness();
//    }
//
//    @Override
//    public int locationSeller() {
//        return getLocation();
//    }
//
//    @Override
//    public void sayPhraseSeller() {
//        sayPhrase();
//    }

    //    @Override
//    public void changeCondition() {
//        try {
//            setCondition(Condition.ANNOYED);
//            System.out.println(getName() + ": состояние поменялось на ANNOYED");
//        } catch (DoubleAnnoyedException e) {
//            System.out.println(e.toString());
//            setPoliteness(Politeness.RUDE);
//            System.out.println(getName() + "*ругается как старый сапожник*" + '(' + getPoliteness() + ')');
//        }
//    }
}
