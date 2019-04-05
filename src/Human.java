import java.io.Serializable;
import java.util.Objects;

public abstract class Human implements Serializable {
    private String name ;
    private int x;
    static final long serialVersionUID = 2;
    public abstract void sayPhrase();
    public  void setName(String name){
        if (name.matches(".*[0-9].*")){
            throw new NameWithNumbersException();
        }
        this.name = name;
    };
    public String getName (){
        return this.name;
    };
    public void setLocation (int x){
        this.x = x;
    };
    public int getLocation (){
        return this.x;
    };

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Human human = (Human) obj;
        return getLocation() == human.getLocation() &&
                getName().equals(human.getName());
    }

    @Override
    public String toString() {
        return "Класс: Human\n" +
                "Имя: name=" + name +
                "\nМестоположение: x=" + x;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, x);
    }
}
