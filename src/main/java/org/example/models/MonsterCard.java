package org.example.models;

public class MonsterCard extends Card{
    private String monsterType; //Goblin, Dragon, usw. (ENUM??)

    public MonsterCard(String id, String name, double damage, String monsterType) {
        super(id, name, damage);
        this.monsterType = monsterType;
    }

    public String getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(String monsterType) {
        this.monsterType = monsterType;
    }
    

}
