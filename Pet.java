public class Pet {
    private String nome;
    private String color;
    private int hunger;
    private int happiness;
    private int health;
    private int age;

    public Pet(String name, String color) {
        this.nome = name;
        setColor(color);  // Validação de cor ao definir o atributo
        this.hunger = 100;
        this.happiness = 100;
        this.health = 100;
        this.age = 0;
    }

    public Pet(String name, String color, int hunger, int happiness, int health, int age) {
        this.nome = name;
        setColor(color);  // Validação de cor ao definir o atributo
        this.hunger = hunger;
        this.happiness = happiness;
        this.health = health;
        this.age = age;
    }

    // Método setter para validar a cor
    private void setColor(String color) {
        if (color.equals("black") || color.equals("grey") || color.equals("white")) {
            this.color = color;
        } else {
            throw new IllegalArgumentException("Cor inválida. As opções válidas são: preto, cinza, branco.");
        }
    }


    public String getName() {
        return nome;
    }

    public String getColor() {
        return color;
    }

    public int getHunger() {
        return hunger;
    }

    public int getHappiness() {
        return happiness;
    }

    public int getHealth() {
        return health;
    }

    public int getAge() {
        return age;
    }

    public void feed() {
        if (hunger < 100) {
            hunger += 10;
            happiness += 5;
            health += 5;
            if (hunger > 100) hunger = 100;
            if (happiness > 100) happiness = 100;
            if (health > 100) health = 100;
            updateAge();
        }
    }

    public void play() {
        happiness += 10;
        hunger -= 5;
        health -= 5;
        if (happiness > 100) happiness = 100;
        if (hunger < 0) hunger = 0;
        if (health < 0) health = 0;
        updateAge();
    }

  public void sleep() {
    health += 10;
    hunger -= 5;
    if (health > 100) health = 100;
    if (hunger < 0) hunger = 0;
    updateAge();
    updateHappiness(); 
}

public void updateHappiness() { 
    if (health < 50) {
        happiness -= 10;
    }
    if (hunger < 50) { 
        happiness -= 10;
    }
}

    private void updateAge() {
        if (hunger >= 100 && happiness >= 100 && health >= 100) {
            age++;
        } 
        else if (hunger <= 50 && health <= 50) {
            health -= 15;
            happiness -= 10;
            if (health < 0) health = 0;
        }
    }

    public String getStatus() {
        return "Name: " + nome + "\nColor: " + color + "\nHunger: " + hunger +
               "\nHappiness: " + happiness + "\nHealth: " + health + "\nAge: " + age;
    }
}
